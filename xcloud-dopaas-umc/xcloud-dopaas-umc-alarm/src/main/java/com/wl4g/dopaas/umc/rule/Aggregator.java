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

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Aggregate type definition.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 19:13:00
 */
public enum Aggregator {

	AVG("avg"), LATEST("latest"), MAX("max"), MIN("min"), SUM("sum");

	private String value;

	public String getValue() {
		return value;
	}

	Aggregator(String value) {
		this.value = value;
	}

	/**
	 * Parse aggregate type of operator string.
	 * 
	 * @param aggregateString
	 * @return
	 */
	public static Aggregator safeOf(String aggregateString) {
		for (Aggregator t : values()) {
			if (equalsIgnoreCase(aggregateString, t.getValue())) {
				return t;
			}
		}
		throw new UnsupportedOperationException(String.format("Unsupport Aggregator(%d)", aggregateString));
	}

}