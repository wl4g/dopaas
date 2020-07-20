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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Set;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster.Reset;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;
import redis.clients.util.Slowlog;

/**
 * {@link DelegateJedis}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年7月18日 v1.0.0
 * @see
 */
public class DelegateJedis implements CompositeJedisOperatorsAdapter {

	/** Jedis single pool */
	final protected JedisPool jedisPool;

	/** Safety mode, validating storage key. */
	final protected boolean safeMode;

	public DelegateJedis(JedisPool jedisPool, boolean safeMode) {
		notNullOf(jedisPool, "jedisPool");
		this.jedisPool = jedisPool;
		this.safeMode = safeMode;
	}

	@Override
	public void close() throws IOException {
		doExecuteWithRedis(jedis -> {
			jedis.close();
			return null;
		});
	}

	@Override
	public Object eval(byte[] script) {
		return doExecuteWithRedis(jedis -> jedis.eval(script));
	}

	@Override
	public Object evalsha(byte[] script) {
		return doExecuteWithRedis(jedis -> jedis.evalsha(script));
	}

	@Override
	public List<Long> scriptExists(byte[]... sha1) {
		return doExecuteWithRedis(jedis -> jedis.scriptExists(sha1));
	}

	@Override
	public byte[] scriptLoad(byte[] script) {
		return doExecuteWithRedis(jedis -> jedis.scriptLoad(script));
	}

	@Override
	public String scriptFlush() {
		return doExecuteWithRedis(jedis -> jedis.scriptFlush());
	}

	@Override
	public String scriptKill() {
		return doExecuteWithRedis(jedis -> jedis.scriptKill());
	}

	@Override
	public List<byte[]> configGet(byte[] pattern) {
		return doExecuteWithRedis(jedis -> jedis.configGet(pattern));
	}

