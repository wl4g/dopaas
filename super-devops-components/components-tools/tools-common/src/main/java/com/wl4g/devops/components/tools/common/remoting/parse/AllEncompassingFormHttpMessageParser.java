/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.devops.components.tools.common.remoting.parse;

import com.wl4g.devops.components.tools.common.lang.ClassUtils2;

/**
 * Extension of {@link FormHttpMessageParser}, adding support for JSON-based
 * parts.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年7月01日 v1.0.0
 * @see
 */
public class AllEncompassingFormHttpMessageParser extends FormHttpMessageParser {

	private static final boolean jackson2Present;

	static {
		ClassLoader classLoader = AllEncompassingFormHttpMessageParser.class.getClassLoader();
		jackson2Present = ClassUtils2.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)
				&& ClassUtils2.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
		// gsonPresent = ClassUtils2.isPresent("com.google.gson.Gson",
		// classLoader);
	}

	public AllEncompassingFormHttpMessageParser() {
		if (jackson2Present) {
			addPartParser(new MappingJackson2HttpMessageParser());
		}
	}

}
