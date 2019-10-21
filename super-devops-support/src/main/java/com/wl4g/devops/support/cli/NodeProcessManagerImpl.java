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

import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static io.netty.util.internal.ThreadLocalRandom.current;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.isTrue;

import java.util.concurrent.locks.Lock;

import org.springframework.util.Assert;

import com.wl4g.devops.common.exception.support.TimeoutDestroyProcessException;
import com.wl4g.devops.support.cli.destroy.DestroySignal;
import com.wl4g.devops.support.cli.repository.ProcessRepository.ProcessInfo;

/**
 * Default cluster node command-line process management implements.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public class NodeProcessManagerImpl extends GenericProcessManager {

	final public static long DEFAULT_DESTROY_ROUND_MS = 300L;

	/**
	 * Destroy command-line process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param processId
	 *            Create command-lien process ID.
	 * @param timeoutMs
	 *            Destroy process timeout Ms.
	 * @throws TimeoutDestroyProcessException
	 */
	@Override
	public void destroy(String processId, long timeoutMs) throws TimeoutDestroyProcessException {
		isTrue(timeoutMs >= DEFAULT_DESTROY_ROUND_MS,
				String.format("Destroy timeoutMs must be less than or equal to %s", DEFAULT_DESTROY_ROUND_MS));

		// Send destruction signal.
		String signalKey = getDestroySignalKey(processId);
		jedisService.setObjectAsJson(signalKey, new DestroySignal(processId, timeoutMs), 0); // MARK1

		// Obtain destroy result signal.
		DestroySignal result = null;
		long i = 0, c = (timeoutMs / DEFAULT_DESTROY_ROUND_MS);
		do {
			result = jedisService.getObjectAsJson(getDestroySignalResultKey(processId), DestroySignal.class);
			try {
				Thread.sleep(current().nextInt(DEFAULT_MIN_WATCH_MS, DEFAULT_MAX_WATCH_MS));
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			++i;
			if (i >= c) { // Timeout?
				throw new TimeoutDestroyProcessException(String.format("Destruction waiting timeout", processId));
			}
		} while (isNull(result) || (nonNull(result) && !result.getDestroyed()));

	}

	@Override
	public void run() {
		try {
			runningWatchProcessesDestroy();
		} catch (InterruptedException e) {
			log.error("Grave warning!!! Killed node process watcher, commands process on this node will not be manual cancel.",
					e);
		}
	}

	/**
	 * Watching process destroy all.
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("deprecation")
	private synchronized void runningWatchProcessesDestroy() throws InterruptedException {
		while (true) {
			Thread.sleep(current().nextInt(DEFAULT_MIN_WATCH_MS, DEFAULT_MAX_WATCH_MS));

			Lock lock = lockManager.getLock("");
			try {
				// Let cluster this node process destroy, nodes that do not
				// acquire lock are on ready in place.
				if (lock.tryLock()) {
					for (ProcessInfo ps : safeList(jedisService.getObjectList("", ProcessInfo.class))) {
						String signalKey = getDestroySignalKey(ps.getProcessId());
						String signalRetKey = getDestroySignalResultKey(ps.getProcessId());
						try {
							// Matcher need destroy process. See:[MARK1]
							DestroySignal signal = jedisService.getObjectAsJson(signalKey, DestroySignal.class);
							if (nonNull(signal)) {
								// Destruction.
								destroy0(signal);
								// Response destroyed.
								jedisService.setObjectAsJson(signalRetKey, new DestroySignal(ps.getProcessId(), true), 1);
								// Cleanup signal.
								jedisService.del(signalKey);
								break;
							}
						} catch (TimeoutDestroyProcessException e) {
							// Response destroy failure.
							jedisService.setObjectAsJson(signalRetKey, new DestroySignal(ps.getProcessId(), false), 1);
						}
					}
				}
			} catch (Throwable ex) {
				log.warn("", ex);
			} finally {
				lock.unlock();
			}
		}

	}

	private String getDestroySignalKey(String processId) {
		Assert.hasText(processId, "ProcessId must not be empty.");
		return "" + processId;
	}

	private String getDestroySignalResultKey(String processId) {
		Assert.hasText(processId, "ProcessId must not be empty.");
		return "" + processId;
	}

}
