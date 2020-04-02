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
package com.wl4g.devops.iam.common.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.UnknownSessionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import static com.wl4g.devops.common.web.RespBase.RetCode.*;
import static com.wl4g.devops.tool.common.lang.Exceptions.*;
import com.wl4g.devops.common.web.error.ErrorConfiguring;

/**
 * IAM authorization error configuring.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-02
 * @since
 */
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class IamErrorConfiguring implements ErrorConfiguring {

	@Override
	public Integer getStatus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model, Exception ex) {
		// IAM Unauthenticated?
		if ((ex instanceof UnauthenticatedException)
				|| (ex instanceof com.wl4g.devops.common.exception.iam.UnauthenticatedException)) {
			return UNAUTHC.getErrcode();
		}
		// IAM Unauthorized?
		else if ((ex instanceof UnauthorizedException)
				|| (ex instanceof com.wl4g.devops.common.exception.iam.UnauthorizedException)) {
			return UNAUTHZ.getErrcode();
		}
		// see: IamSecurityHolder
		else if (ex instanceof UnknownSessionException) {
			return PARAM_ERR.getErrcode();
		}

		// Using next chain configuring.
		return null;
	}

	@Override
	public String getRootCause(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model,
			Exception ex) {
		// IAM Unauthenticated or Unauthorized?
		if ((ex instanceof UnauthenticatedException) || (ex instanceof UnauthorizedException)
				|| (ex instanceof com.wl4g.devops.common.exception.iam.UnauthenticatedException)
				|| (ex instanceof com.wl4g.devops.common.exception.iam.UnauthorizedException)) {
			// return getRootCausesString(ex);
			return getMessage(ex);
		}

		// Using next chain configuring.
		return null;
	}

}