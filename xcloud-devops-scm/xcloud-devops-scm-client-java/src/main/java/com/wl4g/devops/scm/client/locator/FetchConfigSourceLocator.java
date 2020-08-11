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

import static com.wl4g.components.common.lang.Assert2.state;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.AUTHORIZATION;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;

import com.wl4g.components.common.remoting.Netty4ClientHttpRequestFactory;
import com.wl4g.components.common.remoting.RestClient;
import com.wl4g.components.core.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.handler.locator.ScmPropertySourceLocator.GenericRequestHeaderInterceptor;
import com.wl4g.devops.scm.client.utils.InstanceHolder;

/**
 * {@link FetchConfigSourceLocator}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class FetchConfigSourceLocator extends AbstractConfigSourceLocator implements InitializingBean {

	/** Rest client */
	protected RestClient restClient;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.restClient = createRestClient(config.getFetchReadTimeout());
	}

	/**
	 * Create rest template.
	 * 
	 * @param readTimeout
	 * @return
	 */
	protected RestTemplate createRestClient(long readTimeout) {
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

	@Override
	public ReleaseMessage locate() {
		return null;
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
