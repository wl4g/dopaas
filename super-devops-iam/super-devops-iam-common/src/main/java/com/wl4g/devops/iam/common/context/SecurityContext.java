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
package com.wl4g.devops.iam.common.context;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

/**
 * IAM security context handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月10日
 * @since
 */
public interface SecurityContext {

	//
	// A U T H E N T I C A T I N G _ M E T H O D
	//

	/**
	 * Determine the URL of the login success redirection, default: successURL,
	 * can support customization.
	 * 
	 * @param successUrl
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 * @return
	 */
	String determineLoginSuccessUrl(String successUrl, AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response);

	/**
	 * Determine the URL of the login failure redirection, default: loginURL,
	 * can support customization.
	 * 
	 * @param loginUrl
	 * @param token
	 * @param ae
	 * @param request
	 * @param response
	 * @return
	 */
	String determineLoginFailureUrl(String loginUrl, AuthenticationToken token, AuthenticationException ae,
			ServletRequest request, ServletResponse response);

}