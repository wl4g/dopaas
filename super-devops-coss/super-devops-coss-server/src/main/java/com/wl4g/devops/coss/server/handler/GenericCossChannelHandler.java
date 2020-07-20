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
package com.wl4g.devops.coss.server.handler;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * {@link GenericCossChannelHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月17日
 * @since
 */
public abstract class GenericCossChannelHandler extends ChannelDuplexHandler {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.error("Channel channelInactive, remoteAddr: {}", ctx.channel().remoteAddress());
		// close(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Channel exceptionCaught, cause by: {}", cause.getMessage());
		// close(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			log.warn("{}, state:{}", ctx.channel(), event.state());

			switch (event.state()) {
			// e.g: Read timeout event: there may be no service or disconnection
			// in this channel, send heartbeat check.
			case READER_IDLE:
				close(ctx);
				break;
			// e.g: Write timeout event: abnormal situation (not even heartbeat
			// sent, not easy to trigger), direct close reconnect
			case WRITER_IDLE:
				close(ctx);
				break;
			// e.g: Read and write events: abnormal situation (not even
			// heartbeat sent, not easy to trigger), direct close reconnection
			case ALL_IDLE:
				close(ctx);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported idle detection type");
			}
		}

		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg instanceof FullHttpRequest) {
				doChannelRead(ctx, (FullHttpRequest) msg);
			} else {
				log.warn("Ignore unsupported type message: {}", msg);
			}
		} catch (Exception e) {
			log.error("Failed channel process.", e);

		}
	}

	/**
	 * Close channel
	 * 
	 * @param ctx
	 * @return
	 */
	protected ChannelFuture close(ChannelHandlerContext ctx) {
		try {
			if (ctx != null) {
				// Close client channel.
				ctx.channel().close();
				return ctx.close();
			}
		} catch (Exception e) {
			log.error("主动断开无效连接失败.", e);
		}
		return null;
	}

	/**
	 * Do channel read process
	 * 
	 * @param ctx
	 * @param req
	 */
	protected abstract void doChannelRead(ChannelHandlerContext ctx, FullHttpRequest req);

	/**
	 * Responses http message
	 *
	 * @param ctx
	 * @param status
	 */
	protected void responseMessage(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON_VALUE);

		// Add common response headers
		addCommonResponseHeaders(response.headers());

		// Output
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * If necessary, add listener close channel future
	 * 
	 * @param ctx
	 */
	protected void handleHttpKeepAlive(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp) {
		if (nonNull(ctx) && nonNull(req) && HttpUtil.isKeepAlive(req)) {
			if (nonNull(resp)) {
				HttpUtil.setKeepAlive(resp, true);
			}
			ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * Add common response headers
	 * 
	 * @param headers
	 */
	protected void addCommonResponseHeaders(HttpHeaders headers) {

	}

}