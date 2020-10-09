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
package com.wl4g.devops.dts.codegen.engine.specs;

import com.google.common.collect.Lists;
import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.collection.CollectionUtils2;
import com.wl4g.components.common.id.SnowflakeIdGenerator;
import com.wl4g.components.common.lang.StringUtils2;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.bean.extra.GenProjectExtraOption;
import com.wl4g.devops.dts.codegen.utils.BuiltinColumnDefinition;

import javax.validation.constraints.NotBlank;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.components.common.collection.Collections2.disDupCollection;
import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.reflect.ReflectionUtils2.doWithLocalFields;
import static com.wl4g.components.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.components.common.reflect.ReflectionUtils2.makeAccessible;
import static com.wl4g.devops.dts.codegen.engine.specs.BaseSpecs.CommentExtractor.ofExtractor;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Locale.US;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replaceEach;
import static org.apache.commons.lang3.StringUtils.replacePattern;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

/**
 * Generic base specification utility.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public class BaseSpecs {

	// --- Naming. ---

	/**
	 * Gets the string that converts the first letter to uppercase
	 */
	public static String firstUCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		if (97 <= cs[0] && cs[0] <= 122) {
			cs[0] -= 32;
		}
		return valueOf(cs);
	}

	/**
	 * Gets the string that converts the first letter to lowercase
	 */
	public static String firstLCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		if (65 <= cs[0] && cs[0] <= 90) {
			cs[0] += 32;
		}

		return valueOf(cs);
	}

	/**
	 * Gets the string that converts the all letter to upper case
	 */
	public static String uCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.toUpperCase(US);
	}

	/**
	 * Gets the string that converts the all letter to lower case
	 */
	public static String lCase(@Nullable String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.toLowerCase(US);
	}

	/**
	 * Generate next ID.
	 * 
	 * @return
	 */
	public static long genNextId() {
		return SnowflakeIdGenerator.getDefault().nextId();
	}

	// --- Comments. ---

	/**
	 * Clean up and normalize comment strings, such as replacing line breaks,
	 * double quotes, and so on. </br>
	 * </br>
	 * 
	 * for example (unix):
	 * 
	 * <pre>
	 * {@link cleanComment("abcdefgh123456", null, null)} => abcdefgh123456
	 * {@link cleanComment("abcd\refgh123456", null, null)} => abcd efgh123456
	 * {@link cleanComment("abcd\nefgh123456", null, null)} => abcd efgh123456
	 * {@link cleanComment("abcd\r\nefgh123456", null, null)} => abcd efgh123456
	 * {@link cleanComment("abcd\r\nefgh"jack"123456", null, null)} => abcd efgh'jack'123456
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static String cleanComment(@Nullable String str) {
		return cleanComment(str, null, null);
	}

	/**
	 * Clean up and normalize comment strings, such as replacing line breaks,
	 * double quotes, and so on. </br>
	 * </br>
	 * 
	 * for example (unix):
	 * 
	 * <pre>
	 * {@link cleanComment("abcdefgh123456", null, null)} => abcdefgh123456
	 * {@link cleanComment("abcd\refgh123456", null, null)} => abcd efgh123456
	 * {@link cleanComment("abcd\nefgh123456", null, null)} => abcd efgh123456
	 * {@link cleanComment("abcd\r\nefgh123456", null, null)} => abcd efgh123456
	 * {@link cleanComment("abcd\r\nefgh"jack"123456", null, null)} => abcd efgh'jack'123456
	 * </pre>
	 * 
	 * @param str
	 * @param lineReplacement
	 * @param doubleQuotesReplacement
	 * @return
	 */
	public static String cleanComment(@Nullable String str, @Nullable String lineReplacement,
			@Nullable String doubleQuotesReplacement) {
		if (isBlank(str)) {
			return str;
		}
		lineReplacement = isBlank(lineReplacement) ? " " : lineReplacement;
		doubleQuotesReplacement = isBlank(doubleQuotesReplacement) ? "'" : doubleQuotesReplacement;

		// Escape line separators.
		// Match and replace Windows newline character first: '\r\n'
		str = replacePattern(str, "\r\n|\n|\r", lineReplacement);

		// Escape double quotes.
		return replacePattern(str, "\"", doubleQuotesReplacement);
	}

	/**
	 * It is useful to minimize and optimize the compression of comment strings,
	 * such as for menu and list header display. </br>
	 * </br>
	 * 
	 * @param str
	 * @return
	 */
	public static String extractComment(@Nullable String str, @NotBlank String extractor) {
		if (isBlank(str)) {
			return str;
		}
		return cleanComment(ofExtractor(hasTextOf(extractor, "extractor")).getHandler().extract(str));
	}

	/**
	 * Convert content to multiline comments format, and return without any
	 * action if the original content conforms to the format of multiline
	 * comments.
	 * 
	 * @param sourceContent
	 * @return
	 * @see {@link com.wl4g.devops.dts.codegen.engine.specs.BaseSpecsTests#wrapCommentCase()}
	 */
	public static String wrapMultiComment(@Nullable String sourceContent) {
		if (isBlank(sourceContent)) { // Optional
			return EMPTY;
		}

		// Nothing do
		if (sourceContent.contains("/*") && sourceContent.contains("*/")) {
			return sourceContent;
		}

		StringBuffer newCopyright = new StringBuffer("/*");
		try (BufferedReader bfr = new BufferedReader(new StringReader(sourceContent));) {
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

	/**
	 * Convert content to single comments format, and return without any action
	 * if the original content conforms to the format of single comments.
	 * 
	 * @param sourceContent
	 * @param markHead
	 * @return
	 * @see {@link com.wl4g.devops.dts.codegen.engine.specs.BaseSpecsTests#wrapCommentCase()}
	 */
	public static String wrapSingleComment(@Nullable String sourceContent, @NotBlank String markHead) {
		hasTextOf(markHead, "markHead");
		if (isBlank(sourceContent)) { // Optional
			return EMPTY;
		}

		// Nothing do
		if (trim(sourceContent).startsWith(markHead)) {
			return sourceContent;
		}

		StringBuffer newCopyright = new StringBuffer(markHead);
		try (BufferedReader bfr = new BufferedReader(new StringReader(sourceContent));) {
			String line = null;
			while (!isNull(line = bfr.readLine())) {
				newCopyright.append(LINE_SEPARATOR);
				newCopyright.append(markHead);
				newCopyright.append(" ");
				newCopyright.append(line);
			}
			newCopyright.append(LINE_SEPARATOR);
			newCopyright.append(markHead);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		return newCopyright.toString();
	}

	// --- Functions. ---

	/**
	 * Check if the state is true.
	 * 
	 * @param assertion
	 * @return
	 */
	public static boolean isTrue(@Nullable String value) {
		return isTrue(value, false);
	}

	/**
	 * Check if the state is true.
	 * 
	 * @param assertion
	 * @param defaultValue
	 * @return
	 */
	public static boolean isTrue(@Nullable String value, boolean defaultValue) {
		return StringUtils2.isTrue(value, defaultValue);
	}

	/**
	 * Check whether the specified extension configuration item exists.
	 * 
	 * @param configuredOptions
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean isConf(@Nullable List<GenProjectExtraOption> configuredOptions, @NotBlank String name,
			@NotBlank String value) {
		hasTextOf(name, "name");
		hasTextOf(value, "value");

		// Extra config optional
		if (CollectionUtils2.isEmpty(configuredOptions)) {
			return false;
		}

		// Verify name and value contains in options.
		for (GenProjectExtraOption opt : configuredOptions) {
			if (equalsIgnoreCase(opt.getName(), name) && equalsIgnoreCase(opt.getSelectedValue(), value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @see {@link #isConf()}
	 */
	@Deprecated
	public static boolean isTableOptionConf(@Nullable Map<String, String> optionMap, @NotBlank String name,
			@NotBlank String value) {
		for (Map.Entry<String, String> entry : optionMap.entrySet()) {
			if (equalsIgnoreCase(entry.getKey(), name) && equalsIgnoreCase(entry.getValue(), value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Filter out the columns displayed on the front page of
	 * {@link GenTableColumn}.
	 * 
	 * @param <T>
	 * @param columns
	 * @param condition
	 * @return
	 */
	public static List<GenTableColumn> filterColumns(@Nullable List<GenTableColumn> columns) {
		return filterColumns(columns, BuiltinColumnDefinition.COLUMN_NAME_VALUES);
	}

	/**
	 * Filter out the columns displayed on the front page of
	 * {@link GenTableColumn}.
	 * 
	 * @param <T>
	 * @param columns
	 * @param condition
	 * @return
	 */
	public static List<GenTableColumn> filterColumns(@Nullable List<GenTableColumn> columns,
			@Nullable List<String> withoutColumnNames) {
		List<String> conditions = safeList(withoutColumnNames);
		return safeList(columns).stream()
				.filter(e -> !conditions.contains(e.getAttrName()) && !conditions.contains(e.getColumnName())).collect(toList());
	}

	/**
	 * Remove duplicate collection elements.
	 * 
	 * @param list
	 * @return
	 */
	public static List<Object> distinctList(List<Object> list) {
		return Lists.newArrayList(disDupCollection(list).toArray(new Object[0]));
	}

	/**
	 * Transform {@link GenTableColumn} attributes.
	 * 
	 * @param columns
	 * @param fieldName
	 * @return
	 */
	public static List<Object> transformColumns(@Nullable List<GenTableColumn> columns, @NotBlank String fieldName) {
		hasTextOf(fieldName, "fieldName");

		List<Object> transformed = new ArrayList<>(isNull(columns) ? 0 : columns.size());
		safeList(columns).forEach(c -> {
			doWithLocalFields(GenTableColumn.class, f -> {
				if (f.getName().equals(fieldName)) {
					makeAccessible(f);
					transformed.add(getField(f, c));
				}
			});
		});
		return transformed;
	}

	/**
	 * Check if there is a matches value of the field of {@link List}.
	 * 
	 * @param values
	 * @param matchValue
	 * @return
	 */
	public static boolean hasFieldValue(@Nullable List<Object> values, @NotBlank String matchValue) {
		hasTextOf(matchValue, "matchValue");
		return safeList(values).stream().filter(v -> !isNull(v) && v.equals(matchValue)).findFirst().isPresent();
	}

	/**
	 * Useful comment extractor.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-10-07
	 * @sine v1.0.0
	 * @see
	 */
	public static enum CommentExtractor {

		wordSeg(str -> {
			// Chinese word segmentation keyword extraction.
			List<Word> words = safeList(WordSegmenter.seg(str));
			if (words.isEmpty()) {
				return str;
			}
			StringBuffer comment = new StringBuffer();
			for (int i = 0; i < words.size(); i++) {
				Word word = words.get(i);
				if (comment.length() <= 4) {
					comment.append(word.getText());
				} else {
					break;
				}
			}
			return comment.toString();
		}),

		simple(str -> {
			String extracted = str.substring(0, Math.min(str.length(), 5));
			return replaceEach(extracted, new String[] { "，", "。", "（", "）", "(", ")" }, new String[] { "", "", "", "", "", "" });
		});

		private final ExtractHandler handler;

		private CommentExtractor(ExtractHandler handler) {
			this.handler = handler;
		}

		public ExtractHandler getHandler() {
			return handler;
		}

		/**
		 * Parse comment extractor of name
		 * 
		 * @param extractorName
		 * @return
		 */
		public static final CommentExtractor ofExtractor(String extractorName) {
			for (CommentExtractor ext : values()) {
				if (equalsIgnoreCase(ext.name(), extractorName)) {
					return ext;
				}
			}
			throw new IllegalArgumentException(format("No such comment extractor of %s", extractorName));
		}

		/**
		 * {@link ExtractHandler}
		 */
		static interface ExtractHandler {
			String extract(@Nullable String str);
		}

	}

}
