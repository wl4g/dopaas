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

import java.nio.charset.Charset;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;

/**
 * Exception thrown when an unknown (or custom) HTTP status code is received.
 */
public class UnknownHttpStatusCodeException extends RestClientResponseException {

	private static final long serialVersionUID = 7103980251635005491L;

	/**
	 * Construct a new instance of {@code HttpStatusCodeException} based on an
	 * {@link HttpStatus}, status text, and response body content.
	 * 
	 * @param rawStatusCode
	 *            the raw status code value
	 * @param statusText
	 *            the status text
	 * @param responseHeaders
	 *            the response headers (may be {@code null})
	 * @param responseBody
	 *            the response body content (may be {@code null})
	 * @param responseCharset
	 *            the response body charset (may be {@code null})
	 */
	public UnknownHttpStatusCodeException(int rawStatusCode, String statusText, @Nullable HttpHeaders responseHeaders,
			@Nullable byte[] responseBody, @Nullable Charset responseCharset) {

		this("Unknown status code [" + rawStatusCode + "]" + " " + statusText, rawStatusCode, statusText, responseHeaders,
				responseBody, responseCharset);
	}

	/**
	 * Construct a new instance of {@code HttpStatusCodeException} based on an
	 * {@link HttpStatus}, status text, and response body content.
	 * 
	 * @param rawStatusCode
	 *            the raw status code value
	 * @param statusText
	 *            the status text
	 * @param responseHeaders
	 *            the response headers (may be {@code null})
	 * @param responseBody
	 *            the response body content (may be {@code null})
	 * @param responseCharset
	 *            the response body charset (may be {@code null})
	 * @since 5.2.2
	 */
	public UnknownHttpStatusCodeException(String message, int rawStatusCode, String statusText,
			@Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {

		super(message, rawStatusCode, statusText, responseHeaders, responseBody, responseCharset);
	}
}