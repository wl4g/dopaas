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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * {@link RestClientStreamingTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月10日
 * @since
 */
public class RestClientStreamingTests {

	final private static SmartLogger log = getLogger(RestClientStreamingTests.class);

	public static void main(String[] args) {
		downloadTest1();
		// uploadTest2();
	}

	public static void downloadTest1() {
		System.out.println("downloadTest1...");
		startFSTestServer(new FSOutputChannelHandler());

	}

	public static void uploadTest2() {
		System.out.println("uploadTest2...");
		startFSTestServer(new FSInputChannelHandler());

	}

	/**
	 * Listen http fs server for test.
	 */
	public static void startFSTestServer(ChannelHandler handler) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup masters = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			bootstrap.group(masters, worker);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
			bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.option(ChannelOption.SO_BACKLOG, 50);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					log.info("Init coss netty channels...");
					ChannelPipeline p = ch.pipeline();
					p.addLast(new LoggingHandler(LogLevel.INFO));
					p.addLast(new IdleStateHandler(30_000L, 30_000L, 30_000L, MILLISECONDS));
					p.addLast(new HttpClientCodec());
					p.addLast(new HttpObjectAggregator(65535));
					p.addLast(handler);
				}
			});

			ChannelFuture f = bootstrap.bind(HOST, PORT).sync();
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					Throwable t = future.cause();
					if (future.isSuccess()) {
						log.info("Netty server started on: {}:{}", HOST, PORT);
					} else {
						log.error(t.getMessage(), t);
					}
				}
			});
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				masters.shutdownGracefully().sync();
				worker.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				log.error("", e);
			}
		}

	}

	/**
	 * {@link FSInputChannelHandler}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年7月10日
	 * @since
	 */
	public static class FSInputChannelHandler extends ChannelDuplexHandler {

	}

	/**
	 * {@link FSOutputChannelHandler}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年7月10日
	 * @since
	 */
	public static class FSOutputChannelHandler extends ChannelDuplexHandler {

	}

	final private static String HOST = "127.0.0.1";
	final private static int PORT = 60000;

}
