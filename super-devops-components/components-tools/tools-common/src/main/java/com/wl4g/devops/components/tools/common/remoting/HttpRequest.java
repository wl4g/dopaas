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

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.remoting.parse.HttpMessage;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Represents an HTTP request message, consisting of {@linkplain #getMethod()
 * method} and {@linkplain #getURI() uri}.
 *
 */
public interface HttpRequest extends HttpMessage {

	/**
	 * Return the HTTP method of the request.
	 * 
	 * @return the HTTP method as an HttpMethod enum value, or {@code null} if
	 *         not resolvable (e.g. in case of a non-standard HTTP method)
	 * @see #getMethodValue()
	 * @see HttpMethod#resolve(String)
	 */
	@Nullable
	default HttpMethod getMethod() {
		return new HttpMethod(getMethodValue());
	}

	/**
	 * Return the HTTP method of the request as a String value.
	 * 
	 * @return the HTTP method as a plain String
	 * @since 5.0
	 * @see #getMethod()
	 */
	String getMethodValue();

	/**
	 * Return the URI of the request (including a query string if any, but only
	 * if it is well-formed for a URI representation).
	 * 
	 * @return the URI of the request (never {@code null})
	 */
	URI getURI();

}