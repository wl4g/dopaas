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

import java.io.IOException;
import java.net.URI;

import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Abstract base class for {@link ClientHttpRequestFactory} implementations that
 * decorate another request factory.
 */
public abstract class AbstractClientHttpRequestFactoryWrapper implements ClientHttpRequestFactory {

	private final ClientHttpRequestFactory requestFactory;

	/**
	 * Create a {@code AbstractClientHttpRequestFactoryWrapper} wrapping the
	 * given request factory.
	 * 
	 * @param requestFactory
	 *            the request factory to be wrapped
	 */
	protected AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory requestFactory) {
		Assert2.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
		this.requestFactory = requestFactory;
	}

	/**
	 * This implementation simply calls
	 * {@link #createRequest(URI, HttpMethod, ClientHttpRequestFactory)} with
	 * the wrapped request factory provided to the
	 * {@linkplain #AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory)
	 * constructor}.
	 */
	@Override
	public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, HttpHeaders requestHeaders) throws IOException {
		return createRequest(uri, httpMethod, this.requestFactory, requestHeaders);
	}

	/**
	 * Create a new {@link ClientHttpRequest} for the specified URI and HTTP
	 * method by using the passed-on request factory.
	 * <p>
	 * Called from {@link #createRequest(URI, HttpMethod)}.
	 * 
	 * @param uri
	 *            the URI to create a request for
	 * @param httpMethod
	 *            the HTTP method to execute
	 * @param requestFactory
	 *            the wrapped request factory
	 * @param requestHeaders
	 *            Request headers
	 * @return the created request
	 * @throws IOException
	 *             in case of I/O errors
	 */
	protected abstract ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory,
			HttpHeaders requestHeaders) throws IOException;

}