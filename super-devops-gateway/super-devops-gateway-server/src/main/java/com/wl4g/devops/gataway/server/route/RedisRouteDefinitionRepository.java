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
package com.wl4g.devops.gataway.server.route;

import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.gataway.server.redis.JedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis routing information persistence class This class contains route
 * persistence and route refresh of distributed cluster
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-21
 * @since
 */
public class RedisRouteDefinitionRepository extends AbstractRouteRepository {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private JedisService jedisService;

	private static final String REDIS_ROUTE_KEY = "GATEWAY_ROUTE";

	private static final String REDIS_NOTIFY_KEY = "GATEWAY_ROUTE_NOTIFY";

	/**
	 * 获取全部的路由信息
	 * 
	 * @return
	 */
	@Override
	protected Flux<RouteDefinition> getRouteDefinitionsByPermanent() {
		List<RouteDefinition> list = new ArrayList<>();
		Map<String, String> map = jedisService.getMap(REDIS_ROUTE_KEY);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			RouteDefinition routeDefinition = null;
			try {
				routeDefinition = JacksonUtils.parseJSON(value, RouteDefinition.class);
				list.add(routeDefinition);
			} catch (Exception e) {
				log.error("parseJSON fail");
			}
		}
		if (CollectionUtils.isEmpty(list)) {
			return Flux.empty();
		}
		return Flux.fromIterable(list);
	}

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		Map map = new HashMap();
		route.flatMap(routeDefinition -> {
			map.put(routeDefinition.getId(), JacksonUtils.toJSONString(routeDefinition));
			return Mono.empty();
		});
		jedisService.mapPut(REDIS_ROUTE_KEY, map);
		return Mono.empty();
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.flatMap(id -> {
			jedisService.mapRemove(REDIS_ROUTE_KEY, id);
			return Mono.empty();
		});
	}

	@Override
	public Mono<Void> notifyAllRefresh(NotifyType notifyType) {
		log.debug("send notify msg!");
		stringRedisTemplate.getConnectionFactory().getConnection().publish(REDIS_NOTIFY_KEY.getBytes(DEF_CHARTSET),
				notifyType.toString().getBytes(DEF_CHARTSET));
		return Mono.empty();
	}

}