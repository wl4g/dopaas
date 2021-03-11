/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.dts.kit.hbase.utils;

import static com.google.common.base.Charsets.UTF_8;
import static java.lang.System.out;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.io.IOException;

import com.google.common.io.Resources;
import com.wl4g.component.common.resource.resolver.ClassPathResourcePatternResolver;

/**
 * {@link HbaseMigrateUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public class HbaseMigrateUtils {

	final public static String DEFUALT_COUNTER_GROUP = HbaseMigrateUtils.class.getSimpleName() + "@CounterGroup";
	final public static String DEFUALT_COUNTER_TOTAL = "Total@Counter";
	final public static String DEFUALT_COUNTER_PROCESSED = "Processed@Counter";
	final public static String DEFAULT_HBASE_MR_TMPDIR = "/tmp-devops/tmpdir";
	final public static String DEFAULT_HFILE_OUTPUT_DIR = "/tmp-devops/outputdir";
	final public static String DEFAULT_SCAN_BATCH_SIZE = "1000";
	final public static String DEFAULT_USER = "hbase";

	/**
	 * Extract byte array without changing the original array.
	 *
	 * @param bys
	 * @param offset
	 * @param len
	 * @return New arrays ahead of time
	 */
	public static byte[] extractFieldByteArray(byte[] bys, int offset, int len) {
		byte[] b1 = new byte[len];
		System.arraycopy(bys, offset, b1, 0, len);
		return b1;
	}

	/**
	 * Show banner
	 * 
	 * @throws IOException
	 */
	public static void showBanner() throws IOException {
		out.println(
				Resources.toString(new ClassPathResourcePatternResolver().getResource("classpath:banner.txt").getURL(), UTF_8));
	}

	/**
	 * Gets short tableName
	 * 
	 * @param tableName
	 * @return
	 */
	public static String getShortTableName(String tableName) {
		if (isBlank(tableName)) {
			return EMPTY;
		}

		int index = tableName.indexOf(".");
		if (index > 0) {
			String shortTableName = tableName.substring(index + 1);
			return shortTableName;
		}

		return tableName;
	}

	/**
	 * Is ignore hbase qualifier fields. </br>
	 * 
	 * <p>
	 * Exclude HBase internal built-in fields, for example: '_0'
	 * </p>
	 * 
	 * @return
	 */
	public static boolean isIgnoreHbaseQualifier(String qualifier) {
		if (qualifier.startsWith("_") && qualifier.length() == 2) {
			return isNumeric(qualifier.substring(1));
		}
		return false;
	}

}