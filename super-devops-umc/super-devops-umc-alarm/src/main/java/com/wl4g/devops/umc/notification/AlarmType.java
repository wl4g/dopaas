/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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

	EMAIL("1"), SMS("2"), WECHAT("3"), BARK("4"), DINGTALK("5"), FACEBOOK("6"), TWITTER("7");

	private String value;

	public String getValue() {
		return value;
	}

	AlarmType(String value) {
		this.value = value;
	}

	public static AlarmType safeOf(String type) {
		for (AlarmType t : values()) {
			if (StringUtils.equals(t.getValue(), type)) {
				return t;
			}
		}
		return null;
	}

}