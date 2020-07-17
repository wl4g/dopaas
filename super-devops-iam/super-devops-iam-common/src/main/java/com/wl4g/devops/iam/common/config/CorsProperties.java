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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Locale.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import com.wl4g.devops.components.tools.common.collection.RegisteredSetList;

import static com.wl4g.devops.iam.common.config.CorsProperties.IamCorsValidator.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.isSameWildcardOrigin;
import static com.wl4g.devops.iam.common.config.CorsProperties.CorsRule.*;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.web.cors.CorsConfiguration.ALL;
import static org.springframework.http.HttpMethod.*;

/**
 * CORS configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2019年3月4日
 * @since
 */
public class CorsProperties implements InitializingBean, Serializable {
	final private static long serialVersionUID = -5701992202765239835L;

	/**
	 * {@link CorsRule}
	 */
	private Map<String, CorsRule> rules = new HashMap<>();

	public Map<String, CorsRule> getRules() {
		return rules;
	}

	public CorsProperties setRules(Map<String, CorsRule> rules) {
		// if (!isEmpty(rules)) {
		// rules.putAll(rules);
		// }
		this.rules = rules;
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		applyRulesProperties();
	}

	/**
	 * Apply default and requires rules if necessary.
	 */
	private void applyRulesProperties() {
		// Sets default cors rules, if necessary
		if (isEmpty(getRules())) {
			getRules().getOrDefault("/**", new CorsRule()).addAllowsOrigins("http://localhost:8080").setAllowCredentials(true)
					.addAllowsHeaders(DEFAULT_ALLOWED_HEADERS).addAllowsMethods(DEFAULT_ALLOWED_METHODS);
		}

		// Adds requires cors rules
		getRules().values().stream().forEach(rule -> rule.addAllowsHeaders(DEFAULT_REQUIRES_ALLOWED_HEADERS));

	}

	//
	// --- Function's. ---
	//

	/**
	 * Assertion cors allowed headers.
	 * 
	 * @param requestHeaders
	 */
	public void assertCorsHeaders(List<String> requestHeaders) {
		for (CorsRule rule : getRules().values()) {
			List<String> allowedHeaders = rule.resolveIamCorsConfiguration().getAllowedHeaders();
			List<String> legalHeaders = checkCorsHeaders(requestHeaders, allowedHeaders);
			if (isEmpty(legalHeaders)) {
				throw new IllegalArgumentException(
						format("Invalid cors requestHeaders: %s, allowedHeaders: %s", requestHeaders, allowedHeaders));
			}
		}
	}

	/**
	 * Assertion cors allowed methods.
	 * 
	 * @param requestMethods
	 */
	public void assertCorsMethods(HttpMethod requestMethod) {
		for (CorsRule rule : getRules().values()) {
			List<String> allowedMethods = rule.resolveIamCorsConfiguration().getAllowedMethods();
			List<HttpMethod> _allowedMethods = allowedMethods.stream().map(m -> resolve(m)).collect(toList());
			List<HttpMethod> legalMethods = checkCorsMethod(requestMethod, _allowedMethods);
			if (isEmpty(legalMethods)) {
				throw new IllegalArgumentException(
						format("Invalid cors requestMethod: %s, allowedMethods: %s", requestMethod, allowedMethods));
			}
		}
	}

	/**
	 * Assertion cors allowed origins.
	 * 
	 * @param requestOrigns
	 */
	public void assertCorsOrigin(String requestOrigin) {
		for (CorsRule rule : getRules().values()) {
			List<String> allowedOrigins = rule.resolveIamCorsConfiguration().getAllowedOrigins();
			String legalOrigin = checkCorsOrigin(requestOrigin, allowedOrigins, rule.isAllowCredentials());
			if (isBlank(legalOrigin)) {
				throw new IllegalArgumentException(
						format("Invalid cors requestOrigin: %s, allowedOrigins: %s", requestOrigin, allowedOrigins));
			}
		}
	}

	/**
	 * CORS rule configuration
	 *
	 * @author wangl.sir
	 * @version v1.0 2019年3月4日
	 * @since
	 */
	public static class CorsRule implements Serializable {
		private static final long serialVersionUID = 2691186807570014349L;

