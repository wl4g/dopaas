/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"));
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
package com.wl4g.devops.dts.codegen.engine.specs;

import static java.lang.System.out;

/**
 * {@link BaseSpecsTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-10-02
 * @sine v1.0.0
 * @see
 */
public class BaseSpecsTests {

	public static void main(String[] args) {
		out.println(BaseSpecs.cleanComment("abcdefgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\refgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\nefgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\r\nefgh123456"));
		out.println(BaseSpecs.cleanComment("abcd\r\nefgh\"jack\"123456"));
	}

}
