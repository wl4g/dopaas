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

/**
 * Verify code based model, e.g. apply jigsaw CAPTCHA uuid.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-09-01
 * @since
 */
public abstract class VerifyCodeBasedModel implements Serializable {
	private static final long serialVersionUID = -333742824989510195L;

	private String applyUuid;

	public VerifyCodeBasedModel() {
		super();
	}

	public VerifyCodeBasedModel(String applyUuid) {
		super();
		this.applyUuid = applyUuid;
	}

	public String getApplyUuid() {
		return applyUuid;
	}

	public void setApplyUuid(String applyUuid) {
		this.applyUuid = applyUuid;
	}

}
