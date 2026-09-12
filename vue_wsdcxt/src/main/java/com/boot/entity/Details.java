package com.boot.entity;

import com.alibaba.fastjson.JSONObject;
import com.boot.util.VeDate;

// 订单明细表的实体类
public class Details {
	private String detailsid = "D"+VeDate.getStringId(); // 生成主键编号
	private String ordercode; // 订单号
	private String foodsid; // 食品
	private String price; // 单价
	private String num; // 数量
	private String foodsname; // 映射数据
	private Foods foods;// 多对一映射类
	public String getDetailsid() {
		return this.detailsid;
	}

	public void setDetailsid(String detailsid) {
		this.detailsid = detailsid;
	}

	public String getOrdercode() {
		return this.ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}

	public String getFoodsid() {
		return this.foodsid;
	}

	public void setFoodsid(String foodsid) {
		this.foodsid = foodsid;
	}

	public String getPrice() {
		return this.price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getNum() {
		return this.num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Foods getFoods() {
		return this.foods;
	}

	public void setFoods(Foods foods) {
		this.foods = foods;
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
		jsonString.put("detailsid", this.detailsid); // 主键编号
		jsonString.put("ordercode", this.ordercode); // 订单号
		jsonString.put("foodsid", this.foodsid); // 食品
		jsonString.put("price", this.price); // 单价
		jsonString.put("num", this.num); // 数量
		jsonString.put("Foods", this.foods); // 多对一映射类
		jsonString.put("foodsname", this.foodsname); // 映射数据
		return jsonString.toString();
	}




}




