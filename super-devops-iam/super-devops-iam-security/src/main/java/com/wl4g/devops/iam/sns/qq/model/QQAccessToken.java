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
package com.wl4g.devops.iam.sns.qq.model;

import java.util.Map;

import org.apache.shiro.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.tool.common.lang.TypeConverts;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;
import com.wl4g.devops.tool.common.web.WebUtils2;

public class QQAccessToken implements Oauth2AccessToken {
	private static final long serialVersionUID = 6525294825751214763L;

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private Long expiresIn;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@Override
	public String accessToken() {
		return getAccessToken();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		Assert.notNull(accessToken, "'accessToken' must not be null");
		this.accessToken = accessToken;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		Assert.notNull(expiresIn, "'expiresIn' must not be null");
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		Assert.notNull(refreshToken, "'refreshToken' must not be null");
		this.refreshToken = refreshToken;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public QQAccessToken build(String message) {
		Map<String, String> params = WebUtils2.toQueryParams(message);
		setAccessToken(params.get("access_token"));
		setRefreshToken(params.get("refresh_token"));
		setExpiresIn(TypeConverts.parseLongOrNull(params.get("expires_in")));
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public QQAccessToken validate() {
		// TODO
		return this;
	}

}