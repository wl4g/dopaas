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

import com.google.common.annotations.Beta;

/**
 * IAM security listener
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月18日
 * @since
 */
@Beta
public interface SecurityListener {

	/**
	 * Post-handling of login success
	 * 
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 */
	void onPostLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response);

	/**
	 * Post-handling of login failure
	 * 
	 * @param token
	 * @param ae
	 * @param request
	 * @param response
	 */
	void onPostLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
			ServletResponse response);

	/**
	 * Listener before logout
	 * 
	 * @param forced
	 * @param request
	 * @param response
	 */
	void onPreLogout(boolean forced, ServletRequest request, ServletResponse response);

}