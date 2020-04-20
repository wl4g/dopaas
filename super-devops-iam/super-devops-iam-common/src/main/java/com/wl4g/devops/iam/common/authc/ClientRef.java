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
package com.wl4g.devops.iam.common.authc;

/**
 * IAM authentication client reference type definition.
 *
 * @author wangl.sir
 * @version v1.0 2019年3月8日
 * @since
 */
public enum ClientRef {

	/**
	 * Client android platform.
	 */
	Android("Android"),

	/**
	 * Client iOS platform.
	 */
	iOS("iOS"),

	/**
	 * Client iPad platform.
	 */
	IPad("IPad"),

	/**
	 * Client WeChat official platform.
	 */
	WeChatMp("wechatMp"),

	/**
	 * Client WeChat applet platform.
	 */
	WeChatApplet("wechatApplet"),

	/**
	 * Client windows platform.
	 */
	Windows("Windows"),

	/**
	 * Client MAC platform.
	 */
	Mac("Mac"),

	/**
	 * Client linux platform.
	 */
	Linux("Linux"),

	/**
	 * Client SunOS platform.
	 */
	SunOS("SunOS"),

	/**
	 * Client FreeBSD platform.
	 */
	FreeBSD("FreeBSD"),

	/**
	 * Client OpenBSD platform.
	 */
	OpenBSD("OpenBSD"),

	/**
	 * Client Solaris platform.
	 */
	Solaris("Solaris"),

	/**
	 * Client AIX platform.
	 */
	AIX("AIX"),

	/**
	 * Client Irix platform.
	 */
	Irix("Irix"),

	/**
	 * Client HP-UX platform.
	 */
	HP_UX("HP-UX"),

	/**
	 * Client unknown platform.
	 */
	Unknown("Unknown");

	final private String value;

	private ClientRef(String value) {
		this.value = value;
	}

	final public String getValue() {
		return value;
	}

	final public static ClientRef of(String clientRef) {
		ClientRef ref = safeOf(clientRef);
		if (ref == null) {
			throw new IllegalArgumentException(String.format("Illegal clientRef '%s'", clientRef));
		}
		return ref;
	}

	final public static ClientRef safeOf(String clientRef) {
		for (ClientRef ref : values()) {
			if (ref.getValue().equalsIgnoreCase(clientRef) || ref.name().equalsIgnoreCase(clientRef)) {
				return ref;
			}
		}
		return null;
	}

}