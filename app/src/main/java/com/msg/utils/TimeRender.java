package com.msg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ocpsoft.pretty.time.PrettyTime;

public class TimeRender {

	private static SimpleDateFormat formatBuilder;

	private static String getDate(String format, long time) {
		formatBuilder = new SimpleDateFormat(format);
		String data = time + "000";
		return formatBuilder.format(Long.parseLong(data));
	}

	public static String getDate(long create_time) {
		return getDate("yyyy-MM-dd", create_time);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getDate() {
		Date date = new Date();
		long time = date.getTime();
		String dateline = time + "";
		dateline = dateline.substring(0, 10);
		return getDate(Long.parseLong(dateline));
	}

	public static String formatDate(long create_time) {
		PrettyTime p = new PrettyTime();
		return p.format(new Date(create_time));
	}

	public static long stringToLong(String strTime, String formatType) {
		Date date;
		try {
			date = stringToDate(strTime, formatType);
			if (date == null) {
				return 0;
			} else {
				long currentTime = dateToLong(date);
				return currentTime;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static Date stringToDate(String strTime, String formatType)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	private static long dateToLong(Date date) {
		return date.getTime();
	}
}
