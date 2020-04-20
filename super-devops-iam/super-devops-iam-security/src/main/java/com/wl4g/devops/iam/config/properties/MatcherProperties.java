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
package com.wl4g.devops.iam.config.properties;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Password matcher configuration properties
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class MatcherProperties implements InitializingBean, Serializable {
	private static final long serialVersionUID = -6194767776312196341L;

	// --- Credentials ---

	/**
	 * Maximum attempt request login count limit
	 */
	private int failFastMatchMaxAttempts = 10;

	/**
	 * Lock request waits for milliseconds after requesting authentication
	 * failure.
	 */
	private long failFastMatchDelay = 60 * 60 * 1000L;

	// --- CAPTCHA ---

	/**
	 * Continuous match error begins the maximum attempt to enable the
	 * verification code.
	 */
	private int enabledCaptchaMaxAttempts = 3;

	/**
	 * Apply CAPTCHA graph token name.
	 */
	private String applyGraphTokenName = "graphToken";

	/**
	 * Maximum number of consecutive attempts to request an graph verification
	 * code.
	 */
	private int failFastCaptchaMaxAttempts = 20;

	/**
	 * The millisecond of lock wait after requesting CAPTCHA authentication
	 * fails.
	 */
	private long failFastCaptchaDelay = 10 * 60 * 1000L;

	/**
	 * The graph verification code requesting the application expires in
	 * milliseconds.
	 */
	private long captchaExpireMs = 1 * 60 * 1000L;

	// --- SMS ---

	/**
	 * Try to apply for the maximum number of SMS dynamic passwords multiple
	 * times (it will be locked for a while after it is exceeded).
	 */
	private int failFastSmsMaxAttempts = 3;

	/**
	 * The length of time (in milliseconds) that will be locked after trying to
	 * apply for the maximum number of SMS dynamic passwords multiple times.
	 * Reference: failFastSmsMaxAttempts.
	 */
	private long failFastSmsMaxDelay = 30 * 60 * 1000L;

	/**
	 * The number of milliseconds to wait after applying for an SMS dynamic
	 * password (you can reapply).
	 */
	private long failFastSmsDelay = (long) (1.5 * 60 * 1000L);

	/**
	 * Apply for SMS dynamic password every time, valid for authentication
	 * (milliseconds).
	 */
	private long smsExpireMs = 5 * 60 * 1000L;

	public int getFailFastMatchMaxAttempts() {
		return failFastMatchMaxAttempts;
	}

	public void setFailFastMatchMaxAttempts(int failureMaxAttempts) {
		Assert.isTrue(failureMaxAttempts > 0, "failureMaxAttempts code expiration time must be greater than 0");
		this.failFastMatchMaxAttempts = failureMaxAttempts;
	}

	public long getFailFastMatchDelay() {
		return failFastMatchDelay;
	}

	public void setFailFastMatchDelay(long failureDelaySecond) {
		this.failFastMatchDelay = failureDelaySecond;
	}

	public int getEnabledCaptchaMaxAttempts() {
		return enabledCaptchaMaxAttempts;
	}

	public void setEnabledCaptchaMaxAttempts(int enabledCaptchaMaxAttempts) {
		this.enabledCaptchaMaxAttempts = enabledCaptchaMaxAttempts;
	}

	public String getApplyGraphTokenName() {
		return applyGraphTokenName;
	}

	public void setApplyGraphTokenName(String applyGraphTokenName) {
		this.applyGraphTokenName = applyGraphTokenName;
	}

	public int getFailFastCaptchaMaxAttempts() {
		return failFastCaptchaMaxAttempts;
	}

	public void setFailFastCaptchaMaxAttempts(int failFastCaptchaMaxAttempts) {
		this.failFastCaptchaMaxAttempts = failFastCaptchaMaxAttempts;
	}

	public long getFailFastCaptchaDelay() {
		return failFastCaptchaDelay;
	}

	public void setFailFastCaptchaDelay(long failFastCaptchaDelay) {
		this.failFastCaptchaDelay = failFastCaptchaDelay;
	}

	public long getCaptchaExpireMs() {
		return captchaExpireMs;
	}

	public void setCaptchaExpireMs(long captchaExpireMs) {
		Assert.isTrue(captchaExpireMs > 0, "Verification code expiration time must be greater than 0");
		this.captchaExpireMs = captchaExpireMs;
	}

	public int getFailFastSmsMaxAttempts() {
		return failFastSmsMaxAttempts;
	}

	public void setFailFastSmsMaxAttempts(int captchaRequiredAttempts) {
		// Assert.isTrue((captchaRequiredAttempts > 0 &&
		// captchaRequiredAttempts < this.getFailFastMatchMaxAttempts()),
		// String.format(
		// "'captchaRequiredAttempts':%s should be must be greater than 0 or
		// less than 'failureMaxAttempts':%s",
		// captchaRequiredAttempts, getFailFastMatchMaxAttempts()));
		this.failFastSmsMaxAttempts = captchaRequiredAttempts;
	}

	public long getFailFastSmsMaxDelay() {
		return failFastSmsMaxDelay;
	}

	public void setFailFastSmsMaxDelay(long failFastSmsMaxDelay) {
		this.failFastSmsMaxDelay = failFastSmsMaxDelay;
	}

	public long getFailFastSmsDelay() {
		return failFastSmsDelay;
	}

	public void setFailFastSmsDelay(long failFastSmsDelay) {
		this.failFastSmsDelay = failFastSmsDelay;
	}

	public long getSmsExpireMs() {
		return smsExpireMs;
	}

	public void setSmsExpireMs(long smsExpireMs) {
		this.smsExpireMs = smsExpireMs;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Assert.isTrue(getFailFastSmsMaxAttempts() <
		// getFailFastMatchMaxAttempts(),
		// "failVerifyMaxAttempts must be less than failLockMaxAttempts");
	}

}