package com.wl4g.devops.umc.notification;

import org.apache.commons.lang3.StringUtils;

/**
 * Alarm type definition.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 10:13:00
 */
public enum AlarmType {

	EMAIL("1"), SMS("2"), WECHAT("3"), BARK("4");

	private String value;

	public String getValue() {
		return value;
	}

	AlarmType(String value) {
		this.value = value;
	}

	public static AlarmType safeOf(String type) {
		for (AlarmType t : values()) {
			if (StringUtils.equals(t.getValue(), t.getValue())) {
				return t;
			}
		}
		return null;
	}

}
