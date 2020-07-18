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
package com.wl4g.devops.iam.common.config;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_XSRF_BASE;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.iam.common.config.CorsProperties.CorsRule.DEFAULT_CORS_ALLOW_HEADER_PREFIX;
import static java.util.Collections.singletonList;
import static org.apache.shiro.web.filter.mgt.DefaultFilter.anon;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.components.tools.common.collection.Collections2;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * XSRF configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2020年4月26日
 * @since
 */
public class XsrfProperties implements InitializingBean, Serializable {
	final private static long serialVersionUID = -5701992202711439835L;

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Default xsrf cookie name.
	 */
	private String xsrfCookieName = null;

	/**
	 * Default xsrf parameter name.
	 */
	private String xsrfParamName = DEFAULT_XSRF_PARAM_NAME;

	/**
	 * Default xsrf header name.
	 */
	private String xsrfHeaderName = DEFAULT_XSRF_HEADER_NAME;

	/**
	 * Enable cookie http only.</br>
	 * 
	 * <p>
	 * For the implementation of xsrf token, for the front-end and back-end
	 * separation architecture, generally JS obtains and appends the cookie to
	 * the headers. At this time, httponly = true cannot be set
	 * </p>
	 */
	private boolean cookieHttpOnly = false;

	/**
	 * The path that the Cookie will be created with. This will override the
	 * default functionality which uses the request context as the path.
	 */
	private String cookiePath;

	/**
	 * Ignore xsrf validation request mappings.
	 */
	private List<String> excludeValidUriPatterns = new ArrayList<>();

	//
	// --- Temporary fields. ---
	//

	/**
	 * Temporary core configuration.
	 */
	@Autowired
	private transient AbstractIamProperties<? extends ParamProperties> cConfig;

	/**
	 * Temporary cors configuration.
	 */
	@Autowired
	private transient CorsProperties corsConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Apply default settings.
		applyDefaultPropertiesSet();

		if (!isEmpty(excludeValidUriPatterns)) {
			// Remove duplicate.
			Collections2.disDupCollection(excludeValidUriPatterns);
		}

		// @Deprecated, Please use external cors custom configuration.
		//
		// // Add build-in xsrf endpoint cors rules.
		// CorsRule xsrfCors = new CorsRule();
		// /**
		// * @see {@link
		// com.wl4g.devops.iam.common.web.XsrfProtectionEndpoint#applyXsrfToken(HttpServletRequest,
		// HttpServletResponse)}
		// */
		// xsrfCors.addAllowsMethods(HEAD.name());
		// xsrfCors.addAllowsOrigins(allowsOrigins);
		// corsConfig.getRules().put(DEFAULT_XSRF_BASE_PATTERN, xsrfCors);

		// Check header name with cors allowed.
		corsConfig.assertCorsHeaders(singletonList(getXsrfHeaderName()));
	}

	public String getXsrfCookieName() {
		return xsrfCookieName;
	}

	public XsrfProperties setXsrfCookieName(String xsrfCookieName) {
		this.xsrfCookieName = xsrfCookieName;
		return this;
	}

	public String getXsrfParamName() {
		return xsrfParamName;
	}

	public XsrfProperties setXsrfParamName(String xsrfParamName) {
		this.xsrfParamName = xsrfParamName;
		return this;
	}

	public String getXsrfHeaderName() {
		return xsrfHeaderName;
	}

	public XsrfProperties setXsrfHeaderName(String xsrfHeaderName) {
		// hasTextOf(xsrfHeaderName, "xsrfHeaderName");
		this.xsrfHeaderName = xsrfHeaderName;
		return this;
	}

	public boolean isCookieHttpOnly() {
		return cookieHttpOnly;
	}

	/**
	 * Sets the HttpOnly attribute on the cookie containing the CSRF token. The
	 * cookie will only be marked as HttpOnly if both
	 * <code>cookieHttpOnly</code> is <code>true</code> and the underlying
	 * version of Servlet is 3.0 or greater. Defaults to <code>true</code> if
	 * the underlying version of Servlet is 3.0 or greater. NOTE: The
	 * {@link Cookie#setHttpOnly(boolean)} was introduced in Servlet 3.0.
	 *
	 * @param cookieHttpOnly
	 *            <code>true</code> sets the HttpOnly attribute,
	 *            <code>false</code> does not set it (depending on Servlet
	 *            version)
	 * @throws IllegalArgumentException
	 *             if <code>cookieHttpOnly</code> is <code>true</code> and the
	 *             underlying version of Servlet is less than 3.0
	 */
	public XsrfProperties setCookieHttpOnly(boolean cookieHttpOnly) {
		this.cookieHttpOnly = cookieHttpOnly;
		return this;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public XsrfProperties setCookiePath(String cookiePath) {
		hasTextOf(cookiePath, "cookiePath");
		this.cookiePath = cookiePath;
		return this;
	}

	public List<String> getExcludeValidUriPatterns() {
		return excludeValidUriPatterns;
	}

	public XsrfProperties setExcludeValidUriPatterns(List<String> excludeValidUriPatterns) {
		// if (!isEmpty(excludeValidXsrfMapping)) {
		// this.excludeValidUriPatterns.addAll(excludeValidUriPatterns);
		// }
		this.excludeValidUriPatterns.addAll(excludeValidUriPatterns);
		return this;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	/**
	 * Apply default properties fields settings.
	 */
	private void applyDefaultPropertiesSet() {
		getExcludeValidUriPatterns().add(URI_S_BASE + "/**");
		getExcludeValidUriPatterns().add(URI_C_BASE + "/**");

		// Automatically exclude patterns with filter chains of anon type
		cConfig.getFilterChain().forEach((pattern, filter) -> {
			if (StringUtils.equals(filter, anon.name())) {
				getExcludeValidUriPatterns().add(pattern);
			}
		});

	}

	final public static String KEY_XSRF_PREFIX = "spring.cloud.devops.iam.xsrf";

	/**
	 * Use to: IAM-{serviceName}-XSRF-TOKEN
	 */
	@Deprecated
	final public static String DEFAULT_XSRF_COOKIE_NAME = "IAM-XSRF-TOKEN";
	final public static String DEFAULT_XSRF_PARAM_NAME = "_xsrf";
	final public static String DEFAULT_XSRF_HEADER_NAME = DEFAULT_CORS_ALLOW_HEADER_PREFIX + "-Xsrf-Token";
	final public static String DEFAULT_XSRF_BASE_PATTERN = URI_XSRF_BASE + "/**";

}