/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.pipeline;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.common.task.RunnerProperties;
import com.wl4g.component.support.redis.locks.JedisLockManager;
import com.wl4g.component.support.redis.jedis.JedisService;
import com.wl4g.component.support.task.ApplicationTaskRunner;
import com.wl4g.dopaas.uci.config.CiProperties;
import com.wl4g.dopaas.uci.data.PipelineHistoryDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.component.common.lang.Assert2.isTrue;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.support.redis.jedis.JedisClient.RedisProtoUtil.keyFormat;
import static com.wl4g.dopaas.common.constant.CiConstants.KEY_FINALIZER_INTERVALMS;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Global timeout jobs eviction finalizer.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-15
 * @since
 */
public class TimeoutJobsEvictor extends ApplicationTaskRunner<RunnerProperties> {

	final protected SmartLogger log = getLogger(getClass());

	@Autowired
	protected ConfigurableEnvironment environment;
	@Autowired
	protected CiProperties config;
	@Autowired
	protected JedisLockManager lockManager;
	@Autowired
	protected JedisService jedisService;
	@Autowired
	protected PipelineHistoryDao pipelineHistoryDao;

	/** Timing jobs future {@link ScheduledFuture} */
	protected ScheduledFuture<?> future;

	public TimeoutJobsEvictor() {
		super(new RunnerProperties(false, 1));
	}

	@Override
	public void run() {
		this.future = getWorker().scheduleWithRandomDelay(() -> {
			try {
				watchTimeoutJobsDestroy(getDistributedLockName());
			} catch (Exception e) {
				throw new IllegalStateException(
						"Critical error!!! Global timeout cleanup watcher interrupted, timeout jobs will not be cleanup.", e);
			}
		}, 5000, DEFAULT_MIN_WATCH_MS, getJobEvictionInternal(), TimeUnit.MILLISECONDS);
	}

	/**
	 * Watching timeout jobs, updating their status to failure.
	 * 
	 * @param cleanerLockName
	 * @throws InterruptedException
	 */
	private final void watchTimeoutJobsDestroy(String cleanerLockName) throws InterruptedException {
		Lock lock = lockManager.getLock(keyFormat(cleanerLockName));
		try {
			// Cleanup timeout jobs on this node, nodes that do not
			// acquire lock are on ready in place.
			if (lock.tryLock()) {
				long begin = System.currentTimeMillis();
				int count = pipelineHistoryDao.updateStatus(config.getBuild().getJobTimeoutSec());
				if (count > 0) {
					log.info("Updated pipeline timeout jobs, with jobTimeoutSec:{}, count:{}, cost: {}ms",
							config.getBuild().getJobTimeoutSec(), count, (currentTimeMillis() - begin));
				}
			} else {
				log.debug("Skip cleanup jobs of jobTimeoutSec:{}", config.getBuild().getJobTimeoutSec());
			}
		} catch (Throwable ex) {
			log.error("Failed to timeout jobs cleanup", ex);
		} finally {
			lock.unlock();
		}

	}

	/**
	 * Refresh global distributed {@link TimeoutJobsEvictor} watching
	 * intervalMs.
	 * 
	 * @param globalJobCleanMaxIntervalMs
	 * @return
	 */
	public Long refreshEvictionIntervalMs(Long globalJobCleanMaxIntervalMs) {
		// Distributed global intervalMs.
		jedisService.setObjectT(KEY_FINALIZER_INTERVALMS, globalJobCleanMaxIntervalMs, -1);
		log.info("Refreshed global timeoutCleanupFinalizer of intervalMs:<%s>", globalJobCleanMaxIntervalMs);

		// Cancel older task.
		if (!isNull(future) && !future.isCancelled() && !future.isDone()) {
			isTrue(future.cancel(true), "Failed to cancel older task of %s", future);
		}

		run(); // Restart

		// Get available global intervalMs.
		return getJobEvictionInternal();
	}

	/**
	 * Get {@link TimeoutJobsEvictor} watcher locker name.
	 * 
	 * @return
	 */
	private String getDistributedLockName() {
		return environment.getRequiredProperty("spring.application.name") + "." + TimeoutJobsEvictor.class.getSimpleName();
	}

	/**
	 * Get global distributed {@link TimeoutJobsEvictor} watching intervalMs.
	 * 
	 * @return
	 */
	private Long getJobEvictionInternal() {
		Long maxInternalMs = jedisService.getObjectT(KEY_FINALIZER_INTERVALMS, Long.class);
		return nonNull(maxInternalMs) ? maxInternalMs : config.getBuild().getJobCleanMaxIntervalMs();
	}

	public static final long DEFAULT_MIN_WATCH_MS = 2_000L;

}