		/**
		 * Default cors allowed header prefix.
		 */
		final public static String DEFAULT_CORS_ALLOW_HEADER_PREFIX = "X-Iam";

		private List<String> allowsMethods = new RegisteredSetList<>(new ArrayList<>(8));
		private List<String> allowsHeaders = new RegisteredSetList<>(new ArrayList<>(8));
		private List<String> allowsOrigins = new RegisteredSetList<>(new ArrayList<>(8));
		private List<String> exposedHeaders = new RegisteredSetList<>(new ArrayList<>(8));
		private boolean allowCredentials = true;
		private Long maxAge = 1800L;

		//
		// --- Temporary fields. ---
		//

		private transient IamCorsValidator cors;

		public CorsRule() {
			super();
		}

		public List<String> getAllowsMethods() {
			return allowsMethods;
		}

		public CorsRule setAllowsMethods(List<String> allowsMethods) {
			// if (!isEmpty(allowsMethods)) {
			// this.allowsMethods.addAll(allowsMethods);
			// }
			this.allowsMethods = allowsMethods;
			return this;
		}

		public CorsRule addAllowsMethods(String... allowsMethods) {
			if (!isNull(allowsMethods)) {
				this.allowsMethods.addAll(asList(allowsMethods));
			}
			return this;
		}

		public List<String> getAllowsHeaders() {
			return allowsHeaders;
		}

		public CorsRule setAllowsHeaders(List<String> allowsHeaders) {
			// if (!isEmpty(allowsHeaders)) {
			// this.allowsHeaders.addAll(allowsHeaders);
			// }
			this.allowsHeaders = allowsHeaders;
			return this;
		}

		public CorsRule addAllowsHeaders(String... allowsHeaders) {
			if (!isNull(allowsHeaders)) {
				this.allowsHeaders.addAll(asList(allowsHeaders));
			}
			return this;
		}

		public List<String> getAllowsOrigins() {
			return allowsOrigins;
		}

		public CorsRule setAllowsOrigins(List<String> allowsOrigins) {
			// "allowsOrigin" may have a "*" wildcard character.
			// e.g. http://*.mydomain.com
			// if (!isEmpty(allowsOrigins)) {
			// this.allowsOrigins.addAll(allowsOrigins);
			// }
			this.allowsOrigins = allowsOrigins;
			return this;
		}

		public CorsRule addAllowsOrigins(String... allowsOrigins) {
			// "allowsOrigin" may have a "*" wildcard character.
			// e.g. http://*.mydomain.com
			if (!isNull(allowsOrigins)) {
				this.allowsOrigins.addAll(asList(allowsOrigins));
			}
			return this;
		}

		public List<String> getExposedHeaders() {
			return exposedHeaders;
		}

		public CorsRule setExposedHeaders(List<String> exposedHeaders) {
			// if (!isEmpty(exposedHeaders)) {
			// this.exposedHeaders.addAll(exposedHeaders);
			// }
			this.exposedHeaders = exposedHeaders;
			return this;
		}

		public CorsRule addExposedHeader(String... exposedHeaders) {
			if (!isNull(exposedHeaders)) {
				this.exposedHeaders.addAll(asList(exposedHeaders));
			}
			return this;
		}

		public boolean isAllowCredentials() {
			return allowCredentials;
		}

		public CorsRule setAllowCredentials(boolean allowCredentials) {
			this.allowCredentials = allowCredentials;
			return this;
		}

		public Long getMaxAge() {
			return maxAge;
		}

		public CorsRule setMaxAge(Long maxAge) {
			this.maxAge = maxAge;
			return this;
		}

