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
package com.wl4g.devops.iam.captcha.gif.model;

import javax.validation.constraints.NotBlank;

import com.wl4g.devops.iam.verification.model.GenericVerifyResult;

/**
 * GIF simple image model
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月4日
 * @since
 */
public class GifVerifyImgModel extends GenericVerifyResult {
	private static final long serialVersionUID = -5475719110868579286L;

	public GifVerifyImgModel() {
		super();
	}

	public GifVerifyImgModel(@NotBlank String applyToken, @NotBlank String verifyType) {
		setApplyToken(applyToken);
		setVerifyType(verifyType);
	}

}