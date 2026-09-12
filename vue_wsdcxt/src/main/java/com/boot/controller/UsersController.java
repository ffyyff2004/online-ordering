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
import com.boot.entity.Users;
import com.boot.service.UsersService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController // 定义为控制器 返回JSON类型数据
@RequestMapping(value = "/users", produces = "application/json; charset=utf-8") // 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class UsersController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private UsersService usersService;

	// 用户登录
	@PostMapping(value = "login.action")
	public Map<String, Object> login(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String username = obj.getString("username");
		String password = obj.getString("password");
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

	// 预处理 获取基础参数
	@GetMapping(value = "createUsers.action")
	public Map<String, Object> createUsers() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("today", VeDate.getStringDateShort());
		return map;
	}

	// 新增网站用户
	@PostMapping(value = "insertUsers.action")
	public Map<String, Object> insertUsers(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Users users = new Users();
		users.setUsername(obj.getString("username")); // 为用户名赋值
		users.setPassword(obj.getString("password")); // 为密码赋值
		users.setRealname(obj.getString("realname")); // 为姓名赋值
		users.setSex(obj.getString("sex")); // 为性别赋值
		users.setBirthday(obj.getString("birthday")); // 为出生日期赋值
		users.setContact(obj.getString("contact")); // 为联系方式赋值
		users.setRegdate(VeDate.getStringDateShort()); // 为注册日期赋值
		int num = this.usersService.insertUsers(users);
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

	// 按主键删除一个网站用户
	@GetMapping(value = "deleteUsers.action")
	public Map<String, Object> deleteUsers(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.usersService.deleteUsers(id);
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

	// 按主键批量删除网站用户
	@PostMapping(value = "deleteUsersByIds.action")
	public Map<String, Object> deleteUsersByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String usersid : ids) {
			num += this.usersService.deleteUsers(usersid);
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

	// 修改网站用户
	@PostMapping(value = "updateUsers.action")
	public Map<String, Object> updateUsers(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Users users = this.usersService.getUsersById(obj.getString("usersid")); // 获取object中usersid字段
		users.setUsername(obj.getString("username")); // 为用户名赋值
		users.setRealname(obj.getString("realname")); // 为姓名赋值
		users.setSex(obj.getString("sex")); // 为性别赋值
		users.setBirthday(obj.getString("birthday")); // 为出生日期赋值
		users.setContact(obj.getString("contact")); // 为联系方式赋值

		Map<String, Object> map = new HashMap<String, Object>();
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

	// 查询全部网站用户数据 在下拉菜单中显示
	@GetMapping(value = "getAllUsers.action")
	public List<Users> getAllUsers() {
		return this.usersService.getAllUsers();
	}

	// 按关键字查询网站用户数据 在下拉菜单中显示
	@GetMapping(value = "getUsersMap.action")
	public Map<String, Object> getUsersMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Users users = new Users();
		users.setUsername(keywords);
		List<Users> list = this.usersService.getUsersByLike(users);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示网站用户数据
	@GetMapping(value = "getUsersByPage.action")
	public Map<String, Object> getUsersByPage(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Users> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Users> list = this.usersService.getAllUsers();
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

	// 通过AJAX在表格中显示网站用户数据
	@GetMapping(value = "getUsers.action")
	public Map<String, Object> getUsers(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Users> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Users users = new Users();
		users.setUsername(keywords);
		List<Users> list = this.usersService.getUsersByLike(users);
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

	// 通过AJAX在表格中显示网站用户数据
	@GetMapping(value = "getOwnerUsers.action")
	public Map<String, Object> getOwnerUsers(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Users> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Users users = new Users();
		// users.setAdminid(id);
		List<Users> list = this.usersService.getUsersByLike(users);
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

	// 按主键查询网站用户数据
	@GetMapping(value = "getUsersById.action")
	public Users getUsersById(String id) {
		Users users = this.usersService.getUsersById(id);
		return users;
	}

	// TODO Auto-generated method stub
}
