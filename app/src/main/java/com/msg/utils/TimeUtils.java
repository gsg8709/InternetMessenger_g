package com.msg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
	private static SimpleDateFormat sdf1 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	private static SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");

	public static boolean isSameDay(long time1, long time2) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
						.get(Calendar.DAY_OF_YEAR);
	}

	public static String checkDate(String timeString) {

		// 当前时间
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(System.currentTimeMillis());
		//
		Calendar cal2 = Calendar.getInstance();
		try {
			long time = sdf1.parse(timeString).getTime();
			cal2.setTimeInMillis(time);

			// 同一年
			if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {

				return sdf2.format(cal2.getTime());

			} else {
				return timeString;
			}

		} catch (ParseException e) {
			return timeString;

		}

	}

	public static String getTimeAgo(String timeString) {

		long time = 0;
		try {
			time = sdf1.parse(timeString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return "未知";
		}

		// 现在时间
		long now = System.currentTimeMillis();
		if (time > now || time <= 0) {
			return "刚刚";
		}

		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return "刚刚";
		} else if (diff < 2 * MINUTE_MILLIS) {
			return "1分钟前";
		} else if (diff < 50 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + "分钟前";
		} else if (diff < 90 * MINUTE_MILLIS) {
			return "1小时前";
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + "小时前";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "昨天";
		} else {
			return timeString;
		}
	}

	public static String getTimeAgo2(String timeString) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		long time = 0;
		try {
			time = localSimpleDateFormat.parse(timeString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

		// 现在时间
		long now = System.currentTimeMillis();
		if (time > now || time <= 0) {
			return null;
		}

		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return "刚刚";
		} else if (diff < 2 * MINUTE_MILLIS) {
			return "一分钟前";
		} else if (diff < 50 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + "分钟前";
		} else if (diff < 90 * MINUTE_MILLIS) {
			return "1小时前";
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + "小时前";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "昨天";
		} else {
			return timeString;
		}
	}

	/**
	 * 
	 * @Title: getTimeMobile
	 * @Description: 
	 *               时间处理工具:手机时间和系统时间对比，动态发布小于1小时，返回：1-59分钟前；大于等于1小时，在今天24小时前，返回：
	 *               今天 15:17；如果是昨天的，返回：11-04 23:20。如果是去年的返回：2011-11-04 21:22。
	 * @param timeString
	 * @return String
	 * @author gengsong
	 * @throws
	 */
	public static String getTimeMobile(String timeString) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		long time = 0; // 手机时间
		try {
			time = localSimpleDateFormat.parse(timeString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

		// 系统时间
		long now = System.currentTimeMillis();
		if (time > now || time <= 0) {
			return null;
		}

		final long diff = now - time;
		if (diff < HOUR_MILLIS) {
			return "1-59分钟前";
		} else if (diff >= HOUR_MILLIS && diff < 24 * HOUR_MILLIS) {
			return "今天 15:17";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "11-04 23:20";
		} else if (diff < 365 * DAY_MILLIS) {
			return "DAY_MILLIS";
		} else {
			return timeString;
		}

	}
	
	
	public static String converTime(long timestamp) {
		long currentSeconds = System.currentTimeMillis() / 1000;
//		long timeGap = currentSeconds - timestamp;// 与现在时间相差秒数
		long timeGap = timestamp - currentSeconds;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > 24 * 60 * 60) {// 1天以上
			timeStr = timeGap / (24 * 60 * 60) + "天前";
		} else if (timeGap > 60 * 60) {// 1小时-24小时
			timeStr = timeGap / (60 * 60) + "小时前";
		} else if (timeGap > 60) {// 1分钟-59分钟
			timeStr = timeGap / 60 + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public static String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		Date date = new Date(timestamp * 1000);
		sdf.format(date);
		return sdf.format(date);
	}
	

}
