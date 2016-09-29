package com.msg.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class RetrievalSmsListResult {
	@SerializedName("state")
	boolean state;

	@SerializedName("total")
	int total;

	@SerializedName("totalPage")
	int totalPage;

	@SerializedName("nowPage")
	int nowPage;

	@SerializedName("info")
	ArrayList<Msg> info;

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getNowPage() {
		return nowPage;
	}

	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}

	public ArrayList<Msg> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<Msg> info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "RetrievalSmsListResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", info=" + info + "]";
	}
}
