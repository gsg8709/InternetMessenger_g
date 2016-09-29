package com.msg.bean;


import android.graphics.Bitmap;

public class CurrentUserBean {
	private static CurrentUserBean currentUserBean = null;

	private CurrentUserBean() {
	}

	public static CurrentUserBean getInstance() {
		synchronized (CurrentUserBean.class) {
			if (null == currentUserBean) {
				currentUserBean = new CurrentUserBean();
			}
		}

		return currentUserBean;
	}

	// 用户ID
	public String userID;
	// 账号
	public String Account;
	// 密码
	public String password;
	// 用户名
	public String rickName;
	// 性别
	public int sex;
	public Bitmap headImage;

}
