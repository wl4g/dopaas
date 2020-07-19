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

import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Factory for {@link ClientHttpRequest} objects. Requests are created by the
 * {@link #createRequest(URI, HttpMethod)} method.
 */
public interface ClientHttpRequestFactory {

	/**
	 * Create a new {@link ClientHttpRequest} for the specified URI and HTTP
	 * method.
	 * <p>
	 * The returned request can be written to, and then executed by calling
	 * {@link ClientHttpRequest#execute()}.
	 * 
	 * @param uri
	 *            the URI to create a request for
	 * @param httpMethod
	 *            the HTTP method to execute
	 * @param requestProcessor
	 *            Request headers
	 * @return the created request
	 * @throws IOException
	 *             in case of I/O errors
	 */
	ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, HttpHeaders requestHeaders) throws IOException;

}