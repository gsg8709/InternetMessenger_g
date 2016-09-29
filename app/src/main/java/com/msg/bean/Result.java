package com.msg.bean;

import com.google.gson.annotations.SerializedName;

public class Result {

	@SerializedName("state")
	public boolean state;

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
}
