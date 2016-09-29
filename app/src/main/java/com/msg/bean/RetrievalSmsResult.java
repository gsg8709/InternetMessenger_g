package com.msg.bean;

import com.google.gson.annotations.SerializedName;

public class RetrievalSmsResult {
	@SerializedName("state")
	boolean state;
	@SerializedName("msg")
	int info;

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getInfo() {
		return info;
	}

	public void setInfo(int info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "RetrievalSmsResult [state=" + state + ", info=" + info + "]";
	}
}
