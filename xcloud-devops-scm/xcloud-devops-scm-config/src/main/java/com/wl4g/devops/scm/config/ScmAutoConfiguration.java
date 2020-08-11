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
package com.wl4g.devops.scm.config;

import static com.wl4g.components.core.constants.SCMDevOpsConstants.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.wl4g.components.common.crypto.asymmetric.RSACryptor;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.core.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.endpoint.ScmServerEndpoint;
import com.wl4g.devops.scm.handler.CentralConfigureHandler;
import com.wl4g.devops.scm.handler.CheckImpledCentralConfigureHandler;
import com.wl4g.devops.scm.publish.ConfigSourcePublisher;
import com.wl4g.devops.scm.publish.DefaultRedisConfigSourcePublisher;
//import com.wl4g.devops.scm.session.ConfigServerSecurityManager;

/**
 * SCM auto configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public class ScmAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.scm")
	public ScmProperties scmProperties() {
		return new ScmProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public CentralConfigureHandler configContextHandler() {
		return new CheckImpledCentralConfigureHandler();
	}

	@Bean
	public ConfigSourcePublisher configSourcePublisher(JedisService jedisService) {
		return new DefaultRedisConfigSourcePublisher(scmProperties(), jedisService);
	}

	@Bean(BEAN_MVC_EXECUTOR)
	public ThreadPoolTaskExecutor mvcTaskExecutor(ScmProperties config) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(config.getCorePoolSize());
		executor.setQueueCapacity(config.getQueueCapacity());
		executor.setMaxPoolSize(config.getMaxPoolSize());
		return executor;
	}

	@Bean
	public ScmWebMvcConfigurer scmWebMvcConfigurer(ScmProperties config,
			@Qualifier(BEAN_MVC_EXECUTOR) ThreadPoolTaskExecutor executor) {
		return new ScmWebMvcConfigurer(config, executor);
	}

//	@Bean
//	public ConfigServerSecurityManager scmServerConfigSecurityManager() {
//		return new ConfigServerSecurityManager(new RSACryptor(), new AES128ECBPKCS5());
//	}

	//
	// --- Endpoint's. ---
	//

	@Bean
	public ScmServerEndpoint scmServerEnndpoint() {
		return new ScmServerEndpoint();
	}

	@Bean
	public PrefixHandlerMapping scmServerEndpointPrefixHandlerMapping() {
		return super.newPrefixHandlerMapping(URI_S_BASE, ScmEndpoint.class);
	}

	final public static String BEAN_MVC_EXECUTOR = "mvcTaskExecutor";

}