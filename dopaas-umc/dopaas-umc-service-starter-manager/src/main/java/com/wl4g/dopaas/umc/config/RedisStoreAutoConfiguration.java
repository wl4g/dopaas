/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.umc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.support.cache.jedis.JedisService;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.AdminServerWebConfiguration;

@Configuration
@ConditionalOnSingleCandidate(JedisService.class)
@ConditionalOnProperty(prefix = "spring.boot.admin.redis-store", name = "enabled", matchIfMissing = false)
@AutoConfigureBefore({ AdminServerWebConfiguration.class, AdminServerProperties.class })
public class RedisStoreAutoConfiguration {

	@Value("${spring.boot.admin.redis.application-store:sba-application-store}")
	protected String storeMapName;

	@Value("${spring.boot.admin.redis.event-store:sba-event-store}")
	protected String eventListName;

	@Autowired
	protected AdminServerProperties adminServerProperties;

	@Autowired
	protected JedisService jedisService;

	// TODO
	// ...

}