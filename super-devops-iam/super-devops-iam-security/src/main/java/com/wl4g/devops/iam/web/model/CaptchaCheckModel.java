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

/**
 * CAPTCHA check model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class CaptchaCheckModel extends AuthenticationCodeModel {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * CAPTCHA check response key-name.
	 */
	final public static String KEY_CAPTCHA_CHECK = "checkCaptcha";

	/**
	 * Enable login CAPTCHA token for session.
	 */
	private boolean enabled;

	/**
	 * CAPTCHA verify type support.
	 */
	private String support;

	/**
	 * Apply CAPTCHA URL.
	 */
	private String applyUri;

	public CaptchaCheckModel() {
		super();
	}

	public CaptchaCheckModel(boolean enabled) {
		super();
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getApplyUri() {
		return applyUri;
	}

	public void setApplyUri(String applyUrl) {
		this.applyUri = applyUrl;
	}

}