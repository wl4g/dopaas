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
package com.wl4g.devops.components.tools.common.io;

import java.text.DecimalFormat;

/**
 * File size tool.
 *
 * @author poplar.yfyang
 * @version 1.0 2013-01-02 12:50 PM
 * @since JDK 1.5
 */
public abstract class FileSizeUtils {
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