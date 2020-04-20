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

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.exception.iam.SnsApiBindingException;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;

public class WxBasedAccessToken extends WxBasedResponse implements Oauth2AccessToken {
	private static final long serialVersionUID = 6525294825751214763L;

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private Long expiresIn;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("openid")
	private String openId;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("unionid")
	private String unionId;

	public WxBasedAccessToken() {
		super();
	}

	public WxBasedAccessToken(String accessToken, Long expiresIn, String refreshToken) {
		this.setAccessToken(accessToken);
		this.setExpiresIn(expiresIn);
		this.setRefreshToken(refreshToken);
	}

	@Override
	public String accessToken() {
		return getAccessToken();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		// Assert.notNull(accessToken, "'accessToken' must not be null");
		this.accessToken = accessToken;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		// Assert.notNull(expiresIn, "'expiresIn' must not be null");
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		// Assert.notNull(refreshToken, "'refreshToken' must not be null");
		this.refreshToken = refreshToken;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openid) {
		this.openId = openid;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxBasedAccessToken build(String message) {
		WxBasedAccessToken at = parseJSON(message, getClass());
		BeanUtils.copyProperties(at, this);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxBasedAccessToken validate() {
		if (getErrcode() != DEFAULT_WX_OK) {
			throw new SnsApiBindingException(format("[Assertion failed] - WeChat accessToken of %s", toString()));
		}
		return this;
	}

}