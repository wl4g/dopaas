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
package com.wl4g.devops.components.tools.common.remoting;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

import io.netty.handler.codec.http.HttpMethod;

/**
 * {@link ClientHttpRequestFactory} wrapper with support for
 * {@link ClientHttpRequestInterceptor}s.
 *
 * @see ClientHttpRequestFactory
 * @see ClientHttpRequestInterceptor
 */
public class InterceptingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {

	private final List<ClientHttpRequestInterceptor> interceptors;

	/**
	 * Create a new instance of the {@code InterceptingClientHttpRequestFactory}
	 * with the given parameters.
	 * 
	 * @param requestFactory
	 *            the request factory to wrap
	 * @param interceptors
	 *            the interceptors that are to be applied (can be {@code null})
	 */
	public InterceptingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory,
			List<ClientHttpRequestInterceptor> interceptors) {

		super(requestFactory);
		this.interceptors = (interceptors != null ? interceptors : Collections.<ClientHttpRequestInterceptor> emptyList());
	}

	@Override
	protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory,
			HttpHeaders requestHeaders) {
		return new InterceptingClientHttpRequest(requestFactory, this.interceptors, uri, httpMethod, requestHeaders);
	}

}