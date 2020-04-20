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
package com.wl4g.devops.iam.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties;
import com.wl4g.devops.iam.web.JssdkWebappsEndpoint;

/**
 * Iam web jssdk auto configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public class JssdkWebappsAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	final public static String JSSDK_LOCATION = "classpath*:/jssdk-webapps";
	final public static String URI_JSSDK_BASE = "/sdk";

	@Bean
	public JssdkWebappsEndpoint jssdkWebappsEndpoint() {
		return new JssdkWebappsEndpoint(new GenericEmbeddedWebappsProperties(URI_JSSDK_BASE, JSSDK_LOCATION) {
		});
	}

	@Bean
	@ConditionalOnBean(JssdkWebappsEndpoint.class)
	public PrefixHandlerMapping jssdkWebappsEndpointPrefixHandlerMapping(JssdkWebappsEndpoint jssdk) {
		return super.newPrefixHandlerMapping(URI_JSSDK_BASE, jssdk);
	}

}