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
package com.wl4g.devops.gateway.server.config;

import com.wl4g.devops.components.tools.common.task.RunnerProperties;
import com.wl4g.devops.gateway.server.console.GatewayConsole;
import com.wl4g.devops.gateway.server.coordinate.RefreshableConfigurationCoordinator;
import com.wl4g.devops.gateway.server.redis.RedisRouteDefinitionRepository;
import com.wl4g.devops.gateway.server.route.RouteAlterHandler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JEDIS properties configuration.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月16日
 * @since
 */
@Configuration
public class GatewayServerAutoConfiguration {

	@Bean
	public RouteAlterHandler routeAlterHandler() {
		return new RouteAlterHandler();
	}

	@Bean
	public RouteDefinitionRepository redisRouteDefinitionRepository() {
		return new RedisRouteDefinitionRepository();
	}

	@Bean
	public RefreshableConfigurationCoordinator applicationTaskRunner() {
		return new RefreshableConfigurationCoordinator(new RunnerProperties().withConcurrency(1));
	}

	@Bean
	public GatewayConsole gatewayConsole() {
		return new GatewayConsole();
	}

	@Bean
	@ConfigurationProperties(prefix = "gateway")
	public GatewayRefreshProperties gatewayRefreshProperties() {
		return new GatewayRefreshProperties();
	}

}