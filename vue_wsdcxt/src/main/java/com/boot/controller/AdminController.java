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
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.boot.entity.Admin;
import com.boot.service.AdminService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController //定义为控制器 返回JSON类型数据
@RequestMapping(value = "/admin", produces = "application/json; charset=utf-8")// 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class AdminController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private AdminService adminService;

	@PostMapping("editpwd.action") // 定义访问方法路径
	public Map<String, Object> editpwd(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将传递的Json参数 转换成对象类型
		HttpSession session = getSession();
		String adminid = session == null ? null : (String) session.getAttribute("adminUserId");
		String password = obj.getString("password"); // 原密码
		String repassword = obj.getString("repassword"); // 新密码
		Map<String, Object> map = new HashMap<String, Object>(); // 定义Map 其为返回值
		if (adminid == null) {
			map.put("success", false);
			map.put("message", "登录已失效，请重新登录");
			return map;
		}
		Admin admin = this.adminService.getAdminById(adminid); //
		if (password.equals(admin.getPassword())) { // 校验原密码是否正确
			admin.setPassword(repassword); // 重置密码
			this.adminService.updateAdmin(admin); // 更新数据
			map.put("success", true);
			map.put("message", "修改成功");
		} else {
			map.put("success", false);
			map.put("message", "旧密码错误");
		}
		return map;
	}


	// 预处理 获取基础参数
	@GetMapping(value = "createAdmin.action")
	public Map<String, Object> createAdmin() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("today", VeDate.getStringDateShort());
		return map;
	}

	// 新增管理员
	@PostMapping(value = "insertAdmin.action")
	public Map<String, Object> insertAdmin(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Admin admin = new Admin();
		admin.setUsername(obj.getString("username")); //  为用户名赋值
		admin.setPassword(obj.getString("password")); //  为密码赋值
		admin.setRealname(obj.getString("realname")); //  为姓名赋值
		admin.setContact(obj.getString("contact")); //  为联系方式赋值
		admin.setAddtime(VeDate.getStringDateShort()); // 为创建日期赋值 
		int num = this.adminService.insertAdmin(admin);
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

	// 按主键删除一个管理员
	@GetMapping(value = "deleteAdmin.action")
	public Map<String, Object> deleteAdmin(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.adminService.deleteAdmin(id);
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

	// 按主键批量删除管理员
	@PostMapping(value = "deleteAdminByIds.action")
	public Map<String, Object> deleteAdminByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String adminid : ids) {
			num += this.adminService.deleteAdmin(adminid);
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

	// 修改管理员
	@PostMapping(value = "updateAdmin.action")
	public Map<String, Object> updateAdmin(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Admin admin = this.adminService.getAdminById(obj.getString("adminid")); // 获取object中adminid字段
		admin.setUsername(obj.getString("username")); //  为用户名赋值
		admin.setRealname(obj.getString("realname")); //  为姓名赋值
		admin.setContact(obj.getString("contact")); //  为联系方式赋值

		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.adminService.updateAdmin(admin);
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

	// 查询全部管理员数据 在下拉菜单中显示
	@GetMapping(value = "getAllAdmin.action")
	public List<Admin> getAllAdmin() {
		return this.adminService.getAllAdmin();
	}

	// 按关键字查询管理员数据 在下拉菜单中显示
	@GetMapping(value = "getAdminMap.action")
	public Map<String, Object> getAdminMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Admin admin = new Admin();
		admin.setUsername(keywords);
		List<Admin> list = this.adminService.getAdminByLike(admin);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示管理员数据
	@GetMapping(value = "getAdminByPage.action")
	public Map<String, Object> getAdminByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Admin> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Admin> list = this.adminService.getAllAdmin();
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

	// 通过AJAX在表格中显示管理员数据
	@GetMapping(value = "getAdmin.action")
	public Map<String, Object> getAdmin(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Admin> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Admin admin = new Admin();
		admin.setUsername(keywords);
		List<Admin> list = this.adminService.getAdminByLike(admin);
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

	// 通过AJAX在表格中显示管理员数据
	@GetMapping(value = "getOwnerAdmin.action")
	public Map<String, Object> getOwnerAdmin(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Admin> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Admin admin = new Admin();
		//admin.setAdminid(id);
		List<Admin> list = this.adminService.getAdminByLike(admin);
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

	// 按主键查询管理员数据
	@GetMapping(value = "getAdminById.action")
	public Admin getAdminById(String id) {
		Admin admin = this.adminService.getAdminById(id);
		return admin;
	}

	// TODO Auto-generated method stub
}


