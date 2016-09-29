package com.msg.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
	// {"state":true,"info":[{"PWD":"13601156965","NAME":null,"UID":"13601156965","ADDRESS":null,"SEX":1,"ID":6,"REMARK":null,"HEADIMG":null,"DISABLED":0,"CTIME":null}]}

	@SerializedName("state")
	private boolean state;
	@SerializedName("msg")
	public String msg;
	@SerializedName("info")
	private ArrayList<User> info;

	public ArrayList<User> getInfo() {
		return info;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setInfo(ArrayList<User> info) {
		this.info = info;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "LoginResult [state=" + state + ", msg=" + msg + ", info="
				+ info + "]";
	}
}
