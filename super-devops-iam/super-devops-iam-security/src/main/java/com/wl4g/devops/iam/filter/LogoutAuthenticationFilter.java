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
package com.wl4g.devops.iam.filter;

import static com.wl4g.devops.common.web.RespBase.RetCode.*;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.*;
import static com.wl4g.devops.tool.common.web.WebUtils2.isTrue;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.authc.LogoutAuthenticationToken;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;

/**
 * Logout authentication filter
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月6日
 * @since
 */
@IamFilter
public class LogoutAuthenticationFilter extends AbstractIamAuthenticationFilter<LogoutAuthenticationToken>
		implements IamAuthenticationFilter {

	final public static String NAME = "logout";

	@Override
	protected LogoutAuthenticationToken doCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// MARK1: If the current status is unauthenticated, the principle cannot
		// be obtained if the logout is ignored.
		return new LogoutAuthenticationToken(getPrincipal(false), remoteHost);
	}

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		// Using coercion ignores remote exit failures
		boolean forced = isTrue(request, config.getParam().getLogoutForced(), true);
		log.info("Signout... of forced: {}, session: {}", forced, getSessionId());

		// Create logout token
		IamAuthenticationToken token = createToken(request, response);

		// Logout all logged-in external applications
		authHandler.logout(forced, null, toHttp(request), toHttp(response));

		/*
		 * Execute login failure redirect login page logic first, (prevent
		 * logout from getting binding parameters later)
		 */
		super.onLoginFailure(token, null, request, response);
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected RespBase<String> makeFailedResponse(String failRedirectUrl, ServletRequest request, Map params, String errmsg) {
		RespBase<String> resp = super.makeFailedResponse(failRedirectUrl, request, params, errmsg);
		// More useful than RespBase.UNAUTH
		resp.setCode(OK);
		return resp;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return "/logout";
	}

}