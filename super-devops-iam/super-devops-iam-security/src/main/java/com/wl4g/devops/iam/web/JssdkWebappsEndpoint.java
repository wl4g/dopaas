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

import static com.wl4g.devops.components.tools.common.jvm.JvmRuntimeKit.*;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import javax.servlet.http.HttpServletRequest;

import com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties;
import com.wl4g.devops.common.web.embedded.GenericEmbeddedWebappsEndpoint;

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
		} else if (filepath.contains(DEFAULT_JSSDK_FILE_EXT)) {
			return filepath.contains(DEFAULT_JSSDK_FILE_BIN)
					|| equalsAnyIgnoreCase(request.getServerName(), "127.0.0.1", "0:0:0:0:0:0:0:1", "localhost");
		} else {
			return true;
		}
	}

	final public static String DEFAULT_JSSDK_FILE_BIN = ".min.";
	final public static String DEFAULT_JSSDK_FILE_EXT = ".js";

}