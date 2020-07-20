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

import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

/**
 * {@link HttpInputMessage} that can eventually stores a Jackson view that will
 * be used to deserialize the message.
 */
public class MappingJacksonInputMessage implements HttpInputMessage {

	private final InputStream body;

	private final HttpHeaders headers;

	private Class<?> deserializationView;

	public MappingJacksonInputMessage(InputStream body, HttpHeaders headers) {
		this.body = body;
		this.headers = headers;
	}

	public MappingJacksonInputMessage(InputStream body, HttpHeaders headers, Class<?> deserializationView) {
		this(body, headers);
		this.deserializationView = deserializationView;
	}

	@Override
	public InputStream getBody() throws IOException {
		return this.body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public void setDeserializationView(Class<?> deserializationView) {
		this.deserializationView = deserializationView;
	}

	public Class<?> getDeserializationView() {
		return this.deserializationView;
	}

}