package com.msg.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.msg.common.Configs;

/**
 * 功能辅助类
 * 
 * @author Administrator
 * 
 */
public class AuxiliaryUtils {
	private static String SDCardDirCache;
	private static ArrayList<Activity> mActivities = new ArrayList<Activity>();

	public static void addPage(Activity activity) {
		mActivities.add(activity);
	}

	public static void removePage(Activity activity) {
		mActivities.remove(activity);
	}

	public static void closePage() {
		for (Activity activity : mActivities) {
			activity.finish();
		}
	}

	private final static String regxpForWhiteSpace = "&nbsp;|\\s|\\n|\\t|\\r";
	private final static String regxpForHtml = "<([^>]*)>";
	private final static String regxpatForHtml = " <[^ <> ]+> ";

	/**
	 * 是否手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 是否数字
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isNum(String mobiles) {
		Pattern p = Pattern.compile("^[1-9]\\d*$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 是否邮箱地址
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isEmail(String mobiles) {
		Pattern p = Pattern.compile("[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 处理电话号码
	 * 
	 * @param phoneNum
	 * @return
	 */
	public static String checkPhoneNum(String phoneNum) {
		Pattern p1 = Pattern.compile("^((\\+{0,1}86){0,1})1[0-9]{10}");
		Matcher m1 = p1.matcher(phoneNum);
		if (m1.matches()) {
			Pattern p2 = Pattern.compile("^((\\+{0,1}86){0,1})");
			Matcher m2 = p2.matcher(phoneNum);
			StringBuffer sb = new StringBuffer();
			while (m2.find()) {
				m2.appendReplacement(sb, "");
			}
			m2.appendTail(sb);
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * 将字符串中的html标签过滤掉。
	 * 
	 * @param str
	 * @return
	 */
	public static String filterHtml(String str) {
		return str.replaceAll(regxpForHtml, "");
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param context
	 * @param t
	 */
	public static void hideKeyboard(final Context context, final EditText t) {
		try {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(t.getWindowToken(), 0);
				}
			}, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自动弹出键盘
	 * 
	 * @param context
	 * @param et
	 */
	public static void autoPopupSoftInput(final Context context,
			final EditText et) {
		try {
			et.requestFocus();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
				}
			}, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将字符串中的html标签过滤掉。
	 * 
	 * @param str
	 * @return
	 */
	public static String filterAtHtml(String str) {
		return str.replaceAll(regxpatForHtml, "");
	}

	/**
	 * Toast消息
	 * 
	 * @param context
	 * @param str
	 */
	public static void toast(final Activity context, final String str) {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		it.setClassName("com.android.browser",
				"com.android.browser.BrowserActivity");
		context.startActivity(it);
	}

	/**
	 * Toast消息
	 * 
	 * @param context
	 * @param strid
	 */
	public static void toast(Context context, int strid) {
		Toast.makeText(context, context.getString(strid), Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * 将字符串中的html标签和空白字符过滤掉。
	 * 
	 * @param str
	 * @return
	 */
	public static String filterHtmlAndWhiteSpace(String str) {
		return filterHtml(str).replaceAll(regxpForWhiteSpace, "");
	}

	/**
	 * 下载方法
	 * 
	 * @param context
	 * @param download_url
	 *            下载地址
	 */
	public static void download(Context context, String download_url) {
		Uri uri = Uri.parse(download_url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 是否存在sd卡
	 * 
	 * @return
	 */
	public static boolean hasSDCard() {
		String t = android.os.Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(t);
	}

	/**
	 * 是否存在网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}

	/**
	 * 判断网络类型是否为cmwap
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isCMWAP(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();

		if (info == null || !info.isAvailable()) {
			return false;
		} else if (info.getTypeName().equalsIgnoreCase("wifi")) {
			return false;
		} else if (info.getTypeName() != null
				&& info.getTypeName().equalsIgnoreCase("mobile")
				&& info.getExtraInfo() != null
				&& info.getExtraInfo().equalsIgnoreCase("cmwap")) {
			return true;
		}
		return false;
	}

	/**
	 * 获取IMEI
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIMEI(TelephonyManager tm) {
		String retVal = "";
		try {
			retVal = tm.getDeviceId();
			if (retVal == null)
				retVal = "";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retVal;
	}

	/**
	 * 获取IMSI
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIMSI(TelephonyManager tm) {
		String retVal = "";
		try {
			retVal = tm.getSubscriberId();
			if (retVal == null)
				retVal = "";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retVal;
	}

	/**
	 * 判断是否中国移动手机号码
	 * 
	 * @param imsi
	 * @return
	 */
	public static boolean isChinaMobile(String imsi) {
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
				return true;
			} else if (imsi.startsWith("46001")) {
				return false;
			} else if (imsi.startsWith("46003")) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 打开网络设置
	 * 
	 * @param context
	 */
	public static void startNetSetting(final Context context) {
		Builder b = new AlertDialog.Builder(context).setTitle("没有可用的网络")
				.setMessage("请开启GPRS或WIFI网络连接");
		b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					Intent mIntent = new Intent("/");
					ComponentName comp = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					mIntent.setComponent(comp);
					mIntent.setAction("android.intent.action.VIEW");
					context.startActivity(mIntent);
				} catch (Exception e) {
					context.startActivity(new Intent(
							Settings.ACTION_WIFI_SETTINGS));
				}
			}
		}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		}).create();
		b.show();
	}

	/**
	 * md5加密
	 * 
	 * @param s
	 * @return
	 */
	public static String md5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return (buf.toString());// 32位的加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}


	public static String getVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	/**
	 * 截取jid @
	 * 
	 * @param str
	 * @return
	 */
	public static String handlerAt(String str) {
		if (str.contains("@")) {
			return str.split("@")[0].trim();
		}
		return str;
	}

	/**
	 * 处理jid 把@之后的内容md5 ，在加上Scheme
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static String handlerUnAndScheme(String username) throws Exception {
		String[] res = username.replace(" ", "_").split("@");
		return res[0] + "_" + md5(username).substring(0, 10)
				+ "@www.sharesns.com";
	}

	/**
	 * 处理jid 把@之后的内容md5
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static String handlerUn(String username) throws Exception {
		String[] res = username.replace(" ", "_").split("@");
		return res[0] + "_" + md5(username).substring(0, 10);
	}

	/**
	 * 处理jid 添加Scheme
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static String handlerScheme(String username) throws Exception {
		return username + "@www.sharesns.com/Smack";
	}

	/**
	 * 把Bitmap转Byte
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		byte[] b;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		b = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 保存文件
	 * 
	 * @param data
	 * @param path
	 * @return
	 */
	public static boolean storeHead(InputStream is, String url) {
		Bitmap st = null, zoom = null, round = null;
		String name = Configs.HEAD_PATH + md5(IOUtil.getFilename(url));
		File destFile = new File(name);
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream file;
		try {
			st = BitmapFactory.decodeStream(is);
			zoom = ImageUtil.zoomBitmap(st, 180f, 180f);
			round = ImageUtil.getRoundCornerBitmap(zoom, 20.0f);
			file = new FileOutputStream(destFile);
			file.write(Bitmap2Bytes(round));
			file.close();
		} catch (Exception e) {
			return false;
		} catch (OutOfMemoryError error) {
			return false;
		} finally {
			if (destFile != null) {
				destFile = null;
			}

			if (st != null && !st.isRecycled()) {
				st.recycle();
				st = null;
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 存储文件
	 * 
	 * @param file
	 * @param path
	 * @return
	 */
	public static boolean storeFile(File file, String path) {
		if (!file.exists()) {
			return false;
		}

		File destFile = new File(path);
		if (destFile.exists()) {
			return false;
		}
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream fs;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			fs = new FileOutputStream(destFile);
			fs.write(input2byte(is));
			fs.close();
		} catch (Exception e) {
			return false;
		} catch (OutOfMemoryError error) {
			return false;
		} finally {
			if (destFile != null) {
				destFile = null;
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 保存文件
	 * 
	 * @param data
	 * @param path
	 * @return
	 */
	public static boolean storeImage(InputStream is, String url) {
		String name = Configs.IMAGE_PATH + md5(IOUtil.getFilename(url));
		File destFile = new File(name);
		if (destFile.exists()) {
			return false;
		}
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream file;
		try {
			file = new FileOutputStream(destFile);
			file.write(input2byte(is));
			file.close();
		} catch (Exception e) {
			return false;
		} catch (OutOfMemoryError error) {
			return false;
		} finally {
			if (destFile != null) {
				destFile = null;
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static boolean storeSImage(InputStream is, String url) {
		String name;
		Bitmap st = null;
		name = Configs.SIMAGE_PATH + md5(IOUtil.getFilename(url));
		File destFile = new File(name);
		if (destFile.exists()) {
			return false;
		}
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		FileOutputStream file;
		try {
			st = BitmapFactory.decodeStream(is);
			file = new FileOutputStream(destFile);
			file.write(Bitmap2Bytes(st));
			file.close();
		} catch (Exception e) {
			return false;
		} catch (OutOfMemoryError error) {
			return false;
		} finally {
			if (st != null && !st.isRecycled()) {
				st.recycle();
				st = null;
			}

			if (destFile != null) {
				destFile = null;
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private static InputStream getInputStreamFromUrl(String urlStr)
			throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlCon.getInputStream();
		return inputStream;
	}

	/**
	 * 保存文件
	 * 
	 * @param is
	 * @param url
	 * @param grid
	 * @return
	 */
	public static void downLoadAudio(final String url, final Handler handler) {
		final String path = Configs.VOICE_PATH
				+ AuxiliaryUtils.md5(IOUtil.getFilename(url));
		final File destFile = new File(path);
		if (destFile.exists()) {
			handler.obtainMessage(Configs.DOWNLOAD_AUDIO_SUCCESS, path)
					.sendToTarget();
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					InputStream is = null;
					File parent = destFile.getParentFile();
					if (parent != null && !parent.exists()) {
						parent.mkdirs();
					}
					FileOutputStream file;
					try {
						is = getInputStreamFromUrl(url);
						file = new FileOutputStream(destFile);
						file.write(input2byte(is));
						file.close();
					} catch (Exception e) {
						handler.obtainMessage(Configs.DOWNLOAD_AUDIO_FAIL, null)
								.sendToTarget();
					} finally {
						if (is != null) {
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					handler.obtainMessage(Configs.DOWNLOAD_AUDIO_SUCCESS, path)
							.sendToTarget();
				}
			}).start();
		}
	}

	public static final byte[] input2byte(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	/**
	 * 设置WIFI不休眠
	 * 
	 * @param mContext
	 */
	@SuppressWarnings("deprecation")
	public static void WifiNeverDormancy(Context mContext) {
		try {
			ContentResolver resolver = mContext.getContentResolver();
			int value = Settings.System.getInt(resolver,
					Settings.System.WIFI_SLEEP_POLICY,
					Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
			if (Settings.System.WIFI_SLEEP_POLICY_NEVER != value) {
				Settings.System.putInt(resolver,
						Settings.System.WIFI_SLEEP_POLICY,
						Settings.System.WIFI_SLEEP_POLICY_NEVER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取汉字串拼音首字母，英文字符不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音首字母
	 */
	public static String getFirstSpell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							arr[i], defaultFormat);
					if (temp != null) {
						pybf.append(temp[0].charAt(0));
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim();
	}

	/**
	 * 获取汉字串拼音，英文字符不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音
	 */
	public static String getFullSpell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i],
							defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString();
	}

    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return String
     */
    public static String getPinYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        // System.out.println(t1.length);
        String[] t2 = new String[t1.length];
        // System.out.println(t2.length);
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断能否为汉字字符
                // System.out.println(t1[i]);
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
                    t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，间接取出字符并连接到字符串t4后
                    t4 += Character.toString(t1[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return t4;
    }

    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return String
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }
	
	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 加载本地图片
	 *
	 * @param pathString
	 * @return
	 */
	public static Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}


	/**
	 * 判断是否存在sd卡
	 */
	public static boolean isSDCardExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/*
	 * 获取sd卡的路径
	 */
	public static String getSDCardDir() {
		if (null != SDCardDirCache) {
			return SDCardDirCache;
		}
		String rt = "/sdcard/";
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File dir = Environment.getExternalStorageDirectory();
				if (null != dir) {
					String dirStr = dir.getAbsolutePath();
					if (dirStr.endsWith("/")) {
						rt = dirStr;
					} else {
						rt = dirStr + "/";
					}
				}
			}
		} catch (Exception e) {
		}
		SDCardDirCache = rt;
		return rt;

	}
	


}
