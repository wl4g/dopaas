/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.udc.codegen.engine.specs;

import static java.lang.System.out;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

import org.junit.Test;

/**
 * {@link BaseSpecsTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-10-02
 * @sine v1.0.0
 * @see
 */
public class BaseSpecsTests {

	static String copyright = "";

	@Test
	public void cleanCommentCase() {
		out.println(BaseSpecs.cleanComment("abcdefgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\refgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\nefgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\r\nefgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\r\nefgh\"jack\"123456"));
	}

	@Test
	public void extractCommentCase() {
		out.println(BaseSpecs.extractComment("统计类型(1.计划完成 2.实际完成) ", "simple"));
		out.println(BaseSpecs.extractComment("统计类型(1.计划完成 2.实际完成) ", "wordSeg"));
	}

	@Test
	public void wrapCommentCase() {
		out.println("------- Input copyright: ---------\n");
		out.println(copyright);

		out.println("\n\n");

		out.println("------- Output copyright(multi): --------\n");
		out.println(BaseSpecs.wrapMultiComment(copyright));

		out.println("------- Output copyright(single '//'): ------\n");
		out.println(BaseSpecs.wrapSingleComment(copyright, "//"));

		out.println("------- Output copyright(single '#'): ------\n");
		out.println(BaseSpecs.wrapSingleComment(copyright, "#"));
	}

	static {
		copyright += "Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>    "
				+ LINE_SEPARATOR;
		copyright += "                                                                                                "
				+ LINE_SEPARATOR;
		copyright += "Licensed under the Apache License, Version 2.0 (the \"License\");                                 "
				+ LINE_SEPARATOR;
		copyright += "you may not use this file except in compliance with the License.                                "
				+ LINE_SEPARATOR;
		copyright += "You may obtain a copy of the License at                                                         "
				+ LINE_SEPARATOR;
		copyright += "                                                                                                "
				+ LINE_SEPARATOR;
		copyright += "     http://www.apache.org/licenses/LICENSE-2.0                                                 "
				+ LINE_SEPARATOR;
		copyright += "                                                                                                "
				+ LINE_SEPARATOR;
		copyright += "Unless required by applicable law or agreed to in writing, software                             "
				+ LINE_SEPARATOR;
		copyright += "distributed under the License is distributed on an \"AS IS\" BASIS,                               "
				+ LINE_SEPARATOR;
		copyright += "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.                        "
				+ LINE_SEPARATOR;
		copyright += "See the License for the specific language governing permissions and                             "
				+ LINE_SEPARATOR;
		copyright += "limitations under the License.  " + LINE_SEPARATOR;
	}

}