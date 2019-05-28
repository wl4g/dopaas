/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.support.lock;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import static org.apache.commons.lang3.StringUtils.*;

import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/**
 * Simple spin lock manager.
 *
 * @author wangl.sir
 * @version v1.0 2019年3月19日
 * @since
 */
public class SimpleRedisLockManager {

    final private JedisCluster jedisCluster;

    public SimpleRedisLockManager(JedisCluster jedisCluster) {
        Assert.notNull(jedisCluster, "'jedisCluster' must not be null");
        this.jedisCluster = jedisCluster;
    }

    public Lock getLock(String name) {
        return getLock(name, 10, TimeUnit.SECONDS);
    }

    public Lock getLock(String name, int timeout, TimeUnit unit) {
        return new SimpleSpinLock(jedisCluster, Hashing.md5().hashString(name, Charsets.UTF_8).toString(), timeout,
                unit/* , jedisCluster.get(name) */);
    }

    /**
     * Simple spin lock.
     *
     * @author wangl.sir
     * @version v1.0 2019年3月19日
     * @since
     */
    public static class SimpleSpinLock implements Lock {

        final private static String NAMESPACE = "simple_lock_";
        final private static Long SUCCESS = new Long(1L);
        final private static String NXXX = "NX";
        final private static String EXPX = "PX";
        final private static long FREQ_MS = 50L;

        final private JedisCluster jedisCluster;
        final private int timeoutMs;
        final private String name;

        private String processId;

        public SimpleSpinLock(JedisCluster jedisCluster, String name, int timeoutMs,
                              TimeUnit unit/* , String processId */) {
            Assert.notNull(jedisCluster, "'jedisCluster' must not be null");
            Assert.hasText(name, "'name' must not be empty");
            Assert.notNull(unit, "'unit' must not be null");
            Assert.isTrue(timeoutMs > 0, "'timeoutMs' must greater than 0");
            this.jedisCluster = jedisCluster;
            this.name = NAMESPACE + name;
            this.timeoutMs = (int) unit.toMillis(timeoutMs);
            this.processId = /* !StringUtils.isEmpty(processId) ? processId : */UUID.randomUUID().toString().replaceAll("-", "");
        }

        @Override
        public void lock() {
            while (true) {
                if (jedisCluster.set(name, processId, NXXX, EXPX, timeoutMs) != null) {
                    return;
                }
                try {
                    Thread.sleep(FREQ_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryLock() {
            String ret = jedisCluster.set(name, processId, NXXX, EXPX, timeoutMs);
            return endsWithIgnoreCase(ret, "ok") || (isNumeric(ret) && Integer.parseInt(ret) > 0);
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long t = unit.toMillis(time) / FREQ_MS, c = 0;
            while (t > (++c)) {
                Thread.sleep(FREQ_MS);
                if (jedisCluster.set(name, processId, NXXX, EXPX, timeoutMs) != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void unlock() {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object res = jedisCluster.eval(script, Collections.singletonList(name), Collections.singletonList(processId));
            if (SUCCESS.equals(res)) {
                //throw new IllegalMonitorStateException(String.format("Unlock failure '%s'", processId));
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }

    }

}