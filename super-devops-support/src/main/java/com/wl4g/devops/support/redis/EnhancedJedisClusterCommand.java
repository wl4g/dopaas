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

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import static redis.clients.jedis.HostAndPort.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClusterCommand;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.exceptions.JedisException;

/**
 * {@link EnhancedJedisClusterCommand}
 * 
 * @param <T>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月28日 v1.0.0
 * @see
 */
public abstract class EnhancedJedisClusterCommand<T> extends JedisClusterCommand<T> {

	public EnhancedJedisClusterCommand(JedisClusterConnectionHandler connectionHandler, int maxAttempts) {
		super(connectionHandler, maxAttempts);
	}

	@Override
	public T execute(Jedis connection) {
		try {
			return doExecute(connection);
		} catch (JedisException e) {
			// Print details errors.
			String node = connection.getClient().getHost() + ":" + connection.getClient().getPort();
			throw new JedisException(format("Couldn't execution jedis command of node: %s", node), e);
		}
	}

	/**
	 * Do executions.
	 * 
	 * @param connection
	 * @return
	 */
	public abstract T doExecute(Jedis connection);

	/**
	 * Delegate enhanced Jedis cluster connection handler.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年4月17日
	 * @since
	 */
	public static class EnhancedJedisClusterConntionHandler extends JedisSlotBasedConnectionHandler {

		@SuppressWarnings("rawtypes")
		public EnhancedJedisClusterConntionHandler(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig,
				int connectionTimeout, int soTimeout, String password) {
			super(nodes, poolConfig, connectionTimeout, soTimeout, password);
		}

		public Jedis getConnection() {
			return super.getConnection();
		}

		public Jedis getConnectionFromSlot(int slot) {
			try {
				return super.getConnectionFromSlot(slot);
			} catch (Exception ex) {
				// Found current jedis node
				JedisPool jedisPool = cache.getSlotPool(slot);
				Optional<String> opt = cache.getNodes().entrySet().stream().filter(e -> e.getValue() == jedisPool)
						.map(e -> e.getKey()).findFirst();
				// Print details errors.
				String tip = format("slot<%s>", slot);
				if (opt.isPresent()) {
					tip = opt.get();
					// Friendly tip: Whether redis cluster config is not
					// standard
					if (isBlank(parseString(tip).getHost())) {
						tip = format(
								"'%s', Please check the redis cluster configuration. e.g: listen(0.0.0.0:6379)? (it is recommended to explicitly listen to the host address!)",
								tip);
					}
				}
				throw new JedisException(format("Couldn't get a resource, %s", tip), ex);
			}
		}

		@Override
		public Jedis getConnectionFromNode(HostAndPort node) {
			try {
				return super.getConnectionFromNode(node);
			} catch (Exception e) {
				// Print details errors.
				throw new JedisException(format("Couldn't get a resource of '%s'", node), e);
			}
		}

	}

}
