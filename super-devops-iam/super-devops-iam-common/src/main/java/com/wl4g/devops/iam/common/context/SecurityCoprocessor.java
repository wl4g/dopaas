/*
 * Copyright 2015 the original author or authors.
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

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

/**
 * IAM security coprocessor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月5日
 * @since
 */
public interface SecurityCoprocessor {

	/**
	 * Call pre before authentication, For example, the implementation of
	 * restricting client IP white-list to prevent violent cracking of large
	 * number of submission login requests.
	 * 
	 * @param filter
	 * @param request
	 * @param response
	 * @return
	 */
	default boolean preAuthentication(Filter filter, ServletRequest request, ServletResponse response) {
		return true;
	}

	/**
	 * Call post of login success
	 * 
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 */
	default void postLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) {
	}

	/**
	 * Call post login failure
	 * 
	 * @param token
	 * @param ae
	 * @param request
	 * @param response
	 */
	default void postLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
			ServletResponse response) {
	}

	/**
	 * Call before logout
	 * 
	 * @param forced
	 * @param request
	 * @param response
	 */
	default void preLogout(boolean forced, ServletRequest request, ServletResponse response) {
	}

}