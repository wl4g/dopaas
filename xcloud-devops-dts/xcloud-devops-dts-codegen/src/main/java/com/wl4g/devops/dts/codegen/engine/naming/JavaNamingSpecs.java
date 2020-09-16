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
package com.wl4g.devops.dts.codegen.engine.naming;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.components.common.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static java.lang.String.valueOf;
import static java.lang.ThreadLocal.withInitial;

/**
 * {@link JavaNamingSpecs}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-09
 * @since
 */
public abstract class JavaNamingSpecs {

	/**
	 * TableName to className
	 * 
	 * @param tableName
	 * @return
	 */
	public static String tableName2className(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return tableName;
		}
		int i = tableName.indexOf("_");
		if (i >= 0) {
			String sub = tableName.substring(i + 1, tableName.length());
			return underlineToHump(sub);
		}
		return tableName;
	}

	/**
	 * TableName to ClassName
	 * 
	 * @param tableName
	 * @return
	 */
	public static String tableName2ClassName(String tableName) {
		if (isBlank(tableName)) {
			return tableName;
		}
		int i = tableName.indexOf("_");
		if (i >= 0) {
			String sub = tableName.substring(i + 1, tableName.length());
			return fistUpperCase(underlineToHump(sub));
		}
		return tableName;
	}

	/**
	 * Gets the string that converts an underline to a hump
	 */
	public static String underlineToHump(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		str = str.toLowerCase();
		Matcher matcher = underlinePatternCache.get().matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Gets the string that converts the first letter to uppercase
	 */
	public static String fistUpperCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		cs[0] -= 32;
		return valueOf(cs);
	}

	/**
	 * Gets the string that converts the first letter to lowercase
	 */
	public static String fistLowerCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		cs[0] += 32;
		return valueOf(cs);
	}

	public static String packageToPath(@Nullable String str){
		return str.replaceAll("\\.","/");
	}

	/** Underline {@link Pattern} */
	private final static ThreadLocal<Pattern> underlinePatternCache = withInitial(() -> compile("_(\\w)"));

}