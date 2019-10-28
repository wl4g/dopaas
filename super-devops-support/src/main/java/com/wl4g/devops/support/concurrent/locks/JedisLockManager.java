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

import com.google.common.annotations.Beta;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.lang.Math.abs;
import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * REDIS locks manager.
 *
 * @author wangl.sir
 * @version v1.0 2019年3月19日
 * @since
 */
public class JedisLockManager {
	final protected static String NAMESPACE = "reentrant_unfair_lock_";
	final protected static String NXXX = "NX";
	final protected static String EXPX = "PX";
	final protected static long FRAME_INTERVAL_MS = 50L;
	final protected static String UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected JedisCluster jedisCluster;

	/**
	 * Get and create {@link HAReentrantUnFairDistributedRedLock} with name.
	 * 
	 * @param name
	 * @return
	 */
	public Lock getLock(String name) {
		return getLock(name, 10, TimeUnit.SECONDS);
	}

	/**
	 * Get and create {@link HAReentrantUnFairDistributedRedLock} with name.
	 * 
	 * @param name
	 * @param expiredAt
	 * @param unit
	 * @return
	 */
	public Lock getLock(String name, long expiredAt, TimeUnit unit) {
		hasText(name, "Lock name must not be empty.");
		isTrue(expiredAt > 0, "Lock expiredAt must greater than 0");
		notNull(unit, "TimeUnit must not be null.");
		return new HAReentrantUnFairDistributedRedLock(name, unit.toMillis(expiredAt));
	}

	/**
	 * HA Reentrant unfair Redlock implemented by JEDIS, supported redis master
	 * node failover</br>
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年3月21日
	 * @since
	 * @see <a href=
	 *      'https://blog.csdn.net/matt8/article/details/64442064'>Discuss
	 *      Antirez failover analysis</a>
	 * @see <a href=
	 *      'https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html'>Martin
	 *      Kleppmann analysis redlock</a>
	 * @see <a href='https://redis.io/topics/distlock'>Antirez failover
	 *      analysis</a>
	 * @see <a href='http://antirez.com/news/101'>VS Martin Kleppmann for
	 *      Redlock failover analysis</a>
	 */
	@Beta
	private final class HAReentrantUnFairDistributedRedLock extends AbstractDistributedLock {
		private static final long serialVersionUID = -1909894475263151824L;

		/** Current locker reentrant counter. */
		final protected AtomicLong counter;

		public HAReentrantUnFairDistributedRedLock(String name, long expiredMs) {
			this(name, expiredMs, 0L);
		}

		public HAReentrantUnFairDistributedRedLock(String name, long expiredMs, long counterValue) {
			this(name, expiredMs, new AtomicLong(counterValue));
		}

		public HAReentrantUnFairDistributedRedLock(String name, long expiredMs, AtomicLong counter) {
			super((NAMESPACE + name), getThreadCurrentProcessId(), expiredMs);
			isTrue(counter.get() >= 0, "Lock count must greater than 0");
			this.counter = counter;
		}

		@Override
		public void lock() {
			try {
				lockInterruptibly();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			if (interrupted())
				throw new InterruptedException();
			while (true) {
				if (doTryAcquire())
					break;
				sleep(FRAME_INTERVAL_MS);
			}
		}

		@Override
		public boolean tryLock() {
			return doTryAcquire();
		}

		@Override
		public boolean tryLock(long tryTimeout, TimeUnit unit) throws InterruptedException {
			notNull(unit, "TimeUnit must not be null.");
			isTrue((tryTimeout > 0 && tryTimeout <= expiredMs), "TryTimeout must be > 0 && <= " + expiredMs);
			long t = unit.toMillis(tryTimeout) / FRAME_INTERVAL_MS, c = 0;
			while (t > ++c) {
				if (doTryAcquire())
					return true;
				sleep(FRAME_INTERVAL_MS);
			}
			return false;
		}

		@Override
		public void unlock() {
			// Obtain locked processId.
			String acquiredProcessId = jedisCluster.get(name);
			// Current thread is holder?
			if (!currentProcessId.equals(acquiredProcessId)) {
				if (log.isTraceEnabled()) {
					log.trace("No need to unlock of currentProcessId:{}, acquiredProcessId:{}, counter:{}", currentProcessId,
							acquiredProcessId, counter);
				}
				return;
			}

			// Add a thread stack.
			counter.decrementAndGet();
			if (log.isTraceEnabled()) {
				log.trace("No need to unlock and reenter the stack lock layer, counter: {}", counter);
			}

			if (counter.longValue() == 0L) { // All thread stack layers exited?
				Object res = jedisCluster.eval(UNLOCK_LUA, singletonList(name), singletonList(currentProcessId));
				if (!assertValidity(res)) {
					if (log.isDebugEnabled()) {
						log.debug("Failed to unlock for %{}@{}", currentProcessId, name);
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Unlock successful for %{}@{}", currentProcessId, name);
					}
				}
			}
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Execution try acquire locker by reentrant info.
		 * 
		 * @return
		 */
		private final boolean doTryAcquire() {
			if (currentProcessId.equals(jedisCluster.get(name))) {
				counter.incrementAndGet(); // Reduce one thread stack
				return true;
			}

			return assertValidity(jedisCluster.set(name, currentProcessId, NXXX, EXPX, expiredMs));

//			// Solve the problem of master node failover.
//			Map<String, JedisPool> nodes = jedisCluster.getClusterNodes();
//			notEmpty(nodes, "No redis cluster nodes available!");
//
//			Iterator<Entry<String, JedisPool>> it = nodes.entrySet().iterator();
//			long acquired = 0L, firstTime = 0L;
//			for (int i = 0; it.hasNext(); i++) {
//				Entry<String, JedisPool> ent = it.next();
//				try (Jedis jedis = ent.getValue().getResource()) {
//					if (assertValidity(jedis.set(name, currentProcessId, NXXX, EXPX, expiredMs))) {
//						++acquired;
//						if (i == 0) {
//							// [#issue] It's best not to rely too much on system
//							// time.
//							// See:https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html
//							firstTime = System.currentTimeMillis();
//						}
//					}
//				} catch (Exception e) {
//					log.warn(String.format("Can't to tryAcquire lock for node:%s", ent.getKey()), e);
//				}
//			}
//
//			// Check acquire validity. See:https://redis.io/topics/distlock
//			if (acquired >= (nodes.size() / 2 + 1)) { // Most are successful?
//				// Didn't spend too much time?
//				return abs(System.currentTimeMillis() - firstTime) < expiredMs;
//			}
//			return false;
		}

		/**
		 * Assertion validate lock result is acquired/UnAcquired success?
		 * 
		 * @param res
		 * @return
		 */
		private final boolean assertValidity(Object res) {
			if (isNull(res)) {
				return false;
			}
			if (res instanceof String) {
				String res0 = res.toString().trim();
				return "1".equals(res0) || "OK".equalsIgnoreCase(res0);
			} else if (res instanceof Number) {
				return ((Number) res).longValue() >= 1L;
			} else {
				throw new IllegalStateException(String.format("Unknown acquired state for %s", res));
			}
		}

	}

}