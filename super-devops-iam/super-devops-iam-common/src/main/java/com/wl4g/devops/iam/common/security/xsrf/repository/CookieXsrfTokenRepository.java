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
package com.wl4g.devops.iam.common.security.xsrf.repository;

import static java.lang.String.format;
import static java.util.Locale.US;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.shiro.web.util.WebUtils.getPathWithinApplication;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.servlet.Cookie;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.config.XsrfProperties;
import com.wl4g.devops.iam.common.web.servlet.IamCookie;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.extDomainString;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.extTopDomainString;
import static com.wl4g.devops.iam.common.utils.IamAuthenticatingUtils.*;
import static org.springframework.web.util.WebUtils.getCookie;

/**
 * A {@link XsrfTokenRepository} that persists the CSRF token in a cookie named
 * "XSRF-TOKEN" and reads from the header "X-XSRF-TOKEN" following the
 * conventions of AngularJS. When using with AngularJS be sure to use
 * {@link #withHttpOnlyFalse()}.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author Rob Winch
 * @version v1.0 2020年4月27日
 * @since
 */
public final class CookieXsrfTokenRepository implements XsrfTokenRepository {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Xsrf properties config.
	 */
	@Autowired
	protected XsrfProperties xconfig;

	/**
	 * Iam core properties config.
	 */
	@Autowired
	protected AbstractIamProperties<? extends ParamProperties> coreConfig;

	@Override
	public XsrfToken generateXToken(HttpServletRequest request) {
		return new DefaultXsrfToken(xconfig.getXsrfHeaderName(), xconfig.getXsrfParamName(), generateXsrfToken());
	}

	@Override
	public void saveXToken(XsrfToken xtoken, HttpServletRequest request, HttpServletResponse response) {
		String xtokenValue = isNull(xtoken) ? EMPTY : xtoken.getXsrfToken();

		// Delete older xsrf token from cookie.
		int version = -1;
		Cookie oldCookie = IamCookie.build(getCookie(request, getXsrfTokenCookieName(request)));
		if (!isNull(oldCookie)) {
			version = oldCookie.getVersion();
			oldCookie.removeFrom(request, response);
		}

		// New xsrf token to cookie.
		Cookie cookie = new IamCookie(coreConfig.getCookie());
		cookie.setName(getXsrfTokenCookieName(request));
		cookie.setSecure(request.isSecure());
		cookie.setValue(xtokenValue);
		cookie.setVersion(++version);
		if (!isBlank(xconfig.getCookiePath())) {
			cookie.setPath(xconfig.getCookiePath());
		} else {
			// When the root path of web application access is path='/' and the
			// front and back ends are separately deployed, the browser
			// document.cookie can only get cookie of path='/'
			cookie.setPath("/");
			// cookie.setPath(getRequestContext(request));
		}
		if (isNull(xtoken)) {
			cookie.setMaxAge(0);
		} else {
			cookie.setMaxAge(-1);
		}
		// For the implementation of xsrf token, for the front-end and back-end
		// separation architecture, generally JS obtains and appends the cookie
		// to the headers. At this time, httponly=true cannot be set
		cookie.setHttpOnly(xconfig.isCookieHttpOnly());

		// Note: due to the cross domain limitation of set cookie, it can only
		// be set to the current domain or parent domain.
		cookie.setDomain(getXsrfTokenCookieDomain(request));

		cookie.saveTo(request, response);
	}

	@Override
	public XsrfToken getXToken(HttpServletRequest request) {
		if (!isXsrfRequired(request)) {
			log.debug("Requests that do not requires XSRF validation, RequestUri: %s", getPathWithinApplication(request));
			return null;
		}

		javax.servlet.http.Cookie cookie = getCookie(request, getXsrfTokenCookieName(request));
		if (isNull(cookie)) {
			return null;
		}
		String xtoken = cookie.getValue();
		if (equalsAnyIgnoreCase(xtoken, "null", "undefined", EMPTY)) {
			return null;
		}
		return new DefaultXsrfToken(xconfig.getXsrfHeaderName(), xconfig.getXsrfParamName(), xtoken);
	}

	@Override
	public boolean isXsrfRequired(HttpServletRequest request) {
		String origin = getXsrfRequestOrigin(request);
		return !isBlank(origin) && !equalsAnyIgnoreCase(origin, "null", "undefined", EMPTY);
	}

	/**
	 * Generate XSRF token
	 * 
	 * @return
	 */
	private String generateXsrfToken() {
		String tokenSuffix = generateDefaultTokenSuffix(coreConfig.getSpringApplicationName());
		return format("xf%s%s", UUID.randomUUID().toString().replaceAll("-", ""), tokenSuffix);
	}

	/**
	 * Gets request context.
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getRequestContext(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}

	/**
	 * Gets xsrf token with cookie name.
	 * 
	 * @param request
	 * @return
	 */
	private String getXsrfTokenCookieName(HttpServletRequest request) {
		String xsrfCookieName = xconfig.getXsrfCookieName();
		if (!isBlank(xsrfCookieName)) {
			return xsrfCookieName;
		}

		// @see: iam-jssdk-core.js#[MARK55]
		String xsrfOriginUri = getXsrfRequestOrigin(request);
		hasTextOf(xsrfOriginUri, "Could't get XSRF request origin");

		String domain = extDomainString(xsrfOriginUri);
		String topDomain = extTopDomainString(xsrfOriginUri);
		String defaultServName = domain;
		int index = domain.indexOf(topDomain);
		if (index > 0) {
			defaultServName = domain.substring(0, index - 1);
		}
		defaultServName = defaultServName.replace(".", "_").toUpperCase(US);
		xsrfCookieName = "IAM-" + defaultServName + "-XSRF-TOKEN";
		return xsrfCookieName;
	}

	/**
	 * Gets xrf token cookit domain. </br>
	 * <p>
	 * Note: due to the cross domain limitation of set cookie, it can only be
	 * set to the current domain or parent domain.
	 * </p>
	 * 
	 * @param request
	 * @return
	 */
	private String getXsrfTokenCookieDomain(HttpServletRequest request) {
		return extTopDomainString(getXsrfRequestOrigin(request));
	}

	/**
	 * Gets xsrf request origin uri.
	 * 
	 * @param request
	 * @return
	 */
	private String getXsrfRequestOrigin(HttpServletRequest request) {
		String domainUri = request.getHeader("Origin");
		domainUri = isBlank(domainUri) ? request.getHeader("Referer") : domainUri;
		/*
		 * Xsrf validation is required only when there is a request header for
		 * Origin/Referer.
		 */
		// domainUri = isBlank(domainUri) ? request.getServerName() : domainUri;
		return domainUri;
	}

}