/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.authc.credential;

import java.util.List;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

import com.wl4g.devops.iam.authc.CaptchaAuthenticationToken;
import com.wl4g.devops.iam.authc.credential.secure.CredentialsToken;
import com.wl4g.devops.iam.handler.verification.Verification;

/**
 * General account credential matcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class GeneralCredentialsHashedMatcher extends AbstractAttemptsMatcher {

	public GeneralCredentialsHashedMatcher(Verification verification) {
		super(verification);
	}

	@Override
	public boolean doMatching(AuthenticationToken token, AuthenticationInfo info, List<String> factors) {
		CredentialsToken credentialsToken = new CredentialsToken((String) token.getPrincipal(), (String) token.getCredentials());
		return securer.validate(credentialsToken, info);
	}

	@Override
	protected void assertRequestVerify(AuthenticationToken token, String principal, List<String> factors) {
		if (token instanceof CaptchaAuthenticationToken) {
			verification.validate(factors, ((CaptchaAuthenticationToken) token).getCaptcha(), false);
		}
	}

}