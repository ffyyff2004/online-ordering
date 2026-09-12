package com.boot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boot.entity.Article;
import com.boot.entity.Cart;
import com.boot.entity.Cate;
import com.boot.entity.Complains;
import com.boot.entity.Details;
import com.boot.entity.Fav;
import com.boot.entity.Foods;
import com.boot.entity.Items;
import com.boot.entity.Orders;
import com.boot.entity.Topic;
import com.boot.entity.Users;
import com.boot.service.ArticleService;
import com.boot.service.CartService;
import com.boot.service.CateService;
import com.boot.service.ComplainsService;
import com.boot.service.DetailsService;
import com.boot.service.FavService;
import com.boot.service.FoodsService;
import com.boot.service.OrdersService;
import com.boot.service.TopicService;
import com.boot.service.UsersService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController // 定义为控制器 返回JSON类型数据
@RequestMapping(value = "/index", produces = "application/json; charset=utf-8") // 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class IndexController extends BaseController {

	// @Autowired的作用是自动注入依赖的ServiceBean
	@Autowired
	private UsersService usersService;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CateService cateService;
	@Autowired
	private FoodsService foodsService;
	@Autowired
	private FavService favService;
	@Autowired
	private CartService cartService;
	@Autowired
	private OrdersService ordersService;
	@Autowired
	private DetailsService detailsService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private ComplainsService complainsService;

	// TODO Auto-generated method stub

	// 预处理 获取基础参数
	@GetMapping(value = "front.action")
	public Map<String, Object> front() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Cate> cateList = this.cateService.getAllCate();
		map.put("cateList", cateList);
		List<Foods> hotList = this.foodsService.getFoodsByHot();
		map.put("hotList", hotList);
		return map;
	}

	// 前台首页
	@GetMapping(value = "index.action")
	public Map<String, Object> index() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Cate> cateList = this.cateService.getCateFront();
		List<Cate> frontList = new ArrayList<Cate>();
		for (Cate cate : cateList) {
			List<Foods> foodsList = this.foodsService.getFoodsByCate(cate.getCateid());
			cate.setFoodsList(foodsList);
			frontList.add(cate);
		}
		map.put("frontList", frontList);
		return map;
	}

	// 新闻公告
	@GetMapping(value = "article.action")
	public Map<String, Object> article(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Article> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Article> list = this.articleService.getAllArticle();
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", list.size());
		map.put("data", list);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	// 公告详情
	@GetMapping(value = "read.action")
	public Map<String, Object> read(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Article article = this.articleService.getArticleById(id);
		article.setHits("" + (Integer.parseInt(article.getHits()) + 1));
		this.articleService.updateArticle(article);
		map.put("article", article);
		return map;
	}

	// 用户登录
	@PostMapping(value = "login.action")
	public Map<String, Object> login(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String username = obj.getString("username");
		String password = obj.getString("password");
		if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
			map.put("success", false);
			map.put("message", "请输入用户名和密码");
			return map;
		}
		Users usersEntity = new Users();
		usersEntity.setUsername(username);
		List<Users> userslist = this.usersService.getUsersByCond(usersEntity);
		if (userslist.size() == 0) {
			map.put("success", false);
			map.put("message", "用户名不存在");
		} else {
			Users users = userslist.get(0);
			if (password.equals(users.getPassword())) {
				map.put("success", true);
				map.put("message", "登录成功");
				map.put("userid", users.getUsersid());
				map.put("username", users.getUsername());
				map.put("realname", users.getRealname());
			} else {
				map.put("success", false);
				map.put("message", "密码错误");
			}
		}
		return map;
	}

	// 用户注册
	@PostMapping(value = "register.action")
	public Map<String, Object> register(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Users users = new Users();
		users.setUsername(obj.getString("username"));
		users.setPassword(obj.getString("password"));
		users.setSex(obj.getString("sex"));
		users.setBirthday(obj.getString("birthday"));
		users.setContact(obj.getString("contact"));
		users.setRealname(obj.getString("realname"));
		if (users.getUsername() == null || users.getUsername().trim().isEmpty()
				|| users.getPassword() == null || users.getPassword().isEmpty()
				|| users.getRealname() == null || users.getRealname().trim().isEmpty()
				|| users.getContact() == null || users.getContact().trim().isEmpty()) {
			map.put("success", false);
			map.put("code", 0);
			map.put("message", "请完整填写用户名、密码、姓名和联系方式");
			return map;
		}
		Users condition = new Users();
		condition.setUsername(users.getUsername().trim());
		if (!this.usersService.getUsersByCond(condition).isEmpty()) {
			map.put("success", false);
			map.put("code", 0);
			map.put("message", "用户名已存在，请更换用户名");
			return map;
		}
		users.setRegdate(VeDate.getStringDateShort());
		int num = this.usersService.insertUsers(users);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "注册成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "注册失败");
		}
		return map;
	}

	// 忘记密码：使用用户名和注册联系方式验证身份后重置密码
	@PostMapping(value = "forgotpwd.action")
	public Map<String, Object> forgotpwd(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String username = obj.getString("username");
		String contact = obj.getString("contact");
		String newPassword = obj.getString("newPassword");
		if (username == null || username.trim().isEmpty() || contact == null || contact.trim().isEmpty() || newPassword == null || newPassword.isEmpty()) {
			map.put("success", false);
			map.put("message", "请完整填写用户名、联系方式和新密码");
			return map;
		}
		Users condition = new Users();
		condition.setUsername(username.trim());
		List<Users> userslist = this.usersService.getUsersByCond(condition);
		if (userslist.isEmpty() || !contact.trim().equals(userslist.get(0).getContact())) {
			map.put("success", false);
			map.put("message", "用户名或联系方式不匹配");
			return map;
		}
		Users users = userslist.get(0);
		users.setPassword(newPassword);
		int num = this.usersService.updateUsers(users);
		map.put("success", num > 0);
		map.put("code", num);
		map.put("message", num > 0 ? "密码重置成功，请使用新密码登录" : "密码重置失败，请稍后再试");
		return map;
	}

	// 修改密码
	@PostMapping(value = "editpwd.action")
	public Map<String, Object> editpwd(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		String userid = obj.getString("userid");
		String password = obj.getString("password");
		String repassword = obj.getString("repassword");
		int num = 0;
		Users users = this.usersService.getUsersById(userid);
		if (password.equals(users.getPassword())) {
			users.setPassword(repassword);
			num = this.usersService.updateUsers(users);
			if (num > 0) {
				map.put("success", true);
				map.put("code", num);
				map.put("message", "修改成功");
			} else {
				map.put("success", false);
				map.put("code", num);
				map.put("message", "修改失败");
			}
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "旧密码错误");
		}
		return map;
	}

	// 查看个人信息
	@GetMapping(value = "userinfo.action")
	public Map<String, Object> userinfo(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Users users = this.usersService.getUsersById(id);
		map.put("users", users);
		return map;
	}

	// 修改个人信息
	@PostMapping(value = "personal.action")
	public Map<String, Object> personal(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Users users = this.usersService.getUsersById(obj.getString("usersid"));
		users.setUsername(obj.getString("username"));
		users.setSex(obj.getString("sex"));
		users.setBirthday(obj.getString("birthday"));
		users.setContact(obj.getString("contact"));
		users.setRealname(obj.getString("realname"));
		int num = this.usersService.updateUsers(users);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "修改成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "修改失败");
		}
		return map;
	}

	// 查看购物车
	@GetMapping(value = "cart.action")
	public Map<String, Object> cart(String userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Cart cart = new Cart();
		cart.setUsersid(userid);
		List<Cart> cartList = this.cartService.getCartByCond(cart);
		map.put("cartList", cartList);
		map.put("num", cartList.size());
		return map;
	}

	// 删除购物车中的商品
	@RequestMapping(value = "deletecart.action")
	public Map<String, Object> deletecart(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.cartService.deleteCart(id);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "删除成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "删除失败");
		}
		return map;
	}

	// 查看订单
	@RequestMapping(value = "showOrders.action")
	public Map<String, Object> showOrders(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = new Orders();
		orders.setUsersid(userid);
		Page<Orders> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Orders> list = this.ordersService.getOrdersByCond(orders);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", list.size());
		map.put("data", list);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	// 查看订单明细
	@RequestMapping(value = "orderdetail.action")
	public Map<String, Object> orderdetail(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Details details = new Details();
		details.setOrdercode(id);
		List<Details> detailsList = this.detailsService.getDetailsByCond(details);
		map.put("detailsList", detailsList);
		return map;
	}

	// 添加商品到购物车
	@PostMapping(value = "addcart.action")
	public Map<String, Object> addcart(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Cart cart = new Cart();
		cart.setUsersid(obj.getString("userid"));
		cart.setAddtime(VeDate.getStringDateShort());
		cart.setFoodsid(obj.getString("foodsid"));
		cart.setNum(obj.getString("num"));
		cart.setPrice(obj.getString("price"));
		int num = this.cartService.insertCart(cart);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "失败");
		}
		return map;
	}

	// 购物结算
	@RequestMapping(value = "checkout.action", method = RequestMethod.POST)
	public Map<String, Object> checkout(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Cart cart1 = new Cart();
		cart1.setUsersid(obj.getString("userid"));
		List<Cart> cartList = this.cartService.getCartByCond(cart1);
		int num = -1;
		if (cartList.size() == 0) {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "失败");
		} else {
			// 获取一个1000-9999的随机数 防止同时提交
			String ordercode = "PD" + VeDate.getStringDatex();
			double total = 0;
			for (Cart cart : cartList) {
				Details details = new Details();
				details.setDetailsid(VeDate.getStringDatex() + (Math.random() * 9 + 1) * 1000);
				details.setFoodsid(cart.getFoodsid());
				details.setNum(cart.getNum());
				details.setOrdercode(ordercode);
				details.setPrice(cart.getPrice());
				this.detailsService.insertDetails(details);
				Foods foods = this.foodsService.getFoodsById(cart.getFoodsid());
				foods.setSellnum("" + (Integer.parseInt(foods.getSellnum()) + Integer.parseInt(cart.getNum())));
				this.foodsService.updateFoods(foods);
				total += Double.parseDouble(cart.getPrice()) * Double.parseDouble(cart.getNum());
				this.cartService.deleteCart(cart.getCartid());
			}
			Orders orders = new Orders();
			orders.setAddress(obj.getString("address"));
			orders.setAddtime(VeDate.getStringDateShort());
			orders.setContact(obj.getString("contact"));
			orders.setOrdercode(ordercode);
			orders.setReceiver(obj.getString("receiver"));
			orders.setStatus("未付款");
			orders.setTotal("" + VeDate.getDouble(total));
			orders.setUsersid(obj.getString("userid"));
			num = this.ordersService.insertOrders(orders);
		}
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "失败");
		}
		return map;
	}

	// 确认收货
	@RequestMapping(value = "over.action")
	public Map<String, Object> over(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = this.ordersService.getOrdersById(id);
		orders.setStatus("已收货");
		int num = this.ordersService.updateOrders(orders);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "收货成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "收货失败");
		}
		return map;
	}

	// 付款
	@RequestMapping(value = "pay.action")
	public Map<String, Object> pay(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = this.ordersService.getOrdersById(id);
		orders.setStatus("已付款");
		int num = this.ordersService.updateOrders(orders);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "付款成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "付款失败");
		}
		return map;
	}

	// 取消订单
	@RequestMapping(value = "cancel.action")
	public Map<String, Object> cancel(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = this.ordersService.getOrdersById(id);
		orders.setStatus("已取消");
		int num = this.ordersService.updateOrders(orders);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "取消成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "取消失败");
		}
		return map;
	}

	@RequestMapping(value = "prePay.action")
	public Map<String, Object> prePay(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = this.ordersService.getOrdersById(id);
		map.put("orders", orders);
		return map;
	}

	// 加入收藏
	@PostMapping(value = "addfav.action")
	public Map<String, Object> addFav(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		int num = 0;
		Fav fav = new Fav();
		fav.setUsersid(obj.getString("userid"));
		fav.setFoodsid(obj.getString("foodsid"));
		List<Fav> list = this.favService.getFavByCond(fav);
		if (list.size() != 0) {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "您已经收藏本商品");
			return map;
		}
		fav.setAddtime(VeDate.getStringDateShort());
		num = this.favService.insertFav(fav);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "收藏成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "收藏失败");
		}
		return map;
	}

	// 我的收藏
	@GetMapping(value = "myfav.action")
	public Map<String, Object> myFav(String userid, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Fav> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Fav fav = new Fav();
		fav.setUsersid(userid);
		List<Fav> list = this.favService.getFavByCond(fav);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", list.size());
		map.put("data", list);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	// 删除收藏
	@RequestMapping(value = "deletefav.action")
	public Map<String, Object> deleteFav(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.favService.deleteFav(id);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "删除成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "删除失败");
		}
		return map;
	}

	// 添加意见反馈信息
	@PostMapping(value = "addComplains.action")
	public Map<String, Object> addComplains(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Complains complains = new Complains();
		complains.setAddtime(VeDate.getStringDateShort());
		complains.setStatus("未回复");
		complains.setReps(" ");
		complains.setTitle(obj.getString("title"));
		complains.setContents(obj.getString("contents"));
		complains.setUsersid(obj.getString("usersid"));
		int num = this.complainsService.insertComplains(complains);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "反馈成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "反馈失败");
		}
		return map;
	}

	// 我的意见反馈信息
	@RequestMapping(value = "myComplains.action")
	public Map<String, Object> myComplains(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "12") Integer limit, String userid) {
		System.out.println("userid ==> " + userid);
		Map<String, Object> map = new HashMap<String, Object>();
		Complains complains = new Complains();
		complains.setUsersid(userid);
		// 定义当前页和分页条数
		Page<Complains> pager = com.github.pagehelper.PageHelper.startPage(page, limit);
		List<Complains> complainsList = this.complainsService.getComplainsByCond(complains);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", complainsList.size());
		map.put("data", complainsList);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	@RequestMapping(value = "cate.action")
	public Map<String, Object> cate(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "12") Integer limit, String id) {
		System.out.println("cateid  ==>  " + id);
		Map<String, Object> map = new HashMap<String, Object>();
		Cate cate = this.cateService.getCateById(id);
		Foods foods = new Foods();
		foods.setCateid(id);
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Foods> foodsList = this.foodsService.getFoodsByCond(foods);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", foodsList.size());
		map.put("data", foodsList);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		map.put("cate", cate);
		return map;
	}

	@RequestMapping(value = "recommend.action")
	public Map<String, Object> recommend(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "12") Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Foods foods = new Foods();
		foods.setRecommend("是");
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Foods> foodsList = this.foodsService.getFoodsByCond(foods);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", foodsList.size());
		map.put("data", foodsList);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	@RequestMapping(value = "special.action")
	public Map<String, Object> special(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "12") Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Foods foods = new Foods();
		foods.setSpecial("是");
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Foods> foodsList = this.foodsService.getFoodsByCond(foods);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", foodsList.size());
		map.put("data", foodsList);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	@RequestMapping(value = "all.action")
	public Map<String, Object> all(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "12") Integer limit) {
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Foods> foodsList = this.foodsService.getAllFoods();
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", foodsList.size());
		map.put("data", foodsList);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	@RequestMapping(value = "queryFoods.action")
	public Map<String, Object> queryFoods(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "12") Integer limit, String name) {
		System.out.println("name  ==>  " + name);
		Map<String, Object> map = new HashMap<String, Object>();
		Foods foods = new Foods();
		foods.setFoodsname(name);
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Foods> foodsList = this.foodsService.getFoodsByCond(foods);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", foodsList.size());
		map.put("data", foodsList);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	@RequestMapping(value = "detail.action")
	public Map<String, Object> detail(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Foods foods = this.foodsService.getFoodsById(id);
		foods.setHits("" + (Integer.parseInt(foods.getHits()) + 1));
		this.foodsService.updateFoods(foods);
		map.put("foods", foods);
		Topic topic = new Topic();
		topic.setFoodsid(id);
		List<Topic> topicList = this.topicService.getTopicByCond(topic);
		map.put("topicList", topicList);
		return map;
	}

	@RequestMapping(value = "preTopic.action")
	public Map<String, Object> preTopic(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = this.ordersService.getOrdersById(id);
		map.put("orders", orders);
		String ordercode = orders.getOrdercode();
		Details details = new Details();
		details.setOrdercode(ordercode);
		List<Details> detailsList = this.detailsService.getDetailsByCond(details);
		List<Items> itemsList = new ArrayList<Items>();
		for (Details x : detailsList) {
			Items items = new Items();
			items.setDetailsid(x.getDetailsid());
			items.setFoodsid(x.getFoodsid());
			items.setFoodsname(x.getFoodsname());
			items.setOrdercode(ordercode);
			itemsList.add(items);
		}
		map.put("detailsList", itemsList);
		return map;
	}

	@PostMapping(value = "addTopic.action")
	public Map<String, Object> addTopic(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Orders orders = this.ordersService.getOrdersById(obj.getString("ordersid"));
		orders.setStatus("已评价");
		this.ordersService.updateOrders(orders);
		JSONArray detailsList = JSONArray.parseArray(obj.getString("detailsList"));
		int num = 0;
		for (int i = 0; i < detailsList.size(); i++) {
			JSONObject myJson = JSONObject.parseObject(detailsList.getString(i).toString());
			Topic topic = new Topic();
			topic.setAddtime(VeDate.getStringDate());
			topic.setContents(myJson.getString("contents"));
			topic.setFoodsid(myJson.getString("foodsid"));
			topic.setNum(myJson.getString("tnum"));
			topic.setOrdersid(orders.getOrdersid());
			topic.setUsersid(orders.getUsersid());
			topic.setTopicid("T" + VeDate.getStringId() + i);
			num += this.topicService.insertTopic(topic);
		}
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "保存成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "保存失败");
		}
		return map;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/*", method = RequestMethod.OPTIONS)
	public ResponseEntity handleOptions() {
		return (ResponseEntity) ResponseEntity.noContent();
	}

	// TODO Auto-generated method stub
}
