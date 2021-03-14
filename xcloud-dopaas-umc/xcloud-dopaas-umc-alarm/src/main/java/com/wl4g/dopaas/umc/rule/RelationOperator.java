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
package com.wl4g.dopaas.umc.rule;

/**
 * Relation operator definition.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 10:13:00
 */
public enum RelationOperator {

	EQ(1), GT(2), GTE(3), LT(4), LTE(5);

	private int value;

	public int getValue() {
		return value;
	}

	RelationOperator(int value) {
		this.value = value;
	}

	/**
	 * Do operation
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public boolean operate(double value1, double value2) {
		switch (of(getValue())) {
		case EQ:
			return value1 == value2;
		case GT:
			return value1 > value2;
		case GTE:
			return value1 >= value2;
		case LT:
			return value1 < value2;
		case LTE:
			return value1 <= value2;
		default:
			return false;
		}
	}

	/**
	 * Parse operator type of value.
	 * 
	 * @param operator
	 * @return
	 */
	public static RelationOperator of(int operator) {
		for (RelationOperator t : values()) {
			if (operator == t.getValue()) {
				return t;
			}
		}
		throw new UnsupportedOperationException(String.format("Unsupport operator(%d)", operator));
	}

}