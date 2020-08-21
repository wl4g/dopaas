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
package com.wl4g.devops.scm.client.watch;

import com.wl4g.components.common.reflect.ParameterizedTypeReference;
import com.wl4g.components.common.remoting.ClientHttpRequestInterceptor;
import com.wl4g.components.common.remoting.ClientHttpResponse;
import com.wl4g.components.common.remoting.HttpEntity;
import com.wl4g.components.common.remoting.HttpRequest;
import com.wl4g.components.common.remoting.HttpResponseEntity;
import com.wl4g.components.common.remoting.Netty4ClientHttpRequestFactory;
import com.wl4g.components.common.remoting.RestClient;
import com.wl4g.components.common.remoting.exception.ClientHttpRequestExecution;
import com.wl4g.components.common.remoting.standard.HttpHeaders;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.repository.RefreshConfigRepository;
import com.wl4g.devops.scm.common.command.ReportChangedRequest;
import com.wl4g.devops.scm.common.command.ReportChangedRequest.ChangedRecord;
import com.wl4g.devops.scm.common.command.FetchConfigRequest;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wl4g.components.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.components.common.remoting.standard.HttpMediaType.APPLICATION_JSON;
import static com.wl4g.components.common.web.rest.RespBase.isSuccess;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_BASE;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_REFRESHED_REPORT;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

/**
 * HTTP long polling refresh watcher
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class LongPollingRefreshWatcher extends GenericRefreshWatcher {

	/**
	 * This is to solve the time difference between releasing the watching
	 * interface from the server and receiving the response from the client.
	 */
	final public static float LONG_POLL_COST_RATIO = 1.15f;

	/**
	 * Watching connect lock.
	 */
	final private Lock watchingLock = new ReentrantLock();

	/**
	 * Long polling rest client
	 */
	private RestClient http;

	public LongPollingRefreshWatcher(ScmClientProperties<?> config, RefreshConfigRepository repository,
			ConfigEventListener... listeners) {
		super(new RunnerProperties(true, 1), config, repository, listeners);
		this.http = initRestClient(config);
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
		getWorker().scheduleAtRandomRate(() -> { // Loop long-polling watching
			try {
				if (watchingLock.tryLock()) {
					doHandleWatching();
				} else {
					log.warn("Skip the watch request in long polling!");
				}
			} catch (Throwable th) {
				log.error("Unable to watch poll", () -> getRootCauseMessage(th));
				log.debug("Unable to watch poll", th);
			} finally {
				watchingLock.unlock();
			}
		}, 3000L, config.getLongPollingMinDelay(), config.getLongPollingMaxDelay(), MILLISECONDS);
	}

	@Override
	public boolean doReporting(Collection<ChangedRecord> records) {
		String url = config.getBaseUri().concat(URI_S_BASE).concat("/").concat(URI_S_REFRESHED_REPORT);
		RespBase<?> resp = http.exchange(url, POST, new HttpEntity<>(new ReportChangedRequest(records)),
				new ParameterizedTypeReference<RespBase<?>>() {
				}).getBody();
		return isSuccess(resp);
	}

	/**
	 * Execution long-polling watching request.
	 *
	 * @throws Exception
	 */
	protected void doHandleWatching() {
		log.debug("Watching refresh config ... ");

		// Delay freq protection limit
		beforeSafeRefreshProtectDelaying();

		// Gets watch command
		FetchConfigRequest request = createFetchRequest();

		HttpHeaders headers = new HttpHeaders();
		attachHeaders(headers); // Extra headers
		log.debug("Watching request headers : {}", headers);

		HttpEntity<FetchConfigRequest> entity = new HttpEntity<>(request, headers);
		HttpResponseEntity<RespBase<ReleaseConfigInfo>> resp = http.exchange(config.getWatchUri(), POST, entity,
				new ParameterizedTypeReference<RespBase<ReleaseConfigInfo>>() {
				});
		log.debug("Watching fetch received: {}", resp);

		if (!isNull(resp) && !isNull(resp.getBody())) {
			handleWatchResult(resp.getStatusCodeValue(), resp.getBody().getData());
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
	 * Init create {@link RestClient}
	 * 
	 * @param config
	 * @return
	 */
	private RestClient initRestClient(ScmClientProperties<?> config) {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(config.getConnectTimeout());
		factory.setReadTimeout(config.getLongPollTimeout());
		factory.setMaxResponseSize(safeLongToInt(config.getMaxResponseSize()));
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