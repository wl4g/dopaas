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
package com.wl4g.devops.scm.client.locator;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.state;
import static com.wl4g.components.common.remoting.standard.HttpMediaType.APPLICATION_JSON;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.AUTHORIZATION;
import static com.wl4g.devops.scm.client.locator.RefreshConfigHolder.getReleaseMeta;
import static com.wl4g.devops.scm.client.locator.RefreshConfigHolder.pollReleaseMeta;
import static com.wl4g.devops.scm.common.config.SCMConstants.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static io.netty.handler.codec.http.HttpMethod.GET;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wl4g.components.common.reflect.ParameterizedTypeReference;
import com.wl4g.components.common.remoting.ClientHttpRequestInterceptor;
import com.wl4g.components.common.remoting.ClientHttpResponse;
import com.wl4g.components.common.remoting.HttpEntity;
import com.wl4g.components.common.remoting.HttpRequest;
import com.wl4g.components.common.remoting.Netty4ClientHttpRequestFactory;
import com.wl4g.components.common.remoting.RestClient;
import com.wl4g.components.common.remoting.exception.ClientHttpRequestExecution;
import com.wl4g.components.common.remoting.standard.HttpHeaders;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.utils.InstanceHolder;
import com.wl4g.devops.scm.common.exception.ScmException;
import com.wl4g.devops.scm.common.model.GenericInfo.ReleaseMeta;

import com.wl4g.devops.scm.common.model.GetRelease;
import com.wl4g.devops.scm.common.model.ReleaseMessage;

/**
 * {@link HttpFetchConfigSourceLocator}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class HttpFetchConfigSourceLocator extends AbstractConfigSourceLocator {

	/** {@link RestClient} */
	protected final RestClient restClient;

	/** SCM client instance holder */
	protected final InstanceHolder holder;

	public HttpFetchConfigSourceLocator(ScmClientProperties config) {
		super(config);
		this.restClient = createRestClient(config.getFetchReadTimeout());
		this.holder = new InstanceHolder(config);
	}

	@Override
	public ReleaseMessage locate() {
		// TODO

		return null;
	}

	/**
	 * Fetch release configuration from SCM server.
	 * 
	 * @return
	 */
	public ReleaseMessage fetchRemoteReleaseConfig() {
		try {
			// Fetch release URL.
			String uri = config.getBaseUri() + URI_S_BASE + "/" + URI_S_SOURCE_GET;
			// Create release get
			ReleaseMeta meta = getReleaseMeta(false);
			GetRelease get = new GetRelease(holder.getAppName(), config.getNamespaces(), meta, holder.getInstance());

			// To parameters
			String kvs = new BeanMapConvert(get).toUriParmaters();
			String url = uri + "?" + kvs;
			log.debug("Fetch release config url: {}", url);

			HttpHeaders headers = new HttpHeaders();
			attachHeaders(headers);
			final HttpEntity<Void> entity = new HttpEntity<>(null, headers);
			// Attach headers
			log.debug("Adding header for: {}", headers);

			RespBase<ReleaseMessage> resp = restClient
					.exchange(url, GET, entity, new ParameterizedTypeReference<RespBase<ReleaseMessage>>() {
					}).getBody();
			if (!RespBase.isSuccess(resp)) {
				throw new ScmException(format("Locate remote config error! %s, %s", url, resp.getMessage()));
			}

			// Extract release
			ReleaseMessage release = resp.getData();
			notNull(release, "Release message is required, it must not be null");
			release.validation(true, true);

			// Print configuration sources
			printfConfigSources(release);

			log.debug("Fetch release config <= {}", release);
			return release;
		} finally {
			pollReleaseMeta();
		}
	}

	/**
	 * Create RestClient
	 * 
	 * @param readTimeout
	 * @return
	 */
	protected RestClient createRestClient(long readTimeout) {
		state(readTimeout > 0, String.format("Invalid value for read timeout for %s", readTimeout));

		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(config.getConnectTimeout());
		factory.setReadTimeout((int) readTimeout);
		factory.setMaxResponseSize(config.getMaxResponseSize());
		RestClient restClient = new RestClient(factory);

		Map<String, String> headers = new HashMap<>(config.getHeaders());
		if (headers.containsKey(AUTHORIZATION)) {
			// To avoid redundant addition of header
			headers.remove(AUTHORIZATION);
		}
		if (!headers.isEmpty()) {
			restClient.setInterceptors(asList(new GenericRequestHeaderInterceptor(headers)));
		}

		return restClient;
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
