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
package com.wl4g.devops.iam.verification.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.util.Assert;

/**
 * Verify code based model, e.g. apply jigsaw CAPTCHA uuid.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-09-01
 * @since
 */
public abstract class AbstractVerifyCodeResult implements Serializable {
	private static final long serialVersionUID = -333742824989510195L;

	/**
	 * Applied CAPTCHA graph token.
	 */
	@NotBlank
	private String applyToken;

	/**
	 * Applied verification type.
	 */
	@NotBlank
	private String verifyType;

	public AbstractVerifyCodeResult() {
		super();
	}

	public AbstractVerifyCodeResult(@NotBlank String applyToken, @NotBlank String verifyType) {
		setApplyToken(applyToken);
		setVerifyType(verifyType);
	}

	public String getApplyToken() {
		return applyToken;
	}

	public void setApplyToken(String applyToken) {
		Assert.hasText(applyToken, "applyToken must not be empty");
		this.applyToken = applyToken;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		Assert.hasText(verifyType, "verifyType must not be empty");
		this.verifyType = verifyType;
	}

}