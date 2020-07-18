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

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isAlpha;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wl4g.devops.common.exception.framework.ArgumentsSpecificationException;
import com.wl4g.devops.components.tools.common.log.SmartLogger;

import java.util.Set;

import redis.clients.jedis.AdvancedBinaryJedisCommands;
import redis.clients.jedis.AdvancedJedisCommands;
import redis.clients.jedis.BasicCommands;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BinaryJedisClusterCommands;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.BinaryScriptingCommands;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.ClusterCommands;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.JedisCluster.Reset;
import redis.clients.jedis.JedisClusterBinaryScriptingCommands;
import redis.clients.jedis.JedisClusterScriptingCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyBinaryJedisClusterCommands;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.MultiKeyJedisClusterCommands;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ScriptingCommands;
import redis.clients.jedis.SentinelCommands;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Slowlog;

/**
 * Composite jedis single and cluster adapter.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年7月18日 v1.0.0
 * @see
 */
public interface CompositeJedisOperatorsAdapter extends JedisCommands, MultiKeyJedisClusterCommands, JedisClusterScriptingCommands,
		BasicCommands, BinaryJedisClusterCommands, MultiKeyBinaryJedisClusterCommands, JedisClusterBinaryScriptingCommands,
		MultiKeyCommands, AdvancedJedisCommands, ScriptingCommands, ClusterCommands, SentinelCommands, BinaryJedisCommands,
		MultiKeyBinaryCommands, AdvancedBinaryJedisCommands, BinaryScriptingCommands, Closeable {

	default Map<String, JedisPool> getClusterNodes() {
		throw new UnsupportedOperationException();
	}

	@Override
	default void close() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(byte[] script) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(byte[] script) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Long> scriptExists(byte[]... sha1) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] scriptLoad(byte[] script) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String scriptFlush() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String scriptKill() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> configGet(byte[] pattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] configSet(byte[] parameter, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> slowlogGetBinary() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> slowlogGetBinary(long entries) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long objectRefcount(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] objectEncoding(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long objectIdletime(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> blpop(byte[]... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> brpop(byte[]... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> keys(byte[] pattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String watch(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] randomBinaryKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String set(byte[] key, byte[] value, byte[] nxxx) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> blpop(byte[] arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> brpop(byte[] arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long move(byte[] key, int dbIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Map<String, String>> sentinelMasters() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> sentinelGetMasterAddrByName(String masterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sentinelReset(String pattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Map<String, String>> sentinelSlaves(String masterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String sentinelFailover(String masterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String sentinelMonitor(String masterName, String ip, int port, int quorum) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String sentinelRemove(String masterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String sentinelSet(String masterName, Map<String, String> parameterMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterNodes() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterMeet(String ip, int port) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterAddSlots(int... slots) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterDelSlots(int... slots) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> clusterGetKeysInSlot(int slot, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterSetSlotNode(int slot, String nodeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterSetSlotMigrating(int slot, String nodeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterSetSlotImporting(int slot, String nodeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterSetSlotStable(int slot) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterForget(String nodeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterFlushSlots() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long clusterKeySlot(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long clusterCountKeysInSlot(int slot) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterSaveConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterReplicate(String nodeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> clusterSlaves(String nodeId) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterFailover() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Object> clusterSlots() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String clusterReset(Reset resetType) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String readonly() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(String script) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(String script) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean scriptExists(String sha1) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Boolean> scriptExists(String... sha1) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String scriptLoad(String script) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> configGet(String pattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String configSet(String parameter, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String slowlogReset() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long slowlogLen() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Slowlog> slowlogGet() {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Slowlog> slowlogGet(long entries) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long objectRefcount(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String objectEncoding(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long objectIdletime(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> blpop(String... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> brpop(String... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> keys(String pattern) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String watch(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String unwatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String randomKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<String> scan(int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<String> scan(String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(byte[] script, byte[] keyCount, byte[]... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(byte[] script, int keyCount, byte[]... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(byte[] script, byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(byte[] script, byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(byte[] sha1, int keyCount, byte[]... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Long> scriptExists(byte[] key, byte[][] sha1) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] scriptLoad(byte[] script, byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String scriptFlush(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String scriptKill(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long exists(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long del(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> blpop(int timeout, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> brpop(int timeout, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> mget(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String mset(byte[]... keysvalues) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long msetnx(byte[]... keysvalues) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String rename(byte[] oldkey, byte[] newkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long renamenx(byte[] oldkey, byte[] newkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> sdiff(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sdiffstore(byte[] dstkey, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> sinter(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sinterstore(byte[] dstkey, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long smove(byte[] srckey, byte[] dstkey, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sort(byte[] key, SortingParams sortingParameters, byte[] dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sort(byte[] key, byte[] dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> sunion(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sunionstore(byte[] dstkey, byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zinterstore(byte[] dstkey, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zinterstore(byte[] dstkey, ZParams params, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zunionstore(byte[] dstkey, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zunionstore(byte[] dstkey, ZParams params, byte[]... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long publish(byte[] channel, byte[] message) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
		throw new UnsupportedOperationException();

	}

	@Override
	default void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
		throw new UnsupportedOperationException();

	}

	@Override
	default Long bitop(BitOP op, byte[] destKey, byte[]... srcKeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String pfmerge(byte[] destkey, byte[]... sourcekeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pfcount(byte[]... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String set(byte[] key, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] get(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean exists(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long persist(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String type(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long expire(byte[] key, int seconds) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pexpire(byte[] key, long milliseconds) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long expireAt(byte[] key, long unixTime) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pexpireAt(byte[] key, long millisecondsTimestamp) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long ttl(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean setbit(byte[] key, long offset, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean setbit(byte[] key, long offset, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean getbit(byte[] key, long offset) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long setrange(byte[] key, long offset, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] getrange(byte[] key, long startOffset, long endOffset) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] getSet(byte[] key, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long setnx(byte[] key, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String setex(byte[] key, int seconds, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long decrBy(byte[] key, long integer) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long decr(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long incrBy(byte[] key, long integer) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double incrByFloat(byte[] key, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long incr(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long append(byte[] key, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] substr(byte[] key, int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hset(byte[] key, byte[] field, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] hget(byte[] key, byte[] field) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hsetnx(byte[] key, byte[] field, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String hmset(byte[] key, Map<byte[], byte[]> hash) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> hmget(byte[] key, byte[]... fields) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hincrBy(byte[] key, byte[] field, long value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double hincrByFloat(byte[] key, byte[] field, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean hexists(byte[] key, byte[] field) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hdel(byte[] key, byte[]... field) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hlen(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> hkeys(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Collection<byte[]> hvals(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Map<byte[], byte[]> hgetAll(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long rpush(byte[] key, byte[]... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lpush(byte[] key, byte[]... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long llen(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> lrange(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String ltrim(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] lindex(byte[] key, long index) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String lset(byte[] key, long index, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lrem(byte[] key, long count, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] lpop(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] rpop(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sadd(byte[] key, byte[]... member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> smembers(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long srem(byte[] key, byte[]... member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] spop(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> spop(byte[] key, long count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long scard(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean sismember(byte[] key, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] srandmember(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> srandmember(byte[] key, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long strlen(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(byte[] key, double score, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(byte[] key, double score, byte[] member, ZAddParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrange(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zrem(byte[] key, byte[]... member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double zincrby(byte[] key, double score, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double zincrby(byte[] key, double score, byte[] member, ZIncrByParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zrank(byte[] key, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zrevrank(byte[] key, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrange(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeWithScores(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zcard(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double zscore(byte[] key, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> sort(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zcount(byte[] key, double min, double max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zcount(byte[] key, byte[] min, byte[] max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByRank(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByScore(byte[] key, double start, double end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByScore(byte[] key, byte[] start, byte[] end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zlexcount(byte[] key, byte[] min, byte[] max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lpushx(byte[] key, byte[]... arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long rpushx(byte[] key, byte[]... arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long del(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default byte[] echo(byte[] arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long bitcount(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long bitcount(byte[] key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pfadd(byte[] key, byte[]... elements) {
		throw new UnsupportedOperationException();
	}

	@Override
	default long pfcount(byte[] key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long geoadd(byte[] key, Map<byte[], GeoCoordinate> memberCoordinateMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double geodist(byte[] key, byte[] member1, byte[] member2) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> geohash(byte[] key, byte[]... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoCoordinate> geopos(byte[] key, byte[]... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<byte[]> scan(byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<byte[]> sscan(byte[] key, byte[] cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<byte[]> sscan(byte[] key, byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Tuple> zscan(byte[] key, byte[] cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Tuple> zscan(byte[] key, byte[] cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<byte[]> bitfield(byte[] key, byte[]... arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String ping() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String quit() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String flushDB() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long dbSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String select(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String flushAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String auth(String password) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String save() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String bgsave() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String bgrewriteaof() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lastsave() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String shutdown() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String info() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String info(String section) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String slaveof(String host, int port) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String slaveofNoOne() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long getDB() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String debug(DebugParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String configResetStat() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long waitReplicas(int replicas, long timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(String script, int keyCount, String... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(String script, List<String> keys, List<String> args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object eval(String script, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(String script, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(String sha1, List<String> keys, List<String> args) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object evalsha(String sha1, int keyCount, String... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean scriptExists(String sha1, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Boolean> scriptExists(String key, String... sha1) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String scriptLoad(String script, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long exists(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long del(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> blpop(int timeout, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> brpop(int timeout, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> mget(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String mset(String... keysvalues) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long msetnx(String... keysvalues) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String rename(String oldkey, String newkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long renamenx(String oldkey, String newkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String rpoplpush(String srckey, String dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> sdiff(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sdiffstore(String dstkey, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> sinter(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sinterstore(String dstkey, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long smove(String srckey, String dstkey, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sort(String key, SortingParams sortingParameters, String dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sort(String key, String dstkey) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> sunion(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sunionstore(String dstkey, String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zinterstore(String dstkey, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zinterstore(String dstkey, ZParams params, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zunionstore(String dstkey, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zunionstore(String dstkey, ZParams params, String... sets) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String brpoplpush(String source, String destination, int timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long publish(String channel, String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void subscribe(JedisPubSub jedisPubSub, String... channels) {
		throw new UnsupportedOperationException();

	}

	@Override
	default void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		throw new UnsupportedOperationException();

	}

	@Override
	default Long bitop(BitOP op, String destKey, String... srcKeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String pfmerge(String destkey, String... sourcekeys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default long pfcount(String... keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<String> scan(String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String set(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String set(String key, String value, String nxxx, String expx, long time) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String set(String key, String value, String nxxx) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String get(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean exists(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long persist(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String type(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long expire(String key, int seconds) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pexpire(String key, long milliseconds) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long expireAt(String key, long unixTime) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pexpireAt(String key, long millisecondsTimestamp) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long ttl(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pttl(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean setbit(String key, long offset, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean setbit(String key, long offset, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean getbit(String key, long offset) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long setrange(String key, long offset, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String getrange(String key, long startOffset, long endOffset) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String getSet(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long setnx(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String setex(String key, int seconds, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String psetex(String key, long milliseconds, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long decrBy(String key, long integer) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long decr(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long incrBy(String key, long integer) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double incrByFloat(String key, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long incr(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long append(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String substr(String key, int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hset(String key, String field, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String hget(String key, String field) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hsetnx(String key, String field, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String hmset(String key, Map<String, String> hash) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> hmget(String key, String... fields) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hincrBy(String key, String field, long value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double hincrByFloat(String key, String field, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean hexists(String key, String field) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hdel(String key, String... field) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long hlen(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> hkeys(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> hvals(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Map<String, String> hgetAll(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long rpush(String key, String... string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lpush(String key, String... string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long llen(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> lrange(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String ltrim(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String lindex(String key, long index) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String lset(String key, long index, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lrem(String key, long count, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String lpop(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String rpop(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long sadd(String key, String... member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> smembers(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long srem(String key, String... member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String spop(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> spop(String key, long count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long scard(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Boolean sismember(String key, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String srandmember(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> srandmember(String key, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long strlen(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(String key, double score, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(String key, double score, String member, ZAddParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(String key, Map<String, Double> scoreMembers) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrange(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zrem(String key, String... member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double zincrby(String key, double score, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double zincrby(String key, double score, String member, ZIncrByParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zrank(String key, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zrevrank(String key, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrange(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeWithScores(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zcard(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double zscore(String key, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> sort(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> sort(String key, SortingParams sortingParameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zcount(String key, double min, double max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zcount(String key, String min, String max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrangeByScore(String key, double min, double max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrangeByScore(String key, String min, String max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrangeByScore(String key, double max, double min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrangeByScore(String key, String max, String min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByRank(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByScore(String key, double start, double end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByScore(String key, String start, String end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zlexcount(String key, String min, String max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrangeByLex(String key, String min, String max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrangeByLex(String key, String max, String min) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long zremrangeByLex(String key, String min, String max) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long lpushx(String key, String... string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long rpushx(String key, String... string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> blpop(String arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> blpop(int timeout, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> brpop(String arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> brpop(int timeout, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long del(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default String echo(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long move(String key, int dbIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long bitcount(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long bitcount(String key, long start, long end) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long bitpos(String key, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long bitpos(String key, boolean value, BitPosParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<String> sscan(String key, int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Tuple> zscan(String key, int cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<String> sscan(String key, String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<String> sscan(String key, String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Tuple> zscan(String key, String cursor) {
		throw new UnsupportedOperationException();
	}

	@Override
	default ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long pfadd(String key, String... elements) {
		throw new UnsupportedOperationException();
	}

	@Override
	default long pfcount(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long geoadd(String key, double longitude, double latitude, String member) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double geodist(String key, String member1, String member2) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Double geodist(String key, String member1, String member2, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<String> geohash(String key, String... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoCoordinate> geopos(String key, String... members) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		throw new UnsupportedOperationException();
	}

	@Override
	default List<Long> bitfield(String key, String... arguments) {
		throw new UnsupportedOperationException();
	}

	//
	// --- Function's. ---
	//

	/**
	 * Redis key specifications utils(formatter etc).
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年4月10日
	 * @since
	 */
	public static abstract class RedisProtoUtil {

		final private static SmartLogger log = getLogger(RedisProtoUtil.class);

		/**
		 * Check is result is successful.
		 * 
		 * @param res
		 * @return
		 */
		public static boolean isSuccess(String res) {
			return equalsIgnoreCase(res, "OK") || (!isBlank(res) && isNumeric(res) && parseLong(res) > 0);
		}

		/**
		 * Check is result is successful.
		 * 
		 * @param res
		 * @return
		 */
		public static boolean isSuccess(Long res) {
			return !isNull(res) && res > 0;
		}

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

}