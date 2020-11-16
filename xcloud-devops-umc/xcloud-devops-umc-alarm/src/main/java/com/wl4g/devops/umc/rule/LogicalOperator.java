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
package com.wl4g.devops.umc.rule;

/**
 * Logical operator definition.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月30日
 * @since
 */
public enum LogicalOperator {

	AND(1), OR(2);

	private int value;

	public int getValue() {
		return value;
	}

	LogicalOperator(int value) {
		this.value = value;
	}

	/**
	 * Parse operator type of value.
	 * 
	 * @param operator
	 * @return
	 */
	public static LogicalOperator of(int operator) {
		for (LogicalOperator t : values()) {
			if (operator == t.getValue()) {
				return t;
			}
		}
		throw new UnsupportedOperationException(String.format("Unsupport operator(%d)", operator));
	}

}