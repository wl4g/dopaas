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
package com.wl4g.devops.scm.client.refresh.internal;

import static com.github.rholder.retry.StopStrategies.neverStop;
import static com.github.rholder.retry.WaitStrategies.randomWait;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Collection;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotNull;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.CheckpointConfigEvent;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.event.RefreshConfigEvent;
import com.wl4g.devops.scm.client.refresh.GenericRefreshWatcher;
import com.wl4g.devops.scm.client.refresh.RefreshWatcher;
import com.wl4g.devops.scm.common.command.ReportChangedRequest.ChangedRecord;

/**
 * {@link ReportingConfigListener}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-21
 * @since
 */
public class ReportingConfigListener implements ConfigEventListener {

	final protected SmartLogger log = getLogger(getClass());

	/** {@link RefreshWatcher} */
	protected final GenericRefreshWatcher watcher;

	/** SCM client reporting handler */
	protected final ConfigReportingHandler handler;

	public ReportingConfigListener(@NotNull GenericRefreshWatcher watcher) {
		notNullOf(watcher, "watcher");
		this.watcher = watcher;
		this.handler = new ConfigReportingHandler();
	}

	@Override
	public void onRefresh(RefreshConfigEvent event) {
		// Ignore
	}

	@Override
	public void onCheckpoint(CheckpointConfigEvent event) {
		handleRetrievableReporting();
	}

	/**
	 * DO retrievable execution reporting
	 */
	protected void handleRetrievableReporting() {
		if (!watcher.getRepository().getChangedAll().isEmpty()) {
			log.info("SCM retrievable reporting ...");
			try {
				newReportingRetryer().call(handler);
			} catch (Exception e) {
				log.error("Failed to SCM reporting.", e);
			}
		}
	}

	/**
	 * New create reporting {@link Retryer}
	 * 
	 * @return
	 */
	protected Retryer<Boolean> newReportingRetryer() {
		ScmClientProperties<?> config = watcher.getScmConfig();

		return RetryerBuilder.<Boolean> newBuilder().retryIfExceptionOfType(Throwable.class)// Exception-retry-source
				.retryIfResult(res -> (nonNull(res) && !res)) // Retrial-condition
				.withWaitStrategy(randomWait(config.getRetryReportingMinDelay(), MILLISECONDS, config.getRetryReportingMaxDelay(),
						MILLISECONDS)) // Waiting-interval
				.withStopStrategy(neverStop()) // stop-retries
				.withRetryListener(new RetryListener() {
					@Override
					public <V> void onRetry(Attempt<V> attempt) {
						// Discard/cleanup after maximum attempt.(if necessary)
						long threshold = config.getRetryReportingFastFailThreshold();
						if (threshold > 0 && attempt.getAttemptNumber() > threshold) {
							log.warn("Reporting retries max threshold({}), discarded refresh changed record!!!",
									attempt.getAttemptNumber());
							watcher.getRepository().pollChangedAll();
						}
						// Publishing reporting
						watcher.getPublisher().publishReportingEvent(attempt);
					}
				}).build();
	}

	/**
	 * {@link ConfigReportingHandler}
	 *
	 * @since
	 */
	class ConfigReportingHandler implements Callable<Boolean> {
		@Override
		public Boolean call() throws Exception {
			Collection<ChangedRecord> records = watcher.getRepository().getChangedAll();
			boolean result = watcher.doReporting(records);
			if (result) { // Success and cleanup
				records = watcher.getRepository().pollChangedAll();
				log.debug("Reporting success and cleaned for records: {}", records);
			}
			return result;
		}
	}

}
