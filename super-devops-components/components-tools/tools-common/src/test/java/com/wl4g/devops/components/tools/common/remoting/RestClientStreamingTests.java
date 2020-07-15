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

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import com.wl4g.devops.components.tools.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.devops.components.tools.common.collection.multimap.MultiValueMap;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.log.SmartLogger;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedNioFile;
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

	public static void main(String[] args) throws Exception {
		downloadTest1();
		uploadTest2();
	}

	public static void downloadTest1() throws Exception {
		System.out.println("downloadTest1 starting...");
		startFSTestServer(new FSOutputChannelHandler());

		System.out.println("download...");
		URI uri = URI.create("http://".concat(HOST).concat(":").concat(valueOf(PORT).concat("/download")));
		// URI uri = URI
		// .create("http://api.map.baidu.com/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ");

		File downloadFile = File.createTempFile("coss-java-sdk-", ".download.txt");
		downloadFile.deleteOnExit();

		new RestClient().execute(uri, HttpMethod.GET, request -> {
			request.getHeaders().set("Range", format("bytes=%d-%d", sampleFile.length(), sampleFile.getUsableSpace()));
		}, response -> {
			ByteStreamUtils.copy(response.getBody(), new FileOutputStream(downloadFile, true));
			return downloadFile;
		});

	}

	public static void uploadTest2() throws Exception {
		System.out.println("uploadTest2...");
		startFSTestServer(new FSInputChannelHandler());

		System.out.println("upload...");
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("uploadFile", sampleFile);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(HttpMediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		URI uri = URI.create("http://".concat(HOST).concat(":").concat(valueOf(PORT).concat("/upload")));
		HttpResponseEntity<String> response = new RestClient().exchange(uri, HttpMethod.POST, requestEntity, String.class);
		System.out.println("response status: " + response.getStatusCode());
		System.out.println("response body: " + response.getBody());

	}

	/**
	 * Listen http FS for test.
	 */
	public static void startFSTestServer(ChannelHandler handler) {
		new Thread(() -> {
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
				bootstrap.option(ChannelOption.SO_BACKLOG, 100);
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
		}).start();
	}

	/**
	 * Create sample temporary file
	 * 
	 * @return
	 */
	private static File createSampleFile0() {
		try {
			File file = File.createTempFile("coss-java-sdk-", ".txt");
			file.deleteOnExit();

			Writer writer = new OutputStreamWriter(new FileOutputStream(file));
			writer.write("abcdefghijklmnopqrstuvwxyz\n");
			writer.write("0123456789011234567890\n");
			writer.close();

			return file;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * FS input channel handler
	 */
	public static class FSInputChannelHandler extends ChannelDuplexHandler {

	}

	/**
	 * FS output channel handler
	 */
	public static class FSOutputChannelHandler extends ChannelDuplexHandler {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			log.info("channelRead, ctx: {}, msg: {}", ctx, msg);
			FullHttpResponse response = (FullHttpResponse) msg;
			// DefaultFullHttpResponse response = new
			// DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
			// HttpResponseStatus.OK);

			HttpUtil.setContentLength(response, sampleFile.length());
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			// if (HttpUtil.isKeepAlive(msg)) {
			// }
			ctx.write(response);

			ctx.channel().write(new ChunkedNioFile(sampleFile, 4));
			ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);

			// super.channelRead(ctx, msg);
		}

	}

	final public static String HOST = "127.0.0.1";
	final public static int PORT = 60000;
	final public static File sampleFile = createSampleFile0();

}
