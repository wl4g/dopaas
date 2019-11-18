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
package com.wl4g.devops.ci.analyses;

import static java.util.Objects.isNull;
import static org.springframework.util.Assert.notNull;

/**
 * Source code safety analyzer.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public interface CodeAnalyzer {

	/**
	 * Analyzer type definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static enum AnalyzerType {

		/** Analyzer for spotbugs. */
		SPOTBUGS(1);

		final private int value;

		private AnalyzerType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link AnalyzerType}
		 * 
		 * @param analyzer
		 * @return
		 */
		final public static AnalyzerType safeOf(Integer analyzer) {
			if (isNull(analyzer)) {
				return null;
			}
			for (AnalyzerType t : values()) {
				if (String.valueOf(analyzer).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Converter string to {@link AnalyzerType}
		 * 
		 * @param vcsProvider
		 * @return
		 */
		final public static AnalyzerType of(Integer vcsProvider) {
			AnalyzerType type = safeOf(vcsProvider);
			notNull(type, String.format("Unsupported VCS provider for %s", vcsProvider));
			return type;
		}

	}

	/**
	 * VCS provider type definition.
	 * 
	 * @return
	 */
	default AnalyzerType analyzerType() {
		throw new UnsupportedOperationException();
	}

	void analysis(ProjectModel model);

}
