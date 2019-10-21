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
package com.wl4g.devops.support.concurrent.locks;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static io.netty.util.internal.ThreadLocalRandom.current;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * REDIS locks manager.
 *
 * @author wangl.sir
 * @version v1.0 2019年3月19日
 * @since
 */
public class JedisLockManager {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private JedisCluster jedisCluster;

	public Lock getLock(String name) {
		return getLock(name, 15, TimeUnit.SECONDS);
	}

	public Lock getLock(String name, long timeout, TimeUnit unit) {
		return new SimpleSpinJedisLock(jedisCluster, Hashing.md5().hashString(name, Charsets.UTF_8).toString(), timeout,
				unit/* , jedisCluster.get(name) */);
	}

	/**
	 * Simple spin JEDIS lock.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年3月21日
	 * @since
	 */
	public static class SimpleSpinJedisLock implements Lock {
		final private static String NAMESPACE = "simple_spinlock_";
		final private static Long SUCCESS = new Long(1L);
		final private static String NXXX = "NX";
		final private static String EXPX = "PX";
		final private static long FREQ_MS = 50L;

		final private Logger log = LoggerFactory.getLogger(getClass());
		final private JedisCluster jedisCluster;
		final private String name;
		final private String processId;
		final private long expiredMs;

		public SimpleSpinJedisLock(JedisCluster jedisCluster, String name, long expiredMs,
				TimeUnit unit/* , String processId */) {
			Assert.notNull(jedisCluster, "'jedisCluster' must not be null");
			Assert.hasText(name, "'name' must not be empty");
			Assert.notNull(unit, "'unit' must not be null");
			Assert.isTrue(expiredMs > 0, "'timeoutMs' must greater than 0");
			this.jedisCluster = jedisCluster;
			this.name = NAMESPACE + name;
			this.expiredMs = (int) unit.toMillis(expiredMs);
			this.processId = String.valueOf(current().nextInt(100_0000, Integer.MAX_VALUE));
		}

		@Override
		public void lock() {
			while (true) {
				if (jedisCluster.set(name, processId, NXXX, EXPX, expiredMs) != null) {
					return;
				}
				try {
					Thread.sleep(FREQ_MS);
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tryLock() {
			String ret = jedisCluster.set(name, processId, NXXX, EXPX, expiredMs);
			return endsWithIgnoreCase(ret, "ok") || (isNumeric(ret) && Integer.parseInt(ret) > 0);
		}

		@Override
		public boolean tryLock(long tryTimeout, TimeUnit timeUnit) throws InterruptedException {
			long t = timeUnit.toMillis(tryTimeout) / FREQ_MS, c = 0;
			while (t > (++c)) {
				Thread.sleep(FREQ_MS);
				if (Objects.nonNull(jedisCluster.set(name, processId, NXXX, EXPX, expiredMs))) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void unlock() {
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object res = jedisCluster.eval(script, singletonList(name), singletonList(processId));
			if (!SUCCESS.equals(res)) {
				log.warn(String.format("Failed to unlock for '%s'", processId));
			}
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}

	}

}