const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const root = path.join(__dirname, '../../main/resources/static');
const adminHtml = fs.readFileSync(path.join(root, 'admin/listorders.html'), 'utf8');

function setup() {
    let app;
    const requests = [];
    const context = {
        Vue: function(options) {
            app = {...options.data};
            for (const [name, method] of Object.entries(options.methods)) app[name] = method.bind(app);
            return app;
        },
        axios: {
            post(url, body) { requests.push({url, body}); return Promise.resolve({data: {success: true, message: '成功'}}); },
            get(url) { requests.push({url}); return Promise.resolve({data: []}); }
        },
        window: {prompt: () => '测试原因', confirm: () => true},
        alert() {}
    };
    const script = [...adminHtml.matchAll(/<script[^>]*>([\s\S]*?)<\/script>/g)].map(match => match[1]).find(source => source.includes('new Vue'));
    vm.runInNewContext(script, context);
    app.loadTable = () => {};
    return {app, requests, context};
}

test('admin actions send POST with reason and block duplicate clicks', async () => {
    const {app, requests} = setup();
    app.status('order', 'cancelDelivery');
    app.status('order', 'cancelDelivery');
    assert.equal(requests.length, 1);
    assert.equal(requests[0].url, '../orders/status.action');
    assert.equal(requests[0].body.action, 'cancelDelivery');
    assert.equal(requests[0].body.reason, '测试原因');
    await new Promise(resolve => setImmediate(resolve));
    assert.equal(app.busy, false);
});

test('cancelled and blank reason prompts do not submit', () => {
    const {app, requests, context} = setup();
    context.window.prompt = () => null;
    app.status('order', 'rejectRefund');
    context.window.prompt = () => '  ';
    app.status('order', 'cancel');
    assert.equal(requests.length, 0);
});

test('admin exposes cancellation, refund review and history without destructive delete buttons', () => {
    for (const label of ['取消订单', '取消配送', '同意退款', '拒绝退款', '操作记录']) assert.ok(adminHtml.includes(label));
    assert.doesNotMatch(adminHtml, /@click="(?:deleteOrders|datadel)/);
});

test('authentication handler loads after axios on every protected page', () => {
    for (const file of ['index', 'orderslist', 'pay', 'cart', 'checkout', 'detail', 'exit', 'orderdetail', 'addtopic']) {
        const html = fs.readFileSync(path.join(root, 'users', file + '.html'), 'utf8');
        assert.ok(html.indexOf('order-access.js') > html.indexOf('axios.min.js'), file);
        assert.equal((html.match(/order-access.js/g) || []).length, 1, file);
        for (const match of html.matchAll(/<script[^>]*>([\s\S]*?)<\/script>/g)) new vm.Script(match[1]);
    }
});

