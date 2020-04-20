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

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;

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
	private String secretKey;

	public GenericCheckResult() {
		super();
	}

	public GenericCheckResult(String secretKey) {
		// hasTextOf(secretKey, "secretKey");
		setSecretKey(secretKey);
	}

	public String getSecretKey() {
		return secretKey;
	}

	public GenericCheckResult setSecretKey(String secretKey) {
		hasTextOf(secretKey, "secretKey");
		this.secretKey = secretKey;
		return this;
	}

}