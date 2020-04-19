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
package com.wl4g.devops.components.webide.config;

import com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties;

/**
 * WebIdeProperties
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月31日
 * @since
 */
public class WebIdeProperties extends GenericEmbeddedWebappsProperties {

	final public static String KEY_WEBIDE_PREFIX = "spring.cloud.devops.components.webjars.nativejs-editor";
	final public static String URI_WEBIDE_BASE = "/webide/nativejs-editor";
	final public static String PATH_WEBIDE_WEBAPPS = "classpath*:/plugin-webapps" + URI_WEBIDE_BASE;

	public WebIdeProperties() {
		setBaseUri(URI_WEBIDE_BASE);
		setWebappLocation(PATH_WEBIDE_WEBAPPS);
	}

	/**
	 * {@link IdeLanguage}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年3月31日 v1.0.0
	 * @see
	 */
	public static enum IdeLanguage {

		Assembly_x86, // nasm(汇编)

		Cpp,

		Clujure,

		Coffee,

		Csharp, // C#

		D,

		Erlang,

		Golang,

		Groovy,

		Haskell,

		Java, // java8

		Javascript, // nodejs

		Lisp,

		Lua,

		ObjectiveC,

		Perl,

		Php, // php7

		Python, // python3

		R,

		Ruby,

		Scala,

		Rust,

		Sh; // shell

	}

}
