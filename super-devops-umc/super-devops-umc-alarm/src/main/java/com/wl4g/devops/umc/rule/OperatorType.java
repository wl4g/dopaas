package com.wl4g.devops.umc.rule;

/**
 * @author vjay
 * @date 2019-07-05 10:13:00
 */
public enum OperatorType {

	EQ(1), GT(2), GTE(3), LT(4), LTE(5);

	private int value;

	public int getValue() {
		return value;
	}

	OperatorType(int value) {
		this.value = value;
	}

	public static OperatorType safeOf(int operator) {
		for (OperatorType t : values()) {
			if (operator == (t.getValue())) {
				return t;
			}
		}
		return null;
	}

}
