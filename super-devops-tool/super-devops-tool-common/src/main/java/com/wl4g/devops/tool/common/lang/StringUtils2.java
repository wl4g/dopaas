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
package com.wl4g.devops.tool.common.lang;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;

/**
 * String tool class, inheriting the org.apache.commons.lang3.StringUtils class
 * 
 * @author wangl.sir
 * @version v1.0 2017年5月28日
 * @since
 */
public abstract class StringUtils2 extends org.apache.commons.lang3.StringUtils {
	private final static char SEPARATOR = '_';

	/**
	 * Mail regular expression
	 */
	private final static String REGEX_IS_MAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	/**
	 * Number regular expression
	 */
	private final static String REGEX_IS_NUMBER = "^[0-9]*$";
	/**
	 * Decimals regular expression
	 */
	private final static String REGEX_IS_DECIMALS = "([1-9]+[0-9]*|0)(\\.[\\d]+)?";
	/**
	 * IP regular expression
	 */
	private final static String REGEX_IS_IP = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])"
			+ "\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d"
			+ "\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	/**
	 * Domain regular expression.
	 * DNS规定，域名中的标号都由英文字母和数字组成，每一个标号不超过63个字符，也不区分大小写字母。标号中除连字符（-）外不能使用其他的标点符号。级别最低的域名写在最左边，而级别最高的域名写在最右边。由多个标号组成的完整域名总共不超过255个字符。
	 */
	final public static String REGEX_DOMAIN = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
	/**
	 * URL regular expression
	 */
	final public static String REGEX_URL = "^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?$";

	private final static String CHINESE_REGEX = "[\u4e00-\u9fa5]+";
	private static final String FOLDER_SEPARATOR = "/";
	private static final char EXTENSION_SEPARATOR = '.';

	private final static Pattern patternChinese = Pattern.compile(CHINESE_REGEX);
	private final static Pattern patternLetter = Pattern.compile("[A-Za-z]");

	/**
	 * 转换为字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] getBytes(String str) {
		return !isBlank(str) ? str.getBytes(UTF_8) : null;
	}

	/**
	 * 转换为字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static String toString(byte[] bytes) {
		return new String(bytes, UTF_8);
	}

	/**
	 * 是否包含字符串
	 * 
	 * @param str
	 *            验证字符串
	 * @param strs
	 *            字符串组
	 * @return 包含返回true
	 */
	public static boolean inString(String str, String... strs) {
		if (str != null) {
			for (String s : strs) {
				if (str.equals(trim(s))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * 
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html) {
		if (html == null) {
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * 
	 * @param str
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String abbr2(String param, int length) {
		if (param == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}
			try {
				if (!isCode && !isHTML) {
					n += String.valueOf(temp).getBytes("GBK").length;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (n <= length - 3) {
				result.append(temp);
			} else {
				result.append("...");
				break;
			}
		}
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)", "$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result.replaceAll(
				"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
				"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List<String> endHTML = Lists.newArrayList();
		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}
		return result.toString();
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val) {
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val) {
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val) {
		return toLong(val).intValue();
	}

	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("X-Real-IP");
		if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("X-Forwarded-For");
		} else if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("Proxy-Client-IP");
		} else if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("WL-Proxy-Client-IP");
		}
		return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}

	/**
	 * 驼峰命名法工具
	 * 
	 * @return toCamelCase("hello_world") == "helloWorld"
	 *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 *         toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toCamelCase(String s) {
		if (s == null) {
			return null;
		}

		s = s.toLowerCase();

		StringBuilder sb = new StringBuilder(s.length());
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * 驼峰命名法工具
	 * 
	 * @return toCamelCase("hello_world") == "helloWorld"
	 *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 *         toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toCapitalizeCamelCase(String s) {
		if (s == null) {
			return null;
		}
		s = toCamelCase(s);
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * 驼峰命名法工具
	 * 
	 * @return toCamelCase("hello_world") == "helloWorld"
	 *         toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 *         toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toUnderScoreCase(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			boolean nextUpperCase = true;

			if (i < (s.length() - 1)) {
				nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
			}

			if ((i > 0) && Character.isUpperCase(c)) {
				if (!upperCase || !nextUpperCase) {
					sb.append(SEPARATOR);
				}
				upperCase = true;
			} else {
				upperCase = false;
			}

			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();
	}

	/**
	 * 如果不为空，则设置值
	 * 
	 * @param target
	 * @param source
	 */
	public static void setValueIfNotBlank(String target, String source) {
		if (source != null && isNotBlank(source)) {
			target = source;
		}
	}

	/**
	 * 转换为JS获取对象值，生成三目运算返回结果
	 * 
	 * @param objectString
	 *            对象串 例如：row.user.id
	 *            返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
	 */
	public static String jsGetVal(String objectString) {
		StringBuilder result = new StringBuilder();
		StringBuilder val = new StringBuilder();
		String[] vals = split(objectString, ".");
		for (int i = 0; i < vals.length; i++) {
			val.append("." + vals[i]);
			result.append("!" + (val.substring(1)) + "?'':");
		}
		result.append(val.substring(1));
		return result.toString();
	}

	/**
	 * Checks if a CharSequence is empty ("") or null.<br/>
	 * StringUtils.isEmpty("null") = true<br/>
	 * StringUtils.isEmpty("NULL") = true<br/>
	 * StringUtils.isEmpty(null) = true<br/>
	 * StringUtils.isEmpty("") = true<br/>
	 * StringUtils.isEmpty(" ") = false<br/>
	 * StringUtils.isEmpty("bob") = false<br/>
	 * StringUtils.isEmpty(" bob ") = false<br/>
	 * 
	 * @param object
	 *            target object
	 * @return true if the CharSequence is empty or null
	 */
	public static boolean isEmpty(Object object) {
		if (object == null || eqIgnCase(object, "null")) {
			return true;
		} else if (object instanceof CharSequence) {
			return isEmpty(object.toString());
		}
		return false;
	}

	/**
	 * <h1>全部为空才返回 TRUE</h1> <br/>
	 * <li style='color:blue;'>如果有一个不为空则返回false, (即不是所有都为空)</li>
	 * 
	 * @param objectlist
	 *            目标对象
	 * @return 是否为空（包括空对象、空字符串、null等）
	 */
	public static boolean isAllEmpty(Object... objectlist) {

		if (objectlist == null) {
			return true;
		}
		for (Object obj : objectlist) {
			if (!isEmpty(obj)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <h1>存在任何一个(只要有一个)为空就返回 TRUE</h1> <br/>
	 * 
	 * @param objectlist
	 *            目标对象
	 * @return 是否为空（包括空对象、空字符串、null等）
	 */
	public static boolean isAnyEmpty(Object... objectlist) {

		if (objectlist == null) {
			return true;
		}
		for (Object obj : objectlist) {
			if (isEmpty(obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 全部不为空才返回 TRUE
	 * 
	 * @param objectlist
	 * @return
	 */
	public static boolean isNotAllEmpty(Object... objectlist) {

		return !isAnyEmpty(objectlist);
	}

	/**
	 * 存在任何一个(至少一个)不为空则返回 TRUE
	 * 
	 * @param objectlist
	 * @return
	 */
	public static boolean isNotAnyEmpty(Object... objectlist) {

		return !isAllEmpty(objectlist) && !isNotAllEmpty(objectlist) || isNotAllEmpty(objectlist);
	}

	/**
	 * 校验字符串是否相等
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean eqIgnCase(Object obj1, Object obj2) {

		if (obj1 == null || obj2 == null) {
			return true;
			/*
			 * 1. 再次判断obj1.toString()是为了控制这种情况： new
			 * User()对象，但User重写了toString方法且返回空. 2.
			 * 再次判断str1.toString()使用equalsIgnoreCase判断是为了除开这种情况:
			 * obj1属于string类型，obj2属于double类型
			 */
		} else if (obj1 == obj2 || equalsIgnoreCase(obj1.toString(), obj2.toString())) {
			return true;
		} else if (obj1 instanceof CharSequence && obj2 instanceof CharSequence) {
			return equalsIgnoreCase(String.valueOf(obj1).trim(), String.valueOf(obj2).trim());
		}

		return false;
	}

	/**
	 * 是否存在中文, 只要有一个中文字符就返回TRUE
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAnyChinese(String str) {
		return matcheAny(patternChinese, str); // 是否含有中文字符
	}

	/**
	 * 全部时中文才返回TRUE
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAllChinese(String str) {
		return matchesAll(CHINESE_REGEX, str); // 是否全是中文字符
	}

	/**
	 * 是否全部为字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAllLetter(String str) {

		return matchesAll("^[A-Za-z]+$", str); // 是否全是字母构成
	}

	/**
	 * 是否包含字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAnyLetter(String str) {

		return matcheAny(patternLetter, str); // 是否全是字母构成
	}

	/**
	 * 根据字符串表达式计算出对应数字结果<br/>
	 * 例: System.out.println(StringUtil.calculate("3 * (10 % 3 + 2) / 2")); ==>
	 * 4
	 * 
	 * @param expStr
	 *            字符串表达式
	 * @return 返回此表达式的数学整数运行的结果
	 * @throws RuntimeException
	 *             在执行表达式计算时有可能会发生异常，例：表达式不合法、除数为0
	 */
	public static String calculate(String expStr) throws RuntimeException {

		if (isEmpty(expStr)) {
			throw new NullPointerException("表达式expStr不能为空.");
		}
		Object result = null;
		try {
			result = Expression.getExpresser().calculate(expStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result == null ? null : result.toString();
	}

	/**
	 * 根据字符串表达式计算出对应数字结果<br/>
	 * 例: System.out.println(StringUtil.calculate("3 > (10 % 3 + 2) / 2")); ==>
	 * false<br/>
	 * 例: System.out.println(StringUtil.calculate("3 <= (10 % 3 + 2) / 2")); ==>
	 * true
	 * 
	 * @param expStr
	 *            字符串boolean表达式
	 * @return 返回检测结果
	 * @throws RuntimeException
	 */
	public static Boolean test(String expStr) throws RuntimeException {

		if (isEmpty(expStr)) {
			throw new NullPointerException("表达式expStr不能为空.");
		}
		Boolean result = null;
		try {
			result = Expression.getExpresser().test(expStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result == null ? null : result;
	}

	/**
	 * 执行正则匹配
	 * 
	 * @param regex
	 * @param str
	 * @return
	 */
	static boolean matchesAll(String regex, String str) {

		try {
			str = new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str.matches(regex);
	}

	/**
	 * 执行正则匹配
	 * 
	 * @param pattern
	 * @param str
	 * @return
	 */
	static boolean matcheAny(Pattern pattern, String str) {

		try {
			str = new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return pattern.matcher(str).find();
	}

	/**
	 * 获取自定区间(参数数组中最大和最小, 包括两端)的两个随机自然数的随机“+”、“-”、“*”、“/” 的表达式及结果(结果仍然为自然数)；
	 * 
	 * @param digit
	 *            区间数(包含两端)
	 * @return 表达式及结果组成的对象
	 */
	public static ExpressVo createRandomExpres(int min, int max) {
		// 获取两个分别被指定范围的随机自然数
		Integer num1 = null;
		Integer num2 = null;
		// 随机运算表达式
		String expression = null;
		// 表达式结果
		Integer result = null;
		boolean flag = true;
		while (flag) {
			num1 = nextInt(min, max);
			num2 = nextInt(min, max);
			if (num1 < num2) {
				num1 += num2;
				num2 = num1 - num2;
				num1 = num1 - num2;
			}
			switch (nextInt(0, 3)) {
			case 0:
				result = (num1 + num2);
				expression = new StringBuffer().append(num1.intValue()).append(" + ").append(num2.intValue()).append(" = ?")
						.toString();
				flag = false;
				break;
			case 1:
				result = (num1 - num2);
				expression = new StringBuffer().append(num1.intValue()).append(" - ").append(num2.intValue()).append(" = ?")
						.toString();
				flag = false;
				break;
			case 2:
				result = (num1 * num2);
				expression = new StringBuffer().append(num1.intValue()).append(" * ").append(num2.intValue()).append(" = ?")
						.toString();
				flag = false;
				break;
			case 3:
				// 1.除数为0 继续
				if (num2 == 0) {
					continue;
				}
				// 2.除不尽继续,(那么除不尽就四舍五入了)
				BigDecimal big1 = new BigDecimal(num1);
				BigDecimal bigRes = big1.divide(new BigDecimal(num2), 0, BigDecimal.ROUND_HALF_UP);
				result = Integer.valueOf(bigRes.intValue());
				expression = new StringBuffer().append(num1.intValue()).append(" / ").append(num2.intValue()).append(" ≈ ?")
						.toString();
				flag = false;
			}
		}

		// 封装结果数据
		ExpressVo vo = new ExpressVo();
		vo.setExpression(expression.replaceAll("\\*", "×"));
		vo.setResult(result.intValue() + "");
		return vo;
	}

	/**
	 * 校验是否非数字
	 * 
	 * @param str
	 *            待验证字符串
	 * @return 是否数字
	 */
	public static boolean isNaN(String str) {
		return str.matches(REGEX_IS_NUMBER);
	}

	/**
	 * 校验是否为小数
	 * 
	 * @param str
	 *            待验证字符串
	 * @return 是否是小数
	 */
	public static boolean isDecimal(String str) {
		return str.matches(REGEX_IS_DECIMALS);
	}

	/**
	 * 校验是否合法邮箱
	 * 
	 * @param str
	 *            待验证字符串
	 * @return 是否是合法邮箱格式
	 */
	public static boolean isMail(String str) {
		return str.matches(REGEX_IS_MAIL);
	}

	/**
	 * 校验是否可符合IP 规则
	 * 
	 * @param str
	 *            待验证字符串
	 * @return 是否是合法IP 格式
	 */
	public static boolean isIP(String str) {
		return str.matches(REGEX_IS_IP);
	}

	/**
	 * Validate is URL
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isURL(String url) {
		if (isBlank(url)) {
			return false;
		}
		return url.matches(REGEX_URL);
	}

	/**
	 * Validate is domain
	 * 
	 * @param domain
	 * @return
	 */
	public static boolean isDomain(String domain) {
		if (isBlank(domain)) {
			return false;
		}
		return domain.matches(REGEX_DOMAIN) || domain.equalsIgnoreCase("localhost");
	}

	/**
	 * 用于传输随机表达式及运算结果的值封装对象
	 * 
	 * @author wangl.sir
	 * @version v1.0 2016年1月16日
	 * @since
	 */
	public static class ExpressVo {

		/** 随机表达式 */
		private String expression;
		/** 随机表达式的运算结果 */
		private String result;

		/**
		 * @return 随机表达式
		 */
		public String getExpression() {
			return expression;
		}

		/**
		 * @return 随机表达式的运算结果
		 */
		public String getResult() {
			return result;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}

	/**
	 * Mutilate JSON message parse.<br/>
	 * <font color=red>终端可能会同时输入多个数据包, eg: 业务数据包 + 心跳H </font>
	 * 
	 * @param msg
	 * @return
	 */
	public static List<String> unpackingMessage(String msg) {
		msg = (String.valueOf(msg)).replaceAll("\r\n", "").replaceAll("\n", ""); // 否则会执行到dispatcher方法产生异常

		List<String> msgBufs = new ArrayList<String>();
		char[] chars = msg.toCharArray();
		StringBuffer sb = new StringBuffer();
		int leftQuote = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '{') {
				leftQuote++;
				sb.append(chars[i]);
				continue;
			}
			if (chars[i] == '}') {
				leftQuote--;
				sb.append(chars[i]);
				if (leftQuote == 0) {
					msgBufs.add(sb.toString());
					sb.setLength(0);
				}
				continue;
			}
			if (leftQuote > 0) {
				sb.append(chars[i]);
				continue;
			}
			msgBufs.add(String.valueOf(chars[i]));
		}

		return msgBufs;
	}

	/**
	 * Extract the filename from the given Java resource path, e.g.
	 * {@code "mypath/myfile.txt" -> "myfile.txt"}.
	 * 
	 * @param path
	 *            the file path (may be {@code null})
	 * @return the extracted filename, or {@code null} if none
	 */
	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}

		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given Java resource path, e.g.
	 * "mypath/myfile.txt" -> "txt".
	 * 
	 * @param path
	 *            the file path (may be {@code null})
	 * @return the extracted filename extension, or {@code null} if none
	 */
	public static String getFilenameExtension(String path) {
		if (path == null) {
			return null;
		}

		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) {
			return null;
		}

		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) {
			return null;
		}

		return path.substring(extIndex + 1);
	}

	/**
	 * 根据数字表达式计算出对应表达式的值
	 * 
	 * @author His father is wangl.sir
	 * @version v1.0 2014年7月29日
	 * @sine
	 */
	static class Expression {
		private final static String CHAE_L = "(";
		private final static String CHAE_R = ")";
		private final static String ADD = "+";
		private final static String MIN = "-";
		private final static String MUL = "*";
		private final static String DIV = "/";
		private final static String MOD = "%";

		// test
		private final static String BT = ">";
		private final static String LT = "<";
		private final static String EQ = "=";
		private final static String NOT_EQ = "N";// !=
		private final static String AND = "&";
		private final static String OR = "|";
		private final static String NOT = "!";
		private final static String BT_EQ = "B";// >=
		private final static String LT_EQ = "X";// <=

		private final static Expression exper = new Expression();

		private Expression() {
		}

		public final static synchronized Expression getExpresser() {
			return exper;
		}

		/**
		 * calculate result
		 * 
		 * @param expStr
		 * @return
		 * @throws Exception
		 */
		public Object calculate(String expStr) throws Exception {
			// System.out.println("old, expStr : "+expStr);

			// 1. init and check expression str
			expStr = initAndCheck(expStr);

			// 2. get calculate item
			ItemCalculate item = new ItemCalculate(null, expStr);
			item.genCalculateItem();

			// 3. calculate
			item.calculate();
			return item.getResult();
		}

		public boolean test(String expStr) throws Exception {
			Object obj = this.calculate(expStr);
			if (obj instanceof Boolean) {
				return (Boolean) obj;
			} else {
				throw new Exception("test calculate error.");
			}
		}

		private class ItemCalculate {
			public ItemCalculate(String sign, String expStr) {
				this.sign = sign;
				this.expStr = expStr;
			}

			private String sign;
			private String expStr;
			private List<ItemCalculate> sonItemList = null;
			private Object result = null;

			private boolean isBoolean() {
				if (result != null) {
					if (result instanceof Boolean) {
						return true;
					} else {
						return false;
					}
				}
				if ("TRUE".equals(expStr) || "FALSE".equals(expStr)) {
					return true;
				}
				return false;
			}

			private boolean isInteger() {
				if (result != null) {
					if (result instanceof Integer) {
						return true;
					} else {
						return false;
					}
				}
				if (!isBoolean() && expStr.indexOf(".") < 0)
					return true;
				return false;
			}

			private boolean isDouble() {
				if (result != null) {
					if (result instanceof Double) {
						return true;
					} else {
						return false;
					}
				}
				if (expStr.indexOf(".") > 0)
					return true;
				return false;
			}

			public void genCalculateItem() throws Exception {
				int index = 0;
				String lastSign = null;
				for (int i = 0; i < expStr.length(); i++) {
					String one = expStr.substring(i, i + 1);
					if (isSign(one) || isTestSign(one)) {// before
						if (i == 0) {
							lastSign = one;
							index = i + 1;
						} else {
							String numStr = expStr.substring(index, i);
							this.addItem(lastSign, numStr);
							lastSign = one;
							index = i + 1;
						}
					} else if (isLkuohao(one)) {// after
						int oKuohaoIndex = getOtherKuohaoIndex(expStr, i);
						if (oKuohaoIndex < 0)
							throw new Exception("expression str error1, '(',')' not matching.");
						String sonExpStr = expStr.substring(index + 1, oKuohaoIndex);
						ItemCalculate item = this.addItem(lastSign, sonExpStr);
						item.genCalculateItem();
						if (oKuohaoIndex >= expStr.length() - 1)
							return;
						lastSign = expStr.substring(oKuohaoIndex + 1, oKuohaoIndex + 2);
						if (!isSign(lastSign) && !isTestSign(lastSign)) {
							throw new Exception("expression str error, not a sign char (" + lastSign + ").");
						}
						index = oKuohaoIndex + 2;
						i = index - 1;
					} else if (i == expStr.length() - 1) {
						String numStr = expStr.substring(index, i + 1);
						this.addItem(lastSign, numStr);
					}
				}
			}

			public void calculate() throws Exception {

				if (sonItemList == null) {
					if (isBoolean()) {
						Boolean t = getBoolean(getExpStr());
						setResult(t);
					} else if (isDouble()) {
						setResult(getDouble(getExpStr()));
					} else if (isInteger()) {
						setResult(getInteger(getExpStr()));
					}
					return;
				}

				for (int i = 0; i < sonItemList.size(); i++) {
					ItemCalculate sonItem = sonItemList.get(i);
					sonItem.calculate();
					// System.out.println("temp result:"+sonItem.getResult()+",
					// sign:"+sonItem.getSign()+",
					// expStr:"+sonItem.getExpStr());
				}

				ItemCalculate lastItem = null;

				/**
				 * *,/,%
				 */
				lastItem = null;
				for (int i = 0; i < sonItemList.size(); i++) {
					if (sonItemList.size() < 2)
						break;
					ItemCalculate sonItem = sonItemList.get(i);
					String sign = sonItem.getSign();

					if (isHightLeavl(sign) && lastItem != null) {
						Integer lRsti = null;
						Double lRstd = null;
						Integer sRsti = null;
						Double sRstd = null;
						if (lastItem.isInteger())
							lRsti = (Integer) lastItem.getResult();
						else if (lastItem.isDouble())
							lRstd = (Double) lastItem.getResult();
						else {
							throw new Exception("errorA1.");
						}

						if (sonItem.isInteger())
							sRsti = (Integer) sonItem.getResult();
						else if (sonItem.isDouble())
							sRstd = (Double) sonItem.getResult();
						else {
							throw new Exception("errorA2.");
						}

						if (MUL.equals(sign)) {
							if (lRsti != null && sRsti != null) {
								lastItem.setResult(lRsti * sRsti);
							} else if (lRsti != null && sRstd != null) {
								lastItem.setResult(lRsti * sRstd);
							} else if (lRstd != null && sRsti != null) {
								lastItem.setResult(lRstd * sRsti);
							} else if (lRstd != null && sRstd != null) {
								lastItem.setResult(lRstd * sRstd);
							}
						} else if (DIV.equals(sign)) {
							if (lRsti != null && sRsti != null) {
								lastItem.setResult(lRsti / sRsti);
							} else if (lRsti != null && sRstd != null) {
								lastItem.setResult(lRsti / sRstd);
							} else if (lRstd != null && sRsti != null) {
								lastItem.setResult(lRstd / sRsti);
							} else if (lRstd != null && sRstd != null) {
								lastItem.setResult(lRstd / sRstd);
							}
						} else if (MOD.equals(sign)) {
							if (lRsti != null && sRsti != null) {
								lastItem.setResult(lRsti % sRsti);
							} else if (lRsti != null && sRstd != null) {
								lastItem.setResult(lRsti % sRstd);
							} else if (lRstd != null && sRsti != null) {
								lastItem.setResult(lRstd % sRsti);
							} else if (lRstd != null && sRstd != null) {
								lastItem.setResult(lRstd % sRstd);
							}
						}
						sonItemList.remove(i);
						i = -1;
						lastItem = null;
						continue;
					}
					lastItem = sonItem;
				}

				/**
				 * +,-
				 */
				lastItem = null;
				for (int i = 0; i < sonItemList.size(); i++) {
					if (sonItemList.size() < 2)
						break;
					ItemCalculate sonItem = sonItemList.get(i);
					if (i == 0) {
						lastItem = sonItem;
						continue;
					}
					String sign = sonItem.getSign();
					if (isSign(sign) && !isHightLeavl(sign) && lastItem != null) {

						Integer lRsti = null;
						Double lRstd = null;
						Integer sRsti = null;
						Double sRstd = null;
						if (lastItem.isInteger())
							lRsti = (Integer) lastItem.getResult();
						else if (lastItem.isDouble())
							lRstd = (Double) lastItem.getResult();
						else {
							throw new Exception("errorB1.");
						}

						if (sonItem.isInteger())
							sRsti = (Integer) sonItem.getResult();
						else if (sonItem.isDouble())
							sRstd = (Double) sonItem.getResult();
						else {
							throw new Exception("errorB2.");
						}

						if (MIN.equals(lastItem.getSign())) {
							if (lRsti != null)
								lRsti = -lRsti;
							else if (lRstd != null)
								lRstd = -lRstd;
							lastItem.setSign(ADD);
						}

						if (ADD.equals(sign)) {
							if (lRsti != null && sRsti != null) {
								lastItem.setResult(lRsti + sRsti);
							} else if (lRsti != null && sRstd != null) {
								lastItem.setResult(lRsti + sRstd);
							} else if (lRstd != null && sRsti != null) {
								lastItem.setResult(lRstd + sRsti);
							} else if (lRstd != null && sRstd != null) {
								lastItem.setResult(lRstd + sRstd);
							}
						} else if (MIN.equals(sign)) {
							if (lRsti != null && sRsti != null) {
								lastItem.setResult(lRsti - sRsti);
							} else if (lRsti != null && sRstd != null) {
								lastItem.setResult(lRsti - sRstd);
							} else if (lRstd != null && sRsti != null) {
								lastItem.setResult(lRstd - sRsti);
							} else if (lRstd != null && sRstd != null) {
								lastItem.setResult(lRstd - sRstd);
							}
						}
						sonItemList.remove(i);
						i = -1;
						lastItem = null;
					}
				}
				/*
				 * <,>,! .... !true&false)=false (5>2) && ((4+3-6) < 44)
				 */
				lastItem = null;
				for (int i = 0; i < sonItemList.size(); i++) {
					if (sonItemList.size() < 2)
						break;
					ItemCalculate sonItem = sonItemList.get(i);
					String sign = sonItem.getSign();

					if (NOT.equals(sonItem.getSign())) {
						if (!sonItem.isBoolean()) {
							throw new Exception("Error,'!' sign must map a boolean value.");
						}
						Boolean t = (Boolean) sonItem.getResult();
						sonItem.setResult(!t);
						sonItem.setSign(null);
					}

					if (isTestHightLeavl(sign) && lastItem != null) {

						Integer lRsti = null;
						Double lRstd = null;
						Boolean lRstb = null;

						Integer sRsti = null;
						Double sRstd = null;
						Boolean sRstb = null;

						if (lastItem.isInteger())
							lRsti = (Integer) lastItem.getResult();
						else if (lastItem.isDouble())
							lRstd = (Double) lastItem.getResult();
						else if (lastItem.isBoolean()) {
							lRstb = (Boolean) lastItem.getResult();
						}

						if (sonItem.isInteger())
							sRsti = (Integer) sonItem.getResult();
						else if (sonItem.isDouble())
							sRstd = (Double) sonItem.getResult();
						else if (sonItem.isBoolean()) {
							sRstb = (Boolean) sonItem.getResult();
						}

						if (BT.equals(sign)) {
							if (!lastItem.isBoolean() && !sonItem.isBoolean()) {
								if (lRsti != null && sRsti != null) {
									lastItem.setResult(lRsti.intValue() > sRsti.intValue());
								} else if (lRsti != null && sRstd != null) {
									lastItem.setResult(lRsti.intValue() > sRstd.doubleValue());
								} else if (lRstd != null && sRsti != null) {
									lastItem.setResult(lRstd.doubleValue() > sRsti.intValue());
								} else if (lRstd != null && sRstd != null) {
									lastItem.setResult(lRstd.doubleValue() > sRstd.doubleValue());
								}
							} else {
								throw new Exception("Error exp str, can not calculate boolean with number1.");
							}
						} else if (LT.equals(sign)) {
							if (!lastItem.isBoolean() && !sonItem.isBoolean()) {
								if (lRsti != null && sRsti != null) {
									lastItem.setResult(lRsti.intValue() < sRsti.intValue());
								} else if (lRsti != null && sRstd != null) {
									lastItem.setResult(lRsti.intValue() < sRstd.doubleValue());
								} else if (lRstd != null && sRsti != null) {
									lastItem.setResult(lRstd.doubleValue() < sRsti.intValue());
								} else if (lRstd != null && sRstd != null) {
									lastItem.setResult(lRstd.doubleValue() < sRstd.doubleValue());
								}
							} else {
								throw new Exception("Error exp str, can not calculate boolean with number2.");
							}
						} else if (BT_EQ.equals(sign)) {
							if (!lastItem.isBoolean() && !sonItem.isBoolean()) {
								if (lRsti != null && sRsti != null) {
									lastItem.setResult(lRsti.intValue() >= sRsti.intValue());
								} else if (lRsti != null && sRstd != null) {
									lastItem.setResult(lRsti.intValue() >= sRstd.doubleValue());
								} else if (lRstd != null && sRsti != null) {
									lastItem.setResult(lRstd.doubleValue() >= sRsti.intValue());
								} else if (lRstd != null && sRstd != null) {
									lastItem.setResult(lRstd.doubleValue() >= sRstd.doubleValue());
								}
							} else {
								throw new Exception("Error exp str, can not calculate boolean with number3.");
							}
						} else if (LT_EQ.equals(sign)) {
							if (!lastItem.isBoolean() && !sonItem.isBoolean()) {
								if (lRsti != null && sRsti != null) {
									lastItem.setResult(lRsti.intValue() <= sRsti.intValue());
								} else if (lRsti != null && sRstd != null) {
									lastItem.setResult(lRsti.intValue() <= sRstd.doubleValue());
								} else if (lRstd != null && sRsti != null) {
									lastItem.setResult(lRstd.doubleValue() <= sRsti.intValue());
								} else if (lRstd != null && sRstd != null) {
									lastItem.setResult(lRstd.doubleValue() <= sRstd.doubleValue());
								}
							} else {
								throw new Exception("Error exp str, can not calculate boolean with number4.");
							}
						} else if (EQ.equals(sign)) {
							if (!lastItem.isBoolean() && !sonItem.isBoolean()) {
								if (lRsti != null && sRsti != null) {
									lastItem.setResult(lRsti.intValue() == sRsti.intValue());
								} else if (lRsti != null && sRstd != null) {
									lastItem.setResult(lRsti.intValue() == sRstd.doubleValue());
								} else if (lRstd != null && sRsti != null) {
									lastItem.setResult(lRstd.doubleValue() == sRsti.intValue());
								} else if (lRstd != null && sRstd != null) {
									lastItem.setResult(lRstd.doubleValue() == sRstd.doubleValue());
								}
							} else if (lastItem.isBoolean() && sonItem.isBoolean()) {
								// System.out.println("**********"+lRstb.booleanValue()+"/"+sRstb.booleanValue());
								lastItem.setResult(lRstb.booleanValue() == sRstb.booleanValue());
							} else {
								throw new Exception("Error exp str, can not calculate boolean with number5.");
							}
						} else if (NOT_EQ.equals(sign)) {
							if (!lastItem.isBoolean() && !sonItem.isBoolean()) {
								if (lRsti != null && sRsti != null) {
									lastItem.setResult(lRsti.intValue() != sRsti.intValue());
								} else if (lRsti != null && sRstd != null) {
									lastItem.setResult(lRsti.intValue() != sRstd.doubleValue());
								} else if (lRstd != null && sRsti != null) {
									lastItem.setResult(lRstd.doubleValue() != sRsti.intValue());
								} else if (lRstd != null && sRstd != null) {
									lastItem.setResult(lRstd.doubleValue() != sRstd.doubleValue());
								}
							} else if (lastItem.isBoolean() && sonItem.isBoolean()) {
								lastItem.setResult(lRstb.booleanValue() != sRstb.booleanValue());
							} else {
								throw new Exception("Error exp str, can not calculate boolean with number6.");
							}
						}

						// --------
						sonItemList.remove(i);
						i = -1;
						lastItem = null;
						continue;
					}
					lastItem = sonItem;
				}

				/*
				 * &&,||
				 */
				lastItem = null;
				for (int i = 0; i < sonItemList.size(); i++) {
					if (sonItemList.size() < 2)
						break;
					ItemCalculate sonItem = sonItemList.get(i);
					String sign = sonItem.getSign();
					if (isTestSign(sign) && !isTestHightLeavl(sign) && lastItem != null) {
						Boolean lRstb = null;
						Boolean sRstb = null;
						if (lastItem.isBoolean()) {
							lRstb = (Boolean) lastItem.getResult();
						}
						if (sonItem.isBoolean()) {
							sRstb = (Boolean) sonItem.getResult();
						}
						if (AND.equals(sign)) {
							if (lastItem.isBoolean() && sonItem.isBoolean()) {
								// System.out.println("**********"+lRstb.booleanValue()+"/"+sRstb.booleanValue());
								lastItem.setResult(lRstb.booleanValue() == true && sRstb.booleanValue() == true);
							} else {
								throw new Exception(
										"Error exp str, can not calculate (boolean with number) or (number with number)7.");
							}
						} else if (OR.equals(sign)) {
							if (lastItem.isBoolean() && sonItem.isBoolean()) {
								lastItem.setResult(lRstb.booleanValue() == true || sRstb.booleanValue() == true);
							} else {
								throw new Exception(
										"Error exp str, can not calculate (boolean with number) or (number with number)8.");
							}
						}

						// --------
						sonItemList.remove(i);
						i = -1;
						lastItem = null;
						continue;
					}
					lastItem = sonItem;
				}
				// //////////////////////////////////////////////////

				ItemCalculate item = sonItemList.get(0);
				if (MIN.equals(item.getSign())) {
					if (item.isInteger()) {
						Integer res = getInteger(item.getResult());
						setResult(-res);
					} else if (item.isDouble()) {
						Double res = getDouble(item.getResult());
						setResult(-res);
					} else {
						throw new Exception("error1.");
					}
				} else if (NOT.equals(item.getSign())) {
					if (item.getResult() instanceof Boolean) {
						Boolean bool = (Boolean) item.getResult();
						setResult(!bool);
						// System.out.println("============="+item.getResult());
					} else {
						throw new Exception("error2.");
					}
				} else if (ADD.equals(item.getSign()) || item.getSign() == null) {
					setResult(item.getResult());
				} else {
					throw new Exception("error3.");
				}
			}

			@SuppressWarnings("unused")
			public ItemCalculate addItem(String sign, String expStr) {
				// System.out.println("item: sign='"+sign+"',
				// expStr='"+expStr+"'");
				ItemCalculate item = new ItemCalculate(sign, expStr);
				if (item == null)
					return null;
				if (sonItemList == null)
					sonItemList = new ArrayList<ItemCalculate>();
				sonItemList.add(item);
				return item;
			}

			public String getExpStr() {
				return expStr;
			}

			@SuppressWarnings("unused")
			public void setExpStr(String expStr) {
				this.expStr = expStr;
			}

			public String getSign() {
				return sign;
			}

			public void setSign(String sign) {
				this.sign = sign;
			}

			@SuppressWarnings("unused")
			public List<ItemCalculate> getSonItemList() {
				return sonItemList;
			}

			public Object getResult() {
				return result;
			}

			public void setResult(Object result) {
				this.result = result;
			}
		}

		private String replaceAll(String strVal, String o, String n) {
			if (strVal == null || o == null || n == null || "".equals(o))
				return strVal;

			int oLen = o.length();
			StringBuffer buf = new StringBuffer();
			int index = 0;
			int ind = strVal.indexOf(o);
			while (ind > -1) {
				buf.append(strVal.substring(index, ind));
				buf.append(n);
				index = ind + oLen;
				ind = strVal.indexOf(o, index);
			}
			buf.append(strVal.substring(index, strVal.length()));
			return buf.toString();
		}

		/**
		 * 7>4&!(5>1)=false --> 7>4&(!(5>1))=false 7>4&!true=false -->
		 * 7>4&(!true)=false 7>-4&-(4+5)>1 --> 7>(-4)&(-5)>1
		 * 
		 * @param expStr
		 * @return
		 */
		private String updateExpStr(String expStr) {
			String lastSign = null;
			StringBuffer buf = new StringBuffer();
			int index = 0;
			for (int i = 0; i < expStr.length(); i++) {
				String one = expStr.substring(i, i + 1);
				if (isSign(one) || isTestSign(one)) {
					if (lastSign == null) {
						lastSign = one;
						continue;
					}
					String tItem[] = getItemExpStr(expStr, i);
					tItem[0] = updateExpStr(tItem[0]);
					buf.append(expStr.substring(index, i));
					buf.append("(" + tItem[0] + ")");
					index = Integer.parseInt(tItem[1]);
				} else {
					lastSign = null;
				}
			}
			buf.append(expStr.substring(index));
			return buf.toString();
		}

		private String[] getItemExpStr(String expStr, int signInd) {
			int endInd = expStr.length();
			for (int i = signInd + 1; i < expStr.length(); i++) {
				String one = expStr.substring(i, i + 1);
				if (isLkuohao(one) && i == signInd + 1) {
					int rInd = getOtherKuohaoIndex(expStr, i);
					return new String[] { expStr.substring(signInd, rInd + 1), (rInd + 1) + "" };
				}
				if (isSign(one) || isTestSign(one) || isRkuohao(one) || isLkuohao(one)) {
					endInd = i;
					break;
				}
			}
			return new String[] { expStr.substring(signInd, endInd), endInd + "" };
		}

		/**
		 * init and check expression string
		 * 
		 * @param expStr
		 * @return
		 * @throws Exception
		 */
		private String initAndCheck(String expStr) throws Exception {
			if (expStr == null || expStr.trim().equals(""))
				throw new Exception("expression str error, is empty.");

			// to upper case
			expStr = expStr.toUpperCase();

			expStr = replaceAll(expStr, "!=", NOT_EQ);
			expStr = replaceAll(expStr, "<>", NOT_EQ);
			expStr = replaceAll(expStr, ">=", BT_EQ);
			expStr = replaceAll(expStr, "<=", LT_EQ);

			expStr = replaceAll(expStr, "&&", AND);
			expStr = replaceAll(expStr, "==", EQ);
			expStr = replaceAll(expStr, "||", OR);

			// remove " " string
			expStr = expStr.replaceAll(" ", "");

			// System.out.println("mid, expStr : "+expStr);
			expStr = updateExpStr(expStr);
			// System.out.println("end, expStr : "+expStr);

			// check "(",")"
			int lnum = 0;
			int rnum = 0;
			boolean lastIsSign = false;
			String lastSign = null;
			for (int i = 0; i < expStr.length(); i++) {
				String one = expStr.substring(i, i + 1);
				if (CHAE_L.equals(one))
					lnum++;
				if (CHAE_R.equals(one))
					rnum++;
				if (isSign(one) || isTestSign(one)) {
					if (lastIsSign)
						throw new Exception("expression str error, sign together (" + lastSign + one + ").");
					lastIsSign = true;
					lastSign = one;
				} else {
					lastIsSign = false;
					lastSign = "";
				}
			}
			if (rnum != lnum)
				throw new Exception("expression str error, '(',')' not matching.");

			return expStr;
		}

		private int getOtherKuohaoIndex(String expStr, int lkuoHaoIndex) {
			int rnum = 0;
			int lnum = 0;
			for (int i = lkuoHaoIndex + 1; i < expStr.length(); i++) {
				String one = expStr.substring(i, i + 1);
				if (isLkuohao(one)) {
					lnum++;
				} else if (isRkuohao(one)) {
					if (lnum == rnum)
						return i;
					rnum++;
				}
			}
			return -1;
		}

		private Double getDouble(Object val) throws Exception {
			if (val == null)
				throw new Exception("expression str error, Double null.");
			return new Double(val.toString());
		}

		private Boolean getBoolean(Object val) throws Exception {
			if (val == null)
				throw new Exception("expression str error, Boolean null.");
			return new Boolean(val.toString());
		}

		private Integer getInteger(Object val) throws Exception {
			if (val == null)
				throw new Exception("expression str error, Double null.");
			return new Integer(val.toString());
		}

		private boolean isHightLeavl(String val) {
			if (MUL.equals(val) || DIV.equals(val) || MOD.equals(val)) {
				return true;
			}
			return false;
		}

		private boolean isSign(String val) {
			if (ADD.equals(val) || MIN.equals(val) || MUL.equals(val) || DIV.equals(val) || MOD.equals(val)) {
				return true;
			}
			return false;
		}

		private boolean isTestHightLeavl(String val) {
			if (BT.equals(val) || LT.equals(val) || EQ.equals(val) || NOT_EQ.equals(val) || NOT.equals(val) || BT_EQ.equals(val)
					|| LT_EQ.equals(val)) {
				return true;
			}
			return false;
		}

		private boolean isTestSign(String val) {
			if (BT.equals(val) || LT.equals(val) || EQ.equals(val) || NOT_EQ.equals(val) || AND.equals(val) || OR.equals(val)
					|| NOT.equals(val) || BT_EQ.equals(val) || LT_EQ.equals(val)) {
				return true;
			}
			return false;
		}

		private boolean isLkuohao(String val) {
			if (CHAE_L.equals(val)) {
				return true;
			}
			return false;
		}

		private boolean isRkuohao(String val) {
			if (CHAE_R.equals(val)) {
				return true;
			}
			return false;
		}
	}

	public static void main(String[] args) {
		ExpressVo vo = createRandomExpres(1, 10);
		System.out.println(vo.getExpression());
		System.out.println(vo.getResult());

		Object o = 1.0d;
		System.out.println(String.valueOf(o.toString()));
		System.out.println("=============");
		System.out.println(eqIgnCase("1.0", o));
		System.out.println(equalsIgnoreCase("1.0", o.toString()));

		Pattern p = Pattern.compile(
				"^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
		System.out.println(p.matcher("36060219890331301X").find());

		String msg = "H {\"aaa\":\"bbb\"}";
		System.out.println(unpackingMessage(msg));

		System.out.println("============");
		System.out.println(isURL("https://mp.domain.com/mp/index"));
		System.out.println(isDomain("www.next.mp.domain.com"));
		System.out.println(isDomain("*.mp.domain.com"));
		System.out.println(isDomain("www.*.mp.domain.com"));
		System.out.println(isDomain("www..mp.domain.com"));
		System.out.println(isDomain("www.123-est.domain.com"));
		System.out.println(isDomain("www.123_est.domain.com"));
	}

}