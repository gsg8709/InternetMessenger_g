package com.msg.common;

import android.annotation.SuppressLint;

@SuppressLint("SdCardPath")
public class Configs {

	/**
	 * 文件路径
	 */
	public static final String FILE_PATH = "/sdcard/internetmsg/";
	public static final String SIMAGE_PATH = "/sdcard/internetmsg/simg/";
	public static final String IMAGE_PATH = "/sdcard/internetmsg/img/";
	public static final String VOICE_PATH = "/sdcard/internetmsg/voice/";
	public static final String HEAD_PATH = FILE_PATH + "avatar/";
	public static final String MIME_HEAD_PATH = FILE_PATH + "avatar/me/";

	/**
	 * apk 下载地址
	 */
	public static String DOWNLOAD_APK = "http://hulianxs.com/hlxs.apk";
	public static String imei, device, sysVersion;
	
	public static boolean verification = false;

	public static final int REFRESH_SIMAGE = 1010;
	public static final int REFRESH_IMAGE = 1011;
	public static final int REFRESH_HEAD = 1012;
	public static final int REFRESH_MIME_HEAD = 1013;
	public static final int REFRESH_AUDIO = 1014;

	public static final int DOWNLOAD_AUDIO_SUCCESS = 1015;
	public static final int DOWNLOAD_AUDIO_FAIL = 1016;

	/**
	 * 新建短信模板
	 */
	public static final int CREATE_SMS_MODE = 1017;
	/**
	 * 编辑短信模板
	 */
	public static final int EDIT_SMS_MODE = 1018;
	/**
	 * 查询短信模板
	 */
	public static final int QUERY_SMS_MODE = 1019;
	/**
	 * 文件保存成功
	 */
	public static final int UPLOAD_FILE_SUCCESS = 1020;
	/**
	 * 文件保存失败
	 */
	public static final int UPLOAD_FILE_FAIL = 1021;

	/**
	 * 新建联系人
	 */
	public static final int CREATE_CONTACT = 1022;
	
	/**
	 * 新建联系人 从 消息列表陌生号码
	 */
	public static final int CREATE_CONTACT_BY_STRANGE_NUMBER = 1035;

	/**
	 * 编辑联系人
	 */
	public static final int EDIT_CONTACT = 1023;
	/**
	 * 查看联系人
	 */
	public static final int QUERY_CONTACT = 1024;

	/**
	 * 新建挂机短信模板
	 */
	public static final int CREATE_OFHOOK_SMS_MODE = 1025;
	/**
	 * 编辑挂机短信模板
	 */
	public static final int EDIT_OFHOOK_SMS_MODE = 1026;
	/**
	 * 查看挂机短信模板
	 */
	public static final int QUERY_OFHOOK_SMS_MODE = 1027;

	/**
	 * 新建个人记事
	 */
	public static final int CREATE_NOTE = 1028;
	/**
	 * 编辑个人记事
	 */
	public static final int EDIT_NOTE = 1029;

	/**
	 * 互联短信发送
	 */
	public static final int SEND_FROM_INTER_SMS = 1030;
	/**
	 * 普通短信发送
	 */
	public static final int SEND_FROM_SMS = 1031;
	/**
	 * 选择短信模板
	 */
	public static final int SEND_FOR_CHOOSE_SMS_MODE = 1032;
	/**
	 * 选择迷你卡片
	 */
	public static final int SEND_FOR_CHOOSE_MINI_CARD = 1033;
	/**
	 * 选择新鲜事
	 */
	public static final int SEND_FOR_CHOOSE_NEWS = 1034;

	/**
	 * 消息方向 0：接收；1：发送
	 */
	public static final int RECEIVE_MSG = 0;
	public static final int SEND_MSG = 1;

	/**
	 * 消息是否已读 0：已读；1：未读
	 */
	public static final int MSG_READED = 0;
	public static final int MSG_UNREADED = 1;

	/**
	 * 消息类型：0：互联短信，1：普通短信
	 */
	public static final int MSG_TYPE_INTER_SMS = 0;
	public static final int MSG_TYPE_SMS = 1;

	/**
	 * 选择联系人来源页
	 */
	public static final int FROM_SMS_PAGE = 1025;
	public static final int FROM_CARD_PAGE = 1026;
	public static final int FROM_NOTE_PAGE = 1027;
	public static final int FROM_GOURP_PAGE = 1028;
	public static final int FROM_SMS_MODE_PAGE = 1029;

