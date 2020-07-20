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
import java.io.InputStream;
import java.util.Map;

import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * {@link ClientHttpResponse} implementation based on Netty 4.
 */
class Netty4ClientHttpResponse extends AbstractClientHttpResponse {

	private final ChannelHandlerContext context;
	private final FullHttpResponse nettyResponse;
	private final ByteBufInputStream body;
	private volatile HttpHeaders headers;

	public Netty4ClientHttpResponse(ChannelHandlerContext context, FullHttpResponse nettyResponse) {
		Assert2.notNull(context, "ChannelHandlerContext must not be null");
		Assert2.notNull(nettyResponse, "FullHttpResponse must not be null");
		this.context = context;
		this.nettyResponse = nettyResponse;
		this.body = new ByteBufInputStream(nettyResponse.content());
		this.nettyResponse.retain();
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws IllegalArgumentException
	 *             in case of an unknown HTTP status code
	 * @see HttpStatus#valueOf(int)
	 */
	public HttpStatus getStatusCode() throws IOException {
		return HttpStatus.valueOf(getRawStatusCode());
	}

	/**
	 * Return the HTTP status code (potentially non-standard and not resolvable
	 * through the {@link HttpStatus} enum) as an integer.
	 * 
	 * @return the HTTP status as an integer
	 * @throws IOException
	 *             in case of I/O errors
	 * @since 3.1.1
	 * @see #getStatusCode()
	 */
	@SuppressWarnings("deprecation")
	public int getRawStatusCode() throws IOException {
		return nettyResponse.getStatus().code();
	}

	/**
	 * Return the HTTP status text of the response.
	 * 
	 * @return the HTTP status text
	 * @throws IOException
	 *             in case of I/O errors
	 */
	@SuppressWarnings("deprecation")
	public String getStatusText() throws IOException {
		return nettyResponse.getStatus().reasonPhrase();
	}

	/**
	 * Return the headers of this message.
	 * 
	 * @return a corresponding HttpHeaders object (never {@code null})
	 */
	public HttpHeaders getHeaders() {
		if (headers == null) {
			HttpHeaders headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : nettyResponse.headers()) {
				headers.add(entry.getKey(), entry.getValue());
			}
			this.headers = headers;
		}
		return headers;
	}

	/**
	 * Return the body of the message as an input stream.
	 * 
	 * @return the input stream body (never {@code null})
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public InputStream getBody() throws IOException {
		return body;
	}

	@Override
	public void close() {
		nettyResponse.release();
		context.close();
	}

}