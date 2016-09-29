package com.msg.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.msg.ui.MainActivity;
import com.feihong.msg.sms.R;

/**
 * 通知工具类
 * 
 * @author gongchao
 * 
 */
public class NotificationUtils {

	@SuppressWarnings("deprecation")
	public static void notifi(Context context, String content, String jid, String name) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification mNotification = new Notification();
		mNotification.icon = R.drawable.notifi_icon;
		mNotification.tickerText = content;
		mNotification.defaults = Notification.DEFAULT_ALL;
		mNotification.vibrate = new long[] { 0, 100, 200, 300 };
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent mIntent = new Intent(context, MainActivity.class);
		mIntent.putExtra("jid", jid);
		mIntent.putExtra("name", "互联信使");
		mIntent.putExtra("notification", true);
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0,
				mIntent, 0);
		mNotification.setLatestEventInfo(context, name,
				content, mPendingIntent);
		mNotificationManager.notify(1, mNotification);
	}

	/**
	 * 普通消息震动提示
	 * 
	 * @param context
	 */
	public static void notifi(Context context) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(new long[] { 0, 100, 200, 300 }, -1);
	}

	public static void clearNotifi(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}
}
