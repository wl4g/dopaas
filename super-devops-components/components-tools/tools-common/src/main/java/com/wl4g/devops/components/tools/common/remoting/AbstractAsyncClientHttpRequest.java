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
import java.io.OutputStream;

import com.google.common.util.concurrent.ListenableFuture;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.remoting.parse.HttpOutputMessage;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

/**
 * Abstract base for {@link AsyncClientHttpRequest} that makes sure that headers
 * and body are not written multiple times. </br>
 * Represents a client-side asynchronous HTTP request. Created via an
 * implementation of the {@link AsyncClientHttpRequestFactory}.
 *
 * <p>
 * A {@code AsyncHttpRequest} can be {@linkplain #executeAsync() executed},
 * getting a future {@link ClientHttpResponse} which can be read from.
 * 
 * @see AsyncClientHttpRequestFactory#createAsyncRequest
 */
abstract class AbstractAsyncClientHttpRequest implements HttpRequest, HttpOutputMessage {

	private final HttpHeaders headers = new HttpHeaders();
	private boolean executed = false;

	@Override
	public final HttpHeaders getHeaders() {
		return (this.executed ? HttpHeaders.readOnlyHttpHeaders(headers) : headers);
	}

	@Override
	public final OutputStream getBody() throws IOException {
		assert2NotExecuted();
		return getBodyInternal(headers);
	}

	/**
	 * Execute this request asynchronously, resulting in a Future handle.
	 * {@link ClientHttpResponse} that can be read.
	 * 
	 * @return the future response result of the execution
	 * @throws java.io.IOException
	 *             in case of I/O errors
	 */
	public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
		assert2NotExecuted();
		ListenableFuture<ClientHttpResponse> result = executeInternal(headers);
		this.executed = true;
		return result;
	}

	/**
	 * Assert2s that this request has not been {@linkplain #executeAsync()
	 * executed} yet.
	 * 
	 * @throws IllegalStateException
	 *             if this request has been executed
	 */
	protected void assert2NotExecuted() {
		Assert2.state(!executed, "ClientHttpRequest already executed");
	}

	/**
	 * Abstract template method that returns the body.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @return the body output stream
	 */
	protected abstract OutputStream getBodyInternal(HttpHeaders headers) throws IOException;

	/**
	 * Abstract template method that writes the given headers and content to the
	 * HTTP request.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @return the response object for the executed request
	 */
	protected abstract ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException;

}