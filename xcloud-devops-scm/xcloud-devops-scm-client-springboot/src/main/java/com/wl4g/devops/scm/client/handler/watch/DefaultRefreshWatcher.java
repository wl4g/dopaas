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
package com.wl4g.devops.scm.client.handler.watch;

import com.wl4g.components.core.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.scm.model.ReportInfo;
import com.wl4g.components.core.bean.scm.model.ReportInfo.ChangedRecord;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.handler.locator.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.handler.refresh.ScmContextRefresher;
import com.wl4g.devops.scm.common.exception.ReportRetriesCountOutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wl4g.components.common.lang.ThreadUtils2.sleep;
import static com.wl4g.components.common.lang.ThreadUtils2.sleepRandom;
import static com.wl4g.components.common.web.rest.RespBase.isSuccess;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import static com.wl4g.devops.scm.client.handler.RefreshConfigHolder.*;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_BASE;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_REFRESHED_REPORT;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpMethod.POST;

/**
 * Timing refresh watcher
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class DefaultRefreshWatcher extends AbstractRefreshWatcher {

	/**
	 * This is to solve the time difference between releasing the watching
	 * interface from the server and receiving the response from the client.
	 */
	final public static float LONG_POLL_COST_RATIO = 1.15f;

	/**
	 * Watching connect lock.
	 */
	final private Lock watchLock = new ReentrantLock();

	/**
	 * Watching last connected state.
	 */
	final private AtomicBoolean lastWatchState = new AtomicBoolean(false);

	/**
	 * Last Update Time
	 */
	private long lastUpdateTime = 0;

	/**
	 * Retry failure exceed threshold fast-fail
	 */
	@Value(EXP_FASTFAIL)
	private boolean thresholdFastfail;

	/**
	 * Long polling rest template
	 */
	private RestTemplate longPollingTemplate;

	public DefaultRefreshWatcher(ScmClientProperties config, ScmContextRefresher refresher, ScmPropertySourceLocator locator) {
		super(config, refresher, locator);
	}

	@Override
	protected void preStartupProperties() {
		this.longPollingTemplate = locator.createRestTemplate((long) (config.getLongPollTimeout() * LONG_POLL_COST_RATIO));
	}

	/**
	 * [MARK1] Scenes to refresh:
	 * <p>
	 * 1. The client did not connect successfully when it started, but after
	 * multiple reconnection failures, it finally connected successfully.
	 * </p>
	 * <p>
	 * 2. When the client is started, the connection is successful, and there is
	 * an interruption in the middle of the operation. When the connection
	 * status changes from failure to success, it will refresh.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (isActive()) { // Loop long-polling watching
			try {
				if (watchLock.tryLock()) {
					doHandleWatchLongPolling();

					// [MARK1] Re-refresh configuration.
					if (!lastWatchState
							.get() /* && isNull(getReleaseMeta(false)) */) {
						// Records changed keys.
						addChanged(refresher.refresh());
					}
					lastWatchState.set(true);
				} else {
					log.warn("Skip the watch request in long polling!");
				}
			} catch (Throwable th) {
				lastWatchState.set(false);
				log.error("Unable to watch poll", () -> getRootCauseMessage(th));
				log.debug("Unable to watch poll", th);
				sleepRandom(config.getLongPollDelay(), config.getLongPollMaxDelay());
			} finally {
				watchLock.unlock();
			}
		}

	}

	/**
	 * Do handle long-polling watching request.
	 *
	 * @throws Exception
	 */
	private void doHandleWatchLongPolling() throws Exception {
		log.debug("Synchronizing refresh config ... ");
		checkRefreshProtectInterval();

		String url = getWatchingUrl(false);
		ResponseEntity<ConfigMeta> resp = longPollingTemplate.getForEntity(url, ConfigMeta.class);
		log.debug("Watch result <= {}", resp);

		if (!isNull(resp)) {
			switch (resp.getStatusCode()) {
			case OK:
				// Release changed info.
				setReleaseMeta(resp.getBody());
				// Records changed property names.
				addChanged(refresher.refresh());
				lastUpdateTime = currentTimeMillis();
				break;
			case CHECKPOINT:
				// Report refresh changed
				backendReport();
				break;
			case NOT_MODIFIED: // Next long-polling
				log.trace("Unchanged and continue next long-polling ... ");
				break;
			default:
				throw new IllegalStateException(format("Unsupporteds scm protocal status: '%s'", resp.getStatusCodeValue()));
			}
		}

	}

	/**
	 * Check refresh portection internal.
	 */
	private void checkRefreshProtectInterval() {
		long now = currentTimeMillis();
		long intervalMs = now - lastUpdateTime;
		if (intervalMs < config.getRefreshProtectIntervalMs()) {
			log.warn("Refresh too fast? Watch long polling waiting... , lastUpdateTime: {}, now: {}, intervalMs: {}",
					lastUpdateTime, now, intervalMs);
			sleep(config.getRefreshProtectIntervalMs());
		}
	}

	/**
	 * Back-end report changed records
	 */
	@Retryable(value = Throwable.class, maxAttemptsExpression = EXP_MAXATTEMPTS, backoff = @Backoff(delayExpression = EXP_DELAY, maxDelayExpression = EXP_MAXDELAY, multiplierExpression = EXP_MULTIP))
	private void backendReport() {
		String url = config.getBaseUri() + URI_S_BASE + "/" + URI_S_REFRESHED_REPORT;

		Collection<ChangedRecord> records = getChangedQueues();
		// Requests
		RespBase<?> resp = locator.getRestTemplate()
				.exchange(url, POST, new HttpEntity<>(new ReportInfo(records)), new ParameterizedTypeReference<RespBase<?>>() {
				}).getBody();

		// Successful reset
		if (isSuccess(resp)) {
			pollChangedAll();
		} else {
			throw new ReportRetriesCountOutException(String.format("Backend report failure! records for %s", records.size()));
		}
	}

	/**
	 * Report retries exceed count exception.
	 *
	 * @param e
	 */
	@Recover
	public void recoverReportRetriesCountOutException(ReportRetriesCountOutException e) {
		if (thresholdFastfail) {
			if (log.isWarnEnabled()) {
				log.warn("Refresh report retries exceed threshold, discarded refresh changed record!");
			}
			pollChangedAll();
		} else if (log.isWarnEnabled()) {
			log.warn("Refresh report retries exceed threshold!");
		}
	}

}