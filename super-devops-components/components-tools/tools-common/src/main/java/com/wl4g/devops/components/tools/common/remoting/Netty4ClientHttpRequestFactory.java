/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.devops.components.tools.common.remoting;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * {@link com.wl4g.devops.coss.client.channel.netty.ClientHttpRequestFactory}
 * implementation that uses <a href="https://netty.io/">Netty 4</a> to create
 * requests.
 *
 * <p>
 * Allows to use a pre-configured {@link EventLoopGroup} instance: useful for
 * sharing across multiple clients.
 *
 * <p>
 * Note that this implementation consistently closes the HTTP connection on each
 * request.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Mark Paluch
 * @since 4.1.2
 */
public class Netty4ClientHttpRequestFactory implements ClientHttpRequestFactory, Closeable {

	private final EventLoopGroup eventLoopGroup;

	private final boolean defaultEventLoopGroup;

	private int maxResponseSize = DEFAULT_MAX_RESPONSE_SIZE;

	/**
	 * Nullabled
	 */
	private SslContext sslContext;

	private int connectTimeout = -1;

	private int readTimeout = -1;

	/**
	 * Nullabled
	 */
	private volatile Bootstrap bootstrap;

	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with a default
	 * {@link NioEventLoopGroup}.
	 */
	public Netty4ClientHttpRequestFactory() {
		int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
		this.eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
		this.defaultEventLoopGroup = true;
	}

	/**
	 * Create a new {@code Netty4ClientHttpRequestFactory} with the given
	 * {@link EventLoopGroup}.
	 * <p>
	 * <b>NOTE:</b> the given group will <strong>not</strong> be
	 * {@linkplain EventLoopGroup#shutdownGracefully() shutdown} by this
	 * factory; doing so becomes the responsibility of the caller.
	 */
	public Netty4ClientHttpRequestFactory(EventLoopGroup eventLoopGroup) {
		notNull(eventLoopGroup, "EventLoopGroup must not be null");
		this.eventLoopGroup = eventLoopGroup;
		this.defaultEventLoopGroup = false;
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
	 * @return
	 * @throws IOException
	 */
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return new Netty4ClientHttpRequest(getBootstrap(uri), uri, httpMethod);
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

	private Bootstrap getBootstrap(URI uri) {
		boolean isSecure = (uri.getPort() == 443 || "https".equalsIgnoreCase(uri.getScheme()));
		if (isSecure) {
			return buildBootstrap(uri, true);
		} else if (bootstrap == null) {
			this.bootstrap = buildBootstrap(uri, false);
		}
		return bootstrap;
	}

	private Bootstrap buildBootstrap(URI uri, boolean isSecure) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				configureChannel(channel.config());
				ChannelPipeline pipe = channel.pipeline();
				// pipe.addLast(new LoggingHandler(LogLevel.INFO));
				if (isSecure) {
					notNull(getSslContext(), "sslContext should not be null");
					pipe.addLast(getSslContext().newHandler(channel.alloc(), uri.getHost(), uri.getPort()));
				}
				pipe.addLast(new HttpClientCodec());
				pipe.addLast(new HttpObjectAggregator(maxResponseSize));
				if (readTimeout > 0) {
					pipe.addLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
				}
			}
		});
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
	 * The default maximum response size.
	 * 
	 * @see #setMaxResponseSize(int)
	 */
	public static final int DEFAULT_MAX_RESPONSE_SIZE = 1024 * 1024 * 10;

}
