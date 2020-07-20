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
package com.wl4g.devops.components.tools.common.remoting.parse;

/**
 * Thrown by {@link HttpMessageParser} implementations when the
 * {@link HttpMessageParser#read} method fails.
 */
@SuppressWarnings("serial")
public class HttpMessageNotWritableException extends RuntimeException {

	/**
	 * Create a new HttpMessageNotReadableException.
	 * 
	 * @param msg
	 *            the detail message
	 */
	public HttpMessageNotWritableException(String msg) {
		super(msg);
	}

	/**
	 * Create a new HttpMessageNotReadableException.
	 * 
	 * @param msg
	 *            the detail message
	 * @param cause
	 *            the root cause (if any)
	 */
	public HttpMessageNotWritableException(String msg, Throwable cause) {
		super(msg, cause);
	}

}