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
package com.wl4g.devops.iam.web;

import javax.servlet.http.HttpServletRequest;

import com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties;
import com.wl4g.devops.common.web.embedded.GenericEmbeddedWebappsEndpoint;
import static com.wl4g.devops.tool.common.jvm.JvmRuntimeKit.*;

/**
 * Jssdk embedded webapps endpoint.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月10日
 * @since
 */
public class JssdkWebappsEndpoint extends GenericEmbeddedWebappsEndpoint {

	public JssdkWebappsEndpoint(GenericEmbeddedWebappsProperties config) {
		super(config);
	}

	@Override
	protected boolean preResponesPropertiesSet(String filepath, HttpServletRequest request) {
		// Only debug mode can access source code file.
		if (isJVMDebugging) {
			return true;
		} else if (filepath.contains(PATH_JSSDK_JS_FILE)) {
			return filepath.contains(PATH_JSSDK_MIN_FILE);
		} else {
			return true;
		}
	}

	final public static String PATH_JSSDK_MIN_FILE = ".min.";
	final public static String PATH_JSSDK_JS_FILE = ".js";

}
