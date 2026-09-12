package com.boot.entity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.boot.util.VeDate;

// 食品类型表的实体类
public class Cate {
	private String cateid = "C" + VeDate.getStringId(); // 生成主键编号
	private String catename; // 类型名称
	private String addtime; // 创建日期
	private String memo; // 备注
	private List<Foods> foodsList = new ArrayList<Foods>();

	public List<Foods> getFoodsList() {
		return foodsList;
	}

	public void setFoodsList(List<Foods> foodsList) {
		this.foodsList = foodsList;
	}

	public String getCateid() {
		return this.cateid;
	}

	public void setCateid(String cateid) {
		this.cateid = cateid;
	}

	public String getCatename() {
		return this.catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getAddtime() {
		return this.addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	// 重载方法 生成JSON类型字符串
	@Override
	public String toString() {
		return this.toJsonString();
	}

	// 直接转换成JSON字符串
	private String toJsonString() {
		JSONObject jsonString = new JSONObject();
		jsonString.put("cateid", this.cateid); // 主键编号
		jsonString.put("catename", this.catename); // 类型名称
		jsonString.put("addtime", this.addtime); // 创建日期
		jsonString.put("memo", this.memo); // 备注
		return jsonString.toString();
	}

}
