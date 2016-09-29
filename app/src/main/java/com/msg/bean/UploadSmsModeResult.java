package com.msg.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.msg.bean.SmsRuleResult.SmsRule;

public class UploadSmsModeResult {
	@SerializedName("state")
	public boolean state;
	@SerializedName("msg")
	public String msg;
	@SerializedName("info")
	public ArrayList<SmsRule> info;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public ArrayList<SmsRule> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<SmsRule> info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "UploadSmsModeResult [state=" + state + ", msg=" + msg
				+ ", info=" + info + "]";
	}
}
