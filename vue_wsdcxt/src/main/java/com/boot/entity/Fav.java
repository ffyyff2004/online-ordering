package com.boot.entity;

import com.alibaba.fastjson.JSONObject;
import com.boot.util.VeDate;

// 用户收藏表的实体类
public class Fav {
	private String favid = "F"+VeDate.getStringId(); // 生成主键编号
	private String usersid; // 用户
	private String foodsid; // 食品
	private String addtime; // 收藏日期
	private String username; // 映射数据
	private String foodsname; // 映射数据
	private Users users;// 多对一映射类
	private Foods foods;// 多对一映射类
	public String getFavid() {
		return this.favid;
	}

	public void setFavid(String favid) {
		this.favid = favid;
	}

	public String getUsersid() {
		return this.usersid;
	}

	public void setUsersid(String usersid) {
		this.usersid = usersid;
	}

	public String getFoodsid() {
		return this.foodsid;
	}

	public void setFoodsid(String foodsid) {
		this.foodsid = foodsid;
	}

	public String getAddtime() {
		return this.addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Foods getFoods() {
		return this.foods;
	}

	public void setFoods(Foods foods) {
		this.foods = foods;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFoodsname() {
		return this.foodsname;
	}

	public void setFoodsname(String foodsname) {
		this.foodsname = foodsname;
	}


	// 重载方法 生成JSON类型字符串 
	@Override
	public String toString() {
		return this.toJsonString();
	}

	//直接转换成JSON字符串
	private String toJsonString() {
		JSONObject jsonString = new JSONObject();
		jsonString.put("favid", this.favid); // 主键编号
		jsonString.put("usersid", this.usersid); // 用户
		jsonString.put("foodsid", this.foodsid); // 食品
		jsonString.put("addtime", this.addtime); // 收藏日期
		jsonString.put("Users", this.users); // 多对一映射类
		jsonString.put("Foods", this.foods); // 多对一映射类
		jsonString.put("username", this.username); // 映射数据
		jsonString.put("foodsname", this.foodsname); // 映射数据
		return jsonString.toString();
	}




}




