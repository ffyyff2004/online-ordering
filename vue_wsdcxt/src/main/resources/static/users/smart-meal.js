const vue = new Vue({
    el: '#app',
    data: {
        islogin: true, userid: '', realname: '', username: '用户', cate: [], front: [], key: '',
        smartMealVisible: false, smartRequirement: '', smartLoading: false, smartAdding: false,
        smartMessage: '', smartGroups: [], smartBudget: null, smartSelection: {}, smartSubmittedRequirement: ''
    },
    computed: {
        searchSuggestions: function() {
            const keyword = this.key.trim().toLowerCase();
            if (!keyword) return [];
            const allFoods = this.front.reduce((foods, category) => foods.concat(category.foodsList || []), []);
            const characters = keyword.replace(/\s/g, '').split('');
            return allFoods.filter(item => item.foodsname && characters.some(character => item.foodsname.toLowerCase().indexOf(character) >= 0)).slice(0, 6);
        },
        smartTotalCents: function() {
            return Object.values(this.smartSelection).reduce((total, item) => total + Math.round(Number(item.price) * 100), 0);
        },
        smartSummary: function() {
            return '本轮已加入：￥' + (this.smartTotalCents / 100).toFixed(2) +
                (this.smartBudget === null ? '' : ' / 预算 ￥' + Number(this.smartBudget).toFixed(2));
        }
    },
    methods: {
        chooseSuggestion: function(name) {
            this.key = name;
            this.query();
        },
        loadPage: function() {
            this.userid = sessionStorage.getItem('userid');
            this.islogin = !this.userid;
            this.realname = sessionStorage.getItem('realname') || '';
            this.username = sessionStorage.getItem('username') || this.realname || '用户';
            axios.get('../index/front.action').then(result => { this.cate = result.data.cateList || []; }).catch(() => {});
            axios.get('../index/index.action').then(result => { this.front = result.data.frontList || []; }).catch(() => {});
        },
        openSmartMeal: function() {
            this.smartMealVisible = true;
            this.$nextTick(() => this.$refs.smartInput.focus());
        },
        closeSmartMeal: function() {
            this.smartMealVisible = false;
            this.$nextTick(() => document.querySelector('.smart-meal-launcher').focus());
        },
        getSmartRecommendations: function(shuffle) {
            if (this.smartLoading || this.smartAdding) return;
            const requirement = shuffle ? this.smartSubmittedRequirement : this.smartRequirement.trim();
            if (!requirement) { this.smartMessage = '请先输入你的用餐需求。'; return; }
            if (!this.userid) { this.smartMessage = '请先登录后使用智能选餐助手。'; return; }
            this.smartGroups = [];
            this.smartLoading = true;
            this.smartMessage = '正在读取菜单，按你的需求分组挑选…';
            axios.post('../smart-meal/recommend.action', {requirement: requirement}, {timeout: 65000}).then(result => {
                if (result.data.success && Array.isArray(result.data.groups)) {
                    if (!shuffle) this.smartSelection = {};
                    this.smartBudget = result.data.budget == null ? null : Number(result.data.budget);
                    this.smartGroups = result.data.groups;
                    this.smartSubmittedRequirement = requirement;
                    this.smartMessage = result.data.message;
                } else {
                    this.smartMessage = result.data.message || '暂时没有合适的候选，请调整需求后重试。';
                }
            }).catch(() => {
                this.smartMessage = '选餐请求失败或超时，请稍后重试。';
            }).finally(() => { this.smartLoading = false; });
        },
        hasAdded: function(item) { return Boolean(this.smartSelection[item.foodsid]); },
        exceedsBudget: function(item) {
            return this.smartBudget !== null && this.smartTotalCents + Math.round(Number(item.price) * 100) > Math.round(this.smartBudget * 100);
        },
        selectedCount: function(type) {
            return Object.values(this.smartSelection).filter(item => item.type === type).length;
        },
        addLabel: function(item) {
            if (this.hasAdded(item)) return '已加入';
            if (this.exceedsBudget(item)) return '超出本轮预算';
            return '加入购物车';
        },
        addSmartMealToCart: function(item, group) {
            if (this.smartAdding || this.smartLoading || this.hasAdded(item) || this.exceedsBudget(item)) return;
            if (!this.userid) { this.smartMessage = '请先登录后加入购物车。'; return; }
            this.smartAdding = true;
            axios.post('../index/addcart.action', {
                userid: this.userid, foodsid: item.foodsid, price: item.price, num: '1'
            }, {timeout: 15000}).then(result => {
                if (result.data.success) {
                    this.$set(this.smartSelection, item.foodsid, {price: item.price, type: group.type});
                    this.smartMessage = '已将“' + item.foodsname + '”加入购物车，可继续选择其他菜品。';
                } else {
                    this.smartMessage = '加入购物车失败，请稍后重试。';
                }
            }).catch(() => {
                this.smartMessage = '未收到加购确认，请先到购物车检查，避免重复添加。';
            }).finally(() => { this.smartAdding = false; });
        },
        query: function() { window.location.href = 'query.html?id=' + encodeURIComponent(this.key); }
    },
    created: function() { this.loadPage(); }
});
