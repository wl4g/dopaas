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

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;

/**
 * Abstract base class for exceptions based on an {@link HttpStatus}.
 */
public abstract class HttpStatusCodeException extends RestClientResponseException {

	private static final long serialVersionUID = 5696801857651587810L;

	private final HttpStatus statusCode;

	/**
	 * Construct a new instance with an {@link HttpStatus}.
	 * 
	 * @param statusCode
	 *            the status code
	 */
	protected HttpStatusCodeException(HttpStatus statusCode) {
		this(statusCode, statusCode.name(), null, null, null);
	}

	/**
	 * Construct a new instance with an {@link HttpStatus} and status text.
	 * 
	 * @param statusCode
	 *            the status code
	 * @param statusText
	 *            the status text
	 */
	protected HttpStatusCodeException(HttpStatus statusCode, String statusText) {
		this(statusCode, statusText, null, null, null);
	}

	/**
	 * Construct instance with an {@link HttpStatus}, status text, and content.
	 * 
	 * @param statusCode
	 *            the status code
	 * @param statusText
	 *            the status text
	 * @param responseBody
	 *            the response body content, may be {@code null}
	 * @param responseCharset
	 *            the response body charset, may be {@code null}
	 * @since 3.0.5
	 */
	protected HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable byte[] responseBody,
			@Nullable Charset responseCharset) {

		this(statusCode, statusText, null, responseBody, responseCharset);
	}

	/**
	 * Construct instance with an {@link HttpStatus}, status text, content, and
	 * a response charset.
	 * 
	 * @param statusCode
	 *            the status code
	 * @param statusText
	 *            the status text
	 * @param responseHeaders
	 *            the response headers, may be {@code null}
	 * @param responseBody
	 *            the response body content, may be {@code null}
	 * @param responseCharset
	 *            the response body charset, may be {@code null}
	 * @since 3.1.2
	 */
	protected HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders responseHeaders,
			@Nullable byte[] responseBody, @Nullable Charset responseCharset) {

		this(getMessage(statusCode, statusText), statusCode, statusText, responseHeaders, responseBody, responseCharset);
	}

	/**
	 * Construct instance with an {@link HttpStatus}, status text, content, and
	 * a response charset.
	 * 
	 * @param message
	 *            the exception message
	 * @param statusCode
	 *            the status code
	 * @param statusText
	 *            the status text
	 * @param responseHeaders
	 *            the response headers, may be {@code null}
	 * @param responseBody
	 *            the response body content, may be {@code null}
	 * @param responseCharset
	 *            the response body charset, may be {@code null}
	 * @since 5.2.2
	 */
	protected HttpStatusCodeException(String message, HttpStatus statusCode, String statusText,
			@Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {

		super(message, statusCode.value(), statusText, responseHeaders, responseBody, responseCharset);
		this.statusCode = statusCode;
	}

	private static String getMessage(HttpStatus statusCode, String statusText) {
		if (StringUtils.isBlank(statusText)) {
			statusText = statusCode.getReasonPhrase();
		}
		return statusCode.value() + " " + statusText;
	}

	/**
	 * Return the HTTP status code.
	 */
	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

}