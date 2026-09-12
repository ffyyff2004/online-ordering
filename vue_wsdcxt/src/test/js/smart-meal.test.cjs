const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const source = fs.readFileSync(path.join(__dirname, '../../main/resources/static/users/smart-meal.js'), 'utf8');
const settle = () => new Promise(resolve => setImmediate(resolve));

function setup() {
    let app;
    const requests = [];
    let nextResponse;
    const context = {
        Vue: function(options) {
            app = {...options.data};
            for (const [name, method] of Object.entries(options.methods)) app[name] = method.bind(app);
            for (const [name, getter] of Object.entries(options.computed)) Object.defineProperty(app, name, {get: getter.bind(app)});
            app.$set = (target, key, value) => {target[key] = value;};
            app.userid = 'test-user';
            return app;
        },
        axios: {post: (url, body) => {
            requests.push({url, body});
            return nextResponse ? nextResponse() : Promise.resolve({data: {success: true}});
        }},
        window: {location: {}},
        sessionStorage: {getItem: () => null}
    };
    vm.runInNewContext(source, context);
    return {app, requests, response: handler => { nextResponse = handler; }};
}

function grouped() {
    return {success: true, budget: 50, message: '分组成功', groups: [
        {type: 'meat', label: '荤菜', count: 1, items: [{foodsid: 'meat', foodsname: '肉丝', price: 22.6}]},
        {type: 'vegetable', label: '素菜', count: 1, items: [{foodsid: 'veg', foodsname: '豆腐', price: 16.8}]}
    ]};
}

test('loads independent groups rather than fixed three plans', async () => {
    const {app, requests, response} = setup();
    app.smartRequirement = '50元以内，一荤一素';
    response(() => Promise.resolve({data: grouped()}));
    app.getSmartRecommendations(false);
    await settle();
    assert.equal(app.smartGroups.length, 2);
    assert.equal(app.smartGroups[0].label, '荤菜');
    assert.equal(requests[0].body.requirement, '50元以内，一荤一素');
    assert.equal(app.smartLoading, false);
});

test('adds only clicked dishes and blocks simultaneous or repeated clicks', async () => {
    const {app, requests} = setup();
    app.smartBudget = 50;
    const data = grouped();
    const meat = data.groups[0].items[0];
    const vegetable = data.groups[1].items[0];
    app.addSmartMealToCart(meat, data.groups[0]);
    app.addSmartMealToCart(meat, data.groups[0]);
    assert.equal(requests.length, 1);
    await settle();
    app.addSmartMealToCart(meat, data.groups[0]);
    assert.equal(requests.length, 1);
    app.addSmartMealToCart(vegetable, data.groups[1]);
    await settle();
    assert.equal(requests.length, 2);
    assert.equal(requests[1].body.foodsid, 'veg');
    assert.equal(app.smartTotalCents, 3940);
    assert.equal(app.selectedCount('meat'), 1);
    assert.equal(app.selectedCount('vegetable'), 1);
    app.addSmartMealToCart({foodsid: 'extra', price: 20}, data.groups[0]);
    assert.equal(requests.length, 2);
});

test('shuffle retains previous additions and uses the submitted requirement', async () => {
    const {app, requests, response} = setup();
    app.smartSubmittedRequirement = '一荤一素';
    app.smartRequirement = '新的未提交需求';
    app.smartSelection = {meat: {price: 22.6, type: 'meat'}};
    response(() => Promise.resolve({data: grouped()}));
    app.getSmartRecommendations(true);
    await settle();
    assert.equal(requests[0].body.requirement, '一荤一素');
    assert.equal(app.smartTotalCents, 2260);
});

test('no match clears stale cards and shows the server explanation', async () => {
    const {app, response} = setup();
    app.smartGroups = grouped().groups;
    app.smartRequirement = '5元以内，一荤一素';
    response(() => Promise.resolve({data: {success: false, groups: [], message: '没有符合预算的搭配'}}));
    app.getSmartRecommendations(false);
    await settle();
    assert.equal(app.smartGroups.length, 0);
    assert.equal(app.smartMessage, '没有符合预算的搭配');
    assert.equal(app.smartLoading, false);
});

test('network failure does not mark an item as added', async () => {
    const {app, response} = setup();
    response(() => Promise.reject(new Error('network')));
    app.addSmartMealToCart(grouped().groups[0].items[0], grouped().groups[0]);
    await settle();
    assert.equal(app.smartAdding, false);
    assert.equal(app.smartTotalCents, 0);
    assert.match(app.smartMessage, /购物车检查/);
});

test('homepage keeps Chinese, four-column grid, and no raw assistant interpolation', () => {
    const html = fs.readFileSync(path.join(__dirname, '../../main/resources/static/users/index.html'), 'utf8');
    assert.match(html, /网上订餐系统/);
    assert.match(html, /smart-food-grid/);
    assert.match(html, /v-for="group in smartGroups"/);
    assert.doesNotMatch(html, /\{\{\s*(smartMessage|item\.foodsname|item\.price|item\.reason)/);
    assert.doesNotMatch(html, /缃戜笂|鏅鸿兘|\uFFFD/);
});
