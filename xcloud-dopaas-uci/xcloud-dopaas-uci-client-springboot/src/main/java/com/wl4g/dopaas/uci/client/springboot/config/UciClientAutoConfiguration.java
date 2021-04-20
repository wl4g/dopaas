/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.client.springboot.config;

import static com.wl4g.component.common.lang.Assert2.notNullOf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wl4g.dopaas.common.constant.UciConstants;
import com.wl4g.dopaas.uci.client.springboot.web.WebMvcMetaRequestHandlerInterceptor;

/**
 * {@link UciClientAutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-20
 * @sine v1.0
 * @see
 */
public class UciClientAutoConfiguration {

	@ConfigurationProperties(prefix = UciConstants.KEY_UCI_CLIENT_PREFIX)
	@Bean
	public UciClientProperties uciClientProperties() {
		return new UciClientProperties();
	}

	@Bean
	public WebMvcMetaRequestHandlerInterceptor webMvcMetaRequestHandlerInterceptor() {
		return new WebMvcMetaRequestHandlerInterceptor();
	}

	@Bean
	public WebMvcMetaInterceptorConfigurer webMvcMetaInterceptorConfigurer(WebMvcMetaRequestHandlerInterceptor interceptor) {
		return new WebMvcMetaInterceptorConfigurer(interceptor);
	}

	static class WebMvcMetaInterceptorConfigurer implements WebMvcConfigurer {
		private final WebMvcMetaRequestHandlerInterceptor interceptor;

		public WebMvcMetaInterceptorConfigurer(WebMvcMetaRequestHandlerInterceptor interceptor) {
			this.interceptor = notNullOf(interceptor, "interceptor");
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(interceptor).addPathPatterns("/**");
		}
	}

}
