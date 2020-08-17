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
package com.wl4g.devops.scm.client.refresh;

import static com.wl4g.components.common.lang.TypeConverts.safeLongToInt;

import com.wl4g.components.common.reflect.ParameterizedTypeReference;
import com.wl4g.components.common.remoting.ClientHttpRequestInterceptor;
import com.wl4g.components.common.remoting.ClientHttpResponse;
import com.wl4g.components.common.remoting.HttpEntity;
import com.wl4g.components.common.remoting.HttpRequest;
import com.wl4g.components.common.remoting.HttpRequestEntity;
import com.wl4g.components.common.remoting.HttpResponseEntity;
import com.wl4g.components.common.remoting.Netty4ClientHttpRequestFactory;
import com.wl4g.components.common.remoting.RestClient;
import com.wl4g.components.common.remoting.exception.ClientHttpRequestExecution;
import com.wl4g.components.common.remoting.standard.HttpHeaders;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ScmEventPublisher;
import com.wl4g.devops.scm.common.command.WatchCommand;
import com.wl4g.devops.scm.common.command.WatchCommandResult;
import com.wl4g.devops.scm.common.exception.ReportRetriesCountOutException;
import com.wl4g.devops.scm.common.exception.ScmException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.state;
import static com.wl4g.components.common.lang.ThreadUtils2.sleep;
import static com.wl4g.components.common.lang.ThreadUtils2.sleepRandom;
import static com.wl4g.components.common.remoting.standard.HttpMediaType.APPLICATION_JSON;
import static com.wl4g.components.common.remoting.standard.HttpStatus.CHECKPOINT;
import static com.wl4g.components.common.remoting.standard.HttpStatus.NOT_MODIFIED;
import static com.wl4g.components.common.remoting.standard.HttpStatus.OK;
import static com.wl4g.components.common.web.rest.RespBase.isSuccess;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_BASE;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_REFRESHED_REPORT;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

/**
 * Timing refresh watcher
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class HttpLongPollRefreshWatcher extends GenericRefreshWatcher {

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
	 * Retry failure exceed threshold fast-fail
	 */
	private boolean thresholdFastfail;

	/**
	 * Long polling rest client
	 */
	private RestClient longPollingClient;

	public HttpLongPollRefreshWatcher(ScmClientProperties config, ScmEventPublisher publisher) {
		super(config, publisher);
	}

	@Override
	protected void preStartupProperties() {
		this.longPollingClient = createRestClient();
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
					watchLongPolling();

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
	 * Attach headers, e.g. authentication token information
	 * 
	 * @param headers
	 */
	protected void attachHeaders(HttpHeaders headers) {
		headers.setAccept(singletonList(APPLICATION_JSON));
	}

	/**
	 * New Create RestClient
	 * 
	 * @param readTimeout
	 * @return
	 */
	protected RestClient createRestClient() {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(config.getConnectTimeout());
		factory.setReadTimeout(safeLongToInt(config.getWatchReadTimeout()));
		factory.setMaxResponseSize(config.getMaxResponseSize());
		RestClient client = new RestClient(factory);

		Map<String, String> headers = new HashMap<>(config.getHeaders());
		if (headers.containsKey(AUTHORIZATION)) {
			// To avoid redundant addition of header
			headers.remove(AUTHORIZATION);
		}
		if (!headers.isEmpty()) {
			client.setInterceptors(asList(new GenericRequestHeaderInterceptor(headers)));
		}

		return client;
	}

	/**
	 * Handle long-polling watching request.
	 *
	 * @throws Exception
	 */
	protected void watchLongPolling() throws Exception {
		log.debug("Synchronizing refresh config ... ");

		// Delay freq protection limit
		beforeDelayRefreshProtectLimit();

		// Create watch command
		WatchCommand watch = createWatchCommand();

		HttpHeaders headers = new HttpHeaders();
		attachHeaders(headers); // Extra headers
		log.debug("Fetch headers of : {}", headers);

		HttpEntity<WatchCommand> entity = new HttpEntity<>(watch, headers);
		HttpResponseEntity<RespBase<WatchCommandResult>> resp = longPollingClient.exchange(config.getWatchUri(), POST, entity,
				new ParameterizedTypeReference<RespBase<WatchCommandResult>>() {
				});
		log.debug("Watch resp: {}", resp);

		if (!isNull(resp) && !isNull(resp.getBody())) {
			handleWatchResult(resp.getStatusCodeValue(), resp.getBody().getData());
		}

	}

	@Override
	@Retryable(value = Throwable.class, maxAttemptsExpression = EXP_MAXATTEMPTS, backoff = @Backoff(delayExpression = EXP_DELAY, maxDelayExpression = EXP_MAXDELAY, multiplierExpression = EXP_MULTIP))
	protected void backendReport() {
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

	/**
	 * Adds the provided headers to the request.
	 */
	class GenericRequestHeaderInterceptor implements ClientHttpRequestInterceptor {

		final private Map<String, String> headers;

		public GenericRequestHeaderInterceptor(Map<String, String> headers) {
			this.headers = headers;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			for (Entry<String, String> h : headers.entrySet()) {
				request.getHeaders().add(h.getKey(), h.getValue());
			}
			return execution.execute(request, body);
		}

	}

}