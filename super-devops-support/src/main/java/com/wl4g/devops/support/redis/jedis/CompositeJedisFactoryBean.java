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
package com.wl4g.devops.support.redis.jedis;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.lang.String.format;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.support.config.JedisAutoConfiguration.JedisProperties;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * Factory bean of {@link CompositeJedisOperatorsAdapter}.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public class CompositeJedisFactoryBean implements FactoryBean<CompositeJedisOperatorsAdapter>, InitializingBean {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	private final JedisProperties config;

	private CompositeJedisOperatorsAdapter jedisAdapter;

	public CompositeJedisFactoryBean(JedisProperties config) {
		notNullOf(config, "jedisProperties");
		this.config = config;
	}

	@Override
	public CompositeJedisOperatorsAdapter getObject() throws Exception {
		return jedisAdapter;
	}

	@Override
	public Class<?> getObjectType() {
		return (jedisAdapter != null ? jedisAdapter.getClass() : CompositeJedisOperatorsAdapter.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Parse cluster node's
		Set<HostAndPort> haps = config.parseHostAndPort();
		notEmptyOf(haps, "redisNodes");
		haps.forEach(n -> log.info("=> Connecting to redis cluster node: {}", n));

		try {
			if (checkJedisCluster()) { // cluster?
				jedisAdapter = new AdvancedJedisCluster(haps, config.getConnTimeout(), config.getSoTimeout(),
						config.getMaxAttempts(), config.getPasswd(), config.getPoolConfig(), config.isSafeMode());
			} else { // single
				HostAndPort hap = haps.iterator().next();
				JedisPool pool = new JedisPool(config.getPoolConfig(), hap.getHost(), hap.getPort(), config.getConnTimeout(),
						config.getSoTimeout(), config.getPasswd(), 0, config.getClientName(), false, null, null, null);
				jedisAdapter = new DelegateJedis(pool, config.isSafeMode());
			}
		} catch (Exception e) {
			throw new IllegalStateException(format("Can't connect to redis servers: %s", haps), e);
		}
	}

	/**
	 * Check current config in {@link JedisCluster} mode.
	 * 
	 * @return
	 */
	private boolean checkJedisCluster() {
		return config.getNodes().size() > 1;
	}

}