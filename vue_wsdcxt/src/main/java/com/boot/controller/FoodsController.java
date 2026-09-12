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
import com.boot.entity.Foods;
import com.boot.service.FoodsService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController //定义为控制器 返回JSON类型数据
@RequestMapping(value = "/foods", produces = "application/json; charset=utf-8")// 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class FoodsController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private FoodsService foodsService;

	// 预处理 获取基础参数
	@GetMapping(value = "createFoods.action")
	public Map<String, Object> createFoods() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("today", VeDate.getStringDateShort());
		return map;
	}

	// 新增食品
	@PostMapping(value = "insertFoods.action")
	public Map<String, Object> insertFoods(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Foods foods = new Foods();
		foods.setFoodsname(obj.getString("foodsname")); //  为食品名称赋值
		foods.setImage(obj.getString("image")); //  为食品图片赋值
		foods.setCateid(obj.getString("cateid")); //  为食品类型赋值
		foods.setPrice(obj.getString("price")); //  为销售价格赋值
		foods.setRecommend(obj.getString("recommend")); //  为是否推荐赋值
		foods.setSpecial(obj.getString("special")); //  为是否特价赋值
		foods.setAddtime(VeDate.getStringDateShort()); // 为上架日期赋值 
		foods.setHits("0"); //  为点击数赋值
		foods.setSellnum("0"); //  为销售单数赋值
		foods.setContents(obj.getString("contents")); //  为食品介绍赋值
		int num = this.foodsService.insertFoods(foods);
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

	// 按主键删除一个食品
	@GetMapping(value = "deleteFoods.action")
	public Map<String, Object> deleteFoods(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.foodsService.deleteFoods(id);
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

	// 按主键批量删除食品
	@PostMapping(value = "deleteFoodsByIds.action")
	public Map<String, Object> deleteFoodsByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String foodsid : ids) {
			num += this.foodsService.deleteFoods(foodsid);
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

	// 修改食品
	@PostMapping(value = "updateFoods.action")
	public Map<String, Object> updateFoods(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Foods foods = this.foodsService.getFoodsById(obj.getString("foodsid")); // 获取object中foodsid字段
		foods.setFoodsname(obj.getString("foodsname")); //  为食品名称赋值
		foods.setImage(obj.getString("image")); //  为食品图片赋值
		foods.setCateid(obj.getString("cateid")); //  为食品类型赋值
		foods.setPrice(obj.getString("price")); //  为销售价格赋值
		foods.setRecommend(obj.getString("recommend")); //  为是否推荐赋值
		foods.setSpecial(obj.getString("special")); //  为是否特价赋值
		foods.setContents(obj.getString("contents")); //  为食品介绍赋值

		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.foodsService.updateFoods(foods);
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

	// 查询全部食品数据 在下拉菜单中显示
	@GetMapping(value = "getAllFoods.action")
	public List<Foods> getAllFoods() {
		return this.foodsService.getAllFoods();
	}

	// 按关键字查询食品数据 在下拉菜单中显示
	@GetMapping(value = "getFoodsMap.action")
	public Map<String, Object> getFoodsMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Foods foods = new Foods();
		foods.setFoodsname(keywords);
		List<Foods> list = this.foodsService.getFoodsByLike(foods);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示食品数据
	@GetMapping(value = "getFoodsByPage.action")
	public Map<String, Object> getFoodsByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Foods> list = this.foodsService.getAllFoods();
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

	// 通过AJAX在表格中显示食品数据
	@GetMapping(value = "getFoods.action")
	public Map<String, Object> getFoods(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Foods foods = new Foods();
		foods.setFoodsname(keywords);
		List<Foods> list = this.foodsService.getFoodsByLike(foods);
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

	// 通过AJAX在表格中显示食品数据
	@GetMapping(value = "getOwnerFoods.action")
	public Map<String, Object> getOwnerFoods(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Foods> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Foods foods = new Foods();
		//foods.setAdminid(id);
		List<Foods> list = this.foodsService.getFoodsByLike(foods);
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

	// 按主键查询食品数据
	@GetMapping(value = "getFoodsById.action")
	public Foods getFoodsById(String id) {
		Foods foods = this.foodsService.getFoodsById(id);
		return foods;
	}

	// TODO Auto-generated method stub
}


