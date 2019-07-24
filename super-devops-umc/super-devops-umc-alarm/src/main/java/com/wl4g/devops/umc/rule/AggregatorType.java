package com.wl4g.devops.umc.rule;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Aggregate type definition.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 19:13:00
 */
public enum AggregatorType {

	AVG("avg"), LAST("last"), MAX("max"), MIN("min"), SUM("sum");

	private String value;

	public String getValue() {
		return value;
	}

	AggregatorType(String value) {
		this.value = value;
	}

	/**
	 * Parse aggregate type of operator string.
	 * 
	 * @param aggregateString
	 * @return
	 */
	public static AggregatorType of(String aggregateString) {
		for (AggregatorType t : values()) {
			if (equalsIgnoreCase(aggregateString, t.getValue())) {
				return t;
			}
		}
		throw new UnsupportedOperationException(String.format("Unsupport aggregate operator(%d)", aggregateString));
	}
}
