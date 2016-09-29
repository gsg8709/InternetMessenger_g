package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * 人脉记事
 * 
 * @author Chris
 * 
 */
public class NoteResult {
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
	public ArrayList<Note> info;

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

	public ArrayList<Note> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<Note> info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "NoteResult [state=" + state + ", total=" + total
				+ ", totalPage=" + totalPage + ", nowPage=" + nowPage
				+ ", msg=" + msg + ", info=" + info + "]";
	}

	public class Note implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@SerializedName("WORD")
		public String WORD;
		@SerializedName("WID")
		public Integer WID;
		@SerializedName("AUDIO")
		public String AUDIO;
		@SerializedName("UID")
		public String UID;
		@SerializedName("IMAGE")
		public String IMAGE;
		@SerializedName("CTIME")
		public long CTIME;
		@SerializedName("EDITTIME")
		public long EDITTIME;
		@SerializedName("CID")
		public Integer CID;

		public String getIMAGE() {
			return IMAGE;
		}

		public void setIMAGE(String iMAGE) {
			IMAGE = iMAGE;
		}

		public long getEDITTIME() {
			return EDITTIME;
		}

		public void setEDITTIME(long eDITTIME) {
			EDITTIME = eDITTIME;
		}

		public String getWORD() {
			return WORD;
		}

		public void setWORD(String wORD) {
			WORD = wORD;
		}

		public Integer getWID() {
			return WID;
		}

		public void setWID(Integer wID) {
			WID = wID;
		}

		public String getAUDIO() {
			return AUDIO;
		}

		public void setAUDIO(String aUDIO) {
			AUDIO = aUDIO;
		}

		public String getUID() {
			return UID;
		}

		public void setUID(String uID) {
			UID = uID;
		}

		public Integer getCID() {
			return CID;
		}

		public void setCID(Integer cID) {
			CID = cID;
		}

		public long getCTIME() {
			return CTIME;
		}

		public void setCTIME(long cTIME) {
			CTIME = cTIME;
		}

		@Override
		public String toString() {
			return "Note [WORD=" + WORD + ", WID=" + WID + ", AUDIO=" + AUDIO
					+ ", UID=" + UID + ", IMAGE=" + IMAGE + ", CTIME=" + CTIME
					+ ", EDITTIME=" + EDITTIME + ", CID=" + CID + "]";
		}

	}
}
