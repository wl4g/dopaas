/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.support.cache;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.wl4g.devops.support.config.JedisConfiguration.RedisProperties;

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
@ConfigurationProperties(prefix = "redis")
public class JedisClusterFactoryBean implements FactoryBean<JedisCluster>, InitializingBean {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	private RedisProperties config;
	private JedisCluster jedisCluster;

	public JedisClusterFactoryBean(RedisProperties properties) {
		this.config = properties;
		Assert.notNull(properties, "'properties' must not be null");
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
		Set<HostAndPort> haps = this.config.parseHostAndPort();
		// Create Jedis cluster
		if (StringUtils.isEmpty(this.config.getPasswd())) {
			this.jedisCluster = new JedisCluster(haps, config.getConnTimeout(), config.getSoTimeout(), config.getMaxAttempts(),
					config.getPoolConfig());
		} else {
			this.jedisCluster = new JedisCluster(haps, config.getConnTimeout(), config.getSoTimeout(), config.getMaxAttempts(),
					config.getPasswd(), config.getPoolConfig());
		}
		if (log.isInfoEnabled()) {
			log.info("Instantiated redis cluster: {}", haps);
		}
	}

}