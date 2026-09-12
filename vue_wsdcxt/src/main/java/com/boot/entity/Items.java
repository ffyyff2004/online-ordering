package com.boot.entity;

public class Items {

	private String ordercode;
	private String detailsid;
	private String foodsid;
	private String foodsname;
	private String tnum = "5";
	private String contents;

	public String getOrdercode() {
		return ordercode;
	}

	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}

	public String getDetailsid() {
		return detailsid;
	}

	public void setDetailsid(String detailsid) {
		this.detailsid = detailsid;
	}

	public String getFoodsid() {
		return foodsid;
	}

	public void setFoodsid(String foodsid) {
		this.foodsid = foodsid;
	}

	public String getFoodsname() {
		return foodsname;
	}

	public void setFoodsname(String foodsname) {
		this.foodsname = foodsname;
	}

	public String getTnum() {
		return tnum;
	}

	public void setTnum(String tnum) {
		this.tnum = tnum;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

}
