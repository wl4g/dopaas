package com.wl4g.devops.components.webide.generate.parse;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.lang.StringUtils2.endsWithIgnoreCase;

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
