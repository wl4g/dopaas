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

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

import redis.clients.jedis.HostAndPort;
import static redis.clients.jedis.HostAndPort.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClusterCommand;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisNoReachableClusterNodeException;
import redis.clients.jedis.exceptions.JedisRedirectionException;

/**
 * {@link AdvancedJedisClusterCommand}
 * 
 * @param <T>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月28日 v1.0.0
 * @see
 */
public abstract class AdvancedJedisClusterCommand<T> extends JedisClusterCommand<T> {

	final protected SmartLogger log = getLogger(getClass());

	public AdvancedJedisClusterCommand(JedisClusterConnectionHandler connectionHandler, int maxAttempts) {
		super(connectionHandler, maxAttempts);
	}

	@Override
	public T execute(Jedis connection) {
		try {
			return doExecute(connection);
		} catch (JedisException e) {
			/**
			 * {@link redis.clients.jedis.JedisClusterCommand#runWithRetries}
			 */
			if ((e instanceof JedisRedirectionException) || (e instanceof JedisNoReachableClusterNodeException)) {
				throw e;
			}
			// Print details errors.
			String node = connection.getClient().getHost() + ":" + connection.getClient().getPort();
			String errmsg = format("Couldn't execution jedis command of node: %s", node);
			if (e instanceof JedisConnectionException) {
				throw new JedisConnectionException(errmsg, e);
			}
			throw new JedisException(errmsg, e);
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

		final protected SmartLogger log = getLogger(getClass());

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
			} catch (JedisException ex) {
				// Found current jedis node
				JedisPool jedisPool = cache.getSlotPool(slot);
				Optional<String> opt = cache.getNodes().entrySet().stream().filter(e -> e.getValue() == jedisPool)
						.map(e -> e.getKey()).findFirst();
				log.trace("Failed to getsConnectionFromSlot. slot: {}, redis.node.host: {}", slot, opt);

				// Print details errors.
				String errmsg = format("slot: %s", slot);
				if (opt.isPresent()) {
					errmsg = opt.get();
					// Tip: Redis(server) cluster configered warning.
					if (isBlank(parseString(errmsg).getHost())) {
						errmsg = format(
								"'%s', Please check the configuration of the redis-cluster(server). Is it bound (0.0.0.0:<port>)? You should let the redis server process listen for a specific host address!",
								errmsg);
					}
				}
				throw new JedisException(format("Can't get a resource for %s", errmsg), ex);
			}
		}

		@Override
		public Jedis getConnectionFromNode(HostAndPort node) {
			try {
				return super.getConnectionFromNode(node);
			} catch (JedisException e) {
				// Print details errors.
				throw new JedisException(format("Couldn't get a resource of '%s'", node), e);
			}
		}

	}

}