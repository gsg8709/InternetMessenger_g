package com.msg.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ContactListResult {
	@SerializedName("state")
	public boolean state;

	@SerializedName("total")
	public Integer total;

	@SerializedName("totalPage")
	public Integer totalPage;

	@SerializedName("nowPage")
	public Integer nowPage;

	@SerializedName("msg")
	public String msg;
	
	@SerializedName("info")
	public ArrayList<Contacts> info;

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getNowPage() {
		return nowPage;
	}

	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}

	public ArrayList<Contacts> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<Contacts> info) {
		this.info = info;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "ContactListResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", msg=" + msg + ", info=" + info + "]";
	}
}
