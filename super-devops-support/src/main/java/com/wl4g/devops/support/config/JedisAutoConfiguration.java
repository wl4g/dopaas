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

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.redis.EnhancedJedisCluster;
import com.wl4g.devops.support.redis.JedisClusterFactoryBean;
import com.wl4g.devops.support.redis.JedisService;
import com.wl4g.devops.tool.common.log.SmartLogger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;
import static com.wl4g.devops.support.config.JedisAutoConfiguration.JedisProperties.*;

/**
 * JEDIS properties configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月16日
 * @since
 */
@Configuration
public class JedisAutoConfiguration {

	/**
	 * Resolving spring byName injection conflict.
	 */
	final public static String BEAN_NAME_REDIS = "JedisAutoConfiguration.JedisService.Bean";

	@Bean
	@ConditionalOnProperty(name = KEY_JEDIS_PREFIX + ".nodes", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_JEDIS_PREFIX)
	@ConditionalOnClass(JedisCluster.class)
	public JedisProperties jedisProperties() {
		return new JedisProperties();
	}

	@Bean
	@ConditionalOnBean(JedisProperties.class)
	public JedisClusterFactoryBean jedisClusterFactoryBean(JedisProperties properties) {
		return new JedisClusterFactoryBean(properties);
	}

	@Bean(BEAN_NAME_REDIS)
	@ConditionalOnBean(JedisProperties.class)
	public JedisService jedisService(EnhancedJedisCluster jedisCluster) {
		return new JedisService(jedisCluster);
	}

	@Bean
	@ConditionalOnBean(JedisService.class)
	public JedisLockManager jedisLockManager(JedisService jedisService) {
		return new JedisLockManager(jedisService);
	}

	/**
	 * JEDIS properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2018年9月16日
	 * @since
	 */
	public static class JedisProperties implements Serializable {
		final private static long serialVersionUID = 1906168160146495488L;
		final public static String KEY_JEDIS_PREFIX = "redis";
		final protected static Pattern DefaultNodePattern = Pattern.compile("^.+[:]\\d{1,9}\\s*$");

		protected SmartLogger log = getLogger(getClass());

		private List<String> nodes = new ArrayList<>();
		private String passwd;
		private int connTimeout = 10000;
		private int soTimeout = 10000;
		private int maxAttempts = 20;
		private JedisPoolConfig poolConfig = new JedisPoolConfig();

		public JedisProperties() {
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

		public JedisPoolConfig getPoolConfig() {
			return poolConfig;
		}

		public void setPoolConfig(JedisPoolConfig poolConfig) {
			this.poolConfig = poolConfig;
		}

		public synchronized Set<HostAndPort> parseHostAndPort() throws Exception {
			try {
				Set<HostAndPort> haps = new HashSet<HostAndPort>();
				for (String node : this.getNodes()) {
					boolean matched = DefaultNodePattern.matcher(node).matches();
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