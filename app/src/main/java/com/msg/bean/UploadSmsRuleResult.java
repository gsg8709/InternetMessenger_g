package com.msg.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.msg.bean.SmsModeResult.SmsMode;

public class UploadSmsRuleResult {
	@SerializedName("state")
	public boolean state;
	@SerializedName("msg")
	public String msg;
	@SerializedName("info")
	public ArrayList<SmsMode> info;

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

	public ArrayList<SmsMode> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<SmsMode> info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "UploadSmsRuleResult [state=" + state + ", msg=" + msg
				+ ", info=" + info + "]";
	}
}
