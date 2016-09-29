package com.msg.bean;

import java.io.Serializable;

public class ShowMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fromJid;
	private String fromName;
	private String toJid;
	private int msgType;
	private int msgOrient;
	private int msgOpened;
	private String imagePath;
	private String voicePath;
	private String imageUrl;
	private String voiceUrl;
	private String title;
	private String msg;
	private String creationData;
	private String miniPic;

	public ShowMsg(String fromJid, String fromName, String toJid, int msgType,
			int msgOrient, int msgOpened, String image_url, String imagePath,
			String voice_url, String voicePath, String title, String msg,
			String creationData, String miniPic) {
		super();
		this.fromJid = fromJid;
		this.fromName = fromName;
		this.toJid = toJid;
		this.msgType = msgType;
		this.msgOrient = msgOrient;
		this.msgOpened = msgOpened;
		this.imagePath = imagePath;
		this.voicePath = voicePath;
		this.imageUrl = image_url;
		this.voiceUrl = voice_url;
		this.title = title;
		this.msg = msg;
		this.creationData = creationData;
		this.miniPic = miniPic;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromJid() {
		return fromJid;
	}

	public void setFromJid(String fromJid) {
		this.fromJid = fromJid;
	}

	public String getToJid() {
		return toJid;
	}

	public void setToJid(String toJid) {
		this.toJid = toJid;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getMsgOrient() {
		return msgOrient;
	}

	public void setMsgOrient(int msgOrient) {
		this.msgOrient = msgOrient;
	}

	public int getMsgOpened() {
		return msgOpened;
	}

	public void setMsgOpened(int msgOpened) {
		this.msgOpened = msgOpened;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getVoicePath() {
		return voicePath;
	}

	public void setVoicePath(String voicePath) {
		this.voicePath = voicePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCreationData() {
		return creationData;
	}

	public void setCreationData(String creationData) {
		this.creationData = creationData;
	}

	@Override
	public String toString() {
		return "ShowMsg [fromJid=" + fromJid + ", fromName=" + fromName
				+ ", toJid=" + toJid + ", msgType=" + msgType + ", msgOrient="
				+ msgOrient + ", msgOpened=" + msgOpened + ", imagePath="
				+ imagePath + ", voicePath=" + voicePath + ", title=" + title
				+ ", msg=" + msg + ", creationData=" + creationData +  ", miniPic=" + miniPic + "]";
	}

	public String getMiniPic() {
		return miniPic;
	}

	public void setMiniPic(String miniPic) {
		this.miniPic = miniPic;
	}
}
