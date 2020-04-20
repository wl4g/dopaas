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
package com.wl4g.devops.iam.sns.wechat.model;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.exception.iam.SnsApiBindingException;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;

public class WxBasedOpenId extends WxBasedResponse implements Oauth2OpenId {
	private static final long serialVersionUID = 7684131680589315985L;

	@JsonProperty("openid")
	private String openId;

	@JsonProperty("unionid")
	private String unionId;

	public WxBasedOpenId() {
		super();
	}

	public WxBasedOpenId(String openId, String unionId) {
		super();
		this.setOpenId(openId);
		this.setUnionId(unionId);
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	@Override
	public String openId() {
		return this.getOpenId();
	}

	@Override
	public String unionId() {
		return this.getUnionId();
	}

	@Override
	public <O extends Oauth2OpenId> O build(String message) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxBasedOpenId validate() {
		if (getErrcode() != DEFAULT_WX_OK) {
			throw new SnsApiBindingException(format("[Assertion failed] - WeChat openid of %s", toString()));
		}
		return this;
	}

}