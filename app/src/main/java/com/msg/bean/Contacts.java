package com.msg.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Contacts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("CID")
	public int contactId;

	@SerializedName("NAME")
	public String NAME;

	@SerializedName("UID")
	public String UID;

	@SerializedName("TEL")
	public String TEL;

	public int SEX;

	public int SHOW;

	public String BRITHDAY;

	public String COMPANY;

	public String BRANCH;

	public String JOB;

	@SerializedName("GROUPNAME")
	public String GROUPNAME;

	@SerializedName("CTIME")
	public long CTIME;

	public String PINYIN;

	public String FIRSTPY;

	public boolean check;

	public Contacts() {
	}

	public Contacts(String contactId, String pingyin, String firstPy,
			String nAME, String uID, String tEL, String gROUPNAME,
			String cTIME, int sEX, String bRITHDAY, String cOMPANY,
			String bRANCH, String jOB, String check, int sHOW) {
		super();
		this.contactId = Integer.parseInt(contactId);
		NAME = nAME;
		PINYIN = pingyin;
		FIRSTPY = firstPy;
		UID = uID;
		TEL = tEL;
		GROUPNAME = gROUPNAME;
		CTIME = Long.parseLong(cTIME);
		this.check = Boolean.parseBoolean(check);
		SEX = sEX;
		BRITHDAY = bRITHDAY;
		COMPANY = cOMPANY;
		BRANCH = bRANCH;
		JOB = jOB;
		SHOW = sHOW;
	}

	public Contacts(String contactId, String name, String num,
			String gROUPNAME, int sEX, String bRITHDAY, String cOMPANY,
			String bRANCH, String jOB) {
		super();
		this.contactId = Integer.parseInt(contactId);
		this.NAME = name;
		this.TEL = num;
		this.GROUPNAME = gROUPNAME;
		this.SEX = sEX;
		this.BRITHDAY = bRITHDAY;
		this.COMPANY = cOMPANY;
		this.BRANCH = bRANCH;
		this.JOB = jOB;
	}

	public int getSHOW() {
		return SHOW;
	}

	public void setSHOW(int sHOW) {
		SHOW = sHOW;
	}

	public String getFIRSTPY() {
		return FIRSTPY;
	}

	public void setFIRSTPY(String fIRSTPY) {
		FIRSTPY = fIRSTPY;
	}

	public String getPINYIN() {
		return PINYIN;
	}

	public void setPINYIN(String pINYIN) {
		PINYIN = pINYIN;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public String getTEL() {
		return TEL;
	}

	public void setTEL(String tEL) {
		TEL = tEL;
	}

	public String getGROUPNAME() {
		return GROUPNAME;
	}

	public void setGROUPNAME(String gROUPNAME) {
		GROUPNAME = gROUPNAME;
	}

	public long getCTIME() {
		return CTIME;
	}

	public void setCTIME(long cTIME) {
		CTIME = cTIME;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public int getSEX() {
		return SEX;
	}

	public void setSEX(int sEX) {
		SEX = sEX;
	}

	public String getBRITHDAY() {
		return BRITHDAY;
	}

	public void setBRITHDAY(String bRITHDAY) {
		BRITHDAY = bRITHDAY;
	}

	public String getCOMPANY() {
		return COMPANY;
	}

	public void setCOMPANY(String cOMPANY) {
		COMPANY = cOMPANY;
	}

	public String getBRANCH() {
		return BRANCH;
	}

	public void setBRANCH(String bRANCH) {
		BRANCH = bRANCH;
	}

	public String getJOB() {
		return JOB;
	}

	public void setJOB(String jOB) {
		JOB = jOB;
	}

	@Override
	public String toString() {
		return "Contacts [contactId=" + contactId + ", NAME=" + NAME + ", UID="
				+ UID + ", TEL=" + TEL + ", GROUPNAME=" + GROUPNAME
				+ ", CTIME=" + CTIME + ", check=" + check + "]";
	}
}
