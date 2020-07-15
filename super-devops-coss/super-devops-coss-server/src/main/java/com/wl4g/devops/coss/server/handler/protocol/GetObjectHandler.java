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
package com.wl4g.devops.coss.server.handler.protocol;

import com.wl4g.devops.coss.common.model.ObjectValue;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;

/**
 * {@link GetObjectHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月15日
 * @since
 */
public class GetObjectHandler extends AbstractProtocolHandler {

	@Override
	protected void doChannelRead(ChannelHandlerContext ctx, FullHttpRequest req) {
		// Gets actual coss object from server
		ObjectValue value = serverEndpoint.getObject("", "");

		// Transfer coss object to client
		ChannelFuture writingFuture = ctx.write(new ChunkedStream(value.getObjectContent(), 8092), ctx.newProgressivePromise());
		addProgressiveListener(writingFuture);

		ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Add progressive listener transmission status
	 *
	 * @param writingFuture
	 */
	protected void addProgressiveListener(ChannelFuture writingFuture) {
		writingFuture.addListener(new ChannelProgressiveFutureListener() {
			@Override
			public void operationComplete(ChannelProgressiveFuture future) throws Exception {
				log.debug("Transfer complete.");
			}

			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
				if (total < 0) {
					log.debug("Transfer progress: " + progress);
				} else {
					log.debug("Transfer progress: " + progress + "/" + total);
				}
			}
		});
	}

}
