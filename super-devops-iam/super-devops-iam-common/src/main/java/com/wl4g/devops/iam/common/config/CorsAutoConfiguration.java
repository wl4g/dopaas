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
package com.wl4g.devops.iam.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.wl4g.devops.iam.common.config.CorsProperties.KEY_CORS_PREFIX;
import static com.wl4g.devops.iam.common.config.AbstractIamConfiguration.ORDER_CORS_PRECEDENCE;

import com.wl4g.devops.iam.common.security.cors.CorsSecurityFilter;
import com.wl4g.devops.iam.common.security.cors.CorsSecurityFilter.IamCorsProcessor;

/**
 * Cors protection auto configuration.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2020年05月06日
 * @since
 */
public class CorsAutoConfiguration {

	//
	// C O R S _ P R O T E C T I O N _ C O N F I G's.
	//

	@Bean
	@ConditionalOnProperty(name = KEY_CORS_PREFIX + ".enabled", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_CORS_PREFIX)
	public CorsProperties corsProperties() {
		return new CorsProperties();
	}

	@Bean
	@ConditionalOnBean(CorsProperties.class)
	public IamCorsProcessor iamCorsProcessor() {
		return new IamCorsProcessor();
	}

	@Bean
	@ConditionalOnBean(CorsProperties.class)
	public CorsSecurityFilter corsSecurityFilter(CorsProperties config, IamCorsProcessor corsProcessor) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Merger transformation configuration
		config.getRules().forEach((key, rule) -> source.registerCorsConfiguration(key, rule.resolveIamCorsConfiguration()));
		CorsSecurityFilter filter = new CorsSecurityFilter(source);
		filter.setCorsProcessor(corsProcessor);
		return filter;
	}

	/**
	 * The requirement for using the instruction is that the creation of
	 * {@link CorsProperties} object beans must precede this</br>
	 * e.g.
	 *
	 * <pre>
	 * &#64;Bean
	 * public CorsProperties corsProperties() {
	 * 	...
	 * }
	 * </pre>
	 *
	 * <b style="color:red;font-size:40px">&nbsp;↑</b>
	 *
	 * <pre>
	 * &#64;Bean
	 * &#64;ConditionalOnBean(CorsProperties.class)
	 * public FilterRegistrationBean corsResolveSecurityFilterBean(CorsProperties config) {
	 * 	...
	 * }
	 * </pre>
	 */
	@Bean
	@ConditionalOnBean(CorsProperties.class)
	public FilterRegistrationBean<CorsSecurityFilter> corsResolveSecurityFilterBean(CorsSecurityFilter filter) {
		// Register CORS filter
		FilterRegistrationBean<CorsSecurityFilter> filterBean = new FilterRegistrationBean<>(filter);
		filterBean.setOrder(ORDER_CORS_PRECEDENCE);
		// Cannot use '/*' or it will not be added to the container chain (only
		// '/**')
		filterBean.addUrlPatterns("/*");
		return filterBean;
	}

}