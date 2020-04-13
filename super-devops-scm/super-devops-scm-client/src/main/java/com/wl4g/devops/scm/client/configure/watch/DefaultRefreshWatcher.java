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
package com.wl4g.devops.scm.client.configure.watch;

import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;
import com.wl4g.devops.common.bean.scm.model.ReportInfo.ChangedRecord;
import com.wl4g.devops.common.exception.scm.ReportRetriesCountOutException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.configure.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;
import static com.wl4g.devops.tool.common.lang.ThreadUtils2.*;
import static java.lang.String.format;
import static java.util.Objects.isNull;

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

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_REPORT_POST;
import static com.wl4g.devops.common.web.RespBase.isSuccess;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import static com.wl4g.devops.scm.client.configure.RefreshConfigHolder.*;
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

	/** Watching connect lock. */
	final private Lock watchLock = new ReentrantLock();

	/** Watching last connected state. */
	final private AtomicBoolean lastWatchState = new AtomicBoolean(false);

	/** Retry failure exceed threshold fast-fail */
	@Value(EXP_FASTFAIL)
	private boolean thresholdFastfail;

	/** Long polling rest template */
	private RestTemplate longPollingTemplate;

	public DefaultRefreshWatcher(ScmClientProperties config, ScmContextRefresher refresher, ScmPropertySourceLocator locator) {
		super(config, refresher, locator);
	}

	@Override
	protected void preStartupProperties() {
		this.longPollingTemplate = locator.createRestTemplate((long) (config.getLongPollTimeout() * 1.15));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) { // Loop long-polling watching
			try {
				createWatchLongPolling();

				// Optimization: refresh the configuration only when the
				// connection status changes from failure to success.
				if (!lastWatchState.get()) {
					// Records changed property names.
					addChanged(refresher.refresh());
				}
				lastWatchState.set(true);

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
	 * Create long-polling watching request.
	 * 
	 * @throws Exception
	 */
	private void createWatchLongPolling() throws Exception {
		if (watchLock.tryLock()) {
			log.debug("Synchronizing refresh config ... ");

			String url = getWatchingUrl(false);
			ResponseEntity<ReleaseMeta> resp = longPollingTemplate.getForEntity(url, ReleaseMeta.class);
			log.debug("Watch result <= {}", resp);

			if (!isNull(resp)) {
				switch (resp.getStatusCode()) {
				case OK:
					// Release changed info.
					setReleaseMeta(resp.getBody());
					// Records changed property names.
					addChanged(refresher.refresh());
					break;
				case CHECKPOINT:
					// Report refresh changed
					backendReport();
					break;
				case NOT_MODIFIED: // Next long-polling
					break;
				default:
					throw new IllegalStateException(format("Unsupport scm protocal status: '%s'", resp.getStatusCodeValue()));
				}
			}
		} else {
			log.warn("Skip the watch request in long polling!");
		}
	}

	/**
	 * Back-end report changed records
	 */
	@Retryable(value = Throwable.class, maxAttemptsExpression = EXP_MAXATTEMPTS, backoff = @Backoff(delayExpression = EXP_DELAY, maxDelayExpression = EXP_MAXDELAY, multiplierExpression = EXP_MULTIP))
	private void backendReport() {
		String url = config.getBaseUri() + URI_S_BASE + "/" + URI_S_REPORT_POST;

		Collection<ChangedRecord> records = getChangedQueues();
		// Requests
		RespBase<?> resp = locator.getRestTemplate()
				.exchange(url, POST, new HttpEntity<>(new ReportInfo(records)), new ParameterizedTypeReference<RespBase<?>>() {
				}).getBody();

		// Successful reset
		if (isSuccess(resp)) {
			changedReset();
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
			changedReset();
		} else if (log.isWarnEnabled()) {
			log.warn("Refresh report retries exceed threshold!");
		}
	}

}