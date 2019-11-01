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
package com.wl4g.devops.common.bean.iam;

import java.util.Map;

/**
 * Social and system account binding information
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月21日
 * @since
 */
public class SocialConnectInfo extends SocialAuthorizeInfo {
	private static final long serialVersionUID = -434937172743067451L;

	/**
	 * binding principal user
	 */
	private String principal;

	// Optional client information field.
	//
	private String userAgent; // Client name (e.g.browser name)
	private String clientIp; // Client IP
	private String referer; // Referer

	public SocialConnectInfo() {
		super();
	}

	public SocialConnectInfo(String provider, String principal, String openId) {
		this(provider, principal, openId, null);
	}

	public SocialConnectInfo(String provider, String principal, String openId, String unionId) {
		this(principal, provider, openId, unionId, null);
	}

	public SocialConnectInfo(String principal, SocialAuthorizeInfo info) {
		this(principal, info.getProvider(), info.getOpenId(), info.getUnionId(), info.getUserProfile());
	}

	public SocialConnectInfo(String principal, String provider, String openId, String unionId, Map<String, Object> userProfile) {
		super(provider, openId, unionId, userProfile);
		this.principal = principal;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

}