		/**
		 * Resolve & gets spring CORS configuration
		 *
		 * @return
		 */
		public IamCorsValidator resolveIamCorsConfiguration() {
			// Convert to spring CORS configuration.
			if (isNull(cors)) {
				// Merge values elements.
				mergeWithWildcard(getAllowsOrigins());
				mergeWithWildcard(getAllowsHeaders());
				mergeWithWildcard(getAllowsMethods());
				mergeWithWildcard(getExposedHeaders());
				// Convert to cors configuration.
				cors = new IamCorsValidator();
				cors.setAllowCredentials(isAllowCredentials());
				cors.setMaxAge(getMaxAge());
				getAllowsOrigins().forEach(origin -> cors.addAllowedOrigin(origin));
				getAllowsHeaders().forEach(header -> cors.addAllowedHeader(header));
				getAllowsMethods().forEach(method -> cors.addAllowedMethod(method));
				getExposedHeaders().forEach(exposed -> cors.addExposedHeader(exposed));
			}
			return cors;
		}

		/**
		 * Wild-card merge source collection and remove duplicate.
		 *
		 * @param sources
		 * @return
		 */
		private void mergeWithWildcard(Collection<String> sources) {
			if (isEmpty(sources)) {
				return;
			}

			// Clear other specific item configurations if '*' is present
			Iterator<String> it1 = sources.iterator();
			while (it1.hasNext()) {
				String value = it1.next();
				if (!isBlank(value) && trimToEmpty(value).equalsIgnoreCase(ALL)) {
					sources.clear();
					sources.add(ALL);
					break;
				}
			}

			/*
			 * Clean up invalid values that did not resolve successfully through
			 * environment variables. e.g. http://${DEVOPS_SERVICE_ZONE}
			 * https://${DEVOPS_SERVICE_ZONE}
			 */
			Iterator<String> it2 = sources.iterator();
			while (it2.hasNext()) {
				String value = it2.next();
				if (!isBlank(value) && contains(value, "{") && contains(value, "}")) {
					it2.remove();
				}
			}
		}

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

	}

	/**
	 * Iam enhanced logic CORS configuration validator.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月21日
	 * @since
	 */
	public static class IamCorsValidator extends CorsConfiguration {

		/**
		 * <b>Note:</b> "allowsOrigin" may have a "*" wildcard character.</br>
		 * </br>
		 * 
		 * For example supports:
		 * 
		 * <pre>
		 *	http://*.domain.com      ->  http://aa.domain.com
		 *	http://*.aa.domain.com:* ->  http://bb.aa.domain.com:8443
		 * </pre>
		 */
		@Override
		public String checkOrigin(String requestOrigin) {
			return checkCorsOrigin(requestOrigin, getAllowedOrigins(), getAllowCredentials());
		}

		/**
		 * <b>Note:</b> "allowedHeader" may have a "*" wildcard character.</br>
		 * </br>
		 * 
		 * For example supports:
		 * 
		 * <pre>
		 *	X-Iam-*                  ->  X-Iam-AccessToken, X-Iam-Authentication-Code
		 *	X-Iam-Authentication-*   ->  X-Iam-Authentication-Code
		 * </pre>
		 */
		@Override
		public List<String> checkHeaders(List<String> requestHeaders) {
			return checkCorsHeaders(requestHeaders, getAllowedHeaders());
		}

		@Override
		public void addAllowedMethod(String method) {
			if (!isBlank(method)) {
				// Check add method invalid.
				isTrue(Objects.nonNull(HttpMethod.resolve(method.toUpperCase(US))), "Invalid allowed http method: '%s'", method);
				super.addAllowedMethod(method.toUpperCase(US));
			}
		}

