package com.boot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.boot.entity.Orders;
import com.boot.service.OrdersService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController // 定义为控制器 返回JSON类型数据
@RequestMapping(value = "/orders", produces = "application/json; charset=utf-8") // 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class OrdersController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private OrdersService ordersService;

	// 预处理 获取基础参数
	@GetMapping(value = "createOrders.action")
	public Map<String, Object> createOrders() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("today", VeDate.getStringDateShort());
		map.put("ono", "O" + VeDate.getStringDatex());
		return map;
	}

	// 新增订单
	@PostMapping(value = "insertOrders.action")
	public Map<String, Object> insertOrders(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Orders orders = new Orders();
		orders.setOrdercode(obj.getString("ordercode")); // 为订单号赋值
		orders.setUsersid(obj.getString("usersid")); // 为用户赋值
		orders.setTotal(obj.getString("total")); // 为总计赋值
		orders.setStatus("待付款"); // 为状态赋值
		orders.setAddtime(VeDate.getStringDateShort()); // 为日期赋值
		orders.setReceiver(obj.getString("receiver")); // 为收货人赋值
		orders.setAddress(obj.getString("address")); // 为送餐地址赋值
		orders.setContact(obj.getString("contact")); // 为联系方式赋值
		int num = this.ordersService.insertOrders(orders);
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

	// 按主键删除一个订单
	@GetMapping(value = "deleteOrders.action")
	public Map<String, Object> deleteOrders(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.ordersService.deleteOrders(id);
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

	// 按主键批量删除订单
	@PostMapping(value = "deleteOrdersByIds.action")
	public Map<String, Object> deleteOrdersByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String ordersid : ids) {
			num += this.ordersService.deleteOrders(ordersid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
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

	// 修改订单
	@PostMapping(value = "updateOrders.action")
	public Map<String, Object> updateOrders(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Orders orders = this.ordersService.getOrdersById(obj.getString("ordersid")); // 获取object中ordersid字段
		orders.setOrdercode(obj.getString("ordercode")); // 为订单号赋值
		orders.setUsersid(obj.getString("usersid")); // 为用户赋值
		orders.setTotal(obj.getString("total")); // 为总计赋值
		orders.setReceiver(obj.getString("receiver")); // 为收货人赋值
		orders.setAddress(obj.getString("address")); // 为送餐地址赋值
		orders.setContact(obj.getString("contact")); // 为联系方式赋值

		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.ordersService.updateOrders(orders);
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

	// 更新订单状态
	@GetMapping(value = "status.action")
	public Map<String, Object> status(String id, String targetStatus) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = this.ordersService.getOrdersById(id);
		if (orders == null) {
			map.put("success", false);
			map.put("message", "订单不存在");
			return map;
		}
		String currentStatus = orders.getStatus();
		boolean valid = ("已付款".equals(currentStatus) && "已接单".equals(targetStatus))
				|| ("已接单".equals(currentStatus) && "制作中".equals(targetStatus))
				|| ("制作中".equals(currentStatus) && "配送中".equals(targetStatus))
				|| ("配送中".equals(currentStatus) && "已完成".equals(targetStatus))
				|| ("已付款".equals(currentStatus) && "退款中".equals(targetStatus))
				|| ("退款中".equals(currentStatus) && "已退款".equals(targetStatus));
		if (!valid) {
			map.put("success", false);
			map.put("message", "订单不能从“" + currentStatus + "”变更为“" + targetStatus + "”");
			return map;
		}
		orders.setStatus(targetStatus);
		int num = this.ordersService.updateOrders(orders);
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

	// 查询全部订单数据 在下拉菜单中显示
	@GetMapping(value = "getAllOrders.action")
	public List<Orders> getAllOrders() {
		return this.ordersService.getAllOrders();
	}

	// 按关键字查询订单数据 在下拉菜单中显示
	@GetMapping(value = "getOrdersMap.action")
	public Map<String, Object> getOrdersMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Orders orders = new Orders();
		orders.setOrdercode(keywords);
		List<Orders> list = this.ordersService.getOrdersByLike(orders);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示订单数据
	@GetMapping(value = "getOrdersByPage.action")
	public Map<String, Object> getOrdersByPage(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Orders> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Orders> list = this.ordersService.getAllOrders();
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

	// 通过AJAX在表格中显示订单数据
	@GetMapping(value = "getOrders.action")
	public Map<String, Object> getOrders(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Orders> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Orders orders = new Orders();
		orders.setOrdercode(keywords);
		List<Orders> list = this.ordersService.getOrdersByLike(orders);
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

	// 通过AJAX在表格中显示订单数据
	@GetMapping(value = "getUserOrders.action")
	public Map<String, Object> getUserOrders(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Orders> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Orders orders = new Orders();
		orders.setUsersid(id);
		List<Orders> list = this.ordersService.getOrdersByLike(orders);
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

	// 通过AJAX在表格中显示订单数据
	@GetMapping(value = "getOwnerOrders.action")
	public Map<String, Object> getOwnerOrders(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Orders> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Orders orders = new Orders();
		// orders.setAdminid(id);
		List<Orders> list = this.ordersService.getOrdersByLike(orders);
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

	// 按主键查询订单数据
	@GetMapping(value = "getOrdersById.action")
	public Orders getOrdersById(String id) {
		Orders orders = this.ordersService.getOrdersById(id);
		return orders;
	}

	// TODO Auto-generated method stub
}
