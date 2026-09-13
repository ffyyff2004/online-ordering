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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.boot.entity.Admin;
import com.boot.service.AdminService;

@RestController //定义为控制器 返回JSON类型数据
@RequestMapping(value = "/login", produces = "application/json; charset=utf-8")// 设置路径
@CrossOrigin // 允许从不同的域访问其资源
public class LoginController extends BaseController {

	// @Autowired的作用是自动注入依赖的ServiceBean
	@Autowired
	private AdminService adminService;

	// 管理员登录
	@PostMapping(value = "login.action")
	public Map<String, Object> login(@RequestBody String jsonStr, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String username = obj.getString("username");
		String password = obj.getString("password");
		Admin adminEntity = new Admin();
		adminEntity.setUsername(username);
		List<Admin> adminlist = this.adminService.getAdminByCond(adminEntity);
		if (adminlist.size() == 0) {
			map.put("success", false);
			map.put("message", "用户名不存在");
		} else {
			Admin admin = adminlist.get(0);
			if (password != null && password.equals(admin.getPassword())) {
                getRequest().changeSessionId();
				session.setAttribute("adminUserId", admin.getAdminid());
				session.setAttribute("adminUsername", admin.getUsername());
				session.setAttribute("adminRealname", admin.getRealname());
				map.put("success", true);
				map.put("message", "登录成功");
				map.put("adminname", admin.getUsername());
				map.put("realname", admin.getRealname());
				map.put("role", "管理员");
			} else {
				map.put("success", false);
				map.put("message", "密码错误");
			}
		}
		return map;
	}

	// 查询当前管理员登录状态
	@GetMapping("session.action")
	public Map<String, Object> session(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		String adminId = (String) session.getAttribute("adminUserId");
		if (adminId == null) {
			map.put("success", false);
			map.put("message", "管理员登录已失效，请重新登录");
			return map;
		}
		map.put("success", true);
		map.put("adminname", session.getAttribute("adminUsername"));
		map.put("realname", session.getAttribute("adminRealname"));
		map.put("role", "管理员");
		return map;
	}

	// 管理员退出登录
	@GetMapping("exit.action")
	public Map<String, Object> exit(HttpSession session) {
		session.invalidate();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		return map;
	}

}










