/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import static com.wl4g.devops.common.utils.web.WebUtils2.isSameWithOrigin;
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

	private List<CorsRule> rules = new ArrayList<CorsRule>() {
		private static final long serialVersionUID = -8576461225674624807L;
		{
			add(new CorsRule().setPath("/**").addAllowsMethod("GET").addAllowsOrigin("http://localhost:8080"));
		}
	};

	public List<CorsRule> getRules() {
		return rules;
	}

	public void setRules(List<CorsRule> rules) {
		if (rules != null && !rules.isEmpty()) {
			rules.forEach(rule -> {
				if (!this.rules.contains(rule)) {
					this.rules.add(rule);
				}
			});
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
		private String path = "/**";
		private boolean allowCredentials = true;
		private Set<String> allowsOrigins = new HashSet<>();
		private Set<String> allowsHeaders = new HashSet<>();
		private Set<String> allowsMethods = new HashSet<>();
		private Set<String> exposedHeaders = new HashSet<>();
		private Long maxAge = 1800L;

		public CorsRule() {
			super();
		}

		public String getPath() {
			return path;
		}

		public CorsRule setPath(String path) {
			Assert.hasText(path, "Register 'path' must not be empty");
			this.path = path;
			return this;
		}

		public boolean isAllowCredentials() {
			return allowCredentials;
		}

		public CorsRule setAllowCredentials(boolean allowCredentials) {
			this.allowCredentials = allowCredentials;
			return this;
		}

		public Set<String> getAllowsOrigins() {
			return allowsOrigins;
		}

		public CorsRule setAllowsOrigins(Set<String> allowsOrigins) {
			// "allowsOrigin" may have a "*" wildcard character.
			// e.g. http://*.mydomain.com
			this.allowsOrigins.addAll(allowsOrigins);
			return this;
		}

		public CorsRule addAllowsOrigin(String allowsOrigin) {
			// "allowsOrigin" may have a "*" wildcard character.
			// e.g. http://*.mydomain.com
			this.allowsOrigins.add(allowsOrigin);
			return this;
		}

		public Set<String> getAllowsHeaders() {
			return allowsHeaders;
		}

		public CorsRule setAllowsHeaders(Set<String> allowsHeaders) {
			return mergeWithSimpleAll(this.allowsHeaders, allowsHeaders);
		}

		public CorsRule addAllowsHeader(String allowsHeader) {
			return mergeWithSimpleAll(this.allowsHeaders, Arrays.asList(allowsHeader));
		}

		public Set<String> getAllowsMethods() {
			return allowsMethods;
		}

		public CorsRule setAllowsMethods(Set<String> allowsMethods) {
			return mergeWithSimpleAll(this.allowsMethods, allowsMethods);
		}

		public CorsRule addAllowsMethod(String allowsMethod) {
			return mergeWithSimpleAll(this.allowsMethods, Arrays.asList(allowsMethod));
		}

		public Set<String> getExposedHeaders() {
			return exposedHeaders;
		}

		public CorsRule setExposedHeaders(Set<String> exposedHeaders) {
			return mergeWithSimpleAll(this.exposedHeaders, exposedHeaders);
		}

		public CorsRule addExposedHeader(String exposedHeader) {
			return mergeWithSimpleAll(this.exposedHeaders, Arrays.asList(exposedHeader));
		}

		public Long getMaxAge() {
			return maxAge;
		}

		public CorsRule setMaxAge(Long maxAge) {
			this.maxAge = maxAge;
			return this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CorsRule other = (CorsRule) obj;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CorsRule [path=" + path + ", allowCredentials=" + allowCredentials + ", allowsOrigins=" + allowsOrigins
					+ ", allowsHeaders=" + allowsHeaders + ", allowsMethods=" + allowsMethods + "]";
		}

		/**
		 * To Spring CORS configuration
		 * 
		 * @return
		 */
		public CorsConfiguration toSpringCorsConfiguration() {
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
		 * Simple merge collections.
		 * 
		 * @param source
		 * @param dest
		 * @return
		 */
		private CorsRule mergeWithSimpleAll(Collection<String> source, Collection<String> dest) {
			Assert.notNull(source, "'source' must not be null");
			if (!CollectionUtils.isEmpty(dest)) {
				source.addAll(dest);
			}
			// Clear other specific item configurations if '*' is present
			Iterator<String> it = source.iterator();
			while (it.hasNext()) {
				String s = it.next();
				if (s.contains(ALL)) {
					source.clear();
					source.add(ALL);
					break;
				}
			}
			return this;
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
			if (ObjectUtils.isEmpty(getAllowedOrigins())) {
				return null;
			}
			if (getAllowedOrigins().contains(ALL)) {
				if (getAllowCredentials() != Boolean.TRUE) {
					return ALL;
				} else {
					return requestOrigin;
				}
			}
			for (String allowedOrigin : getAllowedOrigins()) {
				if (requestOrigin.equalsIgnoreCase(allowedOrigin)) {
					return requestOrigin;
				}
				// e.g. allowedOrigin is "http://*.aa.mydomain.com"
				if (isSameWithOrigin(allowedOrigin, requestOrigin, true)) {
					return requestOrigin;
				}
			}
			return null;
		}

	}

}