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
package com.wl4g.devops.iam.common.core;

import static com.wl4g.devops.components.tools.common.lang.Assert2.state;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

/**
 * IAM request matching filter chain URI pattern resolver
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月27日
 * @since
 */
public class IamPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {

	final protected SmartLogger log = getLogger(getClass());

	public IamPathMatchingFilterChainResolver() {
		super();
	}

	public IamPathMatchingFilterChainResolver(FilterConfig filterConfig) {
		super(filterConfig);
	}

	/**
	 * Follow the Maximum Matching Principle <br/>
	 * {@link org.springframework.web.servlet.handler.AbstractUrlHandlerMapping#lookupHandler}
	 */
	@Override
	public FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain originalChain) {
		FilterChainManager chainManager = getFilterChainManager();
		state(chainManager.hasChains(), "Shiro filter chain must be implemented");

		// Current request URI
		String requestURI = getPathWithinApplication(request);

		// Candidate matching pattern list.
		List<String> candidateMatchingPatterns = new ArrayList<>(4);

		/*
		 * the 'chain names' in this implementation are actually path patterns
		 * defined by the user. We just use them as the chain name for the
		 * FilterChainManager's requirements
		 */
		for (String registeredPattern : chainManager.getChainNames()) {
			if (pathMatches(registeredPattern, requestURI)) {
				log.trace("Matched path pattern:[{}] for requestURI:[{}]. Utilizing corresponding filter chain...",
						registeredPattern, requestURI);
				candidateMatchingPatterns.add(registeredPattern);
			}
		}
		Collections.sort(candidateMatchingPatterns, new AntPatternComparator(requestURI));
		String bestMatch = candidateMatchingPatterns.get(0); // Best
		return chainManager.proxy(originalChain, bestMatch);
	}

	/**
	 * The default {@link Comparator} implementation returned by
	 * {@link #getPatternComparator(String)}.
	 * <p>
	 * In order, the most "generic" pattern is determined by the following:
	 * <ul>
	 * <li>if it's null or a capture all pattern (i.e. it is equal to
	 * "/**")</li>
	 * <li>if the other pattern is an actual match</li>
	 * <li>if it's a catch-all pattern (i.e. it ends with "**"</li>
	 * <li>if it's got more "*" than the other pattern</li>
	 * <li>if it's got more "{foo}" than the other pattern</li>
	 * <li>if it's shorter than the other pattern</li>
	 * </ul>
	 * {@link org.springframework.util.AntPathMatcher.AntPatternComparator}
	 */
	private final static class AntPatternComparator implements Comparator<String> {
		private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");

		private final String path;

		public AntPatternComparator(String path) {
			this.path = path;
		}

		/**
		 * Compare two patterns to determine which should match first, i.e.
		 * which is the most specific regarding the current path.
		 *
		 * @return a negative integer, zero, or a positive integer as pattern1
		 *         is more specific, equally specific, or less specific than
		 *         pattern2.
		 */
		@Override
		public final int compare(String pattern1, String pattern2) {
			PatternInfo info1 = new PatternInfo(pattern1);
			PatternInfo info2 = new PatternInfo(pattern2);

			if (info1.isLeastSpecific() && info2.isLeastSpecific()) {
				return 0;
			} else if (info1.isLeastSpecific()) {
				return 1;
			} else if (info2.isLeastSpecific()) {
				return -1;
			}

			boolean pattern1EqualsPath = pattern1.equals(path);
			boolean pattern2EqualsPath = pattern2.equals(path);
			if (pattern1EqualsPath && pattern2EqualsPath) {
				return 0;
			} else if (pattern1EqualsPath) {
				return -1;
			} else if (pattern2EqualsPath) {
				return 1;
			}

			if (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0) {
				return 1;
			} else if (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0) {
				return -1;
			}

			if (info1.getTotalCount() != info2.getTotalCount()) {
				return info1.getTotalCount() - info2.getTotalCount();
			}

			if (info1.getLength() != info2.getLength()) {
				return info2.getLength() - info1.getLength();
			}

			if (info1.getSingleWildcards() < info2.getSingleWildcards()) {
				return -1;
			} else if (info2.getSingleWildcards() < info1.getSingleWildcards()) {
				return 1;
			}

			if (info1.getUriVars() < info2.getUriVars()) {
				return -1;
			} else if (info2.getUriVars() < info1.getUriVars()) {
				return 1;
			}

			return 0;
		}

		/**
		 * Value class that holds information about the pattern, e.g. number of
		 * occurrences of "*", "**", and "{" pattern elements.
		 */
		private final static class PatternInfo {

			private final String pattern;

			private int uriVars;

			private int singleWildcards;

			private int doubleWildcards;

			private boolean catchAllPattern;

			private boolean prefixPattern;

			private Integer length;

			public PatternInfo(String pattern) {
				this.pattern = pattern;
				if (this.pattern != null) {
					initCounters();
					this.catchAllPattern = this.pattern.equals("/**");
					this.prefixPattern = !this.catchAllPattern && this.pattern.endsWith("/**");
				}
				if (this.uriVars == 0) {
					this.length = (this.pattern != null ? this.pattern.length() : 0);
				}
			}

			protected void initCounters() {
				int pos = 0;
				while (pos < this.pattern.length()) {
					if (this.pattern.charAt(pos) == '{') {
						this.uriVars++;
						pos++;
					} else if (this.pattern.charAt(pos) == '*') {
						if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == '*') {
							this.doubleWildcards++;
							pos += 2;
						} else if (pos > 0 && !this.pattern.substring(pos - 1).equals(".*")) {
							this.singleWildcards++;
							pos++;
						} else {
							pos++;
						}
					} else {
						pos++;
					}
				}
			}

			public int getUriVars() {
				return this.uriVars;
			}

			public int getSingleWildcards() {
				return this.singleWildcards;
			}

			public int getDoubleWildcards() {
				return this.doubleWildcards;
			}

			public boolean isLeastSpecific() {
				return (this.pattern == null || this.catchAllPattern);
			}

			public boolean isPrefixPattern() {
				return this.prefixPattern;
			}

			public int getTotalCount() {
				return this.uriVars + this.singleWildcards + (2 * this.doubleWildcards);
			}

			/**
			 * Returns the length of the given pattern, where template variables
			 * are considered to be 1 long.
			 */
			public int getLength() {
				if (this.length == null) {
					this.length = VARIABLE_PATTERN.matcher(this.pattern).replaceAll("#").length();
				}
				return this.length;
			}
		}

	}

}