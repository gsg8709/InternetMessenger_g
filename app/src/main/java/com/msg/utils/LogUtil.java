package com.msg.utils;

public class LogUtil {
	public static boolean isDebug = true;
	private static final String TAG = "JIMMY";

	public static void v(String msg) {
		if (isDebug)
			android.util.Log.v(TAG, msg);
	}

	public static void v(String msg, Throwable t) {
		if (isDebug)
			android.util.Log.v(TAG, msg, t);
	}

	public static void d(String msg) {
		if (isDebug)
			android.util.Log.d(TAG, msg);
	}

	public static void d(String msg, Throwable t) {
		if (isDebug)
			android.util.Log.d(TAG, msg, t);
	}

	public static void i(String msg) {
		if (isDebug)
			android.util.Log.i(TAG, msg);
	}

	public static void i(String msg, Throwable t) {
		if (isDebug)
			android.util.Log.i(TAG, msg, t);
	}

	public static void w(String msg) {
		if (isDebug)
			android.util.Log.w(TAG, msg);
	}

	public static void w(String msg, Throwable t) {
		if (isDebug)
			android.util.Log.w(TAG, msg, t);
	}

	public static void e(String msg) {
		if (isDebug)
			android.util.Log.e(TAG, msg);
	}

	public static void e(String msg, Throwable t) {
		if (isDebug)
			android.util.Log.e(TAG, msg, t);
	}
}
