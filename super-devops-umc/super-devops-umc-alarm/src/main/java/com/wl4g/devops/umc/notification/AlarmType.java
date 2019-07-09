package com.wl4g.devops.umc.notification;

/**
 * @author vjay
 * @date 2019-07-05 10:13:00
 */
public enum AlarmType {

	EMAIL(1), SMS(2), WECHAT(3);

	private int value;

	public int getValue() {
		return value;
	}

	AlarmType(int value) {
		this.value = value;
	}

	public static AlarmType safeOf(int operator) {
		for (AlarmType t : values()) {
			if (operator == (t.getValue())) {
				return t;
			}
		}
		return null;
	}

}
