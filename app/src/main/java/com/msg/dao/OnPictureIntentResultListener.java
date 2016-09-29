package com.msg.dao;

import android.graphics.Bitmap;

public interface OnPictureIntentResultListener {
	
	public void onPictureIntentResult(Bitmap bitmap);

	public void OnException(Exception ex);

}
