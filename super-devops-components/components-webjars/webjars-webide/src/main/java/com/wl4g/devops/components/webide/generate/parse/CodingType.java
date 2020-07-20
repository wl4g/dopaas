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
package com.wl4g.devops.components.webide.generate.parse;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.lang.StringUtils2.endsWithIgnoreCase;

/**
 * {@link CodingType}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年4月19日 v1.0.0
 * @see
 */
public enum CodingType {

	JAVA8("jar", new Java8ClassesLibParser());

	final private String libType;

	final private LibParser parser;

	private CodingType(String libType, LibParser parser) {
		hasTextOf(libType, "libType");
		notNullOf(parser, "parser");
		this.libType = libType;
		this.parser = parser;
	}

	public String getLibType() {
		return libType;
	}

	public LibParser getParser() {
		return parser;
	}

	public boolean matchs(String libPath) {
		return endsWithIgnoreCase(libPath, getLibType());
	}
}