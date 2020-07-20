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

import com.wl4g.devops.components.tools.common.remoting.exception.ClientHttpRequestExecution;

/**
 * Intercepts client-side HTTP requests. as to modify the outgoing
 * {@link ClientHttpRequest} and/or the incoming {@link ClientHttpResponse}.
 *
 * <p>
 * The main entry point for interceptors is
 * {@link #intercept(HttpRequest, byte[], ClientHttpRequestExecution)}.
 *
 */
public interface ClientHttpRequestInterceptor {

	/**
	 * Intercept the given request, and return a response. The given
	 * {@link ClientHttpRequestExecution} allows the interceptor to pass on the
	 * request and response to the next entity in the chain.
	 * <p>
	 * A typical implementation of this method would follow the following
	 * pattern:
	 * <ol>
	 * <li>Examine the {@linkplain HttpRequest request} and body</li>
	 * <li>Optionally {@linkplain HttpRequestWrapper wrap} the request to filter
	 * HTTP attributes.</li>
	 * <li>Optionally modify the body of the request.</li>
	 * <li><strong>Either</strong>
	 * <ul>
	 * <li>execute the request using
	 * {@link ClientHttpRequestExecution#execute(HttpRequest, byte[])},</li>
	 * <strong>or</strong>
	 * <li>do not execute the request to block the execution altogether.</li>
	 * </ul>
	 * <li>Optionally wrap the response to filter HTTP attributes.</li>
	 * </ol>
	 * 
	 * @param request
	 *            the request, containing method, URI, and headers
	 * @param body
	 *            the body of the request
	 * @param execution
	 *            the request execution
	 * @return the response
	 * @throws IOException
	 *             in case of I/O errors
	 */
	ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException;

}