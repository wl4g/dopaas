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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import com.wl4g.devops.tool.common.collection.RegisteredSetList;

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.web.WebUtils2.isSameWithOrigin;
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
	private static final long serialVersionUID = -5701992202765239835L;

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
		private List<String> allowsMethods = new RegisteredSetList<>(new ArrayList<>(4));
		private List<String> allowsHeaders = new RegisteredSetList<>(new ArrayList<>(4));
		private List<String> allowsOrigins = new RegisteredSetList<>(new ArrayList<>(4));
		private List<String> exposedHeaders = new RegisteredSetList<>(new ArrayList<>(4));
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
		 * <font color=red><b>Note:</b> "allowsOrigin" may have a "*" wildcard
		 * character.</br>
		 * e.g. http://*.mydomain.com </font>
		 */
		@Override
		public String checkOrigin(String requestOrigin) {
			if (!StringUtils.hasText(requestOrigin)) {
				return null;
			}
			if (isEmpty(getAllowedOrigins())) {
				return null;
			}
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
				// e.g. allowedOrigin => "http://*.aa.mydomain.com"
				if (isSameWithOrigin(allowedOrigin, requestOrigin, true)) {
					return requestOrigin;
				}
			}
			return null;
		}

		@Override
		public void addAllowedMethod(String method) {
			if (!isBlank(method)) {
				// Add for invalid method check.
				isTrue(Objects.nonNull(HttpMethod.resolve(method.toUpperCase(Locale.ENGLISH))),
						"Invalid allowed http method: '%s'", method);
				super.addAllowedMethod(method.toUpperCase(Locale.ENGLISH));
			}
		}

	}

}