		/**
		 * <b>Note:</b> "allowsOrigin" may have a "*" wildcard character.</br>
		 * </br>
		 * 
		 * For example supports:
		 * 
		 * <pre>
		 *	http://*.domain.com      ->  http://aa.domain.com
		 *	http://*.aa.domain.com:* ->  http://bb.aa.domain.com:8443
		 * </pre>
		 * 
		 * @param requestOrigin
		 * @return
		 * @see {@link org.springframework.web.cors.CorsConfiguration#checkOrigin()}
		 */
		public static String checkCorsOrigin(String requestOrigin, List<String> allowedOrigins, boolean allowCredentials) {
			if (isBlank(requestOrigin)) {
				return null;
			}
			if (isEmpty(allowedOrigins)) {
				return null;
			}

			if (allowedOrigins.contains(ALL)) {
				/**
				 * Note: Chrome will prompt: </br>
				 * The value of the 'Access-Control-Allow-Origin' header in the
				 * response must not be the wildcard '*' when the request's
				 * credentials mode is 'include'. The credentials mode of
				 * requests initiated by the XMLHttpRequest is controlled by the
				 * withCredentials attribute.
				 */
				if (!allowCredentials) {
					return ALL;
				} else {
					return requestOrigin;
				}
			}
			for (String allowedOrigin : allowedOrigins) {
				if (equalsIgnoreCase(requestOrigin, allowedOrigin)) {
					return requestOrigin;
				}
				// e.g: allowedOrigin => "http://*.aa.mydomain.com"
				if (isSameWildcardOrigin(allowedOrigin, requestOrigin, true)) {
					return requestOrigin;
				}
			}
			return null;
		}

		/**
		 * Check & gets legal headers name.
		 * 
		 * @param requestHeaders
		 * @param allowedHeaders
		 * @return
		 */
		public static List<String> checkCorsHeaders(List<String> requestHeaders, List<String> allowedHeaders) {
			if (isNull(requestHeaders)) {
				return null;
			}
			if (requestHeaders.isEmpty()) {
				return Collections.emptyList();
			}
			if (ObjectUtils.isEmpty(allowedHeaders)) {
				return null;
			}

			boolean allowAnyHeader = allowedHeaders.contains(ALL);
			List<String> result = new ArrayList<String>(requestHeaders.size());
			for (String requestHeader : requestHeaders) {
				if (StringUtils.hasText(requestHeader)) {
					requestHeader = requestHeader.trim();
					if (allowAnyHeader) {
						result.add(requestHeader);
					} else {
						for (String allowedHeader : allowedHeaders) {
							// e.g: allowedHeader => "X-Iam-*"
							if (allowedHeader.contains(ALL)) {
								String allowedHeaderPrefix = allowedHeader.substring(allowedHeader.indexOf(ALL) + 1);
								if (startsWithIgnoreCase(requestHeader, allowedHeaderPrefix)) {
									result.add(requestHeader);
									break;
								}
							} else if (requestHeader.equalsIgnoreCase(allowedHeader)) {
								result.add(requestHeader);
								break;
							}
						}
					}
				}
			}
			return (result.isEmpty() ? null : result);
		}

		/**
		 * Check the HTTP request method (or the method from the
		 * Access-Control-Request-Method header on a pre-flight request) against
		 * the configured allowed methods.
		 * 
		 * @param requestMethod
		 * @param allowedMethods
		 * @return
		 * @see {@link org.springframework.web.cors.CorsConfiguration#checkHttpMethod()}
		 */
		public static List<HttpMethod> checkCorsMethod(HttpMethod requestMethod, List<HttpMethod> allowedMethods) {
			if (isNull(requestMethod)) {
				return null;
			}
			if (isNull(allowedMethods)) {
				return Collections.singletonList(requestMethod);
			}
			return (allowedMethods.contains(requestMethod) ? allowedMethods : null);
		}

	}

	/**
	 * Cors key properties.
	 */
	final public static String KEY_CORS_PREFIX = "spring.cloud.devops.iam.cors";

	/**
	 * Default requires allowes headers.
	 */
	final public static String[] DEFAULT_REQUIRES_ALLOWED_HEADERS = { "Cookie", "X-Requested-With", "Content-Type",
			"Content-Length", "User-Agent", "Referer", "Origin", "Accept", "Accept-Language", "Accept-Encoding" };

	/**
	 * Default allowes headers.
	 */
	@SuppressWarnings("serial")
	final public static String[] DEFAULT_ALLOWED_HEADERS = new ArrayList<String>() {
		{
			add(DEFAULT_CORS_ALLOW_HEADER_PREFIX + "-*");
		}
	}.toArray(new String[] {});

	/**
	 * Default allowes methods.
	 */
	final public static String[] DEFAULT_ALLOWED_METHODS = { GET.name(), HEAD.name(), POST.name(), OPTIONS.name() };

}