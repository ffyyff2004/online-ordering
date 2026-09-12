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
import com.boot.entity.Topic;
import com.boot.service.TopicService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController //定义为控制器 返回JSON类型数据
@RequestMapping(value = "/topic", produces = "application/json; charset=utf-8")// 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class TopicController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private TopicService topicService;

	// 预处理 获取基础参数
	@GetMapping(value = "createTopic.action")
	public Map<String, Object> createTopic() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("today", VeDate.getStringDateShort());
		return map;
	}

	// 新增订单评价
	@PostMapping(value = "insertTopic.action")
	public Map<String, Object> insertTopic(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Topic topic = new Topic();
		topic.setOrdersid(obj.getString("ordersid")); //  为订单赋值
		topic.setUsersid(obj.getString("usersid")); //  为用户赋值
		topic.setFoodsid(obj.getString("foodsid")); //  为食品赋值
		topic.setNum(obj.getString("num")); //  为评分赋值
		topic.setContents(obj.getString("contents")); //  为内容赋值
		topic.setAddtime(VeDate.getStringDateShort()); // 为日期赋值 
		int num = this.topicService.insertTopic(topic);
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

	// 按主键删除一个订单评价
	@GetMapping(value = "deleteTopic.action")
	public Map<String, Object> deleteTopic(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.topicService.deleteTopic(id);
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

	// 按主键批量删除订单评价
	@PostMapping(value = "deleteTopicByIds.action")
	public Map<String, Object> deleteTopicByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String topicid : ids) {
			num += this.topicService.deleteTopic(topicid);
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

	// 修改订单评价
	@PostMapping(value = "updateTopic.action")
	public Map<String, Object> updateTopic(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Topic topic = this.topicService.getTopicById(obj.getString("topicid")); // 获取object中topicid字段
		topic.setOrdersid(obj.getString("ordersid")); //  为订单赋值
		topic.setUsersid(obj.getString("usersid")); //  为用户赋值
		topic.setFoodsid(obj.getString("foodsid")); //  为食品赋值
		topic.setNum(obj.getString("num")); //  为评分赋值
		topic.setContents(obj.getString("contents")); //  为内容赋值

		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.topicService.updateTopic(topic);
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

	// 查询全部订单评价数据 在下拉菜单中显示
	@GetMapping(value = "getAllTopic.action")
	public List<Topic> getAllTopic() {
		return this.topicService.getAllTopic();
	}

	// 按关键字查询订单评价数据 在下拉菜单中显示
	@GetMapping(value = "getTopicMap.action")
	public Map<String, Object> getTopicMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Topic topic = new Topic();
		topic.setOrdersid(keywords);
		List<Topic> list = this.topicService.getTopicByLike(topic);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示订单评价数据
	@GetMapping(value = "getTopicByPage.action")
	public Map<String, Object> getTopicByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Topic> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Topic> list = this.topicService.getAllTopic();
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

	// 通过AJAX在表格中显示订单评价数据
	@GetMapping(value = "getTopic.action")
	public Map<String, Object> getTopic(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Topic> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Topic topic = new Topic();
		topic.setOrdersid(keywords);
		List<Topic> list = this.topicService.getTopicByLike(topic);
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

	// 通过AJAX在表格中显示订单评价数据
	@GetMapping(value = "getUserTopic.action")
	public Map<String, Object> getUserTopic(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Topic> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Topic topic = new Topic();
		topic.setUsersid(id);
		List<Topic> list = this.topicService.getTopicByLike(topic);
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

	// 通过AJAX在表格中显示订单评价数据
	@GetMapping(value = "getOwnerTopic.action")
	public Map<String, Object> getOwnerTopic(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Topic> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Topic topic = new Topic();
		//topic.setAdminid(id);
		List<Topic> list = this.topicService.getTopicByLike(topic);
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

	// 按主键查询订单评价数据
	@GetMapping(value = "getTopicById.action")
	public Topic getTopicById(String id) {
		Topic topic = this.topicService.getTopicById(id);
		return topic;
	}

	// TODO Auto-generated method stub
}


