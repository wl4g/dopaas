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
package com.wl4g.devops.dts.codegen.engine.naming;

import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

/**
 * {@link JavaSpecsTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-17
 * @since
 */
public class JavaSpecsTests {

	static String copyright = "";

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

	public static void main(String[] args) {
		System.out.println("------- Input copyright: --------");
		System.out.println(copyright);

		System.out.println("\n\n\n\n");

		System.out.println("------- Output copyright: --------");
		System.out.println(JavaSpecs.escapeCopyright(copyright));
	}

}
