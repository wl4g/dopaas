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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.google.common.hash.Hashing.*;
import static com.google.common.base.Charsets.UTF_8;
import static io.netty.util.internal.ThreadLocalRandom.current;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * REDIS locks manager.
 *
 * @author wangl.sir
 * @version v1.0 2019年3月19日
 * @since
 */
public class JedisLockManager {
	final protected static String NAMESPACE = "simple_spinlock_";
	final protected static String NXXX = "NX";
	final protected static String EXPX = "PX";
	final protected static Long FREQ_MS = 50L;
	final protected static String UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected JedisCluster jedisCluster;

	/**
	 * Get and create {@link SimpleSpinJedisLock} with name.
	 * 
	 * @param name
	 * @return
	 */
	public Lock getLock(String name) {
		return getLock(name, 15, TimeUnit.SECONDS);
	}

	/**
	 * Get and create {@link SimpleSpinJedisLock} with name.
	 * 
	 * @param name
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public Lock getLock(String name, long timeout, TimeUnit unit) {
		return new SimpleSpinJedisLock(md5().hashString(name, UTF_8).toString(), timeout, unit);
	}

	/**
	 * JEDIS simple spin lock.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年3月21日
	 * @since
	 */
	private class SimpleSpinJedisLock implements Lock {
		/** Current locker name. */
		final private String name;
		/** Current locker request ID. */
		final private String processId;
		/** Current locker expired time(MS). */
		final private long expiredMs;

		public SimpleSpinJedisLock(String name, long expiredMs, TimeUnit unit) {
			hasText(name, "SimpleSpinJedis lock name must not be empty");
			notNull(unit, "SimpleSpinJedis unit must not be null");
			isTrue(expiredMs > 0, "'timeoutMs' must greater than 0");
			this.name = NAMESPACE + name;
			this.expiredMs = unit.toMillis(expiredMs);
			this.processId = String.valueOf(current().nextInt(1000_0000, Integer.MAX_VALUE));
		}

		@Override
		public void lock() {
			while (true) {
				if (assertSuccess(jedisCluster.set(name, processId, NXXX, EXPX, expiredMs))) {
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
			if (assertSuccess(jedisCluster.set(name, processId, NXXX, EXPX, expiredMs))) {
				return true;
			}
			return false;
		}

		@Override
		public boolean tryLock(long tryTimeout, TimeUnit unit) throws InterruptedException {
			long t = unit.toMillis(tryTimeout) / FREQ_MS, c = 0;
			while (t > (++c)) {
				Thread.sleep(FREQ_MS);
				if (assertSuccess(jedisCluster.set(name, processId, NXXX, EXPX, expiredMs))) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void unlock() {
			if (processId.equals(jedisCluster.get(name))) { // Optimized:Locked?
				Object res = jedisCluster.eval(UNLOCK_LUA, singletonList(name), singletonList(processId));
				if (!assertSuccess(res)) {
					log.warn("Failed to unlock for %{}@{}", processId, name);
				} else if (log.isDebugEnabled()) {
					log.debug("Unlock successful for %{}@{}", processId, name);
				}
			}
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Assertion JEDIS lock result is success?
		 * 
		 * @param res
		 * @return
		 */
		final private boolean assertSuccess(Object res) {
			if (nonNull(res)) {
				if (res instanceof String) {
					String res0 = res.toString().trim();
					return "1".equals(res0) || "ok".equalsIgnoreCase(res0);
				} else if (res instanceof Number) {
					return ((Number) res).longValue() >= 1L;
				} else {
					throw new IllegalStateException(String.format("Unknown result for %s", res));
				}
			}
			return false;
		}

	}

}