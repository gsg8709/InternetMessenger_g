package com.msg.utils;

import android.content.Context;
import android.text.TextUtils;

import com.msg.bean.User;

public class UserManager {
	public static String USER_PREFERENCE = "user_preference";

	private static final String KEY_USERID = "user_id";

	private static final String KEY_PWD = "password";

	private static final String KEY_NIKENAME = "nike_name";

	private static final String KEY_ADDRESS = "address";

	private static final String KEY_SEX = "sex";

	private static final String KEY_HEADIMG = "headimg";

	private static final String KEY_SCREEN_POP = "screen_pop";

	private static final String KEY_OFHOOK_SMS = "ofhook_sms";

	private static final String KEY_REMEBER_PWD = "remeber_pwd";

	private static final String KEY_LOGOUT = "logout";

	private static final String KEY_FIRST = "first";

	public static void saveUserInfo(Context context, User info) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		if (info != null) {
			CustomPreference.save(KEY_PWD, Md5Utils.convertMD5(info.getPWD()));
			CustomPreference.save(KEY_NIKENAME, info.getNAME());
			CustomPreference.save(KEY_USERID, info.getUID());
			CustomPreference.save(KEY_ADDRESS, info.getADDRESS());
			CustomPreference.save(KEY_SEX, info.getSEX());
			CustomPreference.save(KEY_HEADIMG, info.getHEADIMG());
		}
	}

	public static User getUserinfo(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		User info = new User();
		info.setPWD(CustomPreference.read(KEY_PWD, null));
		info.setNAME(CustomPreference.read(KEY_NIKENAME, null));
		info.setUID(CustomPreference.read(KEY_USERID, null));
		info.setADDRESS(CustomPreference.read(KEY_ADDRESS, null));
		info.setSEX(CustomPreference.read(KEY_SEX, 1));
		info.setHEADIMG(CustomPreference.read(KEY_HEADIMG, null));
		if (TextUtils.isEmpty(info.getUID())) {
			return null;
		}
		return info;
	}

	/**
	 * 读取来电弹屏开关
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getScreenPop(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		return CustomPreference.read(KEY_SCREEN_POP, false);
	}

	/**
	 * 保存来电弹屏开关
	 * 
	 * @param context
	 * @param b
	 */
	public static void saveScreenPop(Context context, boolean b) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		CustomPreference.save(KEY_SCREEN_POP, b);
	}

	/**
	 * 读取挂机短信开关
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getOfHookSms(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		return CustomPreference.read(KEY_OFHOOK_SMS, false);
	}

	/**
	 * 保存挂机短信开关
	 * 
	 * @param context
	 * @param b
	 */
	public static void saveOfHookSms(Context context, boolean b) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		CustomPreference.save(KEY_OFHOOK_SMS, b);
	}

	/**
	 * 读取是否记住密码
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getRemeberPwd(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		return CustomPreference.read(KEY_REMEBER_PWD, false);
	}

	/**
	 * 保存是否记住密码
	 * 
	 * @param context
	 * @param b
	 */
	public static void saveRemeberPwd(Context context, boolean b) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		CustomPreference.save(KEY_REMEBER_PWD, b);
	}

	/**
	 * 读取是否第一次打开程序
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getFirstLogin(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		return CustomPreference.read(KEY_FIRST, true);
	}

	/**
	 * 保存是否第一次打开程序
	 * 
	 * @param context
	 * @param b
	 */
	public static void saveFirstLogin(Context context, boolean b) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		CustomPreference.save(KEY_FIRST, b);
	}

	/**
	 * 读取是否退出登录
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getLogout(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		return CustomPreference.read(KEY_LOGOUT, false);
	}

	/**
	 * 保存是否退出登录
	 * 
	 * @param context
	 * @param b
	 */
	public static void saveLogout(Context context, boolean b) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		CustomPreference.save(KEY_LOGOUT, b);
	}

	public static void cleanUserInfo(Context context) {
		CustomPreference.ensureIntializePreference(context, USER_PREFERENCE);
		CustomPreference.save(KEY_NIKENAME, null);
		CustomPreference.save(KEY_PWD, null);
		CustomPreference.save(KEY_USERID, null);
		CustomPreference.save(KEY_ADDRESS, null);
		CustomPreference.save(KEY_SEX, 1);
		CustomPreference.save(KEY_HEADIMG, null);
	}
}
