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
public class GeneralCheckModel implements Serializable {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * General PreCheck response key-name.
	 */
	final public static String KEY_GENERAL_CHECK = "checkGeneral";

	/**
	 * Encrypted secret public key requested before login returns.
	 */
	private String secret;

	public GeneralCheckModel() {
		super();
	}

	public GeneralCheckModel(String secret) {
		super();
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}