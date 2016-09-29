package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SmsRuleResult {
	@SerializedName("state")
	public boolean state;

	@SerializedName("total")
	public Integer total;

	@SerializedName("totalPage")
	public Integer totalPage;

	@SerializedName("nowPage")
	public Integer nowPage;
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

	@Override
	public String toString() {
		return "SmsRuleResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", msg=" + msg + ", info=" + info + "]";
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getNowPage() {
		return nowPage;
	}

	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}

	public ArrayList<SmsRule> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<SmsRule> info) {
		this.info = info;
	}

	public class SmsRule implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@SerializedName("OID")
		public String OID;
		@SerializedName("UID")
		public String UID;
		@SerializedName("MSG")
		public String MSG;
		@SerializedName("IMAGE")
		public String IMAGE;
		@SerializedName("AUDIO")
		public String AUDIO;
		@SerializedName("STARTTIME")
		public String STARTTIME;
		@SerializedName("ENDTIME")
		public String ENDTIME;
		@SerializedName("CTIME")
		public long CTIME;
		@SerializedName("CLASSID")
		public long CLASSID;
		@SerializedName("SELECTED")
		public int SELECTED;

		public int getSELECTED() {
			return SELECTED;
		}

		public void setSELECTED(int sELECTED) {
			SELECTED = sELECTED;
		}

		@Override
		public String toString() {
			return "SmsRule [OID=" + OID + ", UID=" + UID + ", MSG=" + MSG
					+ ", IMAGE=" + IMAGE + ", AUDIO=" + AUDIO + ", STARTTIME="
					+ STARTTIME + ", ENDTIME=" + ENDTIME + ", CTIME=" + CTIME
					+ ", CLASSID=" + CLASSID + ", SELECTED=" + SELECTED + "]";
		}

		public String getOID() {
			return OID;
		}

		public void setOID(String oID) {
			OID = oID;
		}

		public String getUID() {
			return UID;
		}

		public void setUID(String uID) {
			UID = uID;
		}

		public String getMSG() {
			return MSG;
		}

		public void setMSG(String mSG) {
			MSG = mSG;
		}

		public String getIMAGE() {
			return IMAGE;
		}

		public void setIMAGE(String iMAGE) {
			IMAGE = iMAGE;
		}

		public String getAUDIO() {
			return AUDIO;
		}

		public void setAUDIO(String aUDIO) {
			AUDIO = aUDIO;
		}

		public String getSTARTTIME() {
			return STARTTIME;
		}

		public void setSTARTTIME(String sTARTTIME) {
			STARTTIME = sTARTTIME;
		}

		public String getENDTIME() {
			return ENDTIME;
		}

		public void setENDTIME(String eNDTIME) {
			ENDTIME = eNDTIME;
		}

		public long getCTIME() {
			return CTIME;
		}

		public void setCTIME(long cTIME) {
			CTIME = cTIME;
		}

		public long getCLASSID() {
			return CLASSID;
		}

		public void setCLASSID(long cLASSID) {
			CLASSID = cLASSID;
		}

	}
}
