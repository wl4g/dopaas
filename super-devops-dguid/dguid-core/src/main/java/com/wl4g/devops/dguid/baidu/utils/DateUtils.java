/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.dguid.baidu.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @������ DateUtils.java @������
 * 
 * <pre>
 * ��ʱ�丨����(SimpleDateFormat���̲߳���ȫ�ģ�������˾�̬������)
 * </pre>
 * 
 * @���� ׯ�ε��� linhuaichuan1989@126.com @����ʱ�� 2019��7��8�� ����11:15:29
 * 
 * @�汾 1.0.0
 *
 * @�޸ļ�¼
 * 
 *        <pre>
 *     �汾                       �޸��� 		�޸����� 		 �޸���������
 *     ----------------------------------------------
 *     1.0.0 	ׯ�ε��� 	2019��7��8��             
 *     ----------------------------------------------
 *        </pre>
 */
public class DateUtils {
	/**
	 * ����-��ʽ
	 */
	public static final String DAY_PATTERN = "yyyy-MM-dd";

	/**
	 * ����ʱ��-��ʽ
	 */
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * ����ʱ��(������)-��ʽ
	 */
	public static final String DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * ������df����
	 */
	private static ThreadLocal<Map<String, DateFormat>> dfMap = new ThreadLocal<Map<String, DateFormat>>() {
		@Override
		protected Map<String, DateFormat> initialValue() {
			return new HashMap<String, DateFormat>(10);
		}
	};

	/**
	 * @�������� getDateFormat @��������
	 * 
	 * <pre>
	 * ����һ��DateFormat,ÿ���߳�ֻ��newһ��pattern��Ӧ��sdf
	 * </pre>
	 * 
	 * @param pattern
	 *            ��ʽ ���ʽ
	 * @return DateFormat����
	 */
	private static DateFormat getDateFormat(final String pattern) {
		Map<String, DateFormat> tl = dfMap.get();
		DateFormat df = tl.get(pattern);
		if (df == null) {
			df = new SimpleDateFormat(pattern);
			tl.put(pattern, df);
		}
		return df;
	}

	/**
	 * @�������� formatByDateTimePattern @��������
	 * 
	 * <pre>
	 * ��ȡ'yyyy-MM-dd HH:mm:ss'��ʽ��ʱ���ַ���
	 * </pre>
	 * 
	 * @param date
	 *            ʱ�����
	 * @return ʱ���ַ���
	 */
	public static String formatByDateTimePattern(Date date) {
		return getDateFormat(DATETIME_PATTERN).format(date);
	}

	/**
	 * @�������� parseByDayPattern @��������
	 * 
	 * <pre>
	 * ����'yyyy-MM-dd'��ʽ��ʱ��
	 * </pre>
	 * 
	 * @param str
	 *            ʱ���ַ���
	 * @return ʱ�����
	 */
	public static Date parseByDayPattern(String str) {
		return parseDate(str, DAY_PATTERN);
	}

	/**
	 * @�������� parseDate @��������
	 * 
	 * <pre>
	 * ����ָ����ʽ��ʱ��
	 * </pre>
	 * 
	 * @param str
	 *            ʱ���ַ���
	 * @param pattern
	 *            ��ʽ���ʽ
	 * @return ʱ�����
	 */
	public static Date parseDate(String str, String pattern) {
		try {
			return getDateFormat(pattern).parse(str);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}