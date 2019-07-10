package com.wl4g.devops.umc.rule;

/**
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

	public static AggregatorType safeOf(String operator) {
		for (AggregatorType t : values()) {
			if (operator == (t.getValue())) {
				return t;
			}
		}
		return null;
	}
}