	public final static String USERNAME = "13601156965";
	public final static String USERNAME2 = "18601995268";
	public final static String USERNAME3 = "13832156663";

	public static int screenWidth;
	public static int screenHeight;
	public static float scale;
	public static float screenDensity;

	/**
	 * 图片地址前缀
	 */
	public final static String IMAGE_URL_DOMAIN = "http://image.hulianxs.com/";

	/**
	 * 缩略图地址前缀
	 */
	public final static String SIMAGE_URL_DOMAIN = "http://simage.hulianxs.com/";

	/**
	 * 声音地址前缀
	 */
	public final static String AUDIO_URL_DOMAIN = "https://audio.hulianxs.com/";
	
	/**
	 * 迷你选项卡图片+文字连接地址
	 */
	public final static String MINICARD_URL = "https://hulianxs.com/client/minicard/id/";
	
	/**
	 * 迷你选项卡图片+文字连接地址 +发送短信
	 */
	public final static String MINICARD_SEND_URL = "https://hulianxs.com/c/";
	
	/**
	 * 迷你选项卡图片+文字连接地址 +发送短信
	 */
	public final static String MINICARD_SEND_URL_CLIENT = "https://hulianxs.com/client/";
	
	/**
	 * 迷你卡片2
	 */
	public final static String MINICARD_URL2 = "https://hulianxs.com/client/minicard/";

	/********************************************** 接口地址 ********************************************************/

	/**
	 * 服务器地址
	 */
	private final static String SERVER_ADDRESS = "http://211.138.15.139:8089/";

	private final static String SERVER_ADDRESS2 = "https://101.69.180.82:8090";

	/*********************************************** 联系人接口 ********************************************************/

	/**
	 * 联系人列表
	 */
	public final static String CONTACT_LIST_ADDRESS = SERVER_ADDRESS
			+ "contact.do?action=query";
	/**
	 * 添加联系人
	 */
	public final static String CONTACT_ADD_ADDRESS = SERVER_ADDRESS
			+ "contact.do?action=insert";
	/**
	 * 修改联系人
	 */
	public final static String CONTACT_UPDATE_ADDRESS = SERVER_ADDRESS
			+ "contact.do?action=update";
	/**
	 * 删除联系人
	 */
	public final static String CONTACT_DELETE_ADDRESS = SERVER_ADDRESS
			+ "contact.do?action=delete";
	/**
	 * 同步联系人
	 */
	public final static String CONTACT_SYNC_ADDRESS = SERVER_ADDRESS
			+ "contact.do?action=sync";


	/*********************************************** 自定义模板接口 ********************************************************/

	/**
	 * 自定义模板列表
	 */
	public final static String CUSTOM_MODEL_LIST_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=query";
	/**
	 * 自定义模板列表类型
	 */
	public final static String CUSTOM_MODEL_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=query_class";
	/**
	 * 添加自定义模板
	 */
	public final static String CUSTOM_MODEL_ADD_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=insert";
	/**
	 * 修改自定义模板
	 */
	public final static String CUSTOM_MODEL_UPDATE_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=update";
	/**
	 * 删除自定义模板
	 */
	public final static String CUSTOM_MODEL_DELETE_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=delete";

	/*********************************************** 公共模板接口 ********************************************************/

	public final static String PUBLIC_MODEL_LIST_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=query_public";

	public final static String PUBLIC_MODEL_TYPE_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=query_class";

	public final static String PUBLIC_MODEL_TEST_ADDRESS = SERVER_ADDRESS2
			+ "/games/inte_backdoor.do";

	/*********************************************** 自定义挂机短信模板接口 ********************************************************/

	/**
	 * 自定义挂机短信模板列表
	 */
	public final static String CUSTOM_ONHOOK_LIST_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=query_onhook";
	/**
	 * 添加自定义挂机短信模板
	 */
	public final static String CUSTOM_ONHOOK_ADD_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=insert_onhook";
	/**
	 * 修改自定义挂机短信模板
	 */
	public final static String CUSTOM_ONHOOK_UPDATE_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=update_onhook";
	/**
	 * 删除自定义挂机短信模板
	 */
	public final static String CUSTOM_ONHOOK_DELETE_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=delete_onhook";

