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

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * {@link ClientHttpRequest} implementation based on Netty 4.
 *
 * <p>
 * Created via the {@link Netty4ClientHttpRequestFactory}.
 */
class Netty4ClientHttpRequest extends AbstractAsyncClientHttpRequest implements ClientHttpRequest {

	private final Bootstrap bootstrap;
	private final URI uri;
	private final HttpMethod method;
	private final ByteBufOutputStream body;
	private boolean executed = false;

	public Netty4ClientHttpRequest(Bootstrap bootstrap, URI uri, HttpMethod method) {
		notNullOf(bootstrap, "bootstrap");
		notNullOf(uri, "uri");
		notNullOf(method, "method");
		this.bootstrap = bootstrap;
		this.uri = uri;
		this.method = method;
		this.body = new ByteBufOutputStream(Unpooled.buffer(1024));
	}

	/**
	 * Return the HTTP method of the request.
	 * 
	 * @return the HTTP method as an HttpMethod enum value, or {@code null} if
	 *         not resolvable (e.g. in case of a non-standard HTTP method)
	 */
	public HttpMethod getMethod() {
		return this.method;
	}

	@Override
	public String getMethodValue() {
		return this.method.name();
	}

	/**
	 * Return the URI of the request (including a query string if any, but only
	 * if it is well-formed for a URI representation).
	 * 
	 * @return the URI of the request (never {@code null})
	 */
	public URI getURI() {
		return this.uri;
	}

	/**
	 * Execute this request asynchronously, resulting in a Future handle.
	 * {@link ClientHttpResponse} that can be read.
	 * 
	 * @return the future response result of the execution
	 * @throws java.io.IOException
	 *             in case of I/O errors
	 */
	public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
		assertNotExecuted();
		ListenableFuture<ClientHttpResponse> result = executeInternal(getHeaders());
		this.executed = true;
		return result;
	}

	/**
	 * Asserts that this request has not been {@linkplain #executeAsync()
	 * executed} yet.
	 * 
	 * @throws IllegalStateException
	 *             if this request has been executed
	 */
	protected void assertNotExecuted() {
		Assert2.state(!executed, "ClientHttpRequest already executed");
	}

	/**
	 * Execute this request, resulting in a {@link ClientHttpResponse} that can
	 * be read.
	 * 
	 * @return the response result of the execution
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public ClientHttpResponse execute() throws IOException {
		try {
			return executeAsync().get();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted during request execution", ex);
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof IOException) {
				throw (IOException) ex.getCause();
			} else {
				throw new IOException(ex.getMessage(), ex.getCause());
			}
		}
	}

	/**
	 * Abstract template method that returns the body.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @return the body output stream
	 */
	protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
		return this.body;
	}

	/**
	 * Abstract template method that writes the given headers and content to the
	 * HTTP request.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @return the response object for the executed request
	 */
	protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers) throws IOException {
		final SettableFuture<ClientHttpResponse> responseFuture = SettableFuture.create();

		ChannelFutureListener connectionListener = future -> {
			if (future.isSuccess()) {
				Channel channel = future.channel();
				channel.pipeline().addLast(new RequestExecuteHandler(responseFuture));
				channel.writeAndFlush(createFullHttpRequest(headers));
			} else {
				responseFuture.setException(future.cause());
			}
		};

		this.bootstrap.connect(uri.getHost(), getPort(uri)).addListener(connectionListener);
		return responseFuture;
	}

	private FullHttpRequest createFullHttpRequest(HttpHeaders headers) {
		io.netty.handler.codec.http.HttpMethod nettyMethod = io.netty.handler.codec.http.HttpMethod.valueOf(method.name());

		String authority = uri.getRawAuthority();
		String path = uri.toString().substring(uri.toString().indexOf(authority) + authority.length());
		FullHttpRequest nettyRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, nettyMethod, path, body.buffer());

		nettyRequest.headers().set(HttpHeaders.HOST, uri.getHost() + ":" + getPort(uri));
		nettyRequest.headers().set(HttpHeaders.CONNECTION, "close");
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			nettyRequest.headers().add(entry.getKey(), entry.getValue());
		}
		if (!nettyRequest.headers().contains(HttpHeaders.CONTENT_LENGTH) && body.buffer().readableBytes() > 0) {
			nettyRequest.headers().set(HttpHeaders.CONTENT_LENGTH, body.buffer().readableBytes());
		}

		return nettyRequest;
	}

	private static int getPort(URI uri) {
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(uri.getScheme())) {
				port = 80;
			} else if ("https".equalsIgnoreCase(uri.getScheme())) {
				port = 443;
			}
		}
		return port;
	}

	/**
	 * A SimpleChannelInboundHandler to update the given
	 * SettableListenableFuture.
	 */
	private static class RequestExecuteHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

		private final SettableFuture<ClientHttpResponse> responseFuture;

		public RequestExecuteHandler(SettableFuture<ClientHttpResponse> responseFuture) {
			this.responseFuture = responseFuture;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext context, FullHttpResponse response) throws Exception {
			this.responseFuture.set(new Netty4ClientHttpResponse(context, response));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
			this.responseFuture.setException(cause);
		}
	}

}