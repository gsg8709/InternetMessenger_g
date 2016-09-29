package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SmsModeTypeResult {
	@SerializedName("state")
	private boolean state;

	@SerializedName("total")
	private int total;

	@SerializedName("totalPage")
	private int totalPage;

	@SerializedName("nowPage")
	private int nowPage;
	@SerializedName("msg")
	public String msg;
	@SerializedName("info")
	private ArrayList<SmsType> infos;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


	@Override
	public String toString() {
		return "NewsTypeResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", msg=" + msg + ", infos=" + infos + "]";
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getNowPage() {
		return nowPage;
	}

	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}

	public ArrayList<SmsType> getInfos() {
		return infos;
	}

	public void setInfos(ArrayList<SmsType> infos) {
		this.infos = infos;
	}

	public class SmsType implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@SerializedName("SORTID")
		private int SORTID;
		@SerializedName("CLASSID")
		private int CLASSID;
		@SerializedName("PARENTID")
		private int PARENTID;
		@SerializedName("DISABLED")
		private int DISABLED;
		@SerializedName("CTIME")
		private long CTIME;
		@SerializedName("CLASSNAME")
		private String CLASSNAME;

		public int getSORTID() {
			return SORTID;
		}

		public void setSORTID(int sORTID) {
			SORTID = sORTID;
		}

		public int getCLASSID() {
			return CLASSID;
		}

		public void setCLASSID(int cLASSID) {
			CLASSID = cLASSID;
		}

		public int getPARENTID() {
			return PARENTID;
		}

		public void setPARENTID(int pARENTID) {
			PARENTID = pARENTID;
		}

		public int getDISABLED() {
			return DISABLED;
		}

		public void setDISABLED(int dISABLED) {
			DISABLED = dISABLED;
		}

		public long getCTIME() {
			return CTIME;
		}

		public void setCTIME(long cTIME) {
			CTIME = cTIME;
		}

		public String getCLASSNAME() {
			return CLASSNAME;
		}

		public void setCLASSNAME(String cLASSNAME) {
			CLASSNAME = cLASSNAME;
		}

		@Override
		public String toString() {
			return "New [SORTID=" + SORTID + ", CLASSID=" + CLASSID
					+ ", PARENTID=" + PARENTID + ", DISABLED=" + DISABLED
					+ ", CTIME=" + CTIME + ", CLASSNAME=" + CLASSNAME + "]";
		}
	}
}
