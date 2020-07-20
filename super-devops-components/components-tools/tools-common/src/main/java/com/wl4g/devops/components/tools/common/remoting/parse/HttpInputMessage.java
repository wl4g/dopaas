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

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an HTTP input message, consisting of {@linkplain #getHeaders()
 * headers} and a readable {@linkplain #getBody() body}.
 *
 * <p>
 * Typically implemented by an HTTP request handle on the server side, or an
 * HTTP response handle on the client side.
 */
public interface HttpInputMessage extends HttpMessage {

	/**
	 * Return the body of the message as an input stream.
	 * 
	 * @return the input stream body (never {@code null})
	 * @throws IOException
	 *             in case of I/O errors
	 */
	InputStream getBody() throws IOException;

}