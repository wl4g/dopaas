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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.iam.common.security.replay.ReplayProtectionSecurityFilter;
import com.wl4g.devops.iam.common.security.replay.RequiresReplayMatcher;
import com.wl4g.devops.iam.common.security.replay.handler.DefaultReplayRejectHandler;
import com.wl4g.devops.iam.common.security.replay.handler.ReplayRejectHandler;

import static com.wl4g.devops.iam.common.config.AbstractIamConfiguration.ORDER_REPAY_PRECEDENCE;
import static com.wl4g.devops.iam.common.config.ReplayProperties.KEY_REPLAY_PREFIX;

/**
 * Replay attacks protection auto configuration.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2020年05月06日
 * @since
 */
public class ReplayAutoConfiguration {

	//
	// R E P L A Y _ P R O T E C T I O N _ C O N F I G's.
	//

	@Bean
	@ConditionalOnProperty(name = KEY_REPLAY_PREFIX + ".enabled", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_REPLAY_PREFIX)
	public ReplayProperties replayProperties() {
		return new ReplayProperties();
	}

	@Bean
	@ConditionalOnBean(ReplayProperties.class)
	public RequiresReplayMatcher requiresReplayMatcher() {
		return new RequiresReplayMatcher();
	}

	@Bean
	@ConditionalOnBean(ReplayProperties.class)
	@ConditionalOnMissingBean(ReplayRejectHandler.class)
	public DefaultReplayRejectHandler defaultReplayRejectHandler() {
		return new DefaultReplayRejectHandler();
	}

	@Bean
	@ConditionalOnBean(ReplayProperties.class)
	public ReplayProtectionSecurityFilter replayProtectionSecurityFilter() {
		return new ReplayProtectionSecurityFilter();
	}

	@Bean
	@ConditionalOnBean(ReplayProperties.class)
	public FilterRegistrationBean<ReplayProtectionSecurityFilter> replayProtectionSecurityFilterBean(
			ReplayProtectionSecurityFilter filter) {
		// Register XSRF filter
		FilterRegistrationBean<ReplayProtectionSecurityFilter> filterBean = new FilterRegistrationBean<>(filter);
		filterBean.setOrder(ORDER_REPAY_PRECEDENCE);
		// Cannot use '/*' or it will not be added to the container chain (only
		// '/**')
		filterBean.addUrlPatterns("/*"); // TODO config?
		return filterBean;
	}

}