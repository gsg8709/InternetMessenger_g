package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class NewsListResult {
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
	private ArrayList<News> infos;

	@Override
	public String toString() {
		return "NewsListResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", msg=" + msg + ", infos=" + infos + "]";
	}

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

	public ArrayList<News> getInfos() {
		return infos;
	}

	public void setInfos(ArrayList<News> infos) {
		this.infos = infos;
	}

	public class News implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@SerializedName("SORTID")
		private int SORTID;

		@SerializedName("MSG")
		private String MSG;

		@SerializedName("AUDIO")
		private String AUDIO;

		@SerializedName("CLASSID")
		private int CLASSID;

		@SerializedName("IMAGE")
		private String IMAGE;

		@SerializedName("CTIME")
		private long CTIME;

		@SerializedName("SENDID")
		private String SENDID;

		@SerializedName("TITLE")
		private String TITLE;

		@SerializedName("NID")
		private int NID;

		public int getSORTID() {
			return SORTID;
		}

		public void setSORTID(int sORTID) {
			SORTID = sORTID;
		}

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

		public int getCLASSID() {
			return CLASSID;
		}

		public void setCLASSID(int cLASSID) {
			CLASSID = cLASSID;
		}

		public String getIMAGE() {
			return IMAGE;
		}

		public void setIMAGE(String iMAGE) {
			IMAGE = iMAGE;
		}

		public long getCTIME() {
			return CTIME;
		}

		public void setCTIME(long cTIME) {
			CTIME = cTIME;
		}

		public String getSENDID() {
			return SENDID;
		}

		public void setSENDID(String sENDID) {
			SENDID = sENDID;
		}

		public String getTITLE() {
			return TITLE;
		}

		public void setTITLE(String tITLE) {
			TITLE = tITLE;
		}

		public int getNID() {
			return NID;
		}

		public void setNID(int nID) {
			NID = nID;
		}

		@Override
		public String toString() {
			return "News [SORTID=" + SORTID + ", MSG=" + MSG + ", AUDIO="
					+ AUDIO + ", CLASSID=" + CLASSID + ", IMAGE=" + IMAGE
					+ ", CTIME=" + CTIME + ", SENDID=" + SENDID + ", TITLE="
					+ TITLE + ", NID=" + NID + "]";
		}
	}
}
