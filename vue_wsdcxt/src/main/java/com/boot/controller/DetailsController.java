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
import com.boot.entity.Details;
import com.boot.service.DetailsService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController //定义为控制器 返回JSON类型数据
@RequestMapping(value = "/details", produces = "application/json; charset=utf-8")// 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class DetailsController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private DetailsService detailsService;

	// 预处理 获取基础参数
	@GetMapping(value = "createDetails.action")
	public Map<String, Object> createDetails() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dno", "D" + VeDate.getStringDatex());
		return map;
	}

	// 新增订单明细
	@PostMapping(value = "insertDetails.action")
	public Map<String, Object> insertDetails(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Details details = new Details();
		details.setOrdercode(obj.getString("ordercode")); //  为订单号赋值
		details.setFoodsid(obj.getString("foodsid")); //  为食品赋值
		details.setPrice(obj.getString("price")); //  为单价赋值
		details.setNum(obj.getString("num")); //  为数量赋值
		int num = this.detailsService.insertDetails(details);
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

	// 按主键删除一个订单明细
	@GetMapping(value = "deleteDetails.action")
	public Map<String, Object> deleteDetails(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.detailsService.deleteDetails(id);
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

	// 按主键批量删除订单明细
	@PostMapping(value = "deleteDetailsByIds.action")
	public Map<String, Object> deleteDetailsByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String detailsid : ids) {
			num += this.detailsService.deleteDetails(detailsid);
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

	// 修改订单明细
	@PostMapping(value = "updateDetails.action")
	public Map<String, Object> updateDetails(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Details details = this.detailsService.getDetailsById(obj.getString("detailsid")); // 获取object中detailsid字段
		details.setOrdercode(obj.getString("ordercode")); //  为订单号赋值
		details.setFoodsid(obj.getString("foodsid")); //  为食品赋值
		details.setPrice(obj.getString("price")); //  为单价赋值
		details.setNum(obj.getString("num")); //  为数量赋值

		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.detailsService.updateDetails(details);
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

	// 查询全部订单明细数据 在下拉菜单中显示
	@GetMapping(value = "getAllDetails.action")
	public List<Details> getAllDetails() {
		return this.detailsService.getAllDetails();
	}

	// 按关键字查询订单明细数据 在下拉菜单中显示
	@GetMapping(value = "getDetailsMap.action")
	public Map<String, Object> getDetailsMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Details details = new Details();
		details.setOrdercode(keywords);
		List<Details> list = this.detailsService.getDetailsByLike(details);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示订单明细数据
	@GetMapping(value = "getDetailsByPage.action")
	public Map<String, Object> getDetailsByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Details> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Details> list = this.detailsService.getAllDetails();
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

	// 通过AJAX在表格中显示订单明细数据
	@GetMapping(value = "getDetails.action")
	public Map<String, Object> getDetails(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Details> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Details details = new Details();
		details.setOrdercode(keywords);
		List<Details> list = this.detailsService.getDetailsByLike(details);
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

	// 通过AJAX在表格中显示订单明细数据
	@GetMapping(value = "getOwnerDetails.action")
	public Map<String, Object> getOwnerDetails(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Details> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Details details = new Details();
		//details.setAdminid(id);
		List<Details> list = this.detailsService.getDetailsByLike(details);
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

	// 按主键查询订单明细数据
	@GetMapping(value = "getDetailsById.action")
	public Details getDetailsById(String id) {
		Details details = this.detailsService.getDetailsById(id);
		return details;
	}

	// TODO Auto-generated method stub
}


