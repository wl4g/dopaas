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

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isAlpha;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.wl4g.devops.common.exception.framework.ArgumentsSpecificationException;
import com.wl4g.devops.tool.common.log.SmartLogger;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Client;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.JedisClusterHashTagUtil;
import redis.clients.util.KeyMergeUtil;
import redis.clients.util.SafeEncoder;

import static com.wl4g.devops.support.redis.EnhancedJedisClusterCommand.EnhancedJedisClusterConntionHandler;

/**
 * {@link EnhancedJedisCluster}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月28日 v1.0.0
 * @see
 */
public class EnhancedJedisCluster extends JedisCluster {

	final protected SmartLogger log = getLogger(getClass());

	@SuppressWarnings("rawtypes")
	public EnhancedJedisCluster(HostAndPort node, int connectionTimeout, int soTimeout, int maxAttempts,
			final GenericObjectPoolConfig poolConfig) {
		this(singleton(node), connectionTimeout, soTimeout, maxAttempts, null, poolConfig);
	}

	@SuppressWarnings("rawtypes")
	public EnhancedJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts,
			final GenericObjectPoolConfig poolConfig) {
		this(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, null, poolConfig);
	}

	@SuppressWarnings("rawtypes")
	public EnhancedJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts,
			String password, final GenericObjectPoolConfig poolConfig) {
		super(emptySet(), connectionTimeout, soTimeout, maxAttempts, null, null);
		// Overly jedisCluster connection handler
		this.connectionHandler = new EnhancedJedisClusterConntionHandler(jedisClusterNode, poolConfig, connectionTimeout,
				soTimeout, password);
	}

	@Override
	public String set(final String key, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.set(key, value);
			}
		}.run(key);
	}

	@Override
	public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.set(key, value, nxxx, expx, time);
			}
		}.run(key);
	}

	@Override
	public String get(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.get(key);
			}
		}.run(key);
	}

	@Override
	public Boolean exists(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.exists(key);
			}
		}.run(key);
	}

	@Override
	public Long exists(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.exists(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public Long persist(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.persist(key);
			}
		}.run(key);
	}

	@Override
	public String type(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.type(key);
			}
		}.run(key);
	}

	@Override
	public Long expire(final String key, final int seconds) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.expire(key, seconds);
			}
		}.run(key);
	}

	@Override
	public Long pexpire(final String key, final long milliseconds) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pexpire(key, milliseconds);
			}
		}.run(key);
	}

	@Override
	public Long expireAt(final String key, final long unixTime) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.expireAt(key, unixTime);
			}
		}.run(key);
	}

	@Override
	public Long pexpireAt(final String key, final long millisecondsTimestamp) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pexpireAt(key, millisecondsTimestamp);
			}
		}.run(key);
	}

	@Override
	public Long ttl(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.ttl(key);
			}
		}.run(key);
	}

	@Override
	public Long pttl(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pttl(key);
			}
		}.run(key);
	}

	@Override
	public Boolean setbit(final String key, final long offset, final boolean value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.setbit(key, offset, value);
			}
		}.run(key);
	}

	@Override
	public Boolean setbit(final String key, final long offset, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.setbit(key, offset, value);
			}
		}.run(key);
	}

	@Override
	public Boolean getbit(final String key, final long offset) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.getbit(key, offset);
			}
		}.run(key);
	}

	@Override
	public Long setrange(final String key, final long offset, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.setrange(key, offset, value);
			}
		}.run(key);
	}

	@Override
	public String getrange(final String key, final long startOffset, final long endOffset) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.getrange(key, startOffset, endOffset);
			}
		}.run(key);
	}

	@Override
	public String getSet(final String key, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.getSet(key, value);
			}
		}.run(key);
	}

	@Override
	public Long setnx(final String key, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.setnx(key, value);
			}
		}.run(key);
	}

	@Override
	public String setex(final String key, final int seconds, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.setex(key, seconds, value);
			}
		}.run(key);
	}

	@Override
	public String psetex(final String key, final long milliseconds, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.psetex(key, milliseconds, value);
			}
		}.run(key);
	}

	@Override
	public Long decrBy(final String key, final long integer) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.decrBy(key, integer);
			}
		}.run(key);
	}

	@Override
	public Long decr(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.decr(key);
			}
		}.run(key);
	}

	@Override
	public Long incrBy(final String key, final long integer) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.incrBy(key, integer);
			}
		}.run(key);
	}

	@Override
	public Double incrByFloat(final String key, final double value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.incrByFloat(key, value);
			}
		}.run(key);
	}

	@Override
	public Long incr(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.incr(key);
			}
		}.run(key);
	}

	@Override
	public Long append(final String key, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.append(key, value);
			}
		}.run(key);
	}

	@Override
	public String substr(final String key, final int start, final int end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.substr(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Long hset(final String key, final String field, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hset(key, field, value);
			}
		}.run(key);
	}

	@Override
	public String hget(final String key, final String field) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.hget(key, field);
			}
		}.run(key);
	}

	@Override
	public Long hsetnx(final String key, final String field, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hsetnx(key, field, value);
			}
		}.run(key);
	}

	@Override
	public String hmset(final String key, final Map<String, String> hash) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.hmset(key, hash);
			}
		}.run(key);
	}

	@Override
	public List<String> hmget(final String key, final String... fields) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.hmget(key, fields);
			}
		}.run(key);
	}

	@Override
	public Long hincrBy(final String key, final String field, final long value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hincrBy(key, field, value);
			}
		}.run(key);
	}

	@Override
	public Double hincrByFloat(final String key, final String field, final double value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.hincrByFloat(key, field, value);
			}
		}.run(key);
	}

	@Override
	public Boolean hexists(final String key, final String field) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.hexists(key, field);
			}
		}.run(key);
	}

	@Override
	public Long hdel(final String key, final String... field) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hdel(key, field);
			}
		}.run(key);
	}

	@Override
	public Long hlen(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hlen(key);
			}
		}.run(key);
	}

	@Override
	public Set<String> hkeys(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.hkeys(key);
			}
		}.run(key);
	}

	@Override
	public List<String> hvals(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.hvals(key);
			}
		}.run(key);
	}

	@Override
	public Map<String, String> hgetAll(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Map<String, String>>(connectionHandler, maxAttempts) {
			@Override
			public Map<String, String> doExecute(Jedis connection) {
				return connection.hgetAll(key);
			}
		}.run(key);
	}

	@Override
	public Long rpush(final String key, final String... string) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.rpush(key, string);
			}
		}.run(key);
	}

	@Override
	public Long lpush(final String key, final String... string) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.lpush(key, string);
			}
		}.run(key);
	}

	@Override
	public Long llen(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.llen(key);
			}
		}.run(key);
	}

	@Override
	public List<String> lrange(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.lrange(key, start, end);
			}
		}.run(key);
	}

	@Override
	public String ltrim(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.ltrim(key, start, end);
			}
		}.run(key);
	}

	@Override
	public String lindex(final String key, final long index) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.lindex(key, index);
			}
		}.run(key);
	}

	@Override
	public String lset(final String key, final long index, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.lset(key, index, value);
			}
		}.run(key);
	}

	@Override
	public Long lrem(final String key, final long count, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.lrem(key, count, value);
			}
		}.run(key);
	}

	@Override
	public String lpop(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.lpop(key);
			}
		}.run(key);
	}

	@Override
	public String rpop(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.rpop(key);
			}
		}.run(key);
	}

	@Override
	public Long sadd(final String key, final String... member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sadd(key, member);
			}
		}.run(key);
	}

	@Override
	public Set<String> smembers(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.smembers(key);
			}
		}.run(key);
	}

	@Override
	public Long srem(final String key, final String... member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.srem(key, member);
			}
		}.run(key);
	}

	@Override
	public String spop(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.spop(key);
			}
		}.run(key);
	}

	@Override
	public Set<String> spop(final String key, final long count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.spop(key, count);
			}
		}.run(key);
	}

	@Override
	public Long scard(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.scard(key);
			}
		}.run(key);
	}

	@Override
	public Boolean sismember(final String key, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.sismember(key, member);
			}
		}.run(key);
	}

	@Override
	public String srandmember(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.srandmember(key);
			}
		}.run(key);
	}

	@Override
	public List<String> srandmember(final String key, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.srandmember(key, count);
			}
		}.run(key);
	}

	@Override
	public Long strlen(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.strlen(key);
			}
		}.run(key);
	}

	@Override
	public Long zadd(final String key, final double score, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, score, member);
			}
		}.run(key);
	}

	@Override
	public Long zadd(final String key, final double score, final String member, final ZAddParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, score, member, params);
			}
		}.run(key);
	}

	@Override
	public Long zadd(final String key, final Map<String, Double> scoreMembers) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, scoreMembers);
			}
		}.run(key);
	}

	@Override
	public Long zadd(final String key, final Map<String, Double> scoreMembers, final ZAddParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, scoreMembers, params);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrange(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrange(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Long zrem(final String key, final String... member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zrem(key, member);
			}
		}.run(key);
	}

	@Override
	public Double zincrby(final String key, final double score, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.zincrby(key, score, member);
			}
		}.run(key);
	}

	@Override
	public Double zincrby(final String key, final double score, final String member, final ZIncrByParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.zincrby(key, score, member, params);
			}
		}.run(key);
	}

	@Override
	public Long zrank(final String key, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zrank(key, member);
			}
		}.run(key);
	}

	@Override
	public Long zrevrank(final String key, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zrevrank(key, member);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrange(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrange(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeWithScores(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeWithScores(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Long zcard(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zcard(key);
			}
		}.run(key);
	}

	@Override
	public Double zscore(final String key, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.zscore(key, member);
			}
		}.run(key);
	}

	@Override
	public List<String> sort(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.sort(key);
			}
		}.run(key);
	}

	@Override
	public List<String> sort(final String key, final SortingParams sortingParameters) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.sort(key, sortingParameters);
			}
		}.run(key);
	}

	@Override
	public Long zcount(final String key, final double min, final double max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zcount(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Long zcount(final String key, final String min, final String max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zcount(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		}.run(key);
	}

	@Override
	public Long zremrangeByRank(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByRank(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Long zremrangeByScore(final String key, final double start, final double end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByScore(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Long zremrangeByScore(final String key, final String start, final String end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByScore(key, start, end);
			}
		}.run(key);
	}

	@Override
	public Long zlexcount(final String key, final String min, final String max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zlexcount(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrangeByLex(final String key, final String min, final String max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrangeByLex(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrangeByLex(final String key, final String min, final String max, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrangeByLex(key, min, max, offset, count);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrangeByLex(final String key, final String max, final String min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrangeByLex(key, max, min);
			}
		}.run(key);
	}

	@Override
	public Set<String> zrevrangeByLex(final String key, final String max, final String min, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.zrevrangeByLex(key, max, min, offset, count);
			}
		}.run(key);
	}

	@Override
	public Long zremrangeByLex(final String key, final String min, final String max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByLex(key, min, max);
			}
		}.run(key);
	}

	@Override
	public Long linsert(final String key, final LIST_POSITION where, final String pivot, final String value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.linsert(key, where, pivot, value);
			}
		}.run(key);
	}

	@Override
	public Long lpushx(final String key, final String... string) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.lpushx(key, string);
			}
		}.run(key);
	}

	@Override
	public Long rpushx(final String key, final String... string) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.rpushx(key, string);
			}
		}.run(key);
	}

	@Override
	public Long del(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.del(key);
			}
		}.run(key);
	}

	@Override
	public String echo(final String string) {
		// note that it'll be run from arbitary node
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.echo(string);
			}
		}.run(string);
	}

	@Override
	public Long bitcount(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitcount(key);
			}
		}.run(key);
	}

	@Override
	public Long bitcount(final String key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitcount(key, start, end);
			}
		}.run(key);
	}

	@Override
	public ScanResult<String> scan(final String cursor, final ScanParams params) {
		String matchPattern = null;

		if (params == null || (matchPattern = scanMatch(params)) == null || matchPattern.isEmpty()) {
			throw new IllegalArgumentException(
					getClass().getSimpleName() + " only supports SCAN commands with non-empty MATCH patterns");
		}

		if (JedisClusterHashTagUtil.isClusterCompliantMatchPattern(matchPattern)) {

			return new EnhancedJedisClusterCommand<ScanResult<String>>(connectionHandler, maxAttempts) {
				@Override
				public ScanResult<String> doExecute(Jedis connection) {
					return connection.scan(cursor, params);
				}
			}.runBinary(SafeEncoder.encode(matchPattern));
		} else {
			throw new IllegalArgumentException(getClass().getSimpleName()
					+ " only supports SCAN commands with MATCH patterns containing hash-tags ( curly-brackets enclosed strings )");
		}
	}

	@Override
	public Long bitpos(final String key, final boolean value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitpos(key, value);
			}
		}.run(key);
	}

	@Override
	public Long bitpos(final String key, final boolean value, final BitPosParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitpos(key, value, params);
			}
		}.run(key);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final String cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Entry<String, String>>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Entry<String, String>> doExecute(Jedis connection) {
				return connection.hscan(key, cursor);
			}
		}.run(key);
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final String cursor, final ScanParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Entry<String, String>>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Entry<String, String>> doExecute(Jedis connection) {
				return connection.hscan(key, cursor, params);
			}
		}.run(key);
	}

	@Override
	public ScanResult<String> sscan(final String key, final String cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<String>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<String> doExecute(Jedis connection) {
				return connection.sscan(key, cursor);
			}
		}.run(key);
	}

	@Override
	public ScanResult<String> sscan(final String key, final String cursor, final ScanParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<String>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<String> doExecute(Jedis connection) {
				return connection.sscan(key, cursor, params);
			}
		}.run(key);
	}

	@Override
	public ScanResult<Tuple> zscan(final String key, final String cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Tuple> doExecute(Jedis connection) {
				return connection.zscan(key, cursor);
			}
		}.run(key);
	}

	@Override
	public ScanResult<Tuple> zscan(final String key, final String cursor, final ScanParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Tuple> doExecute(Jedis connection) {
				return connection.zscan(key, cursor, params);
			}
		}.run(key);
	}

	@Override
	public Long pfadd(final String key, final String... elements) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pfadd(key, elements);
			}
		}.run(key);
	}

	@Override
	public long pfcount(final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pfcount(key);
			}
		}.run(key);
	}

	@Override
	public List<String> blpop(final int timeout, final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.blpop(timeout, key);
			}
		}.run(key);
	}

	@Override
	public List<String> brpop(final int timeout, final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.brpop(timeout, key);
			}
		}.run(key);
	}

	@Override
	public Long del(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.del(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public List<String> blpop(final int timeout, final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.blpop(timeout, keys);
			}
		}.run(keys.length, keys);

	}

	@Override
	public List<String> brpop(final int timeout, final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.brpop(timeout, keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public List<String> mget(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.mget(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public String mset(final String... keysvalues) {
		String[] keys = new String[keysvalues.length / 2];

		for (int keyIdx = 0; keyIdx < keys.length; keyIdx++) {
			keys[keyIdx] = keysvalues[keyIdx * 2];
		}

		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.mset(keysvalues);
			}
		}.run(keys.length, keys);
	}

	@Override
	public Long msetnx(final String... keysvalues) {
		String[] keys = new String[keysvalues.length / 2];

		for (int keyIdx = 0; keyIdx < keys.length; keyIdx++) {
			keys[keyIdx] = keysvalues[keyIdx * 2];
		}

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.msetnx(keysvalues);
			}
		}.run(keys.length, keys);
	}

	@Override
	public String rename(final String oldkey, final String newkey) {
		checkArgumentsSpecification(oldkey);
		checkArgumentsSpecification(newkey);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.rename(oldkey, newkey);
			}
		}.run(2, oldkey, newkey);
	}

	@Override
	public Long renamenx(final String oldkey, final String newkey) {
		checkArgumentsSpecification(oldkey);
		checkArgumentsSpecification(newkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.renamenx(oldkey, newkey);
			}
		}.run(2, oldkey, newkey);
	}

	@Override
	public String rpoplpush(final String srckey, final String dstkey) {
		checkArgumentsSpecification(srckey);
		checkArgumentsSpecification(dstkey);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.rpoplpush(srckey, dstkey);
			}
		}.run(2, srckey, dstkey);
	}

	@Override
	public Set<String> sdiff(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.sdiff(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public Long sdiffstore(final String dstkey, final String... keys) {
		checkArgumentsSpecification(dstkey);
		checkArgumentsSpecification(keys);
		String[] mergedKeys = KeyMergeUtil.merge(dstkey, keys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sdiffstore(dstkey, keys);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public Set<String> sinter(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.sinter(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public Long sinterstore(final String dstkey, final String... keys) {
		checkArgumentsSpecification(dstkey);
		checkArgumentsSpecification(keys);
		String[] mergedKeys = KeyMergeUtil.merge(dstkey, keys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sinterstore(dstkey, keys);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public Long smove(final String srckey, final String dstkey, final String member) {
		checkArgumentsSpecification(srckey, dstkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.smove(srckey, dstkey, member);
			}
		}.run(2, srckey, dstkey);
	}

	@Override
	public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
		checkArgumentsSpecification(key, dstkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sort(key, sortingParameters, dstkey);
			}
		}.run(2, key, dstkey);
	}

	@Override
	public Long sort(final String key, final String dstkey) {
		checkArgumentsSpecification(key, dstkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sort(key, dstkey);
			}
		}.run(2, key, dstkey);
	}

	@Override
	public Set<String> sunion(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Set<String>>(connectionHandler, maxAttempts) {
			@Override
			public Set<String> doExecute(Jedis connection) {
				return connection.sunion(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public Long sunionstore(final String dstkey, final String... keys) {
		checkArgumentsSpecification(dstkey);
		checkArgumentsSpecification(keys);
		String[] wholeKeys = KeyMergeUtil.merge(dstkey, keys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sunionstore(dstkey, keys);
			}
		}.run(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long zinterstore(final String dstkey, final String... sets) {
		checkArgumentsSpecification(dstkey);
		String[] wholeKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zinterstore(dstkey, sets);
			}
		}.run(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
		checkArgumentsSpecification(dstkey);
		String[] mergedKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zinterstore(dstkey, params, sets);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public Long zunionstore(final String dstkey, final String... sets) {
		checkArgumentsSpecification(dstkey);
		String[] mergedKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zunionstore(dstkey, sets);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
		checkArgumentsSpecification(dstkey);
		String[] mergedKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zunionstore(dstkey, params, sets);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public String brpoplpush(final String source, final String destination, final int timeout) {
		checkArgumentsSpecification(source, destination);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.brpoplpush(source, destination, timeout);
			}
		}.run(2, source, destination);
	}

	@Override
	public Long publish(final String channel, final String message) {
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.publish(channel, message);
			}
		}.runWithAnyNode();
	}

	@Override
	public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
		new EnhancedJedisClusterCommand<Integer>(connectionHandler, maxAttempts) {
			@Override
			public Integer doExecute(Jedis connection) {
				connection.subscribe(jedisPubSub, channels);
				return 0;
			}
		}.runWithAnyNode();
	}

	@Override
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
		new EnhancedJedisClusterCommand<Integer>(connectionHandler, maxAttempts) {
			@Override
			public Integer doExecute(Jedis connection) {
				connection.psubscribe(jedisPubSub, patterns);
				return 0;
			}
		}.runWithAnyNode();
	}

	@Override
	public Long bitop(final BitOP op, final String destKey, final String... srcKeys) {
		checkArgumentsSpecification(destKey);
		checkArgumentsSpecification(srcKeys);
		String[] mergedKeys = KeyMergeUtil.merge(destKey, srcKeys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitop(op, destKey, srcKeys);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public String pfmerge(final String destkey, final String... sourcekeys) {
		checkArgumentsSpecification(destkey);
		checkArgumentsSpecification(sourcekeys);
		String[] mergedKeys = KeyMergeUtil.merge(destkey, sourcekeys);

		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.pfmerge(destkey, sourcekeys);
			}
		}.run(mergedKeys.length, mergedKeys);
	}

	@Override
	public long pfcount(final String... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pfcount(keys);
			}
		}.run(keys.length, keys);
	}

	@Override
	public Object eval(final String script, final int keyCount, final String... params) {
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script, keyCount, params);
			}
		}.run(keyCount, params);
	}

	@Override
	public Object eval(final String script, final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script);
			}
		}.run(key);
	}

	@Override
	public Object eval(final String script, final List<String> keys, final List<String> args) {
		RedisFormatUtils.checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script, keys, args);
			}
		}.run(keys.size(), keys.toArray(new String[keys.size()]));
	}

	@Override
	public Object evalsha(final String sha1, final int keyCount, final String... params) {
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.evalsha(sha1, keyCount, params);
			}
		}.run(keyCount, params);
	}

	@Override
	public Object evalsha(final String sha1, final List<String> keys, final List<String> args) {
		RedisFormatUtils.checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.evalsha(sha1, keys, args);
			}
		}.run(keys.size(), keys.toArray(new String[keys.size()]));
	}

	@Override
	public Object evalsha(final String script, final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.evalsha(script);
			}
		}.run(key);
	}

	@Override
	public Boolean scriptExists(final String sha1, final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.scriptExists(sha1);
			}
		}.run(key);
	}

	@Override
	public List<Boolean> scriptExists(final String key, final String... sha1) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<Boolean>>(connectionHandler, maxAttempts) {
			@Override
			public List<Boolean> doExecute(Jedis connection) {
				return connection.scriptExists(sha1);
			}
		}.run(key);
	}

	@Override
	public String scriptLoad(final String script, final String key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.scriptLoad(script);
			}
		}.run(key);
	}

	/*
	 * below methods will be removed at 3.0
	 */

	/**
	 * @deprecated SetParams is scheduled to be introduced at next major release
	 *             Please use setnx instead for now
	 * @see <a href="https://github.com/xetorthio/jedis/pull/878">issue#878</a>
	 */
	@Deprecated
	@Override
	public String set(String key, String value, String nxxx) {
		checkArgumentsSpecification(key);
		return setnx(key, value) == 1 ? "OK" : null;
	}

	/**
	 * @deprecated unusable command, this will be removed at next major release.
	 */
	@Deprecated
	@Override
	public List<String> blpop(final String arg) {
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.blpop(arg);
			}
		}.run(arg);
	}

	/**
	 * @deprecated unusable command, this will be removed at next major release.
	 */
	@Deprecated
	@Override
	public List<String> brpop(final String arg) {
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.brpop(arg);
			}
		}.run(arg);
	}

	/**
	 * @deprecated Redis Cluster uses only db index 0, so it doesn't make sense.
	 *             scheduled to be removed on next major release
	 */
	@Deprecated
	@Override
	public Long move(final String key, final int dbIndex) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.move(key, dbIndex);
			}
		}.run(key);
	}

	/**
	 * This method is deprecated due to bug (scan cursor should be unsigned
	 * long) And will be removed on next major release
	 * 
	 * @see <a href=
	 *      "https://github.com/xetorthio/jedis/issues/531">issue#531</a>
	 */
	@Deprecated
	@Override
	public ScanResult<Entry<String, String>> hscan(final String key, final int cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Entry<String, String>>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Entry<String, String>> doExecute(Jedis connection) {
				return connection.hscan(key, cursor);
			}
		}.run(key);
	}

	/**
	 * This method is deprecated due to bug (scan cursor should be unsigned
	 * long) And will be removed on next major release
	 * 
	 * @see <a href=
	 *      "https://github.com/xetorthio/jedis/issues/531">issue#531</a>
	 */
	@Deprecated
	@Override
	public ScanResult<String> sscan(final String key, final int cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<String>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<String> doExecute(Jedis connection) {
				return connection.sscan(key, cursor);
			}
		}.run(key);
	}

	/**
	 * This method is deprecated due to bug (scan cursor should be unsigned
	 * long) And will be removed on next major release
	 * 
	 * @see <a href=
	 *      "https://github.com/xetorthio/jedis/issues/531">issue#531</a>
	 */
	@Deprecated
	@Override
	public ScanResult<Tuple> zscan(final String key, final int cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Tuple> doExecute(Jedis connection) {
				return connection.zscan(key, cursor);
			}
		}.run(key);
	}

	@Override
	public Long geoadd(final String key, final double longitude, final double latitude, final String member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.geoadd(key, longitude, latitude, member);
			}
		}.run(key);
	}

	@Override
	public Long geoadd(final String key, final Map<String, GeoCoordinate> memberCoordinateMap) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.geoadd(key, memberCoordinateMap);
			}
		}.run(key);
	}

	@Override
	public Double geodist(final String key, final String member1, final String member2) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.geodist(key, member1, member2);
			}
		}.run(key);
	}

	@Override
	public Double geodist(final String key, final String member1, final String member2, final GeoUnit unit) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.geodist(key, member1, member2, unit);
			}
		}.run(key);
	}

	@Override
	public List<String> geohash(final String key, final String... members) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<String>>(connectionHandler, maxAttempts) {
			@Override
			public List<String> doExecute(Jedis connection) {
				return connection.geohash(key, members);
			}
		}.run(key);
	}

	@Override
	public List<GeoCoordinate> geopos(final String key, final String... members) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoCoordinate>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoCoordinate> doExecute(Jedis connection) {
				return connection.geopos(key, members);
			}
		}.run(key);
	}

	@Override
	public List<GeoRadiusResponse> georadius(final String key, final double longitude, final double latitude, final double radius,
			final GeoUnit unit) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadius(key, longitude, latitude, radius, unit);
			}
		}.run(key);
	}

	@Override
	public List<GeoRadiusResponse> georadius(final String key, final double longitude, final double latitude, final double radius,
			final GeoUnit unit, final GeoRadiusParam param) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadius(key, longitude, latitude, radius, unit, param);
			}
		}.run(key);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(final String key, final String member, final double radius,
			final GeoUnit unit) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadiusByMember(key, member, radius, unit);
			}
		}.run(key);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(final String key, final String member, final double radius,
			final GeoUnit unit, final GeoRadiusParam param) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadiusByMember(key, member, radius, unit, param);
			}
		}.run(key);
	}

	@Override
	public List<Long> bitfield(final String key, final String... arguments) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<Long>>(connectionHandler, maxAttempts) {
			@Override
			public List<Long> doExecute(Jedis connection) {
				return connection.bitfield(key, arguments);
			}
		}.run(key);
	}

	// --- BinaryJedisCluster. ---

	@Override
	public String set(final byte[] key, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.set(key, value);
			}
		}.runBinary(key);
	}

	@Override
	public String set(final byte[] key, final byte[] value, final byte[] nxxx, final byte[] expx, final long time) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.set(key, value, nxxx, expx, time);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] get(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.get(key);
			}
		}.runBinary(key);
	}

	@Override
	public Boolean exists(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.exists(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long exists(final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.exists(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public Long persist(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.persist(key);
			}
		}.runBinary(key);
	}

	@Override
	public String type(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.type(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long expire(final byte[] key, final int seconds) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.expire(key, seconds);
			}
		}.runBinary(key);
	}

	@Override
	public Long pexpire(final byte[] key, final long milliseconds) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pexpire(key, milliseconds);
			}
		}.runBinary(key);
	}

	@Override
	public Long expireAt(final byte[] key, final long unixTime) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.expireAt(key, unixTime);
			}
		}.runBinary(key);
	}

	@Override
	public Long pexpireAt(final byte[] key, final long millisecondsTimestamp) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pexpire(key, millisecondsTimestamp);
			}
		}.runBinary(key);
	}

	@Override
	public Long ttl(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.ttl(key);
			}
		}.runBinary(key);
	}

	@Override
	public Boolean setbit(final byte[] key, final long offset, final boolean value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.setbit(key, offset, value);
			}
		}.runBinary(key);
	}

	@Override
	public Boolean setbit(final byte[] key, final long offset, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.setbit(key, offset, value);
			}
		}.runBinary(key);
	}

	@Override
	public Boolean getbit(final byte[] key, final long offset) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.getbit(key, offset);
			}
		}.runBinary(key);
	}

	@Override
	public Long setrange(final byte[] key, final long offset, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.setrange(key, offset, value);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] getrange(final byte[] key, final long startOffset, final long endOffset) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.getrange(key, startOffset, endOffset);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] getSet(final byte[] key, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.getSet(key, value);
			}
		}.runBinary(key);
	}

	@Override
	public Long setnx(final byte[] key, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.setnx(key, value);
			}
		}.runBinary(key);
	}

	@Override
	public String setex(final byte[] key, final int seconds, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.setex(key, seconds, value);
			}
		}.runBinary(key);
	}

	@Override
	public Long decrBy(final byte[] key, final long integer) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.decrBy(key, integer);
			}
		}.runBinary(key);
	}

	@Override
	public Long decr(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.decr(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long incrBy(final byte[] key, final long integer) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.incrBy(key, integer);
			}
		}.runBinary(key);
	}

	@Override
	public Double incrByFloat(final byte[] key, final double value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.incrByFloat(key, value);
			}
		}.runBinary(key);
	}

	@Override
	public Long incr(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.incr(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long append(final byte[] key, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.append(key, value);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] substr(final byte[] key, final int start, final int end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.substr(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long hset(final byte[] key, final byte[] field, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hset(key, field, value);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] hget(final byte[] key, final byte[] field) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.hget(key, field);
			}
		}.runBinary(key);
	}

	@Override
	public Long hsetnx(final byte[] key, final byte[] field, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hsetnx(key, field, value);
			}
		}.runBinary(key);
	}

	@Override
	public String hmset(final byte[] key, final Map<byte[], byte[]> hash) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.hmset(key, hash);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> hmget(final byte[] key, final byte[]... fields) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.hmget(key, fields);
			}
		}.runBinary(key);
	}

	@Override
	public Long hincrBy(final byte[] key, final byte[] field, final long value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hincrBy(key, field, value);
			}
		}.runBinary(key);
	}

	@Override
	public Double hincrByFloat(final byte[] key, final byte[] field, final double value) {
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.hincrByFloat(key, field, value);
			}
		}.runBinary(key);
	}

	@Override
	public Boolean hexists(final byte[] key, final byte[] field) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.hexists(key, field);
			}
		}.runBinary(key);
	}

	@Override
	public Long hdel(final byte[] key, final byte[]... field) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hdel(key, field);
			}
		}.runBinary(key);
	}

	@Override
	public Long hlen(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.hlen(key);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> hkeys(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.hkeys(key);
			}
		}.runBinary(key);
	}

	@Override
	public Collection<byte[]> hvals(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Collection<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Collection<byte[]> doExecute(Jedis connection) {
				return connection.hvals(key);
			}
		}.runBinary(key);
	}

	@Override
	public Map<byte[], byte[]> hgetAll(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Map<byte[], byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Map<byte[], byte[]> doExecute(Jedis connection) {
				return connection.hgetAll(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long rpush(final byte[] key, final byte[]... args) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.rpush(key, args);
			}
		}.runBinary(key);
	}

	@Override
	public Long lpush(final byte[] key, final byte[]... args) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.lpush(key, args);
			}
		}.runBinary(key);
	}

	@Override
	public Long llen(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.llen(key);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> lrange(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.lrange(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public String ltrim(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.ltrim(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] lindex(final byte[] key, final long index) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.lindex(key, index);
			}
		}.runBinary(key);
	}

	@Override
	public String lset(final byte[] key, final long index, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.lset(key, index, value);
			}
		}.runBinary(key);
	}

	@Override
	public Long lrem(final byte[] key, final long count, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.lrem(key, count, value);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] lpop(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.lpop(key);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] rpop(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.rpop(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long sadd(final byte[] key, final byte[]... member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sadd(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> smembers(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.smembers(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long srem(final byte[] key, final byte[]... member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.srem(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] spop(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.spop(key);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> spop(final byte[] key, final long count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.spop(key, count);
			}
		}.runBinary(key);
	}

	@Override
	public Long scard(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.scard(key);
			}
		}.runBinary(key);
	}

	@Override
	public Boolean sismember(final byte[] key, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Boolean>(connectionHandler, maxAttempts) {
			@Override
			public Boolean doExecute(Jedis connection) {
				return connection.sismember(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] srandmember(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.srandmember(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long strlen(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.strlen(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long zadd(final byte[] key, final double score, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, score, member);
			}
		}.runBinary(key);
	}

	@Override
	public Long zadd(final byte[] key, final Map<byte[], Double> scoreMembers) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, scoreMembers);
			}
		}.runBinary(key);
	}

	@Override
	public Long zadd(final byte[] key, final double score, final byte[] member, final ZAddParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, score, member, params);
			}
		}.runBinary(key);
	}

	@Override
	public Long zadd(final byte[] key, final Map<byte[], Double> scoreMembers, final ZAddParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zadd(key, scoreMembers, params);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrange(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrange(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long zrem(final byte[] key, final byte[]... member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zrem(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public Double zincrby(final byte[] key, final double score, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.zincrby(key, score, member);
			}
		}.runBinary(key);
	}

	@Override
	public Double zincrby(final byte[] key, final double score, final byte[] member, final ZIncrByParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.zincrby(key, score, member, params);
			}
		}.runBinary(key);
	}

	@Override
	public Long zrank(final byte[] key, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zrank(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public Long zrevrank(final byte[] key, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zrevrank(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrange(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrange(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrangeWithScores(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeWithScores(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeWithScores(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long zcard(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zcard(key);
			}
		}.runBinary(key);
	}

	@Override
	public Double zscore(final byte[] key, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.zscore(key, member);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> sort(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.sort(key);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> sort(final byte[] key, final SortingParams sortingParameters) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.sort(key, sortingParameters);
			}
		}.runBinary(key);
	}

	@Override
	public Long zcount(final byte[] key, final double min, final double max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zcount(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Long zcount(final byte[] key, final byte[] min, final byte[] max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zcount(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrangeByScore(final byte[] key, final double min, final double max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrangeByScore(final byte[] key, final byte[] min, final byte[] max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrangeByScore(final byte[] key, final double max, final double min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrangeByScore(final byte[] key, final double min, final double max, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrangeByScore(final byte[] key, final byte[] min, final byte[] max, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrangeByScore(key, min, max, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrangeByScore(final byte[] key, final double max, final double min, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final double min, final double max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final double max, final double min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final double min, final double max, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrangeByScore(key, max, min, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final double max, final double min, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min, final int offset,
			final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public Set<Tuple> doExecute(Jedis connection) {
				return connection.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Long zremrangeByRank(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByRank(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long zremrangeByScore(final byte[] key, final double start, final double end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByScore(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long zremrangeByScore(final byte[] key, final byte[] start, final byte[] end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByScore(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long linsert(final byte[] key, final Client.LIST_POSITION where, final byte[] pivot, final byte[] value) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.linsert(key, where, pivot, value);
			}
		}.runBinary(key);
	}

	@Override
	public Long lpushx(final byte[] key, final byte[]... arg) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.lpushx(key, arg);
			}
		}.runBinary(key);
	}

	@Override
	public Long rpushx(final byte[] key, final byte[]... arg) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.rpushx(key, arg);
			}
		}.runBinary(key);
	}

	@Override
	public Long del(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.del(key);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] echo(final byte[] arg) {
		// note that it'll be run from arbitary node
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.echo(arg);
			}
		}.runBinary(arg);
	}

	@Override
	public Long bitcount(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitcount(key);
			}
		}.runBinary(key);
	}

	@Override
	public Long bitcount(final byte[] key, final long start, final long end) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitcount(key, start, end);
			}
		}.runBinary(key);
	}

	@Override
	public Long pfadd(final byte[] key, final byte[]... elements) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pfadd(key, elements);
			}
		}.runBinary(key);
	}

	@Override
	public long pfcount(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pfcount(key);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> srandmember(final byte[] key, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.srandmember(key, count);
			}
		}.runBinary(key);
	}

	@Override
	public Long zlexcount(final byte[] key, final byte[] min, final byte[] max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zlexcount(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrangeByLex(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrangeByLex(final byte[] key, final byte[] min, final byte[] max, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrangeByLex(key, min, max, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrangeByLex(key, max, min);
			}
		}.runBinary(key);
	}

	@Override
	public Set<byte[]> zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min, final int offset, final int count) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.zrevrangeByLex(key, max, min, offset, count);
			}
		}.runBinary(key);
	}

	@Override
	public Long zremrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zremrangeByLex(key, min, max);
			}
		}.runBinary(key);
	}

	@Override
	public Object eval(final byte[] script, final byte[] keyCount, final byte[]... params) {
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script, keyCount, params);
			}
		}.runBinary(Integer.parseInt(SafeEncoder.encode(keyCount)), params);
	}

	@Override
	public Object eval(final byte[] script, final int keyCount, final byte[]... params) {
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script, keyCount, params);
			}
		}.runBinary(keyCount, params);
	}

	@Override
	public Object eval(final byte[] script, final List<byte[]> keys, final List<byte[]> args) {
		RedisFormatUtils.checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script, keys, args);
			}
		}.runBinary(keys.size(), keys.toArray(new byte[keys.size()][]));
	}

	@Override
	public Object eval(final byte[] script, byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.eval(script);
			}
		}.runBinary(key);
	}

	@Override
	public Object evalsha(final byte[] script, byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.evalsha(script);
			}
		}.runBinary(key);
	}

	@Override
	public Object evalsha(final byte[] sha1, final List<byte[]> keys, final List<byte[]> args) {
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.evalsha(sha1, keys, args);
			}
		}.runBinary(keys.size(), keys.toArray(new byte[keys.size()][]));
	}

	@Override
	public Object evalsha(final byte[] sha1, final int keyCount, final byte[]... params) {
		return new EnhancedJedisClusterCommand<Object>(connectionHandler, maxAttempts) {
			@Override
			public Object doExecute(Jedis connection) {
				return connection.evalsha(sha1, keyCount, params);
			}
		}.runBinary(keyCount, params);
	}

	@Override
	public List<Long> scriptExists(final byte[] key, final byte[][] sha1) {
		return new EnhancedJedisClusterCommand<List<Long>>(connectionHandler, maxAttempts) {
			@Override
			public List<Long> doExecute(Jedis connection) {
				return connection.scriptExists(sha1);
			}
		}.runBinary(key);
	}

	@Override
	public byte[] scriptLoad(final byte[] script, final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.scriptLoad(script);
			}
		}.runBinary(key);
	}

	@Override
	public String scriptFlush(final byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.scriptFlush();
			}
		}.runBinary(key);
	}

	@Override
	public String scriptKill(byte[] key) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.scriptKill();
			}
		}.runBinary(key);
	}

	@Override
	public Long del(final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.del(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public List<byte[]> blpop(final int timeout, final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.blpop(timeout, keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public List<byte[]> brpop(final int timeout, final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.brpop(timeout, keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public List<byte[]> mget(final byte[]... keys) {
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.mget(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public String mset(final byte[]... keysvalues) {
		byte[][] keys = new byte[keysvalues.length / 2][];

		for (int keyIdx = 0; keyIdx < keys.length; keyIdx++) {
			keys[keyIdx] = keysvalues[keyIdx * 2];
		}

		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.mset(keysvalues);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public Long msetnx(final byte[]... keysvalues) {
		byte[][] keys = new byte[keysvalues.length / 2][];

		for (int keyIdx = 0; keyIdx < keys.length; keyIdx++) {
			keys[keyIdx] = keysvalues[keyIdx * 2];
		}

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.msetnx(keysvalues);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public String rename(final byte[] oldkey, final byte[] newkey) {
		checkArgumentsSpecification(oldkey, newkey);
		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.rename(oldkey, newkey);
			}
		}.runBinary(2, oldkey, newkey);
	}

	@Override
	public Long renamenx(final byte[] oldkey, final byte[] newkey) {
		checkArgumentsSpecification(oldkey, newkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.renamenx(oldkey, newkey);
			}
		}.runBinary(2, oldkey, newkey);
	}

	@Override
	public byte[] rpoplpush(final byte[] srckey, final byte[] dstkey) {
		checkArgumentsSpecification(srckey, dstkey);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.rpoplpush(srckey, dstkey);
			}
		}.runBinary(2, srckey, dstkey);
	}

	@Override
	public Set<byte[]> sdiff(final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.sdiff(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public Long sdiffstore(final byte[] dstkey, final byte[]... keys) {
		checkArgumentsSpecification(dstkey);
		checkArgumentsSpecification(keys);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, keys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sdiffstore(dstkey, keys);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Set<byte[]> sinter(final byte[]... keys) {
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.sinter(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public Long sinterstore(final byte[] dstkey, final byte[]... keys) {
		checkArgumentsSpecification(dstkey);
		checkArgumentsSpecification(keys);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, keys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sinterstore(dstkey, keys);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long smove(final byte[] srckey, final byte[] dstkey, final byte[] member) {
		checkArgumentsSpecification(srckey, dstkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.smove(srckey, dstkey, member);
			}
		}.runBinary(2, srckey, dstkey);
	}

	@Override
	public Long sort(final byte[] key, final SortingParams sortingParameters, final byte[] dstkey) {
		checkArgumentsSpecification(key, dstkey);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sort(key, sortingParameters, dstkey);
			}
		}.runBinary(2, key, dstkey);
	}

	@Override
	public Long sort(final byte[] key, final byte[] dstkey) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sort(key, dstkey);
			}
		}.runBinary(2, key, dstkey);
	}

	@Override
	public Set<byte[]> sunion(final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Set<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public Set<byte[]> doExecute(Jedis connection) {
				return connection.sunion(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public Long sunionstore(final byte[] dstkey, final byte[]... keys) {
		checkArgumentsSpecification(dstkey);
		checkArgumentsSpecification(keys);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, keys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.sunionstore(dstkey, keys);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long zinterstore(final byte[] dstkey, final byte[]... sets) {
		checkArgumentsSpecification(dstkey);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zinterstore(dstkey, sets);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long zinterstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
		checkArgumentsSpecification(dstkey);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zinterstore(dstkey, params, sets);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long zunionstore(final byte[] dstkey, final byte[]... sets) {
		checkArgumentsSpecification(dstkey);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zunionstore(dstkey, sets);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long zunionstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
		checkArgumentsSpecification(dstkey);
		byte[][] wholeKeys = KeyMergeUtil.merge(dstkey, sets);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.zunionstore(dstkey, params, sets);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public byte[] brpoplpush(final byte[] source, final byte[] destination, final int timeout) {
		checkArgumentsSpecification(source);
		return new EnhancedJedisClusterCommand<byte[]>(connectionHandler, maxAttempts) {
			@Override
			public byte[] doExecute(Jedis connection) {
				return connection.brpoplpush(source, destination, timeout);
			}
		}.runBinary(2, source, destination);
	}

	@Override
	public Long publish(final byte[] channel, final byte[] message) {
		checkArgumentsSpecification(channel);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.publish(channel, message);
			}
		}.runWithAnyNode();
	}

	@Override
	public void subscribe(final BinaryJedisPubSub jedisPubSub, final byte[]... channels) {
		new EnhancedJedisClusterCommand<Integer>(connectionHandler, maxAttempts) {
			@Override
			public Integer doExecute(Jedis connection) {
				connection.subscribe(jedisPubSub, channels);
				return 0;
			}
		}.runWithAnyNode();
	}

	@Override
	public void psubscribe(final BinaryJedisPubSub jedisPubSub, final byte[]... patterns) {
		new EnhancedJedisClusterCommand<Integer>(connectionHandler, maxAttempts) {
			@Override
			public Integer doExecute(Jedis connection) {
				connection.psubscribe(jedisPubSub, patterns);
				return 0;
			}
		}.runWithAnyNode();
	}

	@Override
	public Long bitop(final BitOP op, final byte[] destKey, final byte[]... srcKeys) {
		checkArgumentsSpecification(destKey);
		checkArgumentsSpecification(srcKeys);
		byte[][] wholeKeys = KeyMergeUtil.merge(destKey, srcKeys);

		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.bitop(op, destKey, srcKeys);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public String pfmerge(final byte[] destkey, final byte[]... sourcekeys) {
		checkArgumentsSpecification(destkey);
		checkArgumentsSpecification(sourcekeys);
		byte[][] wholeKeys = KeyMergeUtil.merge(destkey, sourcekeys);

		return new EnhancedJedisClusterCommand<String>(connectionHandler, maxAttempts) {
			@Override
			public String doExecute(Jedis connection) {
				return connection.pfmerge(destkey, sourcekeys);
			}
		}.runBinary(wholeKeys.length, wholeKeys);
	}

	@Override
	public Long pfcount(final byte[]... keys) {
		checkArgumentsSpecification(keys);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.pfcount(keys);
			}
		}.runBinary(keys.length, keys);
	}

	@Override
	public Long geoadd(final byte[] key, final double longitude, final double latitude, final byte[] member) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.geoadd(key, longitude, latitude, member);
			}
		}.runBinary(key);
	}

	@Override
	public Long geoadd(final byte[] key, final Map<byte[], GeoCoordinate> memberCoordinateMap) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Long>(connectionHandler, maxAttempts) {
			@Override
			public Long doExecute(Jedis connection) {
				return connection.geoadd(key, memberCoordinateMap);
			}
		}.runBinary(key);
	}

	@Override
	public Double geodist(final byte[] key, final byte[] member1, final byte[] member2) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.geodist(key, member1, member2);
			}
		}.runBinary(key);
	}

	@Override
	public Double geodist(final byte[] key, final byte[] member1, final byte[] member2, final GeoUnit unit) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<Double>(connectionHandler, maxAttempts) {
			@Override
			public Double doExecute(Jedis connection) {
				return connection.geodist(key, member1, member2, unit);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> geohash(final byte[] key, final byte[]... members) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.geohash(key, members);
			}
		}.runBinary(key);
	}

	@Override
	public List<GeoCoordinate> geopos(final byte[] key, final byte[]... members) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoCoordinate>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoCoordinate> doExecute(Jedis connection) {
				return connection.geopos(key, members);
			}
		}.runBinary(key);
	}

	@Override
	public List<GeoRadiusResponse> georadius(final byte[] key, final double longitude, final double latitude, final double radius,
			final GeoUnit unit) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadius(key, longitude, latitude, radius, unit);
			}
		}.runBinary(key);
	}

	@Override
	public List<GeoRadiusResponse> georadius(final byte[] key, final double longitude, final double latitude, final double radius,
			final GeoUnit unit, final GeoRadiusParam param) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadius(key, longitude, latitude, radius, unit, param);
			}
		}.runBinary(key);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(final byte[] key, final byte[] member, final double radius,
			final GeoUnit unit) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadiusByMember(key, member, radius, unit);
			}
		}.runBinary(key);
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(final byte[] key, final byte[] member, final double radius,
			final GeoUnit unit, final GeoRadiusParam param) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<List<GeoRadiusResponse>>(connectionHandler, maxAttempts) {
			@Override
			public List<GeoRadiusResponse> doExecute(Jedis connection) {
				return connection.georadiusByMember(key, member, radius, unit, param);
			}
		}.runBinary(key);
	}

	@Override
	public ScanResult<byte[]> scan(final byte[] cursor, final ScanParams params) {

		String matchPattern = null;

		if (params == null || (matchPattern = scanMatch(params)) == null || matchPattern.isEmpty()) {
			throw new IllegalArgumentException(
					BinaryJedisCluster.class.getSimpleName() + " only supports SCAN commands with non-empty MATCH patterns");
		}

		if (JedisClusterHashTagUtil.isClusterCompliantMatchPattern(matchPattern)) {

			return new EnhancedJedisClusterCommand<ScanResult<byte[]>>(connectionHandler, maxAttempts) {
				@Override
				public ScanResult<byte[]> doExecute(Jedis connection) {
					return connection.scan(cursor, params);
				}
			}.runBinary(SafeEncoder.encode(matchPattern));
		} else {
			throw new IllegalArgumentException(BinaryJedisCluster.class.getSimpleName()
					+ " only supports SCAN commands with MATCH patterns containing hash-tags ( curly-brackets enclosed strings )");
		}
	}

	@Override
	public ScanResult<Map.Entry<byte[], byte[]>> hscan(final byte[] key, final byte[] cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Map.Entry<byte[], byte[]>>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Map.Entry<byte[], byte[]>> doExecute(Jedis connection) {
				return connection.hscan(key, cursor);
			}
		}.runBinary(key);
	}

	@Override
	public ScanResult<Map.Entry<byte[], byte[]>> hscan(final byte[] key, final byte[] cursor, final ScanParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Map.Entry<byte[], byte[]>>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Map.Entry<byte[], byte[]>> doExecute(Jedis connection) {
				return connection.hscan(key, cursor, params);
			}
		}.runBinary(key);
	}

	@Override
	public ScanResult<byte[]> sscan(final byte[] key, final byte[] cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<byte[]> doExecute(Jedis connection) {
				return connection.sscan(key, cursor);
			}
		}.runBinary(key);
	}

	@Override
	public ScanResult<byte[]> sscan(final byte[] key, final byte[] cursor, final ScanParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<byte[]> doExecute(Jedis connection) {
				return connection.sscan(key, cursor, params);
			}
		}.runBinary(key);
	}

	@Override
	public ScanResult<Tuple> zscan(final byte[] key, final byte[] cursor) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Tuple> doExecute(Jedis connection) {
				return connection.zscan(key, cursor);
			}
		}.runBinary(key);
	}

	@Override
	public ScanResult<Tuple> zscan(final byte[] key, final byte[] cursor, final ScanParams params) {
		checkArgumentsSpecification(key);
		return new EnhancedJedisClusterCommand<ScanResult<Tuple>>(connectionHandler, maxAttempts) {
			@Override
			public ScanResult<Tuple> doExecute(Jedis connection) {
				return connection.zscan(key, cursor, params);
			}
		}.runBinary(key);
	}

	@Override
	public List<byte[]> bitfield(final byte[] key, final byte[]... arguments) {
		checkArgumentsSpecification(key);

		return new EnhancedJedisClusterCommand<List<byte[]>>(connectionHandler, maxAttempts) {
			@Override
			public List<byte[]> doExecute(Jedis connection) {
				return connection.bitfield(key, arguments);
			}
		}.runBinary(key);
	}

	/**
	 * Check input argument names specification.
	 * 
	 * @param keys
	 * @throws ArgumentsSpecificationException
	 */
	protected void checkArgumentsSpecification(final byte[]... keys) throws ArgumentsSpecificationException {
		RedisFormatUtils.checkArgumentsSpecification(asList(keys));
	}

	/**
	 * Check input argument names specification.
	 * 
	 * @param keys
	 * @throws ArgumentsSpecificationException
	 */
	protected void checkArgumentsSpecification(final String... keys) throws ArgumentsSpecificationException {
		RedisFormatUtils.checkArgumentsSpecification(asList(keys));
	}

	/**
	 * Scan cursor matching.
	 * 
	 * @param params
	 * @return
	 */
	protected String scanMatch(ScanParams params) {
		try {
			return (String) PARAMS_MATCH.invoke(params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	final private static Method PARAMS_MATCH;

	/**
	 * Redis key specifications format utils.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年4月10日
	 * @since
	 */
	public static abstract class RedisFormatUtils {

		final private static SmartLogger log = getLogger(RedisFormatUtils.class);

		/**
		 * Check input argument names specification.
		 * 
		 * @param keys
		 * @throws ArgumentsSpecificationException
		 */
		public static void checkArgumentsSpecification(final List<?> keys) throws ArgumentsSpecificationException {
			notNullOf(keys, "jedis operation key");
			for (Object key : keys) {
				char[] _key = null;
				if (key instanceof String) {
					_key = key.toString().toCharArray();
				} else if (char.class.isAssignableFrom(key.getClass())) {
					_key = new char[] { (char) key };
				} else if (key instanceof char[]) {
					_key = (char[]) key;
				}

				if (isNull(_key)) {
					continue;
				}

				// The check exclusion key contains special characters such
				// as '-', '$', ' ' etc and so on.
				for (char c : _key) {
					String warning = format(
							"The operation redis keys: %s there are unsafe characters: '%s', Because of the binary safety mechanism of redis, it may not be got",
							keys, c);
					if (!checkInvalidCharacter(c)) {
						if (warnKeyChars.contains(c)) { // Warning key chars
							log.warn(warning);
							return;
						} else {
							throw new ArgumentsSpecificationException(warning);
						}
					}
				}
			}
		}

		/**
		 * Formating redis arguments unsafe characters, e.g: '-' to '_'
		 * 
		 * @param key
		 * @return
		 */
		public static String keyFormat(String key) {
			return keyFormat(key, '_');
		}

		/**
		 * Formating redis arguments unsafe characters, e.g: '-' to '_'
		 * 
		 * @param key
		 * @param safeChar
		 *            Replace safe character
		 * @return
		 */
		public static String keyFormat(String key, char safeChar) {
			if (isBlank(key)) {
				return key;
			}
			checkArgumentsSpecification(singletonList(safeChar));

			// The check exclusion key contains special characters such
			// as '-', '$', ' ' etc and so on.
			StringBuffer _key = new StringBuffer(key.length());
			for (char c : key.toString().toCharArray()) {
				if (checkInvalidCharacter(c)) {
					_key.append(c);
				} else {
					_key.append(safeChar);
				}
			}
			return _key.toString();
		}

		/**
		 * Check is invalid redis arguments character.
		 * 
		 * @param c
		 * @return
		 */
		public static boolean checkInvalidCharacter(char c) {
			return !isNull(c) && isNumeric(valueOf(c)) || isAlpha(valueOf(c)) || safeKeyChars.contains(c);
		}

		/**
		 * Redis key-name safe characters.
		 */
		final private static List<Character> safeKeyChars = unmodifiableList(new ArrayList<Character>(4) {
			private static final long serialVersionUID = -7144798722787955277L;
			{
				add(':');
				add('_');
				add('.');
				add('@');
			}
		});

		/**
		 * Redis key-name safe characters.
		 */
		final private static List<Character> warnKeyChars = unmodifiableList(new ArrayList<Character>(4) {
			private static final long serialVersionUID = -7144798722787955277L;
			{
				add('&');
				add('!');
				add('*');
			}
		});

	}

	static {
		Method mothod = null;
		for (Method m : ScanParams.class.getDeclaredMethods()) {
			/**
			 * {@link redis.clients.jedis.ScanParams#match()}
			 */
			if (m.getName().equals("match") && m.getParameterCount() == 0) {
				mothod = m;
			}
		}
		PARAMS_MATCH = mothod;
		notNullOf(PARAMS_MATCH, "redis.clients.jedis.ScanParams#match()");
		PARAMS_MATCH.setAccessible(true);
	}

}
