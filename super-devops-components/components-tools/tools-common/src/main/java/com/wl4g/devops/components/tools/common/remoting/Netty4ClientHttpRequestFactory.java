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

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.lang.Runtime.getRuntime;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * {@link ClientHttpRequestFactory} implementation that uses
 * <a href="https://netty.io/">Netty 4</a> to create requests.
 * <p>
 * Allows to use a pre-configured {@link EventLoopGroup} instance: useful for
 * sharing across multiple clients.
 * <p>
 * Note that this implementation consistently closes the HTTP connection on each
 * request.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年7月01日 v1.0.0
 * @see
 */
public class Netty4ClientHttpRequestFactory implements ClientHttpRequestFactory, Closeable {

	private final boolean defaultEventLoopGroup;
	private final EventLoopGroup eventLoopGroup;

	@Nullable
	private SslContext sslContext;
	private boolean debug = false;
	private int connectTimeout = -1;
	private int readTimeout = -1;
	private int maxResponseSize = DEFAULT_MAX_RESPONSE_SIZE;

	@Nullable
	private volatile Bootstrap bootstrap;

	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with a default
	 * {@link NioEventLoopGroup}.
	 */
	public Netty4ClientHttpRequestFactory() {
		this(false);
	}

	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with a default
	 * {@link NioEventLoopGroup}.
	 * 
	 * @param debug
	 */
	public Netty4ClientHttpRequestFactory(boolean debug) {
		this(new NioEventLoopGroup(getRuntime().availableProcessors() * 2), debug);
	}

	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with the given
	 * {@link EventLoopGroup}.
	 * <p>
	 * <b>NOTE:</b> the given group will <strong>not</strong> be
	 * {@linkplain EventLoopGroup#shutdownGracefully() shutdown} by this
	 * factory; doing so becomes the responsibility of the caller.
	 * 
	 * @param debug
	 */
	public Netty4ClientHttpRequestFactory(EventLoopGroup eventLoopGroup, boolean debug) {
		// notNull(eventLoopGroup, "EventLoopGroup must not be null");
		this.eventLoopGroup = eventLoopGroup;
		this.defaultEventLoopGroup = isNull(eventLoopGroup);
		this.debug = debug;
	}

	/**
	 * Set the default maximum response size.
	 * <p>
	 * By default this is set to {@link #DEFAULT_MAX_RESPONSE_SIZE}.
	 * 
	 * @since 4.1.5
	 * @see HttpObjectAggregator#HttpObjectAggregator(int)
	 */
	public void setMaxResponseSize(int maxResponseSize) {
		this.maxResponseSize = maxResponseSize;
	}

	/**
	 * Set the SSL context. When configured it is used to create and insert an
	 * {@link io.netty.handler.ssl.SslHandler} in the channel pipeline.
	 * <p>
	 * A default client SslContext is configured if none has been provided.
	 */
	public void setSslContext(SslContext sslContext) {
		this.sslContext = sslContext;
	}

	/**
	 * Set the underlying connect timeout (in milliseconds). A timeout value of
	 * 0 specifies an infinite timeout.
	 * 
	 * @see ChannelConfig#setConnectTimeoutMillis(int)
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Set the underlying URLConnection's read timeout (in milliseconds). A
	 * timeout value of 0 specifies an infinite timeout.
	 * 
	 * @see ReadTimeoutHandler
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * Create nttp request of netty.
	 * 
	 * @param uri
	 * @param httpMethod
	 * @param requestHeaders
	 * @return
	 * @throws IOException
	 */
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, HttpHeaders requestHeaders) throws IOException {
		return new Netty4ClientHttpRequest(getBootstrap(uri, requestHeaders), uri, httpMethod);
	}

	/**
	 * Template method for changing properties on the given
	 * {@link SocketChannelConfig}.
	 * <p>
	 * The default implementation sets the connect timeout based on the set
	 * property.
	 * 
	 * @param config
	 *            the channel configuration
	 */
	protected void configureChannel(SocketChannelConfig config) {
		if (this.connectTimeout >= 0) {
			config.setConnectTimeoutMillis(this.connectTimeout);
		}
	}

	private SslContext getSslContext() {
		if (sslContext == null) {
			sslContext = buildClientSslContext();
		}
		return sslContext;
	}

	private SslContext buildClientSslContext() {
		try {
			return SslContextBuilder.forClient().build();
		} catch (SSLException ex) {
			throw new IllegalStateException("Could not create default client SslContext", ex);
		}
	}

	private Bootstrap getBootstrap(URI uri, HttpHeaders requestHeaders) {
		boolean isSecure = (uri.getPort() == 443 || "https".equalsIgnoreCase(uri.getScheme()));
		// if (isSecure) {
		// return createBootstrap(uri, true, requestHeaders);
		// } else if (isNull(bootstrap)) {
		// this.bootstrap = createBootstrap(uri, false, requestHeaders);
		// }
		// return bootstrap;
		return createBootstrap(uri, isSecure, requestHeaders);
	}

	private Bootstrap createBootstrap(final URI uri, final boolean isSecure, final HttpHeaders requestHeaders) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
				.handler(new HttpChannelInitializer(uri, isSecure, requestHeaders));
		return bootstrap;
	}

	@Override
	public void close() throws IOException {
		if (defaultEventLoopGroup) {
			// Clean up the EventLoopGroup if we created it in the constructor
			try {
				eventLoopGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * {@link HttpChannelInitializer}
	 */
	private class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
		final private URI uri;
		final private boolean isSecure;
		final private HttpHeaders requestHeaders;

		HttpChannelInitializer(final URI uri, final boolean isSecure, final HttpHeaders requestHeaders) {
			this.uri = uri;
			this.isSecure = isSecure;
			this.requestHeaders = requestHeaders;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			configureChannel(ch.config());

			ChannelPipeline pipe = ch.pipeline();
			if (debug) {
				pipe.addLast(new LoggingHandler(LogLevel.INFO));
			}
			if (isSecure) {
				notNull(getSslContext(), "sslContext should not be null");
				pipe.addLast(getSslContext().newHandler(ch.alloc(), uri.getHost(), uri.getPort()));
			}
			pipe.addLast(new HttpClientCodec());

			if (nonNull(requestHeaders) && requestHeaders.getContentType().isCompatibleWith(HttpMediaType.MULTIPART_FORM_DATA)) {
				// Remove the following line if you don't want automatic
				// content decompression.
				pipe.addLast("inflater", new HttpContentDecompressor());
				// to be used since huge file transfer
				pipe.addLast("chunkedWriter", new ChunkedWriteHandler());
			} else {
				pipe.addLast(new HttpObjectAggregator(maxResponseSize));
			}

			if (readTimeout > 0) {
				pipe.addLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
			}

		}

	}

	/**
	 * The default maximum response size.
	 * 
	 * @see #setMaxResponseSize(int)
	 */
	public static final int DEFAULT_MAX_RESPONSE_SIZE = 1024 * 1024 * 10;

}