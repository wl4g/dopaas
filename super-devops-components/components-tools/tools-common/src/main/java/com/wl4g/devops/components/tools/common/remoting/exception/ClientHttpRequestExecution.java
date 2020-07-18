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
package com.wl4g.devops.components.tools.common.remoting.exception;

import java.io.IOException;

import com.wl4g.devops.components.tools.common.remoting.ClientHttpRequestInterceptor;
import com.wl4g.devops.components.tools.common.remoting.ClientHttpResponse;
import com.wl4g.devops.components.tools.common.remoting.HttpRequest;

/**
 * Represents the context of a client-side HTTP request execution.
 *
 * <p>
 * Used to invoke the next interceptor in the interceptor chain, or - if the
 * calling interceptor is last - execute the request itself.
 *
 * @see ClientHttpRequestInterceptor
 */
public interface ClientHttpRequestExecution {

	/**
	 * Execute the request with the given request attributes and body, and
	 * return the response.
	 * 
	 * @param request
	 *            the request, containing method, URI, and headers
	 * @param body
	 *            the body of the request to execute
	 * @return the response
	 * @throws IOException
	 *             in case of I/O errors
	 */
	ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException;

}