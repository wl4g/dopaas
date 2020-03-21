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
package com.wl4g.devops.rcm.config;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.rcm.RcmProvider;
import com.wl4g.devops.rcm.RiskAnalysisEngine;
import com.wl4g.devops.rcm.natives.NativeRiskAnalysisEngine;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * RCM core auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月15日
 * @since
 */
@Configuration
public class CoreRcmAutoConfiguration {
	final public static String KEY_PROPERTY_PREFIX = "spring.cloud.devops.rcm.native";

	@Bean
	@Validated
	@ConditionalOnProperty(name = KEY_PROPERTY_PREFIX + ".enable", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_PROPERTY_PREFIX)
	public NativeRcmProperties nativeCossProperties() {
		return new NativeRcmProperties();
	}

	@Bean
	@ConditionalOnBean(NativeRcmProperties.class)
	public RiskAnalysisEngine nativeRiskAnalysisEngine() {
		return new NativeRiskAnalysisEngine();
	}

	@Bean
	public GenericOperatorAdapter<RcmProvider, RiskAnalysisEngine> compositeCossEndpointAdapter(
			List<RiskAnalysisEngine> endpoints) {
		return new GenericOperatorAdapter<RcmProvider, RiskAnalysisEngine>(endpoints) {
		};
	}

}
