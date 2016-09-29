package com.msg.bean;

import com.google.gson.annotations.SerializedName;

public class Msg {
	@SerializedName("MSG")
	String MSG;
	@SerializedName("AUDIO")
	String AUDIO;
	@SerializedName("SENDUID")
	String SENDUID;
	@SerializedName("UID")
	String UID;
	@SerializedName("IMAGE")
	String IMAGE;
	@SerializedName("ISREAD")
	int ISREAD;
	@SerializedName("MID")
	int MID;
	@SerializedName("CTIME")
	long CTIME;
	@SerializedName("TITLE")
	String TITLE;

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getAUDIO() {
		return AUDIO;
	}

	public void setAUDIO(String aUDIO) {
		AUDIO = aUDIO;
	}

	public String getSENDUID() {
		return SENDUID;
	}

	public void setSENDUID(String sENDUID) {
		SENDUID = sENDUID;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public String getIMAGE() {
		return IMAGE;
	}

	public void setIMAGE(String iMAGE) {
		IMAGE = iMAGE;
	}

	public int getISREAD() {
		return ISREAD;
	}

	public void setISREAD(int iSREAD) {
		ISREAD = iSREAD;
	}

	public int getMID() {
		return MID;
	}

	public void setMID(int mID) {
		MID = mID;
	}

	public long getCTIME() {
		return CTIME;
	}

	public void setCTIME(long cTIME) {
		CTIME = cTIME;
	}

	public String getTITLE() {
		return TITLE;
	}

	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}

	@Override
	public String toString() {
		return "Msg [MSG=" + MSG + ", AUDIO=" + AUDIO + ", SENDUID=" + SENDUID
				+ ", UID=" + UID + ", IMAGE=" + IMAGE + ", ISREAD=" + ISREAD
				+ ", MID=" + MID + ", CTIME=" + CTIME + ", TITLE=" + TITLE
				+ "]";
	}
}
