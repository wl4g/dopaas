/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import static org.apache.commons.lang3.RandomUtils.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import static org.springframework.http.HttpMethod.*;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;
import com.wl4g.devops.common.bean.scm.model.ReportInfo.ChangedRecord;
import com.wl4g.devops.common.exception.scm.ReportRetriesCountOutException;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.configure.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;
import static com.wl4g.devops.scm.client.configure.RefreshConfigHolder.*;
import static com.wl4g.devops.common.web.RespBase.*;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_REPORT_POST;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Timing refresh watcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class TimingRefreshWatcher extends AbstractRefreshWatcher {

	final public static String EXP_MAXATTEMPTS = "${spring.cloud.devops.scm.client.retry.max-attempts:5}";
	final public static String EXP_DELAY = "${spring.cloud.devops.scm.client.retry.delay:1000}";
	final public static String EXP_MAXDELAY = "${spring.cloud.devops.scm.client.retry.max-delay:5000}";
	final public static String EXP_MULT = "${spring.cloud.devops.scm.client.retry.multiplier:1.1}";
	final public static String EXP_FASTFAIL = "${spring.cloud.devops.scm.client.retry.threshold-fastfail:true}";

	/** Retry failure exceed threshold fast-fail */
	@Value(EXP_FASTFAIL)
	private boolean thresholdFastfail;

	/** Watching completion state. */
	final private AtomicBoolean watchState = new AtomicBoolean(false);

	public TimingRefreshWatcher(ScmClientProperties config, ScmContextRefresher refresher, ScmPropertySourceLocator locator) {
		super(config, refresher, locator);
	}

	@Override
	public void run() {
		// Loop long-polling watching...
		while (true) {
			try {
				createWatchLongPolling();
			} catch (Exception e) {
				String errtip = "Watching long polling error! causes by: ";
				if (log.isDebugEnabled()) {
					log.error(errtip, getStackTrace(e));
				} else {
					log.error(errtip, e);
				}

				synchronized (this) {
					try {
						wait(nextLong(1000L, 10_000L));
					} catch (InterruptedException e1) {
						log.error("", e);
					}
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
			ResponseEntity<ReleaseMeta> resp = locator.getRestTemplate().getForEntity(url, ReleaseMeta.class);
			if (log.isDebugEnabled()) {
				log.debug("Watch result <= {}", resp);
			}

			// Update watching state
			if (resp != null) {
				switch (resp.getStatusCode()) {
				case OK:
					// Poll release changed
					setReleaseMeta(resp.getBody());
					// Records changed propertys
					addChanged(refresher.refresh());
					break;
				case CHECKPOINT:
					// Report refresh changed
					backendReport();
					break;
				case NOT_MODIFIED: // Continue
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
	@Retryable(value = Throwable.class, maxAttemptsExpression = EXP_MAXATTEMPTS, backoff = @Backoff(delayExpression = EXP_DELAY, maxDelayExpression = EXP_MAXDELAY, multiplierExpression = EXP_MULT))
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
	 * Report retries execed count exception.
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