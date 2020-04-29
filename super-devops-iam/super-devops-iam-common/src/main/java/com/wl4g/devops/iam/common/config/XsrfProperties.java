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

import static com.wl4g.devops.iam.common.config.CorsProperties.CorsRule.DEFAULT_CORS_ALLOW_HEADER_PREFIX;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.tool.common.log.SmartLogger;

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
	private String xsrfCookieName = DEFAULT_XSRF_COOKIE_NAME;

	/**
	 * Default xsrf parameter name.
	 */
	private String xsrfParamName = DEFAULT_XSRF_PARAM_NAME;

	/**
	 * Default xsrf header name.
	 */
	private String xsrfHeaderName = DEFAULT_XSRF_HEADER_NAME;

	/**
	 * Enable cookie http only
	 */
	private boolean cookieHttpOnly = !isNull(setHttpOnlyMethod);

	/**
	 * The path that the Cookie will be created with. This will override the
	 * default functionality which uses the request context as the path.
	 */
	private String cookiePath;

	/**
	 * Ignore xsrf validation request mappings.
	 */
	private List<String> excludeValidXsrfMapping = new ArrayList<>();

	@Override
	public void afterPropertiesSet() throws Exception {
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
		hasTextOf(xsrfHeaderName, "xsrfHeaderName");
		if (!startsWithIgnoreCase(xsrfHeaderName, DEFAULT_CORS_ALLOW_HEADER_PREFIX)) {
			throw new IllegalArgumentException(
					format("Xsrf header name must start with a %s prefix", DEFAULT_CORS_ALLOW_HEADER_PREFIX));
		}
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
		if (cookieHttpOnly && setHttpOnlyMethod == null) {
			throw new IllegalArgumentException(
					"Cookie will not be marked as HttpOnly because you are using a version of Servlet less than 3.0. NOTE: The Cookie#setHttpOnly(boolean) was introduced in Servlet 3.0.");
		}
		this.cookieHttpOnly = cookieHttpOnly;
		return this;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		hasTextOf(cookiePath, "cookiePath");
		this.cookiePath = cookiePath;
	}

	public List<String> getExcludeValidXsrfMapping() {
		return excludeValidXsrfMapping;
	}

	public void setExcludeValidXsrfMapping(List<String> excludeValidXsrfMapping) {
		if (isEmpty(excludeValidXsrfMapping)) {
			for (String mapping : excludeValidXsrfMapping) {
				if (!this.excludeValidXsrfMapping.contains(mapping)) {
					this.excludeValidXsrfMapping.add(mapping);
				} else {
					log.warn("Duplicate exclude valid xsrf mapping of '{}'", mapping);
				}
			}
		}
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	final public static String KEY_XSRF_PREFIX = "spring.cloud.devops.iam.xsrf";

	public static final String DEFAULT_XSRF_COOKIE_NAME = "IAM-XSRF-TOKEN";
	public static final String DEFAULT_XSRF_PARAM_NAME = "_xsrf";
	public static final String DEFAULT_XSRF_HEADER_NAME = DEFAULT_CORS_ALLOW_HEADER_PREFIX + "-Xsrf-Token";
	public static final Method setHttpOnlyMethod = findMethod(Cookie.class, "setHttpOnly", boolean.class);;

}