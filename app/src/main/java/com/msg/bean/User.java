package com.msg.bean;

import com.google.gson.annotations.SerializedName;

public class User {
	public User() {
	}

	@SerializedName("PWD")
	public String PWD;

	@SerializedName("NAME")
	public String NAME;

	@SerializedName("UID")
	public String UID;

	@SerializedName("ADDRESS")
	public String ADDRESS;

	@SerializedName("SEX")
	public Integer SEX;

	@SerializedName("ID")
	public Integer ID;

	@SerializedName("REMARK")
	public String REMARK;

	@SerializedName("HEADIMG")
	public String HEADIMG;

	@SerializedName("DISABLED")
	public Integer DISABLED;

	@SerializedName("CTIME")
	public long CTIME;

	public String getPWD() {
		return PWD;
	}

	public void setPWD(String pWD) {
		PWD = pWD;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getADDRESS() {
		return ADDRESS;
	}

	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}

	public Integer getSEX() {
		return SEX;
	}

	public void setSEX(Integer sEX) {
		SEX = sEX;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getREMARK() {
		return REMARK;
	}

	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}

	public String getHEADIMG() {
		return HEADIMG;
	}

	public void setHEADIMG(String hEADIMG) {
		HEADIMG = hEADIMG;
	}

	public Integer getDISABLED() {
		return DISABLED;
	}

	public void setDISABLED(Integer dISABLED) {
		DISABLED = dISABLED;
	}

	public long getCTIME() {
		return CTIME;
	}

	public void setCTIME(long cTIME) {
		CTIME = cTIME;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	@Override
	public String toString() {
		return "User [PWD=" + PWD + ", NAME=" + NAME + ", UID=" + UID
				+ ", ADDRESS=" + ADDRESS + ", SEX=" + SEX + ", ID=" + ID
				+ ", REMARK=" + REMARK + ", HEADIMG=" + HEADIMG + ", DISABLED="
				+ DISABLED + ", CTIME=" + CTIME + "]";
	}

}
