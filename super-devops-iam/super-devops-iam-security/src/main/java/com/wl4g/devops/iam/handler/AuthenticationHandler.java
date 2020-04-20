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
package com.wl4g.devops.iam.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;

import com.wl4g.devops.common.exception.iam.IllegalApplicationAccessException;
import com.wl4g.devops.common.exception.iam.IllegalCallbackDomainException;
import com.wl4g.devops.iam.common.authc.model.LoggedModel;
import com.wl4g.devops.iam.common.authc.model.LogoutModel;
import com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel;
import com.wl4g.devops.iam.common.authc.model.SessionValidityAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidatedAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidateModel;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;

/**
 * IAM authentication handler.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public interface AuthenticationHandler {

	/**
	 * Assertion the validity of the request parameters before executing the
	 * login. (that is, verify that the <b>'source application'</b> and the
	 * secure callback <b>'redirectUrl'</b> are legitimate)
	 *
	 * @param appName
	 * @param redirectUrl
	 * @throws IllegalCallbackDomainException
	 */
	void checkAuthenticateRedirectValidity(String appName, String redirectUrl) throws IllegalCallbackDomainException;

	/**
	 * Assertion whether the current login account has permission to access the
	 * application. (that is, validating the legitimacy of <b>'principal'</b>
	 * and <b>'application'</b>)
	 *
	 * @param principal
	 * @param appName
	 *            From source application
	 * @throws IllegalApplicationAccessException
	 */
	void assertApplicationAccessAuthorized(String principal, String appName) throws IllegalApplicationAccessException;

	/**
	 * Validate application request ticket
	 *
	 * @param model
	 *            ticket validation request
	 * @return validation assert result
	 */
	TicketValidatedAssertModel<IamPrincipalInfo> validate(TicketValidateModel model);

	/**
	 * Shiro authentication success callback process.
	 *
	 * @param appName
	 *            from source application name
	 * @param subject
	 *            Shiro subject
	 * @return Redirect callback information
	 */
	LoggedModel loggedin(String appName, Subject subject);

	/**
	 * Logout server session, including all external applications logged-in<br/>
	 * <br/>
	 * The Iam server logs out with two entries: <br/>
	 * 1: access http://iam-client/logout <br/>
	 * 2: direct access http://iam-server/logout <br/>
	 * {@link com.wl4g.devops.iam.web.CentralAuthenticatorEndpoint#logout()}
	 * {@link com.wl4g.devops.iam.filter.LogoutAuthenticationFilter#preHandle()}
	 *
	 * @param forced
	 *            logout forced
	 * @param appName
	 *            from source application name
	 * @param request
	 * @param response
	 * @return
	 */
	LogoutModel logout(boolean forced, String appName, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Validation application secondary authentication
	 *
	 * @param secondAuthCode
	 *            Secondary authentication code
	 * @param appName
	 *            from source application name
	 * @return
	 */
	SecondAuthcAssertModel secondaryValidate(String secondAuthCode, String appName);

	/**
	 * Sessions expired validation
	 *
	 * @param param
	 * @return
	 */
	SessionValidityAssertModel sessionValidate(SessionValidityAssertModel param);

}