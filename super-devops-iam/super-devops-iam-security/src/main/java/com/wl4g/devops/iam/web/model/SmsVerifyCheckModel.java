/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
 * @version v1.0 2019-04-22
 * @since
 */
public class SmsVerifyCheckModel implements Serializable {
	private static final long serialVersionUID = 4417187214009227040L;

	/**
	 * SMS PreCheck response key-name.
	 */
	final public static String KEY_SMS_CHECK_NAME = "checkSms";

	/**
	 * Apply SMS verification code to create a time-stamp .
	 */
	private Long createTime;

	/**
	 * The number of milliseconds to wait after applying for an SMS dynamic
	 * password (you can reapply) .
	 */
	private Long delayMs;

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password.
	 */
	private Long remainDelayMs;

	public SmsVerifyCheckModel() {
		super();
	}

	public SmsVerifyCheckModel(Long createTime, Long delayMs, Long remainDelayMs) {
		super();
		this.createTime = createTime;
		this.delayMs = delayMs;
		this.remainDelayMs = remainDelayMs;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getDelayMs() {
		return delayMs;
	}

	public void setDelayMs(Long delayMs) {
		this.delayMs = delayMs;
	}

	public Long getRemainDelayMs() {
		return remainDelayMs;
	}

	public void setRemainDelayMs(Long remainDelayMs) {
		this.remainDelayMs = remainDelayMs;
	}

}
