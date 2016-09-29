package com.msg.bean;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.msg.bean.NoteResult.Note;

public class UploadNoteResult {
	@SerializedName("state")
	public boolean state;
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

	public ArrayList<Note> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<Note> info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "UploadNoteResult [state=" + state + ", msg=" + msg + ", info="
				+ info + "]";
	}

}
