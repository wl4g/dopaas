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
package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

/**
 * General check model.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class GenericCheckResult implements Serializable {

	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * General response key-name.
	 */
	final public static String KEY_GENERIC_CHECK = "checkGeneric";

	/**
	 * Applied secret public key hex.
	 */
	private String secret;

	/**
	 * Session keyname.
	 */
	private String sessionKey;

	/**
	 * Session value.
	 */
	private Serializable sessionValue;

	public GenericCheckResult() {
		super();
	}

	public GenericCheckResult(String secret, String sessionKey, Serializable sessionValue) {
		// hasTextOf(secret, "secret");
		setSecret(secret);
		setSessionKey(sessionKey);
		setSessionValue(sessionValue);
	}

	public String getSecret() {
		return secret;
	}

	public GenericCheckResult setSecret(String secret) {
		this.secret = secret;
		return this;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public GenericCheckResult setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
		return this;
	}

	public Serializable getSessionValue() {
		return sessionValue;
	}

	public GenericCheckResult setSessionValue(Serializable sessionValue) {
		this.sessionValue = sessionValue;
		return this;
	}

}