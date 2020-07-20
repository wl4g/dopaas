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
package com.wl4g.devops.coss.server;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;

import java.util.concurrent.ThreadFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLException;

import com.wl4g.devops.coss.server.config.ChannelServerProperties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * Netty channel coss server
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2017年12月19日
 * @since
 */
public class NettyCossServer extends CossServer {

	public NettyCossServer(ChannelServerProperties config) {
		super(config);
	}

	@Override
	protected void doStartBind() {
		// Create event loop configuration.
		EventLoopConfiguration loopConfig = createEventLoopConfig();
		ServerBootstrap bootstrap = new ServerBootstrap();
		try {
			bootstrap.group(loopConfig.getMaster(), loopConfig.getWorker());
			bootstrap.channel(loopConfig.getChannelClass());
			// 设置通道传输模式，立即传输模式，不需要等待特定大小
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			// 设置重用处于TIME_WAIT但是未完全关闭的socket地址
			// https://www.cnblogs.com/zemliu/p/3692996.html
			// https://www.jianshu.com/p/0bff7c020af2
			bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
			// 设置ByteBuff内存分配器, UnpooledByteBufAllocator/PooledByteBufAllocator
			// 在netty5.0中后者为默认，可重复利用之前分配的内存空间, 可有效减少内存分配
			bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			// 设置worker的socket通道模式，长连接
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			// 设置最大连接数(TCP底层syns队列/accept队列)，是提供给NioServerSocketChannel用来接收进来的连接,也就是boss线程
			// https://www.jianshu.com/p/e6f2036621f4，注意会依赖操作系统的TCP连接队列
			bootstrap.option(ChannelOption.SO_BACKLOG, config.getBacklog());
			// http://www.52im.net/thread-166-1-1.html
			bootstrap.childOption(ChannelOption.SO_SNDBUF, 32);
			bootstrap.childOption(ChannelOption.SO_RCVBUF, 32);
			// 设置slaves handler的处理器队列
			bootstrap.childHandler(new CossServerHandler());

			ChannelFuture f = bootstrap.bind(config.getInetHost(), config.getInetPort()).sync();
			f.addListener(future -> {
				Throwable t = future.cause();
				if (future.isSuccess()) {
					log.info("Netty coss server started on: {}/{}:{}", loopConfig.getChannelClass().getSimpleName(),
							config.getInetHost(), config.getInetPort());
				} else {
					log.error(t.getMessage(), t);
				}
			});

			// The thread begins to wait here unless there is a socket event
			// wake-up.
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			Exception ex = null;
			try {
				loopConfig.getMaster().shutdownGracefully().sync();
			} catch (Exception e) {
				ex = e;
			}
			try {
				loopConfig.getWorker().shutdownGracefully().sync();
			} catch (Exception e) {
				ex = e;
			}
			if (nonNull(ex)) {
				log.error("Failed to destroy netty server", ex);
			} else {
				log.info("Netty server stop gracefully({}:{}).", config.getInetHost(), config.getInetPort());
			}
		}

	}

	/**
	 * Gets server sslContext
	 * 
	 * @return
	 * @see https://developer.aliyun.com/article/574642?spm=a2c6h.13813017.0.dArticle738638.7ef457d8xATQUs
	 */
	protected SslContext getServerSslContext() {
		try {
			// TODO
			return SslContextBuilder.forServer(new KeyManager() {
			}).build();
		} catch (SSLException ex) {
			throw new IllegalStateException("Could not create default server sslContext", ex);
		}
	}

	/**
	 * Template method for changing properties on the given
	 * {@link SocketChannelConfig}.
	 * <p>
	 * The default implementation sets the connect timeout based on the set
	 * property.
	 * 
	 * @param sconfig
	 *            the channel configuration
	 */
	protected void configureChannel(SocketChannelConfig sconfig) {
	}

	/**
	 * Create netty event loop groups configuration.
	 * 
	 * @return
	 * @see https://netty.io/wiki/user-guide.html
	 */
	private EventLoopConfiguration createEventLoopConfig() {
		ThreadFactory masterFactory = new DefaultThreadFactory("NettyCossServerMaster", true);
		ThreadFactory workerFactory = new DefaultThreadFactory("NettyCossServerWorker", true);

		EventLoopGroup master;
		EventLoopGroup worker;
		Class<? extends ServerChannel> channelClass;
		if (IS_OS_LINUX && Epoll.isAvailable()) {
			master = new EpollEventLoopGroup(1, masterFactory);
			worker = new EpollEventLoopGroup(0, workerFactory);
			channelClass = EpollServerSocketChannel.class;
		} else {
			master = new NioEventLoopGroup(1, masterFactory);
			worker = new NioEventLoopGroup(0, workerFactory);
			channelClass = NioServerSocketChannel.class;
		}

		return new EventLoopConfiguration(master, worker, channelClass);
	}

	/**
	 * {@link EventLoopConfiguration}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年7月15日
	 * @since
	 */
	class EventLoopConfiguration {
		final private EventLoopGroup master;
		final private EventLoopGroup worker;
		final private Class<? extends ServerChannel> channelClass;

		public EventLoopConfiguration(EventLoopGroup master, EventLoopGroup worker, Class<? extends ServerChannel> channelClass) {
			notNullOf(master, "eventLoopMaster");
			notNullOf(worker, "eventLoopWorker");
			notNullOf(channelClass, "channelClass");
			this.master = master;
			this.worker = worker;
			this.channelClass = channelClass;
		}

		public EventLoopGroup getMaster() {
			return master;
		}

		public EventLoopGroup getWorker() {
			return worker;
		}

		public Class<? extends ServerChannel> getChannelClass() {
			return channelClass;
		}

	}

	/**
	 * Coss server channel initializer handler.
	 */
	class CossServerHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			log.info("Init coss netty channels...");
			configureChannel(ch.config());

			// Channel configure.
			ChannelPipeline p = ch.pipeline();
			// Log handler
			if (config.isEnableChannelLog()) {
				p.addLast(new LoggingHandler(LogLevel.valueOf(config.getChannelLogLevel())));
				log.info("Netty coss channel enable logLevel: {}", config.getChannelLogLevel());
			}

			// Idle handler
			p.addLast(new IdleStateHandler(config.getReadTimeoutSec(), config.getWriteTimeoutSec(), config.getAllTimeoutSec()));

			// SSL handler
			if (config.isEnableSslSecure()) {
				SslContext sslContext = getServerSslContext();
				notNull(sslContext, "sslContext should not be null");
				// TODO
				p.addLast(sslContext.newHandler(ch.alloc()));
			}

			// HTTP codec handlers
			p.addLast(new HttpClientCodec());
			p.addLast(new HttpObjectAggregator(config.getMaxContentLength()));
			p.addLast(new ChunkedWriteHandler());
		}
	}

	static {
		// Sets Sfl4j logger
		InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
	}

}