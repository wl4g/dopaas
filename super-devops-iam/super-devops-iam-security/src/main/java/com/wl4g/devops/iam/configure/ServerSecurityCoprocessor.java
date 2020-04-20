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
package com.wl4g.devops.iam.configure;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;

import com.google.common.annotations.Beta;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.configure.SecurityCoprocessor;

/**
 * IAM server security coprocessor
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月5日
 * @since
 */
@Beta
public interface ServerSecurityCoprocessor extends SecurityCoprocessor {

	/**
	 * Preprocessing apply captcha handle
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	default boolean preApplyCapcha(ServletRequest request, ServletResponse response) {
		return true;
	}

	/**
	 * Preprocessing apply SMS verifyCode handle
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	default boolean preApplySmsCode(ServletRequest request, ServletResponse response) {
		return true;
	}

	/**
	 * Preprocessing whether the generic authenticating check match is allowed.
	 *
	 * @param token
	 * @param info
	 * @return
	 */
	default boolean preAuthenticatingAllowed(IamAuthenticationToken token, AuthenticationInfo info) throws AccountException {
		return true;
	}

}