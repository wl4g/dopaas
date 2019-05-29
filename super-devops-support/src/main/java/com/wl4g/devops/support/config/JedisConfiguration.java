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
package com.wl4g.devops.support.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.support.cache.JedisClusterFactoryBean;
import com.wl4g.devops.support.cache.JedisService;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisException;

@Configuration
@ConditionalOnClass({ JedisCluster.class, GenericObjectPoolConfig.class })
@ConditionalOnMissingBean({ JedisCluster.class })
public class JedisConfiguration {

	@Bean
	public RedisProperties redisProperties() {
		return new RedisProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public JedisClusterFactoryBean jedisClusterFactoryBean(RedisProperties properties) {
		return new JedisClusterFactoryBean(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public JedisService jedisService(JedisCluster jedisCluster) {
		return new JedisService(jedisCluster);
	}

	@ConfigurationProperties(prefix = "redis")
	public static class RedisProperties implements Serializable {
		private static final long serialVersionUID = 1906168160146495488L;

		private static final Logger log = LoggerFactory.getLogger(RedisProperties.class);
		private static final Pattern pattern = Pattern.compile("^.+[:]\\d{1,9}\\s*$");

		private List<String> nodes = new ArrayList<>();
		private String passwd;
		private int connTimeout = 10000;
		private int soTimeout = 10000;
		private int maxAttempts = 20;
		private GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

		public RedisProperties() {
			// Initialize by default
			this.poolConfig.setMaxWaitMillis(10000);
			this.poolConfig.setMinIdle(10);
			this.poolConfig.setMaxIdle(100);
			this.poolConfig.setMaxTotal(60000);
		}

		public List<String> getNodes() {
			return nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

		public String getPasswd() {
			return passwd;
		}

		public void setPasswd(String passwd) {
			this.passwd = passwd;
		}

		public int getConnTimeout() {
			return connTimeout;
		}

		public void setConnTimeout(int connTimeout) {
			this.connTimeout = connTimeout;
		}

		public int getSoTimeout() {
			return soTimeout;
		}

		public void setSoTimeout(int soTimeout) {
			this.soTimeout = soTimeout;
		}

		public int getMaxAttempts() {
			return maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public GenericObjectPoolConfig getPoolConfig() {
			return poolConfig;
		}

		public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
			this.poolConfig = poolConfig;
		}

		public synchronized Set<HostAndPort> parseHostAndPort() throws Exception {
			try {
				Set<HostAndPort> haps = new HashSet<HostAndPort>();
				for (String node : this.getNodes()) {
					boolean matched = pattern.matcher(node).matches();
					if (!matched) {
						throw new IllegalArgumentException("illegal ip or port");
					}
					String[] addrString = node.split(":");
					HostAndPort hap = new HostAndPort(addrString[0].trim(), Integer.parseInt(addrString[1]));
					if (log.isDebugEnabled()) {
						log.debug("Redis node: {}", hap);
					}
					haps.add(hap);
				}
				return haps;
			} catch (Exception e) {
				throw new JedisException("Resolve of redis cluster configuration failure.", e);
			}
		}

	}

}