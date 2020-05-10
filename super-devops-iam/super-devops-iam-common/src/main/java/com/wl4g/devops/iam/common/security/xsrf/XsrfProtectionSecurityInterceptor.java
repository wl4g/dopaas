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
package com.wl4g.devops.iam.common.security.xsrf;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.getPathWithinApplication;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.util.AntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wl4g.devops.iam.common.config.XsrfProperties;
import com.wl4g.devops.iam.common.security.xsrf.handler.AccessRejectHandler;
import com.wl4g.devops.iam.common.security.xsrf.handler.InvalidXsrfTokenException;
import com.wl4g.devops.iam.common.security.xsrf.handler.MissingXsrfTokenException;
import com.wl4g.devops.iam.common.security.xsrf.repository.XsrfToken;
import com.wl4g.devops.iam.common.security.xsrf.repository.XsrfTokenRepository;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * <p>
 * Applies
 * <a href="https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(XSRF)"
 * >XSRF</a> protection using a synchronizer token pattern. Developers are
 * required to ensure that {@link XsrfProtectionSecurityInterceptor} is invoked
 * for any request that allows state to change. Typically this just means that
 * they should ensure their web application follows proper REST semantics (i.e.
 * do not change state with the HTTP methods GET, HEAD, TRACE, OPTIONS).
 * </p>
 *
 * <p>
 * Typically the {@link XsrfTokenRepository} implementation chooses to store the
 * </p>
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月27日
 * @since
 */
public final class XsrfProtectionSecurityInterceptor implements HandlerInterceptor {

	protected SmartLogger log = getLogger(getClass());

	@Autowired
	private XsrfProperties xconfig;

	@Autowired
	private XsrfTokenRepository xtokenRepository;

	/**
	 * Specifies a {@link XsrfMatcher} that is used to determine if XSRF
	 * protection should be applied. If the {@link XsrfMatcher} returns true for
	 * a given request, then XSRF protection is applied.
	 *
	 * <p>
	 * The default is to apply XSRF protection for any HTTP method other than
	 * GET, HEAD, TRACE, OPTIONS.
	 * </p>
	 *
	 * @param xsrfMatcher
	 *            the {@link XsrfMatcher} used to determine if XSRF protection
	 *            should be applied.
	 */
	@Autowired
	private XsrfMatcher xsrfProtectMatcher;

	/**
	 * Specifies a access denied handler that should be used when XSRF
	 * protection fails.
	 *
	 * <p>
	 * The default is to use AccessDeniedHandlerImpl with no arguments.
	 * </p>
	 */
	@Autowired
	private AccessRejectHandler rejectHandler;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String requestPath = getPathWithinApplication(toHttp(request));

		// Ignore non replay request methods.
		if (!xsrfProtectMatcher.matches(request)) {
			log.debug("Skip xsrf protection of: {}", requestPath);
			return true;
		}

		// Ignore exclude URLs XSRF validation.
		for (String pattern : xconfig.getExcludeValidUriPatterns()) {
			if (defaultExcludeXsrfMatcher.matchStart(pattern, requestPath)) {
				log.debug("Skip exclude url xsrf valid '{}'", requestPath);
				return true;
			}
		}

		// XSRF validation
		// request.setAttribute(HttpServletResponse.class.getName(), response);
		XsrfToken xsrfToken = xtokenRepository.getXToken(request);
		final boolean missingToken = isNull(xsrfToken);
		if (missingToken) {
			xsrfToken = xtokenRepository.generateXToken(request);
			xtokenRepository.saveXToken(xsrfToken, request, response);
		}
		// request.setAttribute(XsrfToken.class.getName(), xsrfToken);
		// request.setAttribute(xsrfToken.getParameterName(), xsrfToken);

		String actualToken = request.getHeader(xsrfToken.getXsrfHeaderName());
		actualToken = isBlank(actualToken) ? getCleanParam(request, xsrfToken.getXsrfParamName()) : actualToken;
		if (!xsrfToken.getXsrfToken().equals(actualToken)) {
			log.debug("Invalid XSRF token found for: {}", requestPath);
			if (missingToken) {
				rejectHandler.handle(request, response, new MissingXsrfTokenException(actualToken));
			} else {
				rejectHandler.handle(request, response, new InvalidXsrfTokenException(xsrfToken, actualToken));
			}
			return false;
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	/**
	 * Exclude xsrf URLs mapping matcher.
	 */
	final private static AntPathMatcher defaultExcludeXsrfMatcher = new AntPathMatcher();

}
