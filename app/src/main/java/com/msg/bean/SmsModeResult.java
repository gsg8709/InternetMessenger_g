package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SmsModeResult {
	@SerializedName("state")
	public boolean state;

	@SerializedName("total")
	public Integer total;

	@SerializedName("totalPage")
	public Integer totalPage;

	@SerializedName("nowPage")
	public Integer nowPage;

	@SerializedName("upload0")
	public String upload0;

	@SerializedName("upload1")
	public String upload1;
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

	@Override
	public String toString() {
		return "SmsModeResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", upload0=" + upload0 + ", upload1=" + upload1 + ", msg="
				+ msg + ", info=" + info + "]";
	}

	public String getUpload0() {
		return upload0;
	}

	public void setUpload0(String upload0) {
		this.upload0 = upload0;
	}

	public String getUpload1() {
		return upload1;
	}

	public void setUpload1(String upload1) {
		this.upload1 = upload1;
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

	public ArrayList<SmsMode> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<SmsMode> info) {
		this.info = info;
	}

	public class SmsMode implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@SerializedName("SORTID")
		public int SORTID;
		@SerializedName("MSG")
		public String MSG;
		@SerializedName("AUDIO")
		public String AUDIO;
		@SerializedName("IMAGE")
		public String IMAGE;
		@SerializedName("MOID")
		public String MOID;
		@SerializedName("CTIME")
		public long CTIME;
		@SerializedName("UID")
		public String UID;

		public String getUID() {
			return UID;
		}

		public void setUID(String uID) {
			UID = uID;
		}

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

		public String getIMAGE() {
			return IMAGE;
		}

		public void setIMAGE(String iMAGE) {
			IMAGE = iMAGE;
		}

		public String getMOID() {
			return MOID;
		}

		public void setMOID(String mOID) {
			MOID = mOID;
		}

		public long getCTIME() {
			return CTIME;
		}

		public void setCTIME(long cTIME) {
			CTIME = cTIME;
		}

		@Override
		public String toString() {
			return "SmsMode [SORTID=" + SORTID + ", MSG=" + MSG + ", AUDIO="
					+ AUDIO + ", IMAGE=" + IMAGE + ", MOID=" + MOID
					+ ", CTIME=" + CTIME + ", UID=" + UID + "]";
		}
	}
}
