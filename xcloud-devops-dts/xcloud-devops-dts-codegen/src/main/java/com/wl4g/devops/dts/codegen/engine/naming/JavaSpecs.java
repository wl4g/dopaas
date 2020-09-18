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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotBlank;

import static java.util.Objects.isNull;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static java.lang.ThreadLocal.withInitial;

/**
 * Java naming specification {@link JavaSpecs}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-09
 * @since
 */
public class JavaSpecs extends BaseSpecs {

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
			return firstUCase(underlineToHump(sub));
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
	 * Class package name to os path.
	 * 
	 * @param str
	 * @return
	 */
	public static String pkgToPath(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.replaceAll("\\.", File.separator);
	}

	/**
	 * Escape copyright string. If there is no multi line comment in the
	 * Copyright string, the identifier is inserted, otherwise nothing is done.
	 * (Multi line annotation conforming to Java specification)
	 * 
	 * @param copyright
	 * @return
	 */
	public static String escapeCopyright(@NotBlank String copyright) {
		hasTextOf(copyright, "copyright");

		// Nothing do
		if (copyright.contains("/*") && copyright.contains("*/")) {
			return copyright;
		}

		StringBuffer newCopyright = new StringBuffer("/*");
		try (BufferedReader bfr = new BufferedReader(new StringReader(copyright));) {
			String line = null;
			while (!isNull(line = bfr.readLine())) {
				newCopyright.append(LINE_SEPARATOR);
				newCopyright.append(" * ");
				newCopyright.append(line);
			}
			newCopyright.append(LINE_SEPARATOR);
			newCopyright.append(" */");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		return newCopyright.toString();
	}

	/** Underline {@link Pattern} */
	private final static ThreadLocal<Pattern> underlinePatternCache = withInitial(() -> compile("_(\\w)"));

}