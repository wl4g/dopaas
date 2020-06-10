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
package com.wl4g.devops.iam.common.configure;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

import com.google.common.annotations.Beta;

/**
 * IAM security configure handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月10日
 * @since
 */
@Beta
public interface SecurityConfigurer {

	//
	// A U T H E N T I C A T I N G _ M E T H O D
	//

	/**
	 * Decorate the URL of the authenticating success redirection, default:
	 * successURL, can support customization.
	 *
	 * @param successUrl
	 *            Authenticated success redirect URL.
	 * @param token
	 *            Authenticating token.
	 * @param subject
	 *            Security context {@link Subject}
	 * @param request
	 * @param response
	 * @return
	 */
	default String decorateAuthenticateSuccessUrl(String successUrl, AuthenticationToken token, Subject subject,
			ServletRequest request, ServletResponse response) {
		return successUrl;
	}

	/**
	 * Decorate the URL of the authenticating failure redirection, default:
	 * loginURL, can support customization.
	 *
	 * @param loginUrl
	 *            Login page URL.
	 * @param token
	 *            Authenticating token.
	 * @param ae
	 *            Authenticating failure expection.
	 * @param request
	 * @param response
	 * @return
	 */
	default String decorateAuthenticateFailureUrl(String loginUrl, AuthenticationToken token, Throwable ae,
			ServletRequest request, ServletResponse response) {
		return loginUrl;
	}

}