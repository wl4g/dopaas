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
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.remoting.exception.ClientHttpRequestExecution;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Wrapper for a {@link ClientHttpRequest} that has support for
 * {@link ClientHttpRequestInterceptor}s.
 */
class InterceptingClientHttpRequest extends AbstractBufferingClientHttpRequest {

	private final ClientHttpRequestFactory requestFactory;
	private final List<ClientHttpRequestInterceptor> interceptors;
	private HttpMethod method;
	private HttpHeaders requestHeaders;
	private URI uri;

	protected InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory,
			List<ClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod method, HttpHeaders requestHeaders) {
		this.requestFactory = requestFactory;
		this.interceptors = interceptors;
		this.method = method;
		this.uri = uri;
		this.requestHeaders = requestHeaders;
	}

	@Override
	public HttpMethod getMethod() {
		return this.method;
	}

	@Override
	public URI getURI() {
		return this.uri;
	}

	@Override
	protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
		InterceptingRequestExecution requestExecution = new InterceptingRequestExecution();
		return requestExecution.execute(this, bufferedOutput);
	}

	private class InterceptingRequestExecution implements ClientHttpRequestExecution {

		private final Iterator<ClientHttpRequestInterceptor> iterator;

		public InterceptingRequestExecution() {
			this.iterator = interceptors.iterator();
		}

		@Override
		public ClientHttpResponse execute(HttpRequest request, final byte[] body) throws IOException {
			if (this.iterator.hasNext()) {
				ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
				return nextInterceptor.intercept(request, body, this);
			} else {
				ClientHttpRequest delegate = requestFactory.createRequest(request.getURI(), request.getMethod(), requestHeaders);
				for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
					List<String> values = entry.getValue();
					for (String value : values) {
						delegate.getHeaders().add(entry.getKey(), value);
					}
				}
				if (body.length > 0) {
					if (delegate instanceof StreamingHttpOutputMessage) {
						StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) delegate;
						streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
							@Override
							public void writeTo(final OutputStream outputStream) throws IOException {
								ByteStreamUtils.copy(body, outputStream);
							}
						});
					} else {
						ByteStreamUtils.copy(body, delegate.getBody());
					}
				}
				return delegate.execute();
			}
		}
	}

	@Override
	public String getMethodValue() {
		return method.name();
	}

}