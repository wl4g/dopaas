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
package com.wl4g.devops.umc.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.umc.notify.CompositeStatusChangeNotifier;

import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;

/**
 * Automatic configuration of custom message notification. <br/>
 * http://www.gdtarena.com/gdkc/javacxy/13554.html <br/>
 * Reference:
 * de.codecentric.boot.admin.config.NotifierConfiguration.SlackNotifierConfiguration.slackNotifier()
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月28日
 * @since
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.boot.admin.notify.composite", name = "enabled")
@AutoConfigureBefore({
		de.codecentric.boot.admin.server.config.AdminServerNotifierAutoConfiguration.CompositeNotifierConfiguration.class })
public class CompositeNotifierAutoConfiguration {

	// TODO
	@Bean
	@ConditionalOnMissingBean
	@ConfigurationProperties("spring.boot.admin.notify.composite")
	public CompositeStatusChangeNotifier compositeStatusChangeNotifier(InstanceRepository repository) {
		return new CompositeStatusChangeNotifier(repository);
	}

}