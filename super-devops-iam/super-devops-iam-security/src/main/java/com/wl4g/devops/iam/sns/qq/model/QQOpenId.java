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

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;

import org.apache.shiro.util.Assert;

import com.wl4g.devops.iam.sns.support.Oauth2OpenId;

public class QQOpenId implements Oauth2OpenId {
	private static final long serialVersionUID = 7990021511401902830L;

	private String client_id;
	private String openid;

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public String openId() {
		return getOpenid();
	}

	@Override
	public String unionId() {
		// throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public String toString() {
		return "OpenId [" + (client_id != null ? "client_id=" + client_id + ", " : "")
				+ (openid != null ? "openid=" + openid : "") + "]";
	}

	@SuppressWarnings("unchecked")
	public QQOpenId build(String message) {
		Assert.notNull(message, "'message' must not be null");
		return parseJSON(cleanStringToken(message), QQOpenId.class);
	}

	private String cleanStringToken(String msg) {
		// callback(
		// {"client_id":"101525381","openid":"6725F3D4CCC904450110FBE01D4A6667"}
		// );
		String cleanPrefix = msg.substring(9, msg.length());
		return cleanPrefix.substring(0, cleanPrefix.length() - 3);
	}

	@SuppressWarnings("unchecked")
	@Override
	public QQOpenId validate() {
		// TODO
		return this;
	}

}