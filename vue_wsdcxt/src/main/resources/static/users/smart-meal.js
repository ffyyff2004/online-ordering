const vue = new Vue({
    el: '#app',
    data: {
        islogin: true,
        userid: '',
        realname: '',
        username: '用户',
        cate: [],
        front: [],
        key: '',
        smartMealVisible: false,
        smartRequirement: '',
        smartLoading: false,
        smartMessage: '',
        smartRecommendations: []
    },
    methods: {
        loadPage: function() {
            this.userid = sessionStorage.getItem('userid');
            this.islogin = !this.userid;
            this.realname = sessionStorage.getItem('realname') || '';
            this.username = sessionStorage.getItem('username') || this.realname || '用户';
            axios.get('../index/front.action').then(result => {
                this.cate = result.data.cateList || [];
            }).catch(() => {});
            axios.get('../index/index.action').then(result => {
                this.front = result.data.frontList || [];
            }).catch(() => {});
        },
        openSmartMeal: function() {
            this.smartMealVisible = true;
            this.$nextTick(() => this.$refs.smartInput.focus());
        },
        closeSmartMeal: function() {
            this.smartMealVisible = false;
            this.$nextTick(() => document.querySelector('.smart-meal-launcher').focus());
        },
        getSmartRecommendations: function() {
            if (this.smartLoading) return;
            this.smartRecommendations = [];
            if (!this.smartRequirement.trim()) {
                this.smartMessage = '请先输入你的用餐需求。';
                return;
            }
            if (!this.userid) {
                this.smartMessage = '请先登录后使用智能选餐助手。';
                return;
            }
            this.smartLoading = true;
            this.smartMessage = '正在根据你的需求挑选餐品，请稍候。';
            axios.post('../smart-meal/recommend.action', {
                username: this.username,
                requirement: this.smartRequirement.trim()
            }, {timeout: 45000}).then(result => {
                if (result.data.success && Array.isArray(result.data.recommendations)) {
                    this.smartRecommendations = result.data.recommendations.map(item => Object.assign({}, item, {added: false, adding: false}));
                    this.smartMessage = '已为你挑选好餐品，请选择喜欢的方案。';
                } else {
                    this.smartMessage = result.data.message || '暂时没有合适的方案，请调整需求后重试。';
                }
            }).catch(() => {
                this.smartMessage = '智能选餐暂时不可用，请稍后重试。';
            }).finally(() => { this.smartLoading = false; });
        },
        addSmartMealToCart: function(item) {
            if (item.adding || item.added) return;
            if (!this.userid) {
                this.smartMessage = '请先登录后加入购物车。';
                return;
            }
            item.adding = true;
            axios.post('../index/addcart.action', {
                userid: this.userid, foodsid: item.foodsid, price: item.price, num: '1'
            }, {timeout: 15000}).then(result => {
                if (result.data.success) {
                    item.added = true;
                    this.smartMessage = '已将“' + item.foodsname + '”加入购物车。';
                } else {
                    this.smartMessage = '加入购物车失败，请稍后重试。';
                }
            }).catch(() => {
                this.smartMessage = '加入购物车失败，请检查网络后重试。';
            }).finally(() => { item.adding = false; });
        },
        query: function() {
            window.location.href = 'query.html?id=' + encodeURIComponent(this.key);
        }
    },
    created: function() { this.loadPage(); }
});
