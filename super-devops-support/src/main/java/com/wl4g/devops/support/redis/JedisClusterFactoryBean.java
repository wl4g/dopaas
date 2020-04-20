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
package com.wl4g.devops.support.redis;

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.support.config.JedisAutoConfiguration.JedisProperties;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * JEDIS cluster factory bean.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public class JedisClusterFactoryBean implements FactoryBean<JedisCluster>, InitializingBean {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	final private JedisProperties config;
	private JedisCluster jedisCluster;

	public JedisClusterFactoryBean(JedisProperties config) {
		notNullOf(config, "jedisProperties");
		this.config = config;
	}

	@Override
	public JedisCluster getObject() throws Exception {
		return jedisCluster;
	}

	@Override
	public Class<? extends JedisCluster> getObjectType() {
		return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Parse cluster node's
		Set<HostAndPort> haps = config.parseHostAndPort();
		haps.forEach(n -> log.info("=> Connecting to redis cluster node: {}", n));
		notEmptyOf(haps, "redisNodes");

		try {
			// Create REDIS cluster
			if (isBlank(config.getPasswd()))
				jedisCluster = new EnhancedJedisCluster(haps, config.getConnTimeout(), config.getSoTimeout(),
						config.getMaxAttempts(), config.getPoolConfig());
			else
				jedisCluster = new EnhancedJedisCluster(haps, config.getConnTimeout(), config.getSoTimeout(),
						config.getMaxAttempts(), config.getPasswd(), config.getPoolConfig());
		} catch (Exception e) {
			throw new IllegalStateException(format("Can't connect to redis cluster: %s", haps), e);
		}

	}

}