	@Override
	public byte[] configSet(byte[] parameter, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.configSet(parameter, value));
	}

	@Override
	public List<byte[]> slowlogGetBinary() {
		return doExecuteWithRedis(jedis -> jedis.slowlogGetBinary());
	}

	@Override
	public List<byte[]> slowlogGetBinary(long entries) {
		return doExecuteWithRedis(jedis -> jedis.slowlogGetBinary(entries));
	}

	@Override
	public Long objectRefcount(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.objectRefcount(key));
	}

	@Override
	public byte[] objectEncoding(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.objectEncoding(key));
	}

	@Override
	public Long objectIdletime(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.objectIdletime(key));
	}

	@Override
	public List<byte[]> blpop(byte[]... args) {
		return doExecuteWithRedis(jedis -> jedis.blpop(args));
	}

	@Override
	public List<byte[]> brpop(byte[]... args) {
		return doExecuteWithRedis(jedis -> jedis.brpop(args));
	}

	@Override
	public Set<byte[]> keys(byte[] pattern) {
		return doExecuteWithRedis(jedis -> jedis.keys(pattern));
	}

	@Override
	public String watch(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.watch(keys));
	}

	@Override
	public byte[] randomBinaryKey() {
		return doExecuteWithRedis(jedis -> jedis.randomBinaryKey());
	}

	@Override
	public String set(byte[] key, byte[] value, byte[] nxxx) {
		return doExecuteWithRedis(jedis -> jedis.set(key, value, nxxx));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<byte[]> blpop(byte[] arg) {
		return doExecuteWithRedis(jedis -> jedis.blpop(arg));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<byte[]> brpop(byte[] arg) {
		return doExecuteWithRedis(jedis -> jedis.brpop(arg));
	}

	@Override
	public Long move(byte[] key, int dbIndex) {
		return doExecuteWithRedis(jedis -> jedis.move(key, dbIndex));
	}

	@Override
	public List<Map<String, String>> sentinelMasters() {
		return doExecuteWithRedis(jedis -> jedis.sentinelMasters());
	}

	@Override
	public List<String> sentinelGetMasterAddrByName(String masterName) {
		return doExecuteWithRedis(jedis -> jedis.sentinelGetMasterAddrByName(masterName));
	}

	@Override
	public Long sentinelReset(String pattern) {
		return doExecuteWithRedis(jedis -> jedis.sentinelReset(pattern));
	}

	@Override
	public List<Map<String, String>> sentinelSlaves(String masterName) {
		return doExecuteWithRedis(jedis -> jedis.sentinelSlaves(masterName));
	}

	@Override
	public String sentinelFailover(String masterName) {
		return doExecuteWithRedis(jedis -> jedis.sentinelFailover(masterName));
	}

	@Override
	public String sentinelMonitor(String masterName, String ip, int port, int quorum) {
		return doExecuteWithRedis(jedis -> jedis.sentinelMonitor(masterName, ip, port, quorum));
	}

	@Override
	public String sentinelRemove(String masterName) {
		return doExecuteWithRedis(jedis -> jedis.sentinelRemove(masterName));
	}

	@Override
	public String sentinelSet(String masterName, Map<String, String> parameterMap) {
		return doExecuteWithRedis(jedis -> jedis.sentinelSet(masterName, parameterMap));
	}

	@Override
	public String clusterNodes() {
		return doExecuteWithRedis(jedis -> jedis.clusterNodes());
	}

	@Override
	public String clusterMeet(String ip, int port) {
		return doExecuteWithRedis(jedis -> jedis.clusterMeet(ip, port));
	}

	@Override
	public String clusterAddSlots(int... slots) {
		return doExecuteWithRedis(jedis -> jedis.clusterAddSlots(slots));
	}

	@Override
	public String clusterDelSlots(int... slots) {
		return doExecuteWithRedis(jedis -> jedis.clusterDelSlots(slots));
	}

	@Override
	public String clusterInfo() {
		return doExecuteWithRedis(jedis -> jedis.clusterInfo());
	}

	@Override
	public List<String> clusterGetKeysInSlot(int slot, int count) {
		return doExecuteWithRedis(jedis -> jedis.clusterGetKeysInSlot(slot, count));
	}

	@Override
	public String clusterSetSlotNode(int slot, String nodeId) {
		return doExecuteWithRedis(jedis -> jedis.clusterSetSlotNode(slot, nodeId));
	}

	@Override
	public String clusterSetSlotMigrating(int slot, String nodeId) {
		return doExecuteWithRedis(jedis -> jedis.clusterSetSlotMigrating(slot, nodeId));
	}

	@Override
	public String clusterSetSlotImporting(int slot, String nodeId) {
		return doExecuteWithRedis(jedis -> jedis.clusterSetSlotImporting(slot, nodeId));
	}

	@Override
	public String clusterSetSlotStable(int slot) {
		return doExecuteWithRedis(jedis -> jedis.clusterSetSlotStable(slot));
	}

	@Override
	public String clusterForget(String nodeId) {
		return doExecuteWithRedis(jedis -> jedis.clusterForget(nodeId));
	}

	@Override
	public String clusterFlushSlots() {
		return doExecuteWithRedis(jedis -> jedis.clusterFlushSlots());
	}

	@Override
	public Long clusterKeySlot(String key) {
		return doExecuteWithRedis(jedis -> jedis.clusterKeySlot(key));
	}

	@Override
	public Long clusterCountKeysInSlot(int slot) {
		return doExecuteWithRedis(jedis -> jedis.clusterCountKeysInSlot(slot));
	}

	@Override
	public String clusterSaveConfig() {
		return doExecuteWithRedis(jedis -> jedis.clusterSaveConfig());
	}

	@Override
	public String clusterReplicate(String nodeId) {
		return doExecuteWithRedis(jedis -> jedis.clusterReplicate(nodeId));
	}

	@Override
	public List<String> clusterSlaves(String nodeId) {
		return doExecuteWithRedis(jedis -> jedis.clusterSlaves(nodeId));
	}

	@Override
	public String clusterFailover() {
		return doExecuteWithRedis(jedis -> jedis.clusterFailover());
	}

	@Override
	public List<Object> clusterSlots() {
		return doExecuteWithRedis(jedis -> jedis.clusterSlots());
	}

	@Override
	public String clusterReset(Reset resetType) {
		return doExecuteWithRedis(jedis -> jedis.clusterReset(resetType));
	}

	@Override
	public String readonly() {
		return doExecuteWithRedis(jedis -> jedis.readonly());
	}

	@Override
	public Object eval(String script) {
		return doExecuteWithRedis(jedis -> jedis.eval(script));
	}

	@Override
	public Object evalsha(String script) {
		return doExecuteWithRedis(jedis -> jedis.evalsha(script));
	}

	@Override
	public Boolean scriptExists(String sha1) {
		return doExecuteWithRedis(jedis -> jedis.scriptExists(sha1));
	}

	@Override
	public List<Boolean> scriptExists(String... sha1) {
		return doExecuteWithRedis(jedis -> jedis.scriptExists(sha1));
	}

	@Override
	public String scriptLoad(String script) {
		return doExecuteWithRedis(jedis -> jedis.scriptLoad(script));
	}

	@Override
	public List<String> configGet(String pattern) {
		return doExecuteWithRedis(jedis -> jedis.configGet(pattern));
	}

	@Override
	public String configSet(String parameter, String value) {
		return doExecuteWithRedis(jedis -> jedis.configSet(parameter, value));
	}

	@Override
	public String slowlogReset() {
		return doExecuteWithRedis(jedis -> jedis.slowlogReset());
	}

	@Override
	public Long slowlogLen() {
		return doExecuteWithRedis(jedis -> jedis.slowlogLen());
	}

	@Override
	public List<Slowlog> slowlogGet() {
		return doExecuteWithRedis(jedis -> jedis.slowlogGet());
	}

	@Override
	public List<Slowlog> slowlogGet(long entries) {
		return doExecuteWithRedis(jedis -> jedis.slowlogGet(entries));
	}

	@Override
	public Long objectRefcount(String string) {
		return doExecuteWithRedis(jedis -> jedis.objectRefcount(string));
	}

	@Override
	public String objectEncoding(String string) {
		return doExecuteWithRedis(jedis -> jedis.objectEncoding(string));
	}

	@Override
	public Long objectIdletime(String string) {
		return doExecuteWithRedis(jedis -> jedis.objectIdletime(string));
	}

	@Override
	public List<String> blpop(String... args) {
		return doExecuteWithRedis(jedis -> jedis.blpop(args));
	}

	@Override
	public List<String> brpop(String... args) {
		return doExecuteWithRedis(jedis -> jedis.brpop(args));
	}

	@Override
	public Set<String> keys(String pattern) {
		return doExecuteWithRedis(jedis -> jedis.keys(pattern));
	}

	@Override
	public String watch(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.watch(keys));
	}

	@Override
	public String unwatch() {
		return doExecuteWithRedis(jedis -> jedis.unwatch());
	}

	@Override
	public String randomKey() {
		return doExecuteWithRedis(jedis -> jedis.randomKey());
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public ScanResult<String> scan(int cursor) {
		return doExecuteWithRedis(jedis -> jedis.scan(cursor));
	}

	@Override
	public ScanResult<String> scan(String cursor) {
		return doExecuteWithRedis(jedis -> jedis.scan(cursor));
	}

	@Override
	public Object eval(byte[] script, byte[] keyCount, byte[]... params) {
		return doExecuteWithRedis(jedis -> jedis.eval(script, keyCount, params));
	}

	@Override
	public Object eval(byte[] script, int keyCount, byte[]... params) {
		return doExecuteWithRedis(jedis -> jedis.eval(script, keyCount, params));
	}

	@Override
	public Object eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		return doExecuteWithRedis(jedis -> jedis.eval(script, keys, args));
	}

	@Override
	public Object eval(byte[] script, byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.eval(script, key));
	}

	@Override
	public Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
		return doExecuteWithRedis(jedis -> jedis.evalsha(sha1, keys, args));
	}

	@Override
	public Object evalsha(byte[] sha1, int keyCount, byte[]... params) {
		return doExecuteWithRedis(jedis -> jedis.evalsha(sha1, keyCount, params));
	}

	@Override
	public Long exists(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.exists(keys));
	}

	@Override
	public Long del(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.del(keys));
	}

	@Override
	public List<byte[]> blpop(int timeout, byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.blpop(timeout, keys));
	}

	@Override
	public List<byte[]> brpop(int timeout, byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.brpop(timeout, keys));
	}

	@Override
	public List<byte[]> mget(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.mget(keys));
	}

	@Override
	public String mset(byte[]... keysvalues) {
		return doExecuteWithRedis(jedis -> jedis.mset(keysvalues));
	}

	@Override
	public Long msetnx(byte[]... keysvalues) {
		return doExecuteWithRedis(jedis -> jedis.msetnx(keysvalues));
	}

	@Override
	public String rename(byte[] oldkey, byte[] newkey) {
		return doExecuteWithRedis(jedis -> jedis.rename(oldkey, newkey));
	}

	@Override
	public Long renamenx(byte[] oldkey, byte[] newkey) {
		return doExecuteWithRedis(jedis -> jedis.renamenx(oldkey, newkey));
	}

	@Override
	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		return doExecuteWithRedis(jedis -> jedis.rpoplpush(srckey, dstkey));
	}

	@Override
	public Set<byte[]> sdiff(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.sdiff(keys));
	}

	@Override
	public Long sdiffstore(byte[] dstkey, byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.sdiffstore(dstkey, keys));
	}

	@Override
	public Set<byte[]> sinter(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.sinter(keys));
	}

	@Override
	public Long sinterstore(byte[] dstkey, byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.sinterstore(dstkey, keys));
	}

	@Override
	public Long smove(byte[] srckey, byte[] dstkey, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.smove(srckey, dstkey, member));
	}

	@Override
	public Long sort(byte[] key, SortingParams sortingParameters, byte[] dstkey) {
		return doExecuteWithRedis(jedis -> jedis.sort(key, sortingParameters, dstkey));
	}

	@Override
	public Long sort(byte[] key, byte[] dstkey) {
		return doExecuteWithRedis(jedis -> jedis.sort(key, dstkey));
	}

	@Override
	public Set<byte[]> sunion(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.sunion(keys));
	}

	@Override
	public Long sunionstore(byte[] dstkey, byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.sunionstore(dstkey, keys));
	}

	@Override
	public Long zinterstore(byte[] dstkey, byte[]... sets) {
		return doExecuteWithRedis(jedis -> jedis.zinterstore(dstkey, sets));
	}

	@Override
	public Long zinterstore(byte[] dstkey, ZParams params, byte[]... sets) {
		return doExecuteWithRedis(jedis -> jedis.zinterstore(dstkey, params, sets));
	}

	@Override
	public Long zunionstore(byte[] dstkey, byte[]... sets) {
		return doExecuteWithRedis(jedis -> jedis.zunionstore(dstkey, sets));
	}

	@Override
	public Long zunionstore(byte[] dstkey, ZParams params, byte[]... sets) {
		return doExecuteWithRedis(jedis -> jedis.zunionstore(dstkey, params, sets));
	}

	@Override
	public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		return doExecuteWithRedis(jedis -> jedis.brpoplpush(source, destination, timeout));
	}

	@Override
	public Long publish(byte[] channel, byte[] message) {
		return doExecuteWithRedis(jedis -> jedis.publish(channel, message));
	}

	@Override
	public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
		doExecuteWithRedis(jedis -> {
			jedis.subscribe(jedisPubSub, channels);
			return null;
		});
	}

	@Override
	public void psubscribe(BinaryJedisPubSub jedisPubSub, byte[]... patterns) {
		doExecuteWithRedis(jedis -> {
			jedis.psubscribe(jedisPubSub, patterns);
			return null;
		});
	}

	@Override
	public Long bitop(BitOP op, byte[] destKey, byte[]... srcKeys) {
		return doExecuteWithRedis(jedis -> jedis.bitop(op, destKey, srcKeys));
	}

	@Override
	public String pfmerge(byte[] destkey, byte[]... sourcekeys) {
		return doExecuteWithRedis(jedis -> jedis.pfmerge(destkey, sourcekeys));
	}

	@Override
	public Long pfcount(byte[]... keys) {
		return doExecuteWithRedis(jedis -> jedis.pfcount(keys));
	}

	@Override
	public String set(byte[] key, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.set(key, value));
	}

	@Override
	public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		return doExecuteWithRedis(jedis -> jedis.set(key, value, nxxx, expx, time));
	}

	@Override
	public byte[] get(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.get(key));
	}

	@Override
	public Boolean exists(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.exists(key));
	}

	@Override
	public Long persist(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.persist(key));
	}

	@Override
	public String type(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.type(key));
	}

	@Override
	public Long expire(byte[] key, int seconds) {
		return doExecuteWithRedis(jedis -> jedis.expire(key, seconds));
	}

	@Override
	public Long pexpire(byte[] key, long milliseconds) {
		return doExecuteWithRedis(jedis -> jedis.pexpire(key, milliseconds));
	}

	@Override
	public Long expireAt(byte[] key, long unixTime) {
		return doExecuteWithRedis(jedis -> jedis.expireAt(key, unixTime));
	}

	@Override
	public Long pexpireAt(byte[] key, long millisecondsTimestamp) {
		return doExecuteWithRedis(jedis -> jedis.pexpireAt(key, millisecondsTimestamp));
	}

	@Override
	public Long ttl(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.ttl(key));
	}

	@Override
	public Boolean setbit(byte[] key, long offset, boolean value) {
		return doExecuteWithRedis(jedis -> jedis.setbit(key, offset, value));
	}

	@Override
	public Boolean setbit(byte[] key, long offset, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.setbit(key, offset, value));
	}

	@Override
	public Boolean getbit(byte[] key, long offset) {
		return doExecuteWithRedis(jedis -> jedis.getbit(key, offset));
	}

	@Override
	public Long setrange(byte[] key, long offset, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.setrange(key, offset, value));
	}

	@Override
	public byte[] getrange(byte[] key, long startOffset, long endOffset) {
		return doExecuteWithRedis(jedis -> jedis.getrange(key, startOffset, endOffset));
	}

	@Override
	public byte[] getSet(byte[] key, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.getSet(key, value));
	}

	@Override
	public Long setnx(byte[] key, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.setnx(key, value));
	}

	@Override
	public String setex(byte[] key, int seconds, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.setex(key, seconds, value));
	}

	@Override
	public Long decrBy(byte[] key, long integer) {
		return doExecuteWithRedis(jedis -> jedis.decrBy(key, integer));
	}

	@Override
	public Long decr(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.decr(key));
	}

	@Override
	public Long incrBy(byte[] key, long integer) {
		return doExecuteWithRedis(jedis -> jedis.incrBy(key, integer));
	}

	@Override
	public Double incrByFloat(byte[] key, double value) {
		return doExecuteWithRedis(jedis -> jedis.incrByFloat(key, value));
	}

	@Override
	public Long incr(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.incr(key));
	}

	@Override
	public Long append(byte[] key, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.append(key, value));
	}

	@Override
	public byte[] substr(byte[] key, int start, int end) {
		return doExecuteWithRedis(jedis -> jedis.substr(key, start, end));
	}

	@Override
	public Long hset(byte[] key, byte[] field, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.hset(key, field, value));
	}

	@Override
	public byte[] hget(byte[] key, byte[] field) {
		return doExecuteWithRedis(jedis -> jedis.hget(key, field));
	}

	@Override
	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.hsetnx(key, field, value));
	}

	@Override
	public String hmset(byte[] key, Map<byte[], byte[]> hash) {
		return doExecuteWithRedis(jedis -> jedis.hmset(key, hash));
	}

	@Override
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		return doExecuteWithRedis(jedis -> jedis.hmget(key, fields));
	}

	@Override
	public Long hincrBy(byte[] key, byte[] field, long value) {
		return doExecuteWithRedis(jedis -> jedis.hincrBy(key, field, value));
	}

	@Override
	public Double hincrByFloat(byte[] key, byte[] field, double value) {
		return doExecuteWithRedis(jedis -> jedis.hincrByFloat(key, field, value));
	}

	@Override
	public Boolean hexists(byte[] key, byte[] field) {
		return doExecuteWithRedis(jedis -> jedis.hexists(key, field));
	}

	@Override
	public Long hdel(byte[] key, byte[]... field) {
		return doExecuteWithRedis(jedis -> jedis.hdel(key, field));
	}

	@Override
	public Long hlen(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.hlen(key));
	}

	@Override
	public Set<byte[]> hkeys(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.hkeys(key));
	}

	@Override
	public Collection<byte[]> hvals(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.hvals(key));
	}

	@Override
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.hgetAll(key));
	}

	@Override
	public Long rpush(byte[] key, byte[]... args) {
		return doExecuteWithRedis(jedis -> jedis.rpush(key, args));
	}

	@Override
	public Long lpush(byte[] key, byte[]... args) {
		return doExecuteWithRedis(jedis -> jedis.lpush(key, args));
	}

	@Override
	public Long llen(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.llen(key));
	}

	@Override
	public List<byte[]> lrange(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.lrange(key, start, end));
	}

	@Override
	public String ltrim(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.ltrim(key, start, end));
	}

	@Override
	public byte[] lindex(byte[] key, long index) {
		return doExecuteWithRedis(jedis -> jedis.lindex(key, index));
	}

	@Override
	public String lset(byte[] key, long index, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.lset(key, index, value));
	}

	@Override
	public Long lrem(byte[] key, long count, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.lrem(key, count, value));
	}

	@Override
	public byte[] lpop(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.lpop(key));
	}

	@Override
	public byte[] rpop(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.rpop(key));
	}

	@Override
	public Long sadd(byte[] key, byte[]... member) {
		return doExecuteWithRedis(jedis -> jedis.sadd(key, member));
	}

	@Override
	public Set<byte[]> smembers(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.smembers(key));
	}

	@Override
	public Long srem(byte[] key, byte[]... member) {
		return doExecuteWithRedis(jedis -> jedis.srem(key, member));
	}

	@Override
	public byte[] spop(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.spop(key));
	}

	@Override
	public Set<byte[]> spop(byte[] key, long count) {
		return doExecuteWithRedis(jedis -> jedis.spop(key, count));
	}

	@Override
	public Long scard(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.scard(key));
	}

	@Override
	public Boolean sismember(byte[] key, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.sismember(key, member));
	}

	@Override
	public byte[] srandmember(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.srandmember(key));
	}

	@Override
	public List<byte[]> srandmember(byte[] key, int count) {
		return doExecuteWithRedis(jedis -> jedis.srandmember(key, count));
	}

	@Override
	public Long strlen(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.strlen(key));
	}

	@Override
	public Long zadd(byte[] key, double score, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, score, member));
	}

	@Override
	public Long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, scoreMembers));
	}

	@Override
	public Long zadd(byte[] key, double score, byte[] member, ZAddParams params) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, score, member, params));
	}

	@Override
	public Long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, scoreMembers, params));
	}

	@Override
	public Set<byte[]> zrange(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrange(key, start, end));
	}

	@Override
	public Long zrem(byte[] key, byte[]... member) {
		return doExecuteWithRedis(jedis -> jedis.zrem(key, member));
	}

	@Override
	public Double zincrby(byte[] key, double score, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.zincrby(key, score, member));
	}

	@Override
	public Double zincrby(byte[] key, double score, byte[] member, ZIncrByParams params) {
		return doExecuteWithRedis(jedis -> jedis.zincrby(key, score, member, params));
	}

	@Override
	public Long zrank(byte[] key, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.zrank(key, member));
	}

	@Override
	public Long zrevrank(byte[] key, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.zrevrank(key, member));
	}

	@Override
	public Set<byte[]> zrevrange(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrevrange(key, start, end));
	}

	@Override
	public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrangeWithScores(key, start, end));
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeWithScores(key, start, end));
	}

	@Override
	public Long zcard(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.zcard(key));
	}

	@Override
	public Double zscore(byte[] key, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.zscore(key, member));
	}

	@Override
	public List<byte[]> sort(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.sort(key));
	}

	@Override
	public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		return doExecuteWithRedis(jedis -> jedis.sort(key, sortingParameters));
	}

	@Override
	public Long zcount(byte[] key, double min, double max) {
		return doExecuteWithRedis(jedis -> jedis.zcount(key, min, max));
	}

	@Override
	public Long zcount(byte[] key, byte[] min, byte[] max) {
		return doExecuteWithRedis(jedis -> jedis.zcount(key, min, max));
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max));
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max));
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min));
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max, offset, count));
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min));
	}

	@Override
	public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max, offset, count));
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min, offset, count));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max, offset, count));
	}

	@Override
	public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min, offset, count));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max, offset, count));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min, offset, count));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min, offset, count));
	}

	@Override
	public Long zremrangeByRank(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByRank(key, start, end));
	}

	@Override
	public Long zremrangeByScore(byte[] key, double start, double end) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByScore(key, start, end));
	}

	@Override
	public Long zremrangeByScore(byte[] key, byte[] start, byte[] end) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByScore(key, start, end));
	}

	@Override
	public Long zlexcount(byte[] key, byte[] min, byte[] max) {
		return doExecuteWithRedis(jedis -> jedis.zlexcount(key, min, max));
	}

	@Override
	public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByLex(key, min, max));
	}

	@Override
	public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByLex(key, min, max, offset, count));
	}

	@Override
	public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByLex(key, max, min));
	}

	@Override
	public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByLex(key, max, min, offset, count));
	}

	@Override
	public Long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByLex(key, min, max));
	}

	@Override
	public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
		return doExecuteWithRedis(jedis -> jedis.linsert(key, where, pivot, value));
	}

	@Override
	public Long lpushx(byte[] key, byte[]... arg) {
		return doExecuteWithRedis(jedis -> jedis.lpushx(key, arg));
	}

	@Override
	public Long rpushx(byte[] key, byte[]... arg) {
		return doExecuteWithRedis(jedis -> jedis.rpushx(key, arg));
	}

	@Override
	public Long del(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.del(key));
	}

	@Override
	public byte[] echo(byte[] arg) {
		return doExecuteWithRedis(jedis -> jedis.echo(arg));
	}

	@Override
	public Long bitcount(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.bitcount(key));
	}

	@Override
	public Long bitcount(byte[] key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.bitcount(key, start, end));
	}

	@Override
	public Long pfadd(byte[] key, byte[]... elements) {
		return doExecuteWithRedis(jedis -> jedis.pfadd(key, elements));
	}

	@Override
	public long pfcount(byte[] key) {
		return doExecuteWithRedis(jedis -> jedis.pfcount(key));
	}

	@Override
	public Long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
		return doExecuteWithRedis(jedis -> jedis.geoadd(key, longitude, latitude, member));
	}

	@Override
	public Long geoadd(byte[] key, Map<byte[], GeoCoordinate> memberCoordinateMap) {
		return doExecuteWithRedis(jedis -> jedis.geoadd(key, memberCoordinateMap));
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2) {
		return doExecuteWithRedis(jedis -> jedis.geodist(key, member1, member2));
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit) {
		return doExecuteWithRedis(jedis -> jedis.geodist(key, member1, member2, unit));
	}

	@Override
	public List<byte[]> geohash(byte[] key, byte[]... members) {
		return doExecuteWithRedis(jedis -> jedis.geohash(key, members));
	}

	@Override
	public List<GeoCoordinate> geopos(byte[] key, byte[]... members) {
		return doExecuteWithRedis(jedis -> jedis.geopos(key, members));
	}

	@Override
	public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit) {
		return doExecuteWithRedis(jedis -> jedis.georadius(key, longitude, latitude, radius, unit));
	}

	@Override
	public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		return doExecuteWithRedis(jedis -> jedis.georadius(key, longitude, latitude, radius, unit, param));
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit) {
		return doExecuteWithRedis(jedis -> jedis.georadiusByMember(key, member, radius, unit));
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		return doExecuteWithRedis(jedis -> jedis.georadiusByMember(key, member, radius, unit, param));
	}

	@Override
	public ScanResult<byte[]> scan(byte[] cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.scan(cursor, params));
	}

	@Override
	public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor) {
		return doExecuteWithRedis(jedis -> jedis.hscan(key, cursor));
	}

	@Override
	public ScanResult<Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.hscan(key, cursor, params));
	}

	@Override
	public ScanResult<byte[]> sscan(byte[] key, byte[] cursor) {
		return doExecuteWithRedis(jedis -> jedis.sscan(key, cursor));
	}

	@Override
	public ScanResult<byte[]> sscan(byte[] key, byte[] cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.sscan(key, cursor, params));
	}

	@Override
	public ScanResult<Tuple> zscan(byte[] key, byte[] cursor) {
		return doExecuteWithRedis(jedis -> jedis.zscan(key, cursor));
	}

	@Override
	public ScanResult<Tuple> zscan(byte[] key, byte[] cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.zscan(key, cursor, params));
	}

	@Override
	public List<byte[]> bitfield(byte[] key, byte[]... arguments) {
		return doExecuteWithRedis(jedis -> jedis.bitfield(key, arguments));
	}

	@Override
	public String ping() {
		return doExecuteWithRedis(jedis -> jedis.ping());
	}

	@Override
	public String quit() {
		return doExecuteWithRedis(jedis -> jedis.quit());
	}

	@Override
	public String flushDB() {
		return doExecuteWithRedis(jedis -> jedis.flushDB());
	}

	@Override
	public Long dbSize() {
		return doExecuteWithRedis(jedis -> jedis.dbSize());
	}

	@Override
	public String select(int index) {
		return doExecuteWithRedis(jedis -> jedis.select(index));
	}

	@Override
	public String flushAll() {
		return doExecuteWithRedis(jedis -> jedis.flushAll());
	}

	@Override
	public String auth(String password) {
		return doExecuteWithRedis(jedis -> jedis.auth(password));
	}

	@Override
	public String save() {
		return doExecuteWithRedis(jedis -> jedis.save());
	}

	@Override
	public String bgsave() {
		return doExecuteWithRedis(jedis -> jedis.bgsave());
	}

	@Override
	public String bgrewriteaof() {
		return doExecuteWithRedis(jedis -> jedis.bgrewriteaof());
	}

	@Override
	public Long lastsave() {
		return doExecuteWithRedis(jedis -> jedis.lastsave());
	}

	@Override
	public String shutdown() {
		return doExecuteWithRedis(jedis -> jedis.shutdown());
	}

	@Override
	public String info() {
		return doExecuteWithRedis(jedis -> jedis.info());
	}

	@Override
	public String info(String section) {
		return doExecuteWithRedis(jedis -> jedis.info(section));
	}

	@Override
	public String slaveof(String host, int port) {
		return doExecuteWithRedis(jedis -> jedis.slaveof(host, port));
	}

	@Override
	public String slaveofNoOne() {
		return doExecuteWithRedis(jedis -> jedis.slaveofNoOne());
	}

	@Override
	public Long getDB() {
		return doExecuteWithRedis(jedis -> jedis.getDB());
	}

	@Override
	public String debug(DebugParams params) {
		return doExecuteWithRedis(jedis -> jedis.debug(params));
	}

	@Override
	public String configResetStat() {
		return doExecuteWithRedis(jedis -> jedis.configResetStat());
	}

	@Override
	public Long waitReplicas(int replicas, long timeout) {
		return doExecuteWithRedis(jedis -> jedis.waitReplicas(replicas, timeout));
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {
		return doExecuteWithRedis(jedis -> jedis.eval(script, keyCount, params));
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		return doExecuteWithRedis(jedis -> jedis.eval(script, keys, args));
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		return doExecuteWithRedis(jedis -> jedis.evalsha(sha1, keys, args));
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		return doExecuteWithRedis(jedis -> jedis.evalsha(sha1, keyCount, params));
	}

	@Override
	public Long exists(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.exists(keys));
	}

	@Override
	public Long del(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.del(keys));
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		return doExecuteWithRedis(jedis -> jedis.blpop(timeout, keys));
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		return doExecuteWithRedis(jedis -> jedis.brpop(timeout, keys));
	}

	@Override
	public List<String> mget(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.mget(keys));
	}

	@Override
	public String mset(String... keysvalues) {
		return doExecuteWithRedis(jedis -> jedis.mset(keysvalues));
	}

	@Override
	public Long msetnx(String... keysvalues) {
		return doExecuteWithRedis(jedis -> jedis.msetnx(keysvalues));
	}

	@Override
	public String rename(String oldkey, String newkey) {
		return doExecuteWithRedis(jedis -> jedis.rename(oldkey, newkey));
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		return doExecuteWithRedis(jedis -> jedis.renamenx(oldkey, newkey));
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		return doExecuteWithRedis(jedis -> jedis.rpoplpush(srckey, dstkey));
	}

	@Override
	public Set<String> sdiff(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.sdiff(keys));
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		return doExecuteWithRedis(jedis -> jedis.sdiffstore(dstkey, keys));
	}

	@Override
	public Set<String> sinter(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.sinter(keys));
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		return doExecuteWithRedis(jedis -> jedis.sinterstore(dstkey, keys));
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		return doExecuteWithRedis(jedis -> jedis.smove(srckey, dstkey, member));
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		return doExecuteWithRedis(jedis -> jedis.sort(key, sortingParameters, dstkey));
	}

	@Override
	public Long sort(String key, String dstkey) {
		return doExecuteWithRedis(jedis -> jedis.sort(key, dstkey));
	}

	@Override
	public Set<String> sunion(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.sunion(keys));
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		return doExecuteWithRedis(jedis -> jedis.sunionstore(dstkey, keys));
	}

	@Override
	public Long zinterstore(String dstkey, String... sets) {
		return doExecuteWithRedis(jedis -> jedis.zinterstore(dstkey, sets));
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {
		return doExecuteWithRedis(jedis -> jedis.zinterstore(dstkey, params, sets));
	}

	@Override
	public Long zunionstore(String dstkey, String... sets) {
		return doExecuteWithRedis(jedis -> jedis.zunionstore(dstkey, sets));
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {
		return doExecuteWithRedis(jedis -> jedis.zunionstore(dstkey, params, sets));
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		return doExecuteWithRedis(jedis -> jedis.brpoplpush(source, destination, timeout));
	}

	@Override
	public Long publish(String channel, String message) {
		return doExecuteWithRedis(jedis -> jedis.publish(channel, message));
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		doExecuteWithRedis(jedis -> {
			jedis.subscribe(jedisPubSub, channels);
			return null;
		});
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		doExecuteWithRedis(jedis -> {
			jedis.psubscribe(jedisPubSub, patterns);
			return null;
		});
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		return doExecuteWithRedis(jedis -> jedis.bitop(op, destKey, srcKeys));
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		return doExecuteWithRedis(jedis -> jedis.pfmerge(destkey, sourcekeys));
	}

	@Override
	public long pfcount(String... keys) {
		return doExecuteWithRedis(jedis -> jedis.pfcount(keys));
	}

	@Override
	public ScanResult<String> scan(String cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.scan(cursor, params));
	}

	@Override
	public String set(String key, String value) {
		return doExecuteWithRedis(jedis -> jedis.set(key, value));
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		return doExecuteWithRedis(jedis -> jedis.set(key, value, nxxx, expx, time));
	}

	@Override
	public String set(String key, String value, String nxxx) {
		return doExecuteWithRedis(jedis -> jedis.set(key, value, nxxx));
	}

	@Override
	public String get(String key) {
		return doExecuteWithRedis(jedis -> jedis.get(key));
	}

	@Override
	public Boolean exists(String key) {
		return doExecuteWithRedis(jedis -> jedis.exists(key));
	}

	@Override
	public Long persist(String key) {
		return doExecuteWithRedis(jedis -> jedis.persist(key));
	}

	@Override
	public String type(String key) {
		return doExecuteWithRedis(jedis -> jedis.type(key));
	}

	@Override
	public Long expire(String key, int seconds) {
		return doExecuteWithRedis(jedis -> jedis.expire(key, seconds));
	}

	@Override
	public Long pexpire(String key, long milliseconds) {
		return doExecuteWithRedis(jedis -> jedis.pexpire(key, milliseconds));
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		return doExecuteWithRedis(jedis -> jedis.expireAt(key, unixTime));
	}

	@Override
	public Long pexpireAt(String key, long millisecondsTimestamp) {
		return doExecuteWithRedis(jedis -> jedis.pexpireAt(key, millisecondsTimestamp));
	}

	@Override
	public Long ttl(String key) {
		return doExecuteWithRedis(jedis -> jedis.ttl(key));
	}

	@Override
	public Long pttl(String key) {
		return doExecuteWithRedis(jedis -> jedis.pttl(key));
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		return doExecuteWithRedis(jedis -> jedis.setbit(key, offset, value));
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		return doExecuteWithRedis(jedis -> jedis.setbit(key, offset, value));
	}

	@Override
	public Boolean getbit(String key, long offset) {
		return doExecuteWithRedis(jedis -> jedis.getbit(key, offset));
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return doExecuteWithRedis(jedis -> jedis.setrange(key, offset, value));
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		return doExecuteWithRedis(jedis -> jedis.getrange(key, startOffset, endOffset));
	}

	@Override
	public String getSet(String key, String value) {
		return doExecuteWithRedis(jedis -> jedis.getSet(key, value));
	}

	@Override
	public Long setnx(String key, String value) {
		return doExecuteWithRedis(jedis -> jedis.setnx(key, value));
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return doExecuteWithRedis(jedis -> jedis.setex(key, seconds, value));
	}

	@Override
	public String psetex(String key, long milliseconds, String value) {
		return doExecuteWithRedis(jedis -> jedis.psetex(key, milliseconds, value));
	}

	@Override
	public Long decrBy(String key, long integer) {
		return doExecuteWithRedis(jedis -> jedis.decrBy(key, integer));
	}

	@Override
	public Long decr(String key) {
		return doExecuteWithRedis(jedis -> jedis.decr(key));
	}

	@Override
	public Long incrBy(String key, long integer) {
		return doExecuteWithRedis(jedis -> jedis.incrBy(key, integer));
	}

	@Override
	public Double incrByFloat(String key, double value) {
		return doExecuteWithRedis(jedis -> jedis.incrByFloat(key, value));
	}

	@Override
	public Long incr(String key) {
		return doExecuteWithRedis(jedis -> jedis.incr(key));
	}

	@Override
	public Long append(String key, String value) {
		return doExecuteWithRedis(jedis -> jedis.append(key, value));
	}

	@Override
	public String substr(String key, int start, int end) {
		return doExecuteWithRedis(jedis -> jedis.substr(key, start, end));
	}

	@Override
	public Long hset(String key, String field, String value) {
		return doExecuteWithRedis(jedis -> jedis.hset(key, field, value));
	}

	@Override
	public String hget(String key, String field) {
		return doExecuteWithRedis(jedis -> jedis.hget(key, field));
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return doExecuteWithRedis(jedis -> jedis.hsetnx(key, field, value));
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		return doExecuteWithRedis(jedis -> jedis.hmset(key, hash));
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return doExecuteWithRedis(jedis -> jedis.hmget(key, fields));
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		return doExecuteWithRedis(jedis -> jedis.hincrBy(key, field, value));
	}

	@Override
	public Double hincrByFloat(String key, String field, double value) {
		return doExecuteWithRedis(jedis -> jedis.hincrByFloat(key, field, value));
	}

	@Override
	public Boolean hexists(String key, String field) {
		return doExecuteWithRedis(jedis -> jedis.hexists(key, field));
	}

	@Override
	public Long hdel(String key, String... field) {
		return doExecuteWithRedis(jedis -> jedis.hdel(key, field));
	}

	@Override
	public Long hlen(String key) {
		return doExecuteWithRedis(jedis -> jedis.hlen(key));
	}

	@Override
	public Set<String> hkeys(String key) {
		return doExecuteWithRedis(jedis -> jedis.hkeys(key));
	}

	@Override
	public List<String> hvals(String key) {
		return doExecuteWithRedis(jedis -> jedis.hvals(key));
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		return doExecuteWithRedis(jedis -> jedis.hgetAll(key));
	}

	@Override
	public Long rpush(String key, String... string) {
		return doExecuteWithRedis(jedis -> jedis.rpush(key, string));
	}

	@Override
	public Long lpush(String key, String... string) {
		return doExecuteWithRedis(jedis -> jedis.lpush(key, string));
	}

	@Override
	public Long llen(String key) {
		return doExecuteWithRedis(jedis -> jedis.llen(key));
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.lrange(key, start, end));
	}

	@Override
	public String ltrim(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.ltrim(key, start, end));
	}

	@Override
	public String lindex(String key, long index) {
		return doExecuteWithRedis(jedis -> jedis.lindex(key, index));
	}

	@Override
	public String lset(String key, long index, String value) {
		return doExecuteWithRedis(jedis -> jedis.lset(key, index, value));
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return doExecuteWithRedis(jedis -> jedis.lrem(key, count, value));
	}

	@Override
	public String lpop(String key) {
		return doExecuteWithRedis(jedis -> jedis.lpop(key));
	}

	@Override
	public String rpop(String key) {
		return doExecuteWithRedis(jedis -> jedis.rpop(key));
	}

	@Override
	public Long sadd(String key, String... member) {
		return doExecuteWithRedis(jedis -> jedis.sadd(key, member));
	}

	@Override
	public Set<String> smembers(String key) {
		return doExecuteWithRedis(jedis -> jedis.smembers(key));
	}

	@Override
	public Long srem(String key, String... member) {
		return doExecuteWithRedis(jedis -> jedis.srem(key, member));
	}

	@Override
	public String spop(String key) {
		return doExecuteWithRedis(jedis -> jedis.spop(key));
	}

	@Override
	public Set<String> spop(String key, long count) {
		return doExecuteWithRedis(jedis -> jedis.spop(key, count));
	}

	@Override
	public Long scard(String key) {
		return doExecuteWithRedis(jedis -> jedis.scard(key));
	}

	@Override
	public Boolean sismember(String key, String member) {
		return doExecuteWithRedis(jedis -> jedis.sismember(key, member));
	}

	@Override
	public String srandmember(String key) {
		return doExecuteWithRedis(jedis -> jedis.srandmember(key));
	}

	@Override
	public List<String> srandmember(String key, int count) {
		return doExecuteWithRedis(jedis -> jedis.srandmember(key, count));
	}

	@Override
	public Long strlen(String key) {
		return doExecuteWithRedis(jedis -> jedis.strlen(key));
	}

	@Override
	public Long zadd(String key, double score, String member) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, score, member));
	}

	@Override
	public Long zadd(String key, double score, String member, ZAddParams params) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, score, member, params));
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, scoreMembers));
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
		return doExecuteWithRedis(jedis -> jedis.zadd(key, scoreMembers, params));
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrange(key, start, end));
	}

	@Override
	public Long zrem(String key, String... member) {
		return doExecuteWithRedis(jedis -> jedis.zrem(key, member));
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return doExecuteWithRedis(jedis -> jedis.zincrby(key, score, member));
	}

	@Override
	public Double zincrby(String key, double score, String member, ZIncrByParams params) {
		return doExecuteWithRedis(jedis -> jedis.zincrby(key, score, member, params));
	}

	@Override
	public Long zrank(String key, String member) {
		return doExecuteWithRedis(jedis -> jedis.zrank(key, member));
	}

	@Override
	public Long zrevrank(String key, String member) {
		return doExecuteWithRedis(jedis -> jedis.zrevrank(key, member));
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrevrange(key, start, end));
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrangeWithScores(key, start, end));
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeWithScores(key, start, end));
	}

	@Override
	public Long zcard(String key) {
		return doExecuteWithRedis(jedis -> jedis.zcard(key));
	}

	@Override
	public Double zscore(String key, String member) {
		return doExecuteWithRedis(jedis -> jedis.zscore(key, member));
	}

	@Override
	public List<String> sort(String key) {
		return doExecuteWithRedis(jedis -> jedis.sort(key));
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		return doExecuteWithRedis(jedis -> jedis.sort(key, sortingParameters));
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return doExecuteWithRedis(jedis -> jedis.zcount(key, min, max));
	}

	@Override
	public Long zcount(String key, String min, String max) {
		return doExecuteWithRedis(jedis -> jedis.zcount(key, min, max));
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max));
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max));
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min));
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max, offset, count));
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min));
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScore(key, min, max, offset, count));
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min, offset, count));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max, offset, count));
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScore(key, max, min, offset, count));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min));
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByScoreWithScores(key, min, max, offset, count));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min, offset, count));
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min, offset, count));
	}

	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByRank(key, start, end));
	}

	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByScore(key, start, end));
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByScore(key, start, end));
	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		return doExecuteWithRedis(jedis -> jedis.zlexcount(key, min, max));
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByLex(key, min, max));
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrangeByLex(key, min, max, offset, count));
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByLex(key, max, min));
	}

	@Override
	public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
		return doExecuteWithRedis(jedis -> jedis.zrevrangeByLex(key, max, min, offset, count));
	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		return doExecuteWithRedis(jedis -> jedis.zremrangeByLex(key, min, max));
	}

	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		return doExecuteWithRedis(jedis -> jedis.linsert(key, where, pivot, value));
	}

	@Override
	public Long lpushx(String key, String... string) {
		return doExecuteWithRedis(jedis -> jedis.lpushx(key, string));
	}

	@Override
	public Long rpushx(String key, String... string) {
		return doExecuteWithRedis(jedis -> jedis.rpushx(key, string));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<String> blpop(String arg) {
		return doExecuteWithRedis(jedis -> jedis.blpop(arg));
	}

	@Override
	public List<String> blpop(int timeout, String key) {
		return doExecuteWithRedis(jedis -> jedis.blpop(timeout, key));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public List<String> brpop(String arg) {
		return doExecuteWithRedis(jedis -> jedis.brpop(arg));
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		return doExecuteWithRedis(jedis -> jedis.brpop(timeout, key));
	}

	@Override
	public Long del(String key) {
		return doExecuteWithRedis(jedis -> jedis.del(key));
	}

	@Override
	public String echo(String string) {
		return doExecuteWithRedis(jedis -> jedis.echo(string));
	}

	@Override
	public Long move(String key, int dbIndex) {
		return doExecuteWithRedis(jedis -> jedis.move(key, dbIndex));
	}

	@Override
	public Long bitcount(String key) {
		return doExecuteWithRedis(jedis -> jedis.bitcount(key));
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		return doExecuteWithRedis(jedis -> jedis.bitcount(key, start, end));
	}

	@Override
	public Long bitpos(String key, boolean value) {
		return doExecuteWithRedis(jedis -> jedis.bitpos(key, value));
	}

	@Override
	public Long bitpos(String key, boolean value, BitPosParams params) {
		return doExecuteWithRedis(jedis -> jedis.bitpos(key, value, params));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		return doExecuteWithRedis(jedis -> jedis.hscan(key, cursor));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public ScanResult<String> sscan(String key, int cursor) {
		return doExecuteWithRedis(jedis -> jedis.sscan(key, cursor));
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public ScanResult<Tuple> zscan(String key, int cursor) {
		return doExecuteWithRedis(jedis -> jedis.zscan(key, cursor));
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		return doExecuteWithRedis(jedis -> jedis.hscan(key, cursor));
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.hscan(key, cursor, params));
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		return doExecuteWithRedis(jedis -> jedis.sscan(key, cursor));
	}

	@Override
	public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.sscan(key, cursor, params));
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		return doExecuteWithRedis(jedis -> jedis.zscan(key, cursor));
	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
		return doExecuteWithRedis(jedis -> jedis.zscan(key, cursor, params));
	}

	@Override
	public Long pfadd(String key, String... elements) {
		return doExecuteWithRedis(jedis -> jedis.pfadd(key, elements));
	}

	@Override
	public long pfcount(String key) {
		return doExecuteWithRedis(jedis -> jedis.pfcount(key));
	}

	@Override
	public Long geoadd(String key, double longitude, double latitude, String member) {
		return doExecuteWithRedis(jedis -> jedis.geoadd(key, longitude, latitude, member));
	}

	@Override
	public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
		return doExecuteWithRedis(jedis -> jedis.geoadd(key, memberCoordinateMap));
	}

	@Override
	public Double geodist(String key, String member1, String member2) {
		return doExecuteWithRedis(jedis -> jedis.geodist(key, member1, member2));
	}

	@Override
	public Double geodist(String key, String member1, String member2, GeoUnit unit) {
		return doExecuteWithRedis(jedis -> jedis.geodist(key, member1, member2, unit));
	}

	@Override
	public List<String> geohash(String key, String... members) {
		return doExecuteWithRedis(jedis -> jedis.geohash(key, members));
	}

	@Override
	public List<GeoCoordinate> geopos(String key, String... members) {
		return doExecuteWithRedis(jedis -> jedis.geopos(key, members));
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
		return doExecuteWithRedis(jedis -> jedis.georadius(key, longitude, latitude, radius, unit));
	}

	@Override
	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		return doExecuteWithRedis(jedis -> jedis.georadius(key, longitude, latitude, radius, unit, param));
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
		return doExecuteWithRedis(jedis -> jedis.georadiusByMember(key, member, radius, unit));
	}

	@Override
	public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit,
			GeoRadiusParam param) {
		return doExecuteWithRedis(jedis -> jedis.georadiusByMember(key, member, radius, unit, param));
	}

	@Override
	public List<Long> bitfield(String key, String... arguments) {
		return doExecuteWithRedis(jedis -> jedis.bitfield(key, arguments));
	}

	/**
	 * Do execute with redis operations.
	 * 
	 * @param invoker
	 * @return
	 */
	protected <T> T doExecuteWithRedis(Function<Jedis, T> invoker) {
		try (Jedis jedis = jedisPool.getResource();) {
			return invoker.apply(jedis);
		} catch (Throwable t) {
			throw new JedisException("Errors jedis processing.", t);
		}
	}

}