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

import java.util.Date;

import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.model.ObjectValue;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedStream;

/**
 * {@link GetObjectHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月15日
 * @since
 */
public class GetObjectHandler extends AbstractProtocolHandler {

	/**
	 * <pre>
	 *  HTTP/1.1 200
	 *  Cache-Control: no-cache, no-store, must-revalidate
	 *  Content-Disposition: attachment; filename=file4383190990004865558.tmp.txt
	 *  Pragma: no-cache
	 *  Expires: 0
	 *  Content-Type: application/octet-stream;charset=utf-8
	 *  Content-Length: 16
	 *  Date: Sun, 17 May 2020 07:26:47 GMT
	 *  ETag: hs1GgaZfh34y
	 *  
	 *  stream array bytes contents...
	 *  ...
	 * </pre>
	 */
	@Override
	protected void doChannelRead(ChannelHandlerContext ctx, FullHttpRequest req) {
		// Gets actual coss object from server
		ObjectValue value = serverEndpoint.getObject("", "");

		HttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		ObjectMetadata metadata = value.getMetadata();
		resp.headers().set(HttpHeaderNames.CONTENT_TYPE, metadata.getContentType());
		resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, metadata.getContentLength());
		resp.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, metadata.getContentDisposition());
		resp.headers().set(HttpHeaderNames.CONTENT_ENCODING, metadata.getContentEncoding());
		resp.headers().set(HttpHeaderNames.LAST_MODIFIED, metadata.getMtime());
		resp.headers().set(HttpHeaderNames.CONTENT_MD5, metadata.getContentMd5());
		resp.headers().set(HttpHeaderNames.ETAG, metadata.getEtag());
		resp.headers().set(HttpHeaderNames.EXPIRES, metadata.getEtime());
		resp.headers().set(HttpHeaderNames.CACHE_CONTROL, metadata.getCacheControl());
		resp.headers().set(HttpHeaderNames.DATE, new Date());

		// Transfer coss object to client
		ChannelFuture writingFuture = ctx.write(new ChunkedStream(value.getObjectContent(), 8092), ctx.newProgressivePromise());
		addProgressiveListener(writingFuture);

		// Handle keepAlive
		handleHttpKeepAlive(ctx, req, null);
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