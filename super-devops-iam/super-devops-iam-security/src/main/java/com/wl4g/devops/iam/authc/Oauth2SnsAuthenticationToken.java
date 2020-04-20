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
package com.wl4g.devops.iam.authc;

import static org.springframework.util.Assert.notNull;

import com.wl4g.devops.common.bean.iam.SocialAuthorizeInfo;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken;

/**
 * Abstract SNS Oauth2 authentication token<br/>
 *
 * <font color=red>Note: Social network login does not require login
 * account(principal)</font>
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public abstract class Oauth2SnsAuthenticationToken extends AbstractIamAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	/**
	 * Social networking authorized information.
	 */
	final private SocialAuthorizeInfo social;

	public Oauth2SnsAuthenticationToken(final String remoteHost, final RedirectInfo redirectInfo,
			final SocialAuthorizeInfo social) {
		super(remoteHost, redirectInfo);
		notNull(social, "'social' must not be null");
		this.social = (social == null ? new SocialAuthorizeInfo() : social);
	}

	@Override
	final public Object getPrincipal() {
		return null; // Oauth2 login, no principal
	}

	@Override
	final public Object getCredentials() {
		return null; // Oauth2 login, no credentials
	}

	public SocialAuthorizeInfo getSocial() {
		return social;
	}

	@Override
	public String toString() {
		return "[social=" + social + "]";
	}

}