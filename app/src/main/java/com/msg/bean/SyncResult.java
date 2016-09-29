package com.msg.bean;

import com.google.gson.annotations.SerializedName;

public class SyncResult {
	// "BeginDate":"2013-11-01 14:32:56","updateSuccess":0,
	// "updateError":0,"insertSuccess":0,"insertError":0,"updateErrorMsg":"",
	// "insertErrorMsg":"","state":true,"EndDate":"2013-11-01 14:32:57"}

	@SerializedName("BeginDate")
	public String BeginDate;

	@SerializedName("updateSuccess")
	public Integer updateSuccess;

	@SerializedName("updateError")
	public Integer updateError;

	@SerializedName("insertSuccess")
	public Integer insertSuccess;

	@SerializedName("insertError")
	public Integer insertError;

	@SerializedName("updateErrorMsg")
	public String updateErrorMsg;

	@SerializedName("insertErrorMsg")
	public String insertErrorMsg;

	@SerializedName("state")
	public boolean state;

	@SerializedName("EndDate")
	public String EndDate;

	public String getBeginDate() {
		return BeginDate;
	}

	public void setBeginDate(String beginDate) {
		BeginDate = beginDate;
	}

	public Integer getUpdateSuccess() {
		return updateSuccess;
	}

	public void setUpdateSuccess(Integer updateSuccess) {
		this.updateSuccess = updateSuccess;
	}

	public Integer getUpdateError() {
		return updateError;
	}

	public void setUpdateError(Integer updateError) {
		this.updateError = updateError;
	}

	public Integer getInsertSuccess() {
		return insertSuccess;
	}

	public void setInsertSuccess(Integer insertSuccess) {
		this.insertSuccess = insertSuccess;
	}

	public Integer getInsertError() {
		return insertError;
	}

	public void setInsertError(Integer insertError) {
		this.insertError = insertError;
	}

	public String getUpdateErrorMsg() {
		return updateErrorMsg;
	}

	public void setUpdateErrorMsg(String updateErrorMsg) {
		this.updateErrorMsg = updateErrorMsg;
	}

	public String getInsertErrorMsg() {
		return insertErrorMsg;
	}

	public void setInsertErrorMsg(String insertErrorMsg) {
		this.insertErrorMsg = insertErrorMsg;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getEndDate() {
		return EndDate;
	}

	public void setEndDate(String endDate) {
		EndDate = endDate;
	}

	@Override
	public String toString() {
		return "SyncResult [BeginDate=" + BeginDate + ", updateSuccess="
				+ updateSuccess + ", updateError=" + updateError
				+ ", insertSuccess=" + insertSuccess + ", insertError="
				+ insertError + ", updateErrorMsg=" + updateErrorMsg
				+ ", insertErrorMsg=" + insertErrorMsg + ", state=" + state
				+ ", EndDate=" + EndDate + "]";
	}

}
