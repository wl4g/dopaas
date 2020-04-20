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
package com.wl4g.devops.support.cli;

import static com.wl4g.devops.common.constants.SupportDevOpsConstants.*;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.devops.tool.common.lang.ThreadUtils2.sleepRandom;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import com.wl4g.devops.common.exception.support.TimeoutDestroyProcessException;
import com.wl4g.devops.support.cli.destroy.DestroySignal;
import com.wl4g.devops.support.cli.destroy.DestroySignalMessage;
import com.wl4g.devops.support.cli.process.DestroableProcess;
import com.wl4g.devops.support.cli.repository.ProcessRepository;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.redis.JedisService;

import static com.wl4g.devops.support.cli.destroy.DestroySignalMessage.DestroyState.*;
import static com.wl4g.devops.support.redis.EnhancedJedisCluster.RedisFormatUtils.*;

/**
 * Implementation of distributed destroable command process based on jedis
 * cluster.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public class NodeProcessManagerImpl extends GenericProcessManager {
	final public static long DEFAULT_MIN_WATCH_MS = 2_00L;
	final public static long DEFAULT_MAX_WATCH_MS = 2_000L;
	/** Default destruction signal expired seconds. */
	final public static int DEFAULT_SIGNAL_EXPIRED_SEC = (int) (3 * TimeUnit.MILLISECONDS.toSeconds(DEFAULT_MAX_WATCH_MS));

	@Autowired
	protected ConfigurableEnvironment environment;

	/** Jedis service. */
	@Autowired
	protected JedisService jedisService;

	/** Jedis locks manager. */
	@Autowired
	protected JedisLockManager lockManager;

	public NodeProcessManagerImpl(ProcessRepository repository) {
		super(repository);
	}

	@Override
	public void destroyForComplete(DestroySignal signal) throws TimeoutDestroyProcessException, IllegalStateException {
		// Send destruction signal.
		String signalKey = getDestroySignalKey(signal.getProcessId());
		if (log.isInfoEnabled()) {
			log.info("Send destruction signal:{} for processId:{}", signalKey, signal.getProcessId());
		}
		jedisService.setObjectAsJson(signalKey, signal, DEFAULT_SIGNAL_EXPIRED_SEC); // MARK1

		// Wait for complete.
		int sleepTotal = 0; // Total waiting time for destruction.
		DestroySignalMessage ret = null;
		while (isNull(ret = pollDestroyMessage(signal.getProcessId()))) {
			sleepTotal += sleepRandom(100, 800);
			if (sleepTotal >= signal.getTimeoutMs()) {
				throw new TimeoutDestroyProcessException(
						String.format("Timeout destory command process for %s", signal.getProcessId()));
			}
		}

		// Check destory failure.
		if (ret.getState() == DESTROY_FAIL) {
			throw new IllegalStateException(
					String.format("Failed to destory process for %s, caused by: %s", signal.getProcessId(), ret.getMessage()));
		}

	}

	@Override
	public void run() {
		getWorker().scheduleWithRandomDelay(() -> {
			try {
				doInspectForProcessesDestroy(getDestroyLockName());
			} catch (Exception e) {
				throw new IllegalStateException(
						"Critical error! Killed node process watcher, commands process on this node will not be manual cancel.",
						e);
			}
		}, 5000, DEFAULT_MIN_WATCH_MS, DEFAULT_MAX_WATCH_MS, TimeUnit.MILLISECONDS);
	}

	/**
	 * Inspecting process destroy all.</br>
	 * Akka can be used instead of distributed message transmission. This is for
	 * a more lightweight implementation, so redis scanning is used
	 * 
	 * @param destroyLockName
	 * @throws InterruptedException
	 */
	private void doInspectForProcessesDestroy(String destroyLockName) throws InterruptedException {
		Lock lock = lockManager.getLock(keyFormat(destroyLockName));
		try {
			// Let cluster this node process destroy, nodes that do not
			// acquire lock are on ready in place.
			if (lock.tryLock()) {
				Collection<DestroableProcess> pss = repository.getProcessRegistry();
				if (log.isDebugEnabled()) {
					log.debug("Destroable processes: {}", pss);
				}
				for (DestroableProcess ps : pss) {
					String signalKey = getDestroySignalKey(ps.getProcessId());
					// Match & destroy process. See:[MARK1]
					DestroySignal signal = jedisService.getObjectAsJson(signalKey, DestroySignal.class);
					try {
						if (nonNull(signal)) {
							doDestroy(signal);
							publishDestroyMessage(signal, null);
							break;
						}
					} catch (Exception e) {
						log.error("Failed to destroy process.", e);
						publishDestroyMessage(signal, e);
					} finally {
						jedisService.del(signalKey); // Cleanup.
					}
				}

			} else if (log.isDebugEnabled()) {
				log.debug("Skip destroy processes ...");
			}
		} catch (Throwable ex) {
			log.error("Destruction error", ex);
		} finally {
			lock.unlock();
		}

	}

	/**
	 * Publish destroy result message.
	 * 
	 * @param signal
	 * @param th
	 */
	private void publishDestroyMessage(DestroySignal signal, Throwable th) {
		DestroySignalMessage ret = new DestroySignalMessage(signal);
		if (nonNull(th)) {
			ret.setState(DESTROY_FAIL);
			ret.setMessage(getRootCausesString(th));
		}
		jedisService.setObjectAsJson(getDestroyMessageKey(signal.getProcessId()), ret, 10_000);
	}

	/**
	 * Poll destroy result message.
	 * 
	 * @param signal
	 * @param th
	 */
	private DestroySignalMessage pollDestroyMessage(String processId) {
		String destroyMsgKey = getDestroyMessageKey(processId);
		try {
			return jedisService.getObjectAsJson(destroyMsgKey, DestroySignalMessage.class);
		} finally {
			jedisService.del(destroyMsgKey);
		}
	}

	/**
	 * Obtain process destruction lock name.
	 * 
	 * @return
	 */
	private String getDestroyLockName() {
		return environment.getRequiredProperty("spring.application.name") + "." + LOCK_CLI_PROCESS_DESTROY;
	}

	/**
	 * Get processId destory signal key.
	 * 
	 * @param processId
	 * @return
	 */
	private String getDestroySignalKey(String processId) {
		Assert.hasText(processId, "ProcessId must not be empty.");
		return SIGNAL_PROCESS_DESTROY + processId;
	}

	/**
	 * Get processId destory signal result key.
	 * 
	 * @param processId
	 * @return
	 */
	private String getDestroyMessageKey(String processId) {
		Assert.hasText(processId, "ProcessId must not be empty.");
		return SIGNAL_PROCESS_DESTROY_RET + processId;
	}

}