package com.wl4g.devops.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

	public static final String UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String YMD = "yyyy-MM-dd";

	public static List<String> GetDates(String startDate, String endDate) throws Exception {
		Date d1 = new SimpleDateFormat(YMD).parse(startDate);// 定义起始日期
		Date d2 = new SimpleDateFormat(YMD).parse(endDate);// 定义结束日期
		Calendar dd = Calendar.getInstance();// 定义日期实例
		dd.setTime(d1);// 设置日期起始时间
		ArrayList<String> dates = new ArrayList<String>();
		while (dd.getTime().getTime() <= d2.getTime()) {// 判断是否到结束日期
			SimpleDateFormat sdf = new SimpleDateFormat(YMD);
			String str = sdf.format(dd.getTime());
			dates.add(str);
			dd.add(Calendar.DAY_OF_MONTH, 1);// 进行当前日期月份加1
		}
		return dates;
	}

	public static String toUtc(String time) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(UTC);
		SimpleDateFormat sdf2 = new SimpleDateFormat(YMD_HMS);
		String str = null;
		try {
			Date date = sdf2.parse(time);// 拿到Date对象
			str = sdf1.format(date);// 输出格式：2017-01-22 09:28:33

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String getNowTime() {
		SimpleDateFormat sf = new SimpleDateFormat(YMD_HMS);
		return sf.format(new Date());
	}

	public static String getNowTime(String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(new Date());
	}

	public static String ymdhmsToymd(String date) {
		return date.substring(0, 10);
	}

	/**
	 * 获取当前时间前几秒钟
	 * 
	 * @param stuff
	 *            前几秒钟
	 * @return
	 */
	public static String getCurrentTimeBySecound(int stuff) {
		SimpleDateFormat sdf = new SimpleDateFormat(YMD_HMS);
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.SECOND, stuff);// 几秒钟之前的时间
		Date beforeD = beforeTime.getTime();
		String time = sdf.format(beforeD);
		return time;
	}

	/**
	 * 获取当前时间前几分钟
	 * 
	 * @param stuff
	 *            前几分钟
	 * @return
	 */
	public static String getCurrentTime(int stuff) {
		SimpleDateFormat sdf = new SimpleDateFormat(YMD_HMS);
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, stuff);// 几分钟之前的时间
		Date beforeD = beforeTime.getTime();
		String time = sdf.format(beforeD);
		return time;
	}

	/**
	 * 获取当前时间前几小时
	 * 
	 * @param stuff
	 *            前几小时
	 * @return
	 */
	public static String getCurrentTimeByHour(int stuff) {
		SimpleDateFormat sdf = new SimpleDateFormat(YMD_HMS);
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.HOUR, stuff);
		Date beforeD = beforeTime.getTime();
		String time = sdf.format(beforeD);
		return time;
	}

}
