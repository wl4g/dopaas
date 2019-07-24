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
	public static OperatorType of(int operator) {
		for (OperatorType t : values()) {
			if (operator == t.getValue()) {
				return t;
			}
		}
		throw new UnsupportedOperationException(String.format("Unsupport operator(%d)", operator));
	}

}
