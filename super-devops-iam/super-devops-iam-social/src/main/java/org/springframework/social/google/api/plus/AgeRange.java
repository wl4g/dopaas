/**
 * Copyright 2011-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.google.api.plus;

/**
 * Enum representing a user's age range. Valid ranges are 17 or younger, 18 to
 * 20, and 21 or older.
 *
 * @author Michal Szwed
 */
public enum AgeRange {
	UNKNOWN(null, null), AGE_17_MINUS(null, 17), AGE_18_20(18, 20), AGE_21_PLUS(21, null);

	private Integer min;
	private Integer max;

	AgeRange(final Integer min, final Integer max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Constructs an AgeRange from the min/max age values.
	 *
	 * @param min
	 *            The minimum age
	 * @param max
	 *            The maximum age
	 * @return an AgeRange
	 */
	public static AgeRange fromMinMax(final Integer min, final Integer max) {
		if (min == null && max != null && max == 17) {
			return AGE_17_MINUS;
		} else if (min != null && min == 18 && max != null && max == 20) {
			return AGE_18_20;
		} else if (min != null && min == 21 && max == null) {
			return AGE_21_PLUS;
		}

		final AgeRange unknown = AgeRange.UNKNOWN;
		unknown.min = min;
		unknown.max = max;
		return unknown;
	}

	/**
	 * @return The minimum integer value for the range (possibly null).
	 */
	public Integer getMin() {
		return min;
	}

	/**
	 * @return The maximum integer value for the range (possibly null).
	 */
	public Integer getMax() {
		return max;
	}
}
