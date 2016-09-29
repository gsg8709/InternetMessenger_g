package com.msg.ui;

import android.app.Application;
import android.os.Handler;

import com.lzy.okhttputils.OkHttpUtils;

public class ImApplication extends Application {

	private Handler refreshHandler;
	private Handler chatRefreshHandler;
	private Handler friendRefreshHandler;
	private boolean visibility;

	@Override
	public void onCreate() {
		super.onCreate();
		OkHttpUtils.init(this);
	}

	public boolean isVisibility() {
		return visibility;
	}

	public Handler getFriendRefreshHandler() {
		return friendRefreshHandler;
	}

	public void setFriendRefreshHandler(Handler friendRefreshHandler) {
		this.friendRefreshHandler = friendRefreshHandler;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public Handler getRefreshHandler() {
		return refreshHandler;
	}

	public void setRefreshHandler(Handler refreshHandler) {
		this.refreshHandler = refreshHandler;
	}

	public Handler getChatRefreshHandler() {
		return chatRefreshHandler;
	}

	public void setChatRefreshHandler(Handler chatRefreshHandler) {
		this.chatRefreshHandler = chatRefreshHandler;
	}
}
