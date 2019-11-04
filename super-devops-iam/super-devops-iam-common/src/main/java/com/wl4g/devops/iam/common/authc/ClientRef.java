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
	 * Client android type.
	 */
	Android("Android"),

	/**
	 * Client iOS type.
	 */
	iOS("iOS"),

	/**
	 * Client MAC type.
	 */
	Mac("Mac"),

	/**
	 * Client iPad type.
	 */
	IPad("IPad"),

	/**
	 * Client WeChat official platform type.
	 */
	WeChatMp("wechatMp"),

	/**
	 * Client WeChat applet type.
	 */
	WeChatApplet("wechatApplet"),

	/**
	 * Client windows type.
	 */
	WINDOWS("Windows"),

	/**
	 * Client unknown type.
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