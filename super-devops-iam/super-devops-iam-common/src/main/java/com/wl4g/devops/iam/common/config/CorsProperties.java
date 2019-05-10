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
import org.springframework.web.cors.CorsConfiguration;

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
			return this.merge(this.allowsOrigins, allowsOrigins);
		}

		public CorsRule addAllowsOrigin(String allowsOrigin) {
			return this.merge(this.allowsOrigins, Arrays.asList(allowsOrigin));
		}

		public Set<String> getAllowsHeaders() {
			return allowsHeaders;
		}

		public CorsRule setAllowsHeaders(Set<String> allowsHeaders) {
			return this.merge(this.allowsHeaders, allowsHeaders);
		}

		public CorsRule addAllowsHeader(String allowsHeader) {
			return this.merge(this.allowsHeaders, Arrays.asList(allowsHeader));
		}

		public Set<String> getAllowsMethods() {
			return allowsMethods;
		}

		public CorsRule setAllowsMethods(Set<String> allowsMethods) {
			return this.merge(this.allowsMethods, allowsMethods);
		}

		public CorsRule addAllowsMethod(String allowsMethod) {
			return this.merge(this.allowsMethods, Arrays.asList(allowsMethod));
		}

		public Set<String> getExposedHeaders() {
			return exposedHeaders;
		}

		public CorsRule setExposedHeaders(Set<String> exposedHeaders) {
			return this.merge(this.exposedHeaders, exposedHeaders);
		}

		public CorsRule addExposedHeader(String exposedHeader) {
			return this.merge(this.exposedHeaders, Arrays.asList(exposedHeader));
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
		 * To CORS configuration
		 * 
		 * @return
		 */
		public CorsConfiguration toCorsConfiguration() {
			CorsConfiguration cors = new CorsConfiguration();
			cors.setAllowCredentials(this.isAllowCredentials());
			cors.setMaxAge(this.getMaxAge());
			this.getAllowsOrigins().forEach(origin -> cors.addAllowedOrigin(origin));
			this.getAllowsHeaders().forEach(header -> cors.addAllowedHeader(header));
			this.getAllowsMethods().forEach(method -> cors.addAllowedMethod(method));
			this.getExposedHeaders().forEach(exposed -> cors.addExposedHeader(exposed));
			return cors;
		}

		/**
		 * Merge collections
		 * 
		 * @param source
		 * @param dest
		 * @return
		 */
		private CorsRule merge(Collection<String> source, Collection<String> dest) {
			Assert.notNull(source, "'source' must not be null");
			if (!CollectionUtils.isEmpty(dest)) {
				source.addAll(dest);
				// dest.forEach(val -> {
				// try {
				// HttpMethod.valueOf(val.toUpperCase());
				// source.add(val);
				// } catch (Exception e) {
				// throw new IllegalArgumentException(String.format("Illegal
				// arguments %s", val));
				// }
				// });
			}

			// Clear other specific item configurations if '*' is present
			Iterator<String> it = source.iterator();
			while (it.hasNext()) {
				if (it.next().contains(CorsConfiguration.ALL)) {
					source.clear();
					source.add(CorsConfiguration.ALL);
					break;
				}
			}
			return this;
		}

	}

}