	/*********************************************** 发送挂机短信接口 ********************************************************/

	public final static String ONHOOK_SEND_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=send_onhook";

	/*********************************************** 公共挂机短信模板接口 ********************************************************/

	public final static String PUBLIC_ONHOOK_LIST_ADDRESS = SERVER_ADDRESS
			+ "model.do?action=query_onhook_public";

	/*********************************************** 新奇事接口 ********************************************************/

	/**
	 * 新奇事列表
	 */
	public final static String NEWS_LIST_ADDRESS = SERVER_ADDRESS
			+ "news.do?action=query";
	/**
	 * 新奇事类型
	 */
	public final static String NEWS_TYPE_LIST_ADDRESS = SERVER_ADDRESS
			+ "news.do?action=query_class";
	/**
	 * 查询短信链接
	 */
	public final static String NEWS_INFO_ADDRESS = SERVER_ADDRESS
			+ "/news.do?action=newsInfo";

	/*********************************************** 迷你卡片接口 ********************************************************/

	/**
	 * 迷你卡片列表
	 */
	public final static String MINI_CARD_LIST_ADDRESS = SERVER_ADDRESS
			+ "minicard.do?action=query";
	/**
	 * 迷你卡片类型
	 */
	public final static String MINI_CARD_TYPE_ADDRESS = SERVER_ADDRESS
			+ "minicard.do?action=query_class";

	/*********************************************** 用户接口 ********************************************************/

	/**
	 * 用户查询
	 */
	public final static String USER_INFO_ADDRESS = SERVER_ADDRESS
			+ "users.do?action=userInfo";
	/**
	 * 用户登录
	 */
	public final static String USER_LOGIN_ADDRESS = SERVER_ADDRESS
			+ "users.do?action=login";
	/**
	 * 用户修改
	 */
	public final static String USER_UPDATE_ADDRESS = SERVER_ADDRESS
			+ "users.do?action=update";

	/**
	 * 检查用户是否为包月
	 */
	public final static String USER_CHECKVIP = SERVER_ADDRESS
			+ "users.do?action=checkvip";

	/**
	 * 获取验证码
	 */
	public final static String CONTACT_GET_VERYCODE = SERVER_ADDRESS
			+ "users.do?action=authimg";


	/*********************************************** 个人记事接口 ********************************************************/

	/**
	 * 个人记事列表
	 */
	public final static String WORDPAD_LIST_ADDRESS = SERVER_ADDRESS
			+ "wordpad.do?action=query";
	/**
	 * 添加个人记事
	 */
	public final static String WORDPAD_ADD_ADDRESS = SERVER_ADDRESS
			+ "wordpad.do?action=insert";
	/**
	 * 修改个人记事
	 */
	public final static String WORDPAD_UPDATE_ADDRESS = SERVER_ADDRESS
			+ "wordpad.do?action=update";
	/**
	 * 删除个人记事
	 */
	public final static String WORDPAD_DELETE_ADDRESS = SERVER_ADDRESS
			+ "wordpad.do?action=delete";

	/*********************************************** 短信接口 ********************************************************/

	/**
	 * 短信列表
	 */
	public final static String MSG_LIST_ADDRESS = SERVER_ADDRESS
			+ "msg.do?action=query";
	/**
	 * 添加短信
	 */
	public final static String MSG_ADD_ADDRESS = SERVER_ADDRESS
			+ "msg.do?action=insert";
	/**
	 * 查询最新短信
	 */
	public final static String MSG_LASEST_ADDRESS = SERVER_ADDRESS
			+ "msg.do?action=query_latest";
	/**
	 * 查询是否有未读短信
	 */
	public final static String MSG_NOREAD_ADDRESS = SERVER_ADDRESS
			+ "msg.do?action=query_noread";
	/**
	 * 根据短码查询短信
	 */
	public final static String MSG_GET_ADDRESS = SERVER_ADDRESS
			+ "msg.do?action=getMsg";
	
	/**
	 * 检查敏感关键字
	 */
	public final static String MSG_CHECKVKEYWORD = SERVER_ADDRESS
			+ "msg.do?action=check_keyword";
	
	/************************************************设置********************************************************/
	/**
	 * 检查版本更新
	 */
	public final static String CHECKVERSION = SERVER_ADDRESS + "/setting.do?action=version";
	
	
}
