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

import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOCK_WATCH_PROCESS_DESTROY;
import static io.netty.util.internal.ThreadLocalRandom.current;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.isTrue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
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

	/** Default destruction signal expired seconds. */
	final public static int DEFAULT_SIGNAL_EXPIRED_SEC = (int) (3 * TimeUnit.MILLISECONDS.toSeconds(DEFAULT_MAX_WATCH_MS));

	/**
	 * Send signal for destroy command-line process.</br>
	 * 
	 * @param processId
	 *            Create command-lien process ID.
	 * @param timeoutMs
	 *            Destroy process timeout Ms.
	 * @throws TimeoutDestroyProcessException
	 */
	@Override
	public void destroy(String processId, long timeoutMs) {
		isTrue(timeoutMs >= DEFAULT_DESTROY_ROUND_MS,
				String.format("Destroy timeoutMs must be less than or equal to %s", DEFAULT_DESTROY_ROUND_MS));

		// Send destruction signal.
		String signalKey = getDestroySignalKey(processId);
		if (log.isInfoEnabled()) {
			log.info("Send destruction signal:{} for processId:{}", signalKey, processId);
		}
		jedisService.setObjectAsJson(signalKey, new DestroySignal(processId, timeoutMs), DEFAULT_SIGNAL_EXPIRED_SEC); // MARK1
	}

	@Override
	public void run() {
		try {
			loopWatchProcessesDestroy();
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
	private synchronized void loopWatchProcessesDestroy() throws InterruptedException {
		while (true) {
			Thread.sleep(current().nextLong(DEFAULT_MIN_WATCH_MS, DEFAULT_MAX_WATCH_MS));

			Lock lock = lockManager.getLock(LOCK_WATCH_PROCESS_DESTROY);
			try {
				// Let cluster this node process destroy, nodes that do not
				// acquire lock are on ready in place.
				if (lock.tryLock()) {
					Collection<ProcessInfo> processes = repository.getProcesses();
					if (log.isInfoEnabled()) {
						log.info("Destruction processes: {}", processes);
					}
					processes.stream().forEach(ps -> {
						String signalKey = getDestroySignalKey(ps.getProcessId());
						try {
							// Match & destroy process. See:[MARK1]
							DestroySignal signal = jedisService.getObjectAsJson(signalKey, DestroySignal.class);
							if (nonNull(signal)) {
								destroy0(signal); // Destruction process.
								return;
							}
						} catch (Exception e) {
							log.error("Failed to destruction process.", e);
						} finally {
							jedisService.del(signalKey); // Cleanup.
						}
					});
				}
			} catch (Throwable ex) {
				log.error("Destruction error", ex);
			} finally {
				lock.unlock();
			}
		}

	}

	/**
	 * Obtain processId destruction signal key.
	 * 
	 * @param processId
	 * @return
	 */
	private String getDestroySignalKey(String processId) {
		Assert.hasText(processId, "ProcessId must not be empty.");
		return "" + processId;
	}

}
