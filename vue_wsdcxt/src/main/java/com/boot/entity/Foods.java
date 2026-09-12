package com.boot.entity;

import com.alibaba.fastjson.JSONObject;
import com.boot.util.VeDate;

// 食品表的实体类
public class Foods {
	private String foodsid = "F"+VeDate.getStringId(); // 生成主键编号
	private String foodsname; // 食品名称
	private String image; // 食品图片
	private String cateid; // 食品类型
	private String price; // 销售价格
	private String recommend; // 是否推荐
	private String special; // 是否特价
	private String addtime; // 上架日期
	private String hits; // 点击数
	private String sellnum; // 销售单数
	private String contents; // 食品介绍
	private String catename; // 映射数据
	private Cate cate;// 多对一映射类
	public String getFoodsid() {
		return this.foodsid;
	}

	public void setFoodsid(String foodsid) {
		this.foodsid = foodsid;
	}

	public String getFoodsname() {
		return this.foodsname;
	}

	public void setFoodsname(String foodsname) {
		this.foodsname = foodsname;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCateid() {
		return this.cateid;
	}

	public void setCateid(String cateid) {
		this.cateid = cateid;
	}

	public String getPrice() {
		return this.price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getRecommend() {
		return this.recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getSpecial() {
		return this.special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public String getAddtime() {
		return this.addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getHits() {
		return this.hits;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

	public String getSellnum() {
		return this.sellnum;
	}

	public void setSellnum(String sellnum) {
		this.sellnum = sellnum;
	}

	public String getContents() {
		return this.contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Cate getCate() {
		return this.cate;
	}

	public void setCate(Cate cate) {
		this.cate = cate;
	}

	public String getCatename() {
		return this.catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}


	// 重载方法 生成JSON类型字符串 
	@Override
	public String toString() {
		return this.toJsonString();
	}

	//直接转换成JSON字符串
	private String toJsonString() {
		JSONObject jsonString = new JSONObject();
		jsonString.put("foodsid", this.foodsid); // 主键编号
		jsonString.put("foodsname", this.foodsname); // 食品名称
		jsonString.put("image", this.image); // 食品图片
		jsonString.put("cateid", this.cateid); // 食品类型
		jsonString.put("price", this.price); // 销售价格
		jsonString.put("recommend", this.recommend); // 是否推荐
		jsonString.put("special", this.special); // 是否特价
		jsonString.put("addtime", this.addtime); // 上架日期
		jsonString.put("hits", this.hits); // 点击数
		jsonString.put("sellnum", this.sellnum); // 销售单数
		jsonString.put("contents", this.contents); // 食品介绍
		jsonString.put("Cate", this.cate); // 多对一映射类
		jsonString.put("catename", this.catename); // 映射数据
		return jsonString.toString();
	}




}




