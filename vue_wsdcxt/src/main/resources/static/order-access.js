axios.interceptors.response.use(function(response) { return response; }, function(error) {
    var response = error.response;
    if (response && response.status === 401) {
        alert('登录已失效，请重新登录。');
        if (window.location.pathname.indexOf('/admin/') >= 0) {
            window.top.location.href = new URL('index.html', window.location.href).href;
        } else {
            sessionStorage.removeItem('userid');
            window.location.href = 'login.html';
        }
    } else {
        alert(response && response.data && response.data.message || '请求失败，请刷新后重试。');
    }
    return Promise.reject(error);
});

