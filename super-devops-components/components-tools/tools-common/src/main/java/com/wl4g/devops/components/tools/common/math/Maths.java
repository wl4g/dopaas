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
package com.wl4g.devops.components.tools.common.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Simple math utility {@link Maths} tools.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月4日 v1.0.0
 * @see
 */
public abstract class Maths {
	/** Default division accuracy */
	final private static int DEFAULT_DIV_SCALE = 2;

	// --- Basic mathematical function's. ---

	/**
	 * Exact addition
	 * 
	 * @param v1
	 *            Augend
	 * @param v2
	 *            Addition number
	 * @param scale
	 *            The representation needs to be accurate to several decimal
	 *            places
	 * @return The sum of two parameters (BigDecimal)
	 */
	public static BigDecimal add(double v1, double v2) {
		return add(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * Exact addition
	 * 
	 * @param v1
	 *            Augend
	 * @param v2
	 *            Addition number
	 * @param scale
	 *            The representation needs to be accurate to several decimal
	 *            places
	 * @return The sum of two parameters (BigDecimal)
	 */
	public static BigDecimal add(double v1, double v2, int scale) {
		return add(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2)).setScale(scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * Exact addition
	 * 
	 * @param v1
	 *            Augend
	 * @param v2
	 *            Addition number
	 * @return The sum of two parameters (BigDecimal)
	 */
	public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
		if (null == v1) {
			v1 = BigDecimal.ZERO;
		}
		if (null == v2) {
			v2 = BigDecimal.ZERO;
		}
		return v1.add(v2);
	}

	/**
	 * Exact subtraction
	 * 
	 * @param v1
	 *            Minuend
	 * @param v2
	 *            Reduction
	 * @param scale
	 *            The representation needs to be accurate to several decimal
	 * @return Difference between two parameters (BigDecimal)
	 */
	public static BigDecimal subtract(double v1, double v2) {
		return subtract(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * Exact subtraction
	 * 
	 * @param v1
	 *            Minuend
	 * @param v2
	 *            Reduction
	 * @param scale
	 *            The representation needs to be accurate to several decimal
	 * @return Difference between two parameters (BigDecimal)
	 */
	public static BigDecimal subtract(double v1, double v2, int scale) {
		return subtract(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2)).setScale(scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * Exact subtraction
	 * 
	 * @param v1
	 *            Minuend
	 * @param v2
	 *            Reduction
	 * @return Difference between two parameters (BigDecimal)
	 */
	public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
		if (null == v1) {
			v1 = BigDecimal.ZERO;
		}
		if (null == v2) {
			v2 = BigDecimal.ZERO;
		}
		return v1.subtract(v2);
	}

	/**
	 * Exact multiplication
	 * 
	 * @param v1
	 *            multiplicand
	 * @param v2
	 *            multiplier
	 * @return Product of two parameters (BigDecimal)
	 */
	public static BigDecimal multiply(double v1, double v2) {
		return multiply(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2));
	}

	/**
	 * Exact multiplication
	 * 
	 * @param v1
	 *            multiplicand
	 * @param v2
	 *            multiplier
	 * @return Product of two parameters (BigDecimal)
	 */
	public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
		if (null == v1) {
			v1 = BigDecimal.ONE;
		}
		if (null == v2) {
			v2 = BigDecimal.ONE;
		}
		return v1.multiply(v2);
	}

	/**
	 * (relative) accurate division operation. In case of inexhaustible
	 * division, it shall be accurate to 2 digits after the decimal point, and
	 * then the number shall be rounded
	 * 
	 * @param v1
	 *            dividend
	 * @param v2
	 *            divisor
	 * @return Quotient of two parameters (BigDecimal)
	 */
	public static BigDecimal divide(double v1, double v2) {
		return divide(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * (relative) accurate division operation. In case of inexhaustible
	 * division, it shall be accurate to 2 digits after the decimal point, and
	 * then the number shall be rounded
	 * 
	 * @param v1
	 *            dividend
	 * @param v2
	 *            divisor
	 * @param scale
	 *            The representation needs to be accurate to several decimal
	 * 
	 * @return Quotient of two parameters (BigDecimal)
	 */
	public static BigDecimal divide(double v1, double v2, int scale) {
		return divide(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2));
	}

	/**
	 * (relative) accurate division operation. In case of inexhaustible
	 * division, it shall be accurate to 2 digits after the decimal point, and
	 * then the number shall be rounded
	 * 
	 * @param v1
	 *            dividend
	 * @param v2
	 *            divisor
	 * @return Quotient of two parameters (BigDecimal)
	 */
	public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
		return divide(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 
	 * (relative) precise division operation. In case of incomplete division,
	 * the scale parameter specifies the precision, and then the number is
	 * rounded
	 * 
	 * @param v1
	 *            dividend
	 * @param v2
	 *            divisor
	 * @param scale
	 *            The representation needs to be accurate to several decimal
	 *            places
	 * @return Quotient of two parameters (BigDecimal)
	 */
	public static BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {
		if (null == v1) {
			return BigDecimal.ZERO;
		}
		if (null == v2) {
			v2 = BigDecimal.ONE;
		}

		if (v2.compareTo(BigDecimal.ZERO) == 0) {
			throw new IllegalArgumentException("除数不能为0");
		}

		if (scale < 0) {
			throw new IllegalArgumentException("精确度不能小于0");
		}

		return v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
	}

	// --- String mathematical function's ---

	/**
	 * Exact addition
	 * 
	 * @param v1
	 *            Augend
	 * @param v2
	 *            Addition number
	 * @return The sum of two parameters (string)
	 */
	public static String add(String v1, String v2) {
		if (isBlank0(v1)) {
			v1 = "0";
		}
		if (isBlank0(v2)) {
			v2 = "0";
		}
		BigDecimal b1 = new BigDecimal(v1.trim());
		BigDecimal b2 = new BigDecimal(v2.trim());
		return String.valueOf(add(b1, b2));
	}

	/**
	 * Exact subtraction
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差(String)
	 */
	public static String subtract(String v1, String v2) {
		if (isBlank0(v1)) {
			v1 = "0";
		}
		if (isBlank0(v2)) {
			v2 = "0";
		}
		BigDecimal b1 = new BigDecimal(v1.trim());
		BigDecimal b2 = new BigDecimal(v2.trim());
		return String.valueOf(subtract(b1, b2));
	}

	/**
	 * Exact multiplication
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积(String)
	 */
	public static String multiply(String v1, String v2) {
		if (isBlank0(v1)) {
			v1 = "1";
		}
		if (isBlank0(v2)) {
			v2 = "1";
		}
		BigDecimal b1 = new BigDecimal(v1.trim());
		BigDecimal b2 = new BigDecimal(v2.trim());
		return String.valueOf(multiply(b1, b2));
	}

	/**
	 * (relative) accurate division operation. In case of inexhaustible
	 * division, it shall be accurate to 2 digits after the decimal point, and
	 * then the number shall be rounded
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商(String)
	 */
	public static String divide(String v1, String v2) {
		return divide(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * (relative) precise division operation. In case of incomplete division,
	 * the scale parameter specifies the precision, and then the number is
	 * rounded
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位
	 * @return 两个参数的商(String)
	 */
	public static String divide(String v1, String v2, int scale) {
		if (null == v1) {
			return "0";
		}
		if (null == v2) {
			v2 = "1";
		}
		BigDecimal b1 = new BigDecimal(v1.trim());
		BigDecimal b2 = new BigDecimal(v2.trim());
		return String.valueOf(divide(b1, b2, scale));
	}

	// --- Array mathematical function's. ---

	/**
	 * Precise addition operation to calculate the sum of multiple values. If
	 * there is a null value, it will be ignored
	 * 
	 * @param valList
	 *            Addend set
	 * @return The sum of two parameters (BigDecimal)
	 */
	public static BigDecimal sum(BigDecimal v1, BigDecimal... valList) {
		if (null == v1) {
			v1 = BigDecimal.ZERO;
		}
		if (null == valList || valList.length == 0) {
			return v1;
		}
		for (BigDecimal val : valList) {
			if (null != val) {
				v1 = v1.add(val);
			}
		}
		return v1;
	}

	/**
	 * Precise addition operation to calculate the sum of multiple values. If
	 * there is a null value, it will be ignored
	 * 
	 * @param valList
	 *            Addend set
	 * @return The sum of two parameters (string)
	 */
	public static String sum(String v1, String... valList) {
		if (isBlank0(v1)) {
			v1 = "0";
		}
		if (null == valList || valList.length == 0) {
			return v1;
		}
		BigDecimal b1 = new BigDecimal(v1.trim());
		for (String val : valList) {
			if (!isBlank0(val)) {
				b1 = add(b1, new BigDecimal(val.trim()));
			}
		}
		return String.valueOf(b1);
	}

	/**
	 * Get average
	 * 
	 * @param valList
	 * @return
	 */
	public static BigDecimal avg(BigDecimal... valList) {
		if (null != valList && valList.length != 0) {
			return divide(sum(BigDecimal.ZERO, valList), new BigDecimal(valList.length));
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Get average
	 * 
	 * @param valList
	 * @return
	 */
	public static String avg(String... valList) {
		if (null != valList && valList.length != 0) {
			return divide(sum("0", valList), String.valueOf(valList.length));
		}
		return "0";
	}

	/**
	 * Get maximum
	 * 
	 * @param v1
	 * @param valList
	 * @return
	 */
	public static BigDecimal max(BigDecimal v1, BigDecimal... valList) {
		BigDecimal max = v1;
		if (null == valList || valList.length == 0) {
			return max;
		}
		for (BigDecimal val : valList) {
			if (null != val && val.compareTo(max) > 0) {
				max = val;
			}
		}
		return max;
	}

	/**
	 * Get maximum
	 * 
	 * @param valList
	 * @return
	 */
	public static BigDecimal maxArr(BigDecimal... valList) {
		if (null == valList || valList.length == 0) {
			return null;
		}

		return max(valList[0], valList);
	}

	/**
	 * Get minimum
	 * 
	 * @param v1
	 * @param valList
	 * @return
	 */
	public static BigDecimal min(BigDecimal v1, BigDecimal... valList) {
		BigDecimal min = v1;
		if (null == valList || valList.length == 0) {
			return min;
		}
		for (BigDecimal val : valList) {
			if (null != val && val.compareTo(min) < 0) {
				min = val;
			}
		}
		return min;
	}

	/**
	 * Get minimum
	 * 
	 * @param valList
	 * @return
	 */
	public static BigDecimal minArr(BigDecimal... valList) {
		if (null == valList || valList.length == 0) {
			return null;
		}
		return min(valList[0], valList);
	}

	/**
	 * Get maximum
	 * 
	 * @param v1
	 * @param valList
	 * @return
	 */
	public static String max(String v1, String... valList) {
		if (isBlank0(v1)) {
			return null;
		}
		if (null == valList || valList.length == 0) {
			return v1;
		}
		BigDecimal maxBd = new BigDecimal(v1.trim());

		for (String val : valList) {
			if (!isBlank0(val) && new BigDecimal(val).compareTo(maxBd) > 0) {
				maxBd = new BigDecimal(val);
			}
		}
		return String.valueOf(maxBd);
	}

	/**
	 * Get maximum
	 * 
	 * @param valList
	 * @return
	 */
	public static String maxArr(String... valList) {
		if (null == valList || valList.length == 0) {
			return null;
		}
		return max(valList[0], valList);
	}

	/**
	 * Get minimum
	 * 
	 * @param v1
	 * @param valList
	 * @return
	 */
	public static String min(String v1, String... valList) {
		if (isBlank0(v1)) {
			return null;
		}
		if (null == valList || valList.length == 0) {
			return v1;
		}
		BigDecimal minBd = new BigDecimal(v1.trim());

		for (String val : valList) {
			if (!isBlank0(val) && new BigDecimal(val).compareTo(minBd) < 0) {
				minBd = new BigDecimal(val);
			}
		}
		return String.valueOf(minBd);
	}

	/**
	 * Get minimum
	 * 
	 * @param valList
	 * @return
	 */
	public static String minArr(String... valList) {
		if (null == valList || valList.length == 0) {
			return null;
		}
		return min(valList[0], valList);
	}

	/**
	 * Judge whether the string is empty (independent of the third party)
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isBlank0(String str) {
		return null == str || str.trim().length() == 0;
	}

}