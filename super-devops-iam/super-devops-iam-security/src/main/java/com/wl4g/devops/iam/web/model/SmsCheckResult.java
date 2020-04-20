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
 * SMS verify check model.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class SmsCheckResult implements Serializable {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * SMS PreCheck response key-name.
	 */
	final public static String KEY_SMS_CHECK = "checkSms";

	/**
	 * Enable SMS login apply for session.
	 */
	private boolean enabled;

	/**
	 * Mobile number.
	 */
	private Long mobileNum;

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password.
	 */
	private Long remainDelayMs;

	public SmsCheckResult() {
		super();
	}

	public SmsCheckResult(Long mobileNum, Long remainDelayMs) {
		this(true, mobileNum, remainDelayMs);
	}

	public SmsCheckResult(boolean enabled, Long mobileNum, Long remainDelayMs) {
		super();
		this.enabled = enabled;
		this.mobileNum = mobileNum;
		this.remainDelayMs = remainDelayMs;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Long getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(Long mobileNum) {
		this.mobileNum = mobileNum;
	}

	public Long getRemainDelayMs() {
		return remainDelayMs;
	}

	public void setRemainDelayMs(Long remainDelayMs) {
		this.remainDelayMs = remainDelayMs;
	}

}