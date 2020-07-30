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

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

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

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.redis.jedis.AdvancedJedisCluster;
import com.wl4g.devops.support.redis.jedis.CompositeJedisFactoryBean;
import com.wl4g.devops.support.redis.jedis.JedisService;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * JEDIS properties configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月16日
 * @since
 */
@Configuration
public class JedisAutoConfiguration {

	@Bean
	@ConditionalOnProperty(name = KEY_JEDIS_PREFIX + ".enable", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_JEDIS_PREFIX)
	@ConditionalOnClass(JedisCluster.class)
	public JedisProperties jedisProperties() {
		return new JedisProperties();
	}

	@Bean
	@ConditionalOnBean(JedisProperties.class)
	public CompositeJedisFactoryBean compositeJedisFactoryBean(JedisProperties properties) {
		return new CompositeJedisFactoryBean(properties);
	}

	@Bean(BEAN_NAME_REDIS)
	@ConditionalOnBean(JedisProperties.class)
	public JedisService jedisService(AdvancedJedisCluster jedisCluster) {
		return new JedisService(jedisCluster);
	}

	@Bean
	@ConditionalOnBean(JedisService.class)
	public JedisLockManager jedisLockManager(JedisService jedisService) {
		return new JedisLockManager(jedisService);
	}

	/**
	 * Jedis properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2018年9月16日
	 * @since
	 */
	public static class JedisProperties implements Serializable {
		private final static long serialVersionUID = 1906168160146495488L;

		protected SmartLogger log = getLogger(getClass());

		private List<String> nodes = new ArrayList<>();
		private String passwd;
		private String clientName;
		private int connTimeout = 10_000;
		private int soTimeout = 10_000;
		private int maxAttempts = 20;
		private int database = 0;

		private JedisPoolConfig poolConfig = new JedisPoolConfig();
		private boolean safeMode = true;

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

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
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

		public int getDatabase() {
			return database;
		}

		public void setDatabase(int database) {
			this.database = database;
		}

		public JedisPoolConfig getPoolConfig() {
			return poolConfig;
		}

		public void setPoolConfig(JedisPoolConfig poolConfig) {
			this.poolConfig = poolConfig;
		}

		public boolean isSafeMode() {
			return safeMode;
		}

		public void setSafeMode(boolean safeMode) {
			this.safeMode = safeMode;
		}

		public synchronized Set<HostAndPort> parseHostAndPort() throws Exception {
			try {
				Set<HostAndPort> haps = new HashSet<HostAndPort>();
				for (String node : getNodes()) {
					boolean matched = defaultNodePattern.matcher(node).matches();
					if (!matched) {
						throw new IllegalArgumentException("illegal ip or port");
					}
					String[] addrString = node.split(":");
					HostAndPort hap = new HostAndPort(addrString[0].trim(), Integer.parseInt(addrString[1]));
					log.debug("Redis node: {}", hap);
					haps.add(hap);
				}
				return haps;
			} catch (Exception e) {
				throw new JedisException("Resolve of redis cluster configuration failure.", e);
			}
		}

		private final static Pattern defaultNodePattern = Pattern.compile("^.+[:]\\d{1,9}\\s*$");

	}

	/**
	 * That jedis configuration property key.
	 */
	final public static String KEY_JEDIS_PREFIX = "redis";

	/**
	 * Resolving spring byName injection conflict.
	 */
	final public static String BEAN_NAME_REDIS = "JedisAutoConfiguration.JedisService.Bean";

}