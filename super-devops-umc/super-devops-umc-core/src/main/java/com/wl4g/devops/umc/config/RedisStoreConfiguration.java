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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.support.redis.JedisService;

import de.codecentric.boot.admin.config.AdminServerProperties;
import de.codecentric.boot.admin.config.AdminServerWebConfiguration;
import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.journal.store.JournaledEventStore;
import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.store.ApplicationStore;

@Configuration
@ConditionalOnSingleCandidate(JedisService.class)
@ConditionalOnProperty(prefix = "spring.boot.admin.redis-store", name = "enabled", matchIfMissing = false)
@AutoConfigureBefore({ AdminServerWebConfiguration.class, AdminServerProperties.class })
public class RedisStoreConfiguration {

	@Value("${spring.boot.admin.redis.application-store:sba-application-store}")
	private String storeMapName;
	@Value("${spring.boot.admin.redis.event-store:sba-event-store}")
	private String eventListName;

	@Autowired
	private AdminServerProperties adminServerProperties;
	@Autowired
	private JedisService jedisService;

	@Bean
	public ApplicationStore applicationStore() {
		return new ApplicationStore() {

			@Override
			public Application save(Application app) {
				// Status life time.
				int lifeTime = (int) adminServerProperties.getMonitor().getStatusLifetime();

				Map<String, Object> map = jedisService.getObjectMap(storeMapName);
				map.put(app.getId(), app);
				jedisService.setObjectMap(storeMapName, map, lifeTime);
				return app;
			}

			@Override
			public Collection<Application> findByName(String name) {
				List<Application> list = new ArrayList<>();
				Map<String, Object> map = jedisService.getObjectMap(storeMapName);
				for (Entry<String, Object> e : map.entrySet()) {
					Application app = (Application) e.getValue();
					if (app.getName().equals(name)) {
						list.add(app);
					}
				}
				return list;
			}

			@Override
			public Collection<Application> findAll() {
				List<Application> list = new ArrayList<>();
				Map<String, Object> map = jedisService.getObjectMap(storeMapName);
				for (Entry<String, Object> e : map.entrySet()) {
					list.add((Application) e.getValue());
				}
				return list;
			}

			@Override
			public Application find(String id) {
				Map<String, Object> map = jedisService.getObjectMap(storeMapName);
				return (Application) map.get(id);
			}

			@Override
			public Application delete(String id) {
				// Status life time.
				int lifeTime = (int) adminServerProperties.getMonitor().getStatusLifetime();

				Map<String, Object> map = jedisService.getObjectMap(storeMapName);
				Application app = (Application) map.remove(id);
				jedisService.setObjectMap(storeMapName, map, lifeTime);
				return app;
			}
		};
	}

	@Bean
	public JournaledEventStore journaledEventStore() {
		return new JournaledEventStore() {

			@Override
			public void store(ClientApplicationEvent event) {
				jedisService.listObjectAdd(eventListName, event);
			}

			@Override
			public Collection<ClientApplicationEvent> findAll() {
				List<ClientApplicationEvent> events = jedisService.getObjectList(eventListName, ClientApplicationEvent.class);
				Collections.reverse(events);
				return events;
			}
		};
	}

}