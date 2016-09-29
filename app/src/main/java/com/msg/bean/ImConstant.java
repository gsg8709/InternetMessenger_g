package com.msg.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.msg.utils.AuxiliaryUtils;

public class ImConstant {

	public static final String EHUI_DIR = AuxiliaryUtils.getSDCardDir() + "im/img/";
	public static final String EHUI_CAMERA_PIC = EHUI_DIR + getPhotoFileName();

	private static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

}
