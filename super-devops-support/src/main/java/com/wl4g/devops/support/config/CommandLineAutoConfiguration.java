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
package com.wl4g.devops.support.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.support.cli.NodeProcessManagerImpl;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.repository.DefaultProcessRepository;
import com.wl4g.devops.support.cli.repository.ProcessRepository;

/**
 * Command-line support auto configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.devops.support.cli.enable", matchIfMissing = false)
public class CommandLineAutoConfiguration {

	@Bean
	public ProcessRepository processRepository() {
		return new DefaultProcessRepository();
	}

	@Bean
	public DestroableProcessManager nodeProcessManagerImpl(ProcessRepository repository) {
		return new NodeProcessManagerImpl(repository);
	}

}