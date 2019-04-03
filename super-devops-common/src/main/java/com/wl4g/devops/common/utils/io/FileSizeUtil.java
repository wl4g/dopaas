/**
 * Copyright (c) 2014 ~ , wangl.sir Individual Inc. All rights reserved. It was made by wangl.sir Auto Build Generated file. Contact us 983708408@qq.com.
 */
/**
 * Copyright (c) 2014 ~ , wangl.sir Individual Inc. All rights reserved. It was made by wangl.sir Auto Build Generated file. Contact us 983708408@qq.com.
 */
package com.wl4g.devops.common.utils.io;

import java.text.DecimalFormat;

/**
 * File size tool.
 *
 * @author poplar.yfyang
 * @version 1.0 2013-01-02 12:50 PM
 * @since JDK 1.5
 */
public abstract class FileSizeUtil {
	public static long ONE_KB = 1024;
	public static long ONE_MB = ONE_KB * 1024;
	public static long ONE_GB = ONE_MB * 1024;
	public static long ONE_TB = ONE_GB * (long) 1024;
	public static long ONE_PB = ONE_TB * (long) 1024;

	public static String getHumanReadable(Long fileSize) {
		if (fileSize == null)
			return null;
		return getHumanReadable(fileSize.longValue());
	}

	public static String getHumanReadable(long fileSize) {
		if (fileSize < 0) {
			return String.valueOf(fileSize);
		}
		String result = getHumanReadable(fileSize, ONE_PB, "PB");
		if (result != null) {
			return result;
		}

		result = getHumanReadable(fileSize, ONE_TB, "TB");
		if (result != null) {
			return result;
		}
		result = getHumanReadable(fileSize, ONE_GB, "GB");
		if (result != null) {
			return result;
		}
		result = getHumanReadable(fileSize, ONE_MB, "MB");
		if (result != null) {
			return result;
		}
		result = getHumanReadable(fileSize, ONE_KB, "KB");
		if (result != null) {
			return result;
		}
		return String.valueOf(fileSize) + "B";
	}

	private static String getHumanReadable(long fileSize, long unit, String unitName) {
		if (fileSize == 0)
			return "0";

		if (fileSize / unit >= 1) {
			double value = fileSize / (double) unit;
			DecimalFormat df = new DecimalFormat("######.##" + unitName);
			return df.format(value);
		}
		return null;
	}
}
