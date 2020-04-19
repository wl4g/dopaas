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

import org.springframework.http.HttpMethod;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import com.wl4g.devops.tool.common.collection.RegisteredSetList;

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.web.WebUtils2.isSameWildcardOrigin;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * CORS configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2019年3月4日
 * @since
 */
public class CorsProperties implements Serializable {
	final private static long serialVersionUID = -5701992202765239835L;

	final public static String KEY_CORS_PREFIX = "spring.cloud.devops.iam.cors";

	/**
	 * {@link CorsRule}
	 */
	private Map<String, CorsRule> rules = new HashMap<String, CorsRule>() {
		private static final long serialVersionUID = -8576461225674624807L;
		{
			// Default settings.
			put("/**", new CorsRule().setAllowCredentials(true).addAllowsMethods("GET", "POST")
					.addAllowsOrigins("http://localhost:8080"));
		}
	};

	public Map<String, CorsRule> getRules() {
		return rules;
	}

	public void setRules(Map<String, CorsRule> rules) {
		if (!isEmpty(rules)) {
			rules.putAll(rules);
		}
	}

	@Override
	public String toString() {
		return "CorsProperties [rules=" + rules + "]";
	}

	/**
	 * CORS rule configuration
	 *
	 * @author wangl.sir
	 * @version v1.0 2019年3月4日
	 * @since
	 */
	public static class CorsRule {
		private List<String> allowsMethods = new RegisteredSetList<>(new ArrayList<>(8));
		private List<String> allowsHeaders = new RegisteredSetList<>(new ArrayList<>(8));
		private List<String> allowsOrigins = new RegisteredSetList<>(new ArrayList<>(8));
		private List<String> exposedHeaders = new RegisteredSetList<>(new ArrayList<>(8));
		private boolean allowCredentials = true;
		private Long maxAge = 1800L;

		public CorsRule() {
			super();
		}

		public List<String> getAllowsMethods() {
			return allowsMethods;
		}

		public CorsRule setAllowsMethods(List<String> allowsMethods) {
			if (!isEmpty(allowsMethods)) {
				this.allowsMethods.addAll(allowsMethods);
			}
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
			if (!isEmpty(allowsHeaders)) {
				this.allowsHeaders.addAll(allowsHeaders);
			}
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
			if (!isEmpty(allowsOrigins)) {
				this.allowsOrigins.addAll(allowsOrigins);
			}
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
			if (!isEmpty(exposedHeaders)) {
				this.exposedHeaders.addAll(exposedHeaders);
			}
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
		 * To Spring CORS configuration
		 *
		 * @return
		 */
		public CorsConfiguration toSpringCorsConfiguration() {
			// Merge values elements.
			mergeWithWildcard(getAllowsOrigins());
			mergeWithWildcard(getAllowsHeaders());
			mergeWithWildcard(getAllowsMethods());
			mergeWithWildcard(getExposedHeaders());

			// Convert to spring CORS configuration.
			CorsConfiguration cors = new AdvancedCorsConfiguration();
			cors.setAllowCredentials(isAllowCredentials());
			cors.setMaxAge(getMaxAge());
			getAllowsOrigins().forEach(origin -> cors.addAllowedOrigin(origin));
			getAllowsHeaders().forEach(header -> cors.addAllowedHeader(header));
			getAllowsMethods().forEach(method -> cors.addAllowedMethod(method));
			getExposedHeaders().forEach(exposed -> cors.addExposedHeader(exposed));
			return cors;
		}

		/**
		 * Wild-card merge source collection and remove duplicate.
		 *
		 * @param sources
		 * @return
		 */
		private void mergeWithWildcard(Collection<String> sources) {
			if (isEmpty(sources))
				return;

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
			 * environment variables. e.g. http://${DEVOPS_DOMAIN_TOP}
			 * https://${DEVOPS_DOMAIN_TOP}
			 */
			Iterator<String> it2 = sources.iterator();
			while (it2.hasNext()) {
				String value = it2.next();
				if (!isBlank(value) && contains(value, "{") && contains(value, "}")) {
					it2.remove();
				}
			}
		}

	}

	/**
	 * Custom advanced logic CORS configuration processing.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月21日
	 * @since
	 */
	public static class AdvancedCorsConfiguration extends CorsConfiguration {

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
			if (isBlank(requestOrigin))
				return null;
			if (isEmpty(getAllowedOrigins()))
				return null;

			if (getAllowedOrigins().contains(ALL)) {
				/**
				 * Note: Chrome will prompt: </br>
				 * The value of the 'Access-Control-Allow-Origin' header in the
				 * response must not be the wildcard '*' when the request's
				 * credentials mode is 'include'. The credentials mode of
				 * requests initiated by the XMLHttpRequest is controlled by the
				 * withCredentials attribute.
				 */
				if (!getAllowCredentials()) {
					return ALL;
				} else {
					return requestOrigin;
				}
			}
			for (String allowedOrigin : getAllowedOrigins()) {
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
			if (requestHeaders == null) {
				return null;
			}
			if (requestHeaders.isEmpty()) {
				return Collections.emptyList();
			}
			if (ObjectUtils.isEmpty(getAllowedHeaders())) {
				return null;
			}

			boolean allowAnyHeader = getAllowedHeaders().contains(ALL);
			List<String> result = new ArrayList<String>(requestHeaders.size());
			for (String requestHeader : requestHeaders) {
				if (StringUtils.hasText(requestHeader)) {
					requestHeader = requestHeader.trim();
					if (allowAnyHeader) {
						result.add(requestHeader);
					} else {
						for (String allowedHeader : getAllowedHeaders()) {
							// e.g: allowedHeader => "X-Iam-*"
							if (allowedHeader.contains(ALL)) {
								String allowedHeaderPrefix = allowedHeader.substring(allowedHeader.indexOf(ALL) + 1);
								if (requestHeader.startsWith(allowedHeaderPrefix)) {
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

		@Override
		public void addAllowedMethod(String method) {
			if (!isBlank(method)) {
				// Add for invalid method check.
				isTrue(Objects.nonNull(HttpMethod.resolve(method.toUpperCase(US))), "Invalid allowed http method: '%s'", method);
				super.addAllowedMethod(method.toUpperCase(US));
			}
		}

	}

}