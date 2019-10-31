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

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_REPORT_POST;
import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.common.web.RespBase.isSuccess;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import static com.wl4g.devops.scm.client.configure.RefreshConfigHolder.*;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.springframework.http.HttpMethod.POST;

/**
 * Timing refresh watcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class DefaultRefreshWatcher extends AbstractRefreshWatcher {

	/** Watching completion state. */
	final private AtomicBoolean watchState = new AtomicBoolean(false);

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

	@Override
	public void run() {
		// Loop long-polling watcher
		while (true) {
			try {
				createWatchLongPolling();
			} catch (Throwable th) {
				String errtip = "Unable to watch error, causes by: {}";
				if (log.isDebugEnabled()) {
					log.error(errtip, getStackTrace(th));
				} else {
					log.warn(errtip, getRootCausesString(th));
				}
				try {
					Thread.sleep(nextLong(config.getLongPollDelay(), config.getLongPollMaxDelay()));
				} catch (InterruptedException e1) {
					log.error("", th);
				}
			} finally {
				watchState.compareAndSet(true, false);
			}
		}
	}

	/**
	 * Create long-polling watching request.
	 * 
	 * @throws Exception
	 */
	private void createWatchLongPolling() throws Exception {
		if (watchState.compareAndSet(false, true)) {
			if (log.isDebugEnabled()) {
				log.debug("Synchronizing refresh config ... ");
			}

			String url = getWatchingUrl(false);

			ResponseEntity<ReleaseMeta> resp = longPollingTemplate.getForEntity(url, ReleaseMeta.class);
			if (log.isDebugEnabled()) {
				log.debug("Watch result <= {}", resp);
			}

			// Update watching state
			if (resp != null) {
				switch (resp.getStatusCode()) {
				case OK:
					// Poll release changed
					setReleaseMeta(resp.getBody());
					// Records changed property names
					addChanged(refresher.refresh());
					break;
				case CHECKPOINT:
					// Report refresh changed
					backendReport();
					break;
				case NOT_MODIFIED: // Next long-polling
					break;
				default:
					throw new IllegalStateException(
							String.format("Unsupport scm protocal status for: '%s'", resp.getStatusCodeValue()));
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