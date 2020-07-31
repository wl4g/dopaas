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
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.junit.Test;

import com.wl4g.devops.components.tools.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.devops.components.tools.common.collection.multimap.MultiValueMap;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;
import com.wl4g.devops.components.tools.common.resource.FileStreamResource;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.util.CharsetUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.netty.buffer.Unpooled.*;

import static com.google.common.base.Charsets.UTF_8;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * {@link RestClientStreamingTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月10日
 * @since
 */
public class RestClientStreamingTests {

	@Test
	public void downloadTest1() throws Exception {
		System.out.println("downloadTest1, startSampleFSServer...");
		startSampleFSServer(new HttpServerCodec(), new HttpObjectAggregator(65535), new ChunkedWriteHandler(),
				new HttpStaticFileServerHandler());
		System.out.println("downloading...");

		// URI uri = URI
		// .create("http://api.map.baidu.com/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ");
		URI downloadUri = URI.create("http://".concat(HOST).concat(":").concat(valueOf(PORT).concat("/download")));

		File downloadFile = File.createTempFile("coss-java-sdk-", ".download.txt");
		downloadFile.deleteOnExit();

		new RestClient(true).execute(downloadUri, HttpMethod.GET, null, response -> {
			ByteStreamUtils.copy(response.getBody(), new FileOutputStream(downloadFile, true));
			return downloadFile;
		});

		System.out.println("Success download file: " + downloadFile.getCanonicalPath());
	}

	@Test
	public void uploadTest2() throws Exception {
		System.out.println("uploadTest2, startSampleFSServer...");
		// Remove the following line if you don't want automatic content
		// compression.
		startSampleFSServer(new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpContentCompressor(),
				new HttpUploadServerHandler());
		System.out.println("uploading...");

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("myfile", new FileStreamResource(sampleFile));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(HttpMediaType.MULTIPART_FORM_DATA);
		HttpEntity<?> requestEntity = new HttpEntity<>(bodyMap, headers);

		URI uri = URI.create("http://".concat(HOST).concat(":").concat(valueOf(PORT).concat("/upload")));
		HttpResponseEntity<String> response = new RestClient(true).exchange(uri, POST, requestEntity, String.class);
		System.out.println("Upload response status: " + response.getStatusCode());
		System.out.println("Upload response body: " + response.getBody());

	}

	/**
	 * Listen http FS for testing.
	 */
	private void startSampleFSServer(ChannelHandler... handlers) {
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
						System.out.println("Init coss netty channels...");
						ChannelPipeline p = ch.pipeline();
						p.addLast(new LoggingHandler(LogLevel.INFO));
						p.addLast(new ChannelTrafficShapingHandler(100 * 1024, 100 * 1024));
						p.addLast(new IdleStateHandler(30_000L, 30_000L, 30_000L, MILLISECONDS));
						// FS server handlers
						// p.addLast(new HttpServerCodec());
						// p.addLast(new HttpObjectAggregator(65535));
						// p.addLast(new ChunkedWriteHandler());
						p.addLast(handlers);
					}
				});

				ChannelFuture f = bootstrap.bind(HOST, PORT).sync();
				f.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						Throwable t = future.cause();
						if (future.isSuccess()) {
							System.out.println(format("Netty server started on: %s:%s", HOST, PORT));
						} else {
							System.err.println(t);
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
					System.err.println(e);
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
	 * Input FS server channel handler
	 */
	public static class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

		// Disk if size exceed
		private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
		private final StringBuilder responseContent = new StringBuilder();
		private HttpRequest request;
		private HttpData partialContent;
		private HttpPostRequestDecoder decoder;

		static {
			// should delete file on exit (in normal exit)
			DiskFileUpload.deleteOnExitTemporaryFile = true;
			// system temp directory
			DiskFileUpload.baseDirectory = null;
			// should delete file on exit (in normal exit)
			DiskAttribute.deleteOnExitTemporaryFile = true;
			// system temp directory
			DiskAttribute.baseDirectory = null;
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			System.out.println("exceptionCaught: " + cause);
			ctx.channel().close();
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("channelInactive: " + ctx);
			if (decoder != null) {
				decoder.cleanFiles();
			}
		}

		@Override
		protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg) throws Exception {
			if (msg instanceof HttpRequest) {
				HttpRequest request = this.request = (HttpRequest) msg;
				URI uri = new URI(request.uri());
				if (!uri.getPath().startsWith("/upload")) {
					// Write Menu
					writeMenu(ctx);
					return;
				}
				responseContent.setLength(0);
				responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
				responseContent.append("===================================\r\n");
				responseContent.append("VERSION: " + request.protocolVersion().text() + "\r\n");
				responseContent.append("REQUEST_URI: " + request.uri() + "\r\n\r\n");
				responseContent.append("\r\n\r\n");

				// new getMethod
				for (Entry<String, String> entry : request.headers()) {
					responseContent.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
				}
				responseContent.append("\r\n\r\n");

				// new getMethod
				Set<Cookie> cookies;
				String value = request.headers().get(HttpHeaderNames.COOKIE);
				if (value == null) {
					cookies = Collections.emptySet();
				} else {
					cookies = ServerCookieDecoder.STRICT.decode(value);
				}
				for (Cookie cookie : cookies) {
					responseContent.append("COOKIE: " + cookie + "\r\n");
				}
				responseContent.append("\r\n\r\n");

				QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
				Map<String, List<String>> uriAttributes = decoderQuery.parameters();
				for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
					for (String attrVal : attr.getValue()) {
						responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
					}
				}
				responseContent.append("\r\n\r\n");

				// if GET Method: should not try to create an
				// HttpPostRequestDecoder
				if (HttpMethod.GET.equals(request.method())) {
					// GET Method: should not try to create an
					// HttpPostRequestDecoder
					// So stop here
					responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
					// Not now: LastHttpContent will be sent
					// writeResponse(ctx.channel());
					return;
				}
				try {
					decoder = new HttpPostRequestDecoder(factory, request);
				} catch (ErrorDataDecoderException e1) {
					e1.printStackTrace();
					responseContent.append(e1.getMessage());
					writeResponse(ctx.channel(), true);
					return;
				}

				boolean readingChunks = HttpUtil.isTransferEncodingChunked(request);
				responseContent.append("Is Chunked: " + readingChunks + "\r\n");
				responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");
				if (readingChunks) {
					// Chunk version
					responseContent.append("Chunks: ");
				}
			}

			// check if the decoder was constructed before
			// if not it handles the form get
			if (decoder != null) {
				if (msg instanceof HttpContent) {
					// New chunk is received
					HttpContent chunk = (HttpContent) msg;
					try {
						decoder.offer(chunk);
					} catch (ErrorDataDecoderException e1) {
						e1.printStackTrace();
						responseContent.append(e1.getMessage());
						writeResponse(ctx.channel(), true);
						return;
					}
					responseContent.append('o');
					// example of reading chunk by chunk (minimize memory usage
					// due to
					// Factory)
					readHttpDataChunkByChunk();
					// example of reading only if at the end
					if (chunk instanceof LastHttpContent) {
						writeResponse(ctx.channel());
						reset();
					}
				}
			} else {
				writeResponse(ctx.channel());
			}
		}

		private void reset() {
			request = null;
			// destroy the decoder to release all resources
			decoder.destroy();
			decoder = null;
		}

		/**
		 * Example of reading request by chunk and getting values from chunk to
		 * chunk
		 */
		private void readHttpDataChunkByChunk() {
			try {
				while (decoder.hasNext()) {
					InterfaceHttpData data = decoder.next();
					if (data != null) {
						// check if current HttpData is a FileUpload and
						// previously set as partial
						if (partialContent == data) {
							System.out.println(" 100% (FinalSize: " + partialContent.length() + ")");
							partialContent = null;
						}
						// new value
						writeHttpData(data);
					}
				}
				// Check partial decoding for a FileUpload
				InterfaceHttpData data = decoder.currentPartialHttpData();
				if (data != null) {
					StringBuilder builder = new StringBuilder();
					if (partialContent == null) {
						partialContent = (HttpData) data;
						if (partialContent instanceof FileUpload) {
							builder.append("Start FileUpload: ").append(((FileUpload) partialContent).getFilename()).append(" ");
						} else {
							builder.append("Start Attribute: ").append(partialContent.getName()).append(" ");
						}
						builder.append("(DefinedSize: ").append(partialContent.definedLength()).append(")");
					}
					if (partialContent.definedLength() > 0) {
						builder.append(" ").append(partialContent.length() * 100 / partialContent.definedLength()).append("% ");
						System.out.println(builder.toString());
					} else {
						builder.append(" ").append(partialContent.length()).append(" ");
						System.out.println(builder.toString());
					}
				}
			} catch (EndOfDataDecoderException e1) {
				// end
				responseContent.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");
			}
		}

		private void writeHttpData(InterfaceHttpData data) {
			if (data.getHttpDataType() == HttpDataType.Attribute) {
				Attribute attribute = (Attribute) data;
				String value;
				try {
					value = attribute.getValue();
				} catch (IOException e1) {
					// Error while reading data from File, only print name and
					// error
					e1.printStackTrace();
					responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ": "
							+ attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
					return;
				}
				if (value.length() > 100) {
					responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ": "
							+ attribute.getName() + " data too long\r\n");
				} else {
					responseContent
							.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ": " + attribute + "\r\n");
				}
			} else {
				responseContent.append("\r\nBODY FileUpload: " + data.getHttpDataType().name() + ": " + data + "\r\n");
				if (data.getHttpDataType() == HttpDataType.FileUpload) {
					FileUpload fileUpload = (FileUpload) data;
					if (fileUpload.isCompleted()) {
						if (fileUpload.length() < 10000) {
							responseContent.append("\tContent of file\r\n");
							try {
								responseContent.append(fileUpload.getString(fileUpload.getCharset()));
							} catch (IOException e1) {
								// do nothing for the example
								e1.printStackTrace();
							}
							responseContent.append("\r\n");
						} else {
							responseContent.append("\tFile too long to be printed out:" + fileUpload.length() + "\r\n");
						}
						// fileUpload.isInMemory();// tells if the file is in
						// Memory
						// or on File
						// fileUpload.renameTo(dest); // enable to move into
						// another
						// File dest
						// decoder.removeFileUploadFromClean(fileUpload);
						// //remove
						// the File of to delete file
					} else {
						responseContent.append("\tFile to be continued but should not!\r\n");
					}
				}
			}
		}

		private void writeResponse(Channel channel) {
			writeResponse(channel, false);
		}

		private void writeResponse(Channel channel, boolean forceClose) {
			// Convert the response content to a ChannelBuffer.
			ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
			responseContent.setLength(0);

			// Decide whether to close the connection or not.
			boolean keepAlive = HttpUtil.isKeepAlive(request) && !forceClose;

			// Build the response object.
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

			if (!keepAlive) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			} else if (request.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			Set<Cookie> cookies;
			String value = request.headers().get(HttpHeaderNames.COOKIE);
			if (value == null) {
				cookies = Collections.emptySet();
			} else {
				cookies = ServerCookieDecoder.STRICT.decode(value);
			}
			if (!cookies.isEmpty()) {
				// Reset the cookies if necessary.
				for (Cookie cookie : cookies) {
					response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
				}
			}
			// Write the response.
			ChannelFuture future = channel.writeAndFlush(response);
			// Close the connection after the write operation is done if
			// necessary.
			if (!keepAlive) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
		}

		private void writeMenu(ChannelHandlerContext ctx) {
			// print several HTML forms
			// Convert the response content to a ChannelBuffer.
			responseContent.setLength(0);

			// create Pseudo Menu
			responseContent.append("<html>");
			responseContent.append("<head>");
			responseContent.append("<title>Netty Test Form</title>\r\n");
			responseContent.append("</head>\r\n");
			responseContent.append("<body bgcolor=white><style>td{font-size: 12pt;}</style>");

			responseContent.append("<table border=\"0\">");
			responseContent.append("<tr>");
			responseContent.append("<td>");
			responseContent.append("<h1>Netty Test Form</h1>");
			responseContent.append("Choose one FORM");
			responseContent.append("</td>");
			responseContent.append("</tr>");
			responseContent.append("</table>\r\n");

			// GET
			responseContent.append("<CENTER>GET FORM<HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");
			responseContent.append("<FORM ACTION=\"/formget\" METHOD=\"GET\">");
			responseContent.append("<input type=hidden name=getform value=\"GET\">");
			responseContent.append("<table border=\"0\">");
			responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"info\" size=10></td></tr>");
			responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"secondinfo\" size=20>");
			responseContent.append("<tr><td>Fill with value: <br> <textarea name=\"thirdinfo\" cols=40 rows=10></textarea>");
			responseContent.append("</td></tr>");
			responseContent.append("<tr><td><INPUT TYPE=\"submit\" NAME=\"Send\" VALUE=\"Send\"></INPUT></td>");
			responseContent.append("<td><INPUT TYPE=\"reset\" NAME=\"Clear\" VALUE=\"Clear\" ></INPUT></td></tr>");
			responseContent.append("</table></FORM>\r\n");
			responseContent.append("<CENTER><HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");

			// POST
			responseContent.append("<CENTER>POST FORM<HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");
			responseContent.append("<FORM ACTION=\"/formpost\" METHOD=\"POST\">");
			responseContent.append("<input type=hidden name=getform value=\"POST\">");
			responseContent.append("<table border=\"0\">");
			responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"info\" size=10></td></tr>");
			responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"secondinfo\" size=20>");
			responseContent.append("<tr><td>Fill with value: <br> <textarea name=\"thirdinfo\" cols=40 rows=10></textarea>");
			responseContent.append(
					"<tr><td>Fill with file (only file name will be transmitted): <br> " + "<input type=file name=\"myfile\">");
			responseContent.append("</td></tr>");
			responseContent.append("<tr><td><INPUT TYPE=\"submit\" NAME=\"Send\" VALUE=\"Send\"></INPUT></td>");
			responseContent.append("<td><INPUT TYPE=\"reset\" NAME=\"Clear\" VALUE=\"Clear\" ></INPUT></td></tr>");
			responseContent.append("</table></FORM>\r\n");
			responseContent.append("<CENTER><HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");

			// POST with enctype="multipart/form-data"
			responseContent.append("<CENTER>POST MULTIPART FORM<HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");
			responseContent.append("<FORM ACTION=\"/formpostmultipart\" ENCTYPE=\"multipart/form-data\" METHOD=\"POST\">");
			responseContent.append("<input type=hidden name=getform value=\"POST\">");
			responseContent.append("<table border=\"0\">");
			responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"info\" size=10></td></tr>");
			responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"secondinfo\" size=20>");
			responseContent.append("<tr><td>Fill with value: <br> <textarea name=\"thirdinfo\" cols=40 rows=10></textarea>");
			responseContent.append("<tr><td>Fill with file: <br> <input type=file name=\"myfile\">");
			responseContent.append("</td></tr>");
			responseContent.append("<tr><td><INPUT TYPE=\"submit\" NAME=\"Send\" VALUE=\"Send\"></INPUT></td>");
			responseContent.append("<td><INPUT TYPE=\"reset\" NAME=\"Clear\" VALUE=\"Clear\" ></INPUT></td></tr>");
			responseContent.append("</table></FORM>\r\n");
			responseContent.append("<CENTER><HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");

			responseContent.append("</body>");
			responseContent.append("</html>");

			ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
			// Build the response object.
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

			// Decide whether to close the connection or not.
			boolean keepAlive = HttpUtil.isKeepAlive(request);
			if (!keepAlive) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			} else if (request.protocolVersion().equals(HttpVersion.HTTP_1_0)) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			// Write the response.
			ChannelFuture future = ctx.channel().writeAndFlush(response);
			// Close the connection after the write operation is done if
			// necessary.
			if (!keepAlive) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
		}

	}

	/**
	 * Http static file server channel handler
	 */
	public static class HttpStaticFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

		private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
		private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
		private static final int HTTP_CACHE_SECONDS = 60;
		private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
		private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			System.out.println(format("channelInactive: %s", ctx));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			System.out.println(format("exceptionCaught, ctx: %s, cause: %s", ctx, cause));
			if (ctx.channel().isActive()) {
				sendError(ctx, INTERNAL_SERVER_ERROR, null);
			}
		}

		@Override
		public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
			System.out.println(format("channelRead, ctx: %s, msg: %s", ctx, request));
			if (!request.decoderResult().isSuccess()) {
				sendError(ctx, BAD_REQUEST, request);
				return;
			}
			if (!GET.equals(request.method())) {
				sendError(ctx, METHOD_NOT_ALLOWED, request);
				return;
			}

			final boolean keepAlive = HttpUtil.isKeepAlive(request);
			final String uri = request.uri();
			final String path = sanitizeUri(uri);
			if (path == null) {
				sendError(ctx, FORBIDDEN, request);
				return;
			}

			File file = new File(path);
			// if (file.isHidden() || !file.exists()) {
			// sendError(ctx, NOT_FOUND, request);
			// return;
			// }
			//
			// if (file.isDirectory()) {
			// if (uri.endsWith("/")) {
			// sendListing(ctx, file, uri, request);
			// } else {
			// sendRedirect(ctx, uri + '/', request);
			// }
			// return;
			// }
			//
			// if (!file.isFile()) {
			// sendError(ctx, FORBIDDEN, request);
			// return;
			// }

			// Cache Validation
			String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
			if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
				SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
				Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

				// Only compare up to the second because the datetime format we
				// send to the client
				// does not have milliseconds
				long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
				long fileLastModifiedSeconds = file.lastModified() / 1000;
				if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
					sendNotModified(ctx, request);
					return;
				}
			}

			long fileLength = sampleFile.length();

			HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
			HttpUtil.setContentLength(response, fileLength);
			setContentTypeHeader(response, file);
			setDateAndCacheHeaders(response, file);

			if (!keepAlive) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			} else if (request.protocolVersion().equals(HTTP_1_0)) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			// Write the initial line and the header.
			ctx.write(response);

			// Write the content.
			ChannelFuture sendFileFuture;
			ChannelFuture lastContentFuture;
			if (ctx.pipeline().get(SslHandler.class) == null) {
				sendFileFuture = ctx.write(
						new DefaultFileRegion(new RandomAccessFile(sampleFile, "r").getChannel(), 0, fileLength),
						ctx.newProgressivePromise());
				// Write the end marker.
				lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			} else {
				sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(sampleFile, 4)),
						ctx.newProgressivePromise());
				// HttpChunkedInput will write the end marker (LastHttpContent)
				// for us.
				lastContentFuture = sendFileFuture;
			}

			sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
				@Override
				public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
					if (total < 0) { // total unknown
						System.err.println(future.channel() + " Transfer progress: " + progress);
					} else {
						System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
					}
				}

				@Override
				public void operationComplete(ChannelProgressiveFuture future) {
					System.err.println(future.channel() + " Transfer complete.");
				}
			});

			// Decide whether to close the connection or not.
			if (!keepAlive) {
				// Close the connection when the whole content is written out.
				lastContentFuture.addListener(ChannelFutureListener.CLOSE);
			}
		}

		private static String sanitizeUri(String uri) {
			// Decode the path.
			try {
				uri = URLDecoder.decode(uri, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new Error(e);
			}

			if (uri.isEmpty() || uri.charAt(0) != '/') {
				return null;
			}

			// Convert file separators.
			uri = uri.replace('/', File.separatorChar);

			// Simplistic dumb security check.
			// You will have to do something serious in the production
			// environment.
			if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.charAt(0) == '.'
					|| uri.charAt(uri.length() - 1) == '.' || INSECURE_URI.matcher(uri).matches()) {
				return null;
			}

			// Convert to absolute path.
			return SystemPropertyUtil.get("user.dir") + File.separator + uri;
		}

		@SuppressWarnings("unused")
		private void sendListing(ChannelHandlerContext ctx, File dir, String dirPath, FullHttpRequest request) {
			StringBuilder buf = new StringBuilder().append("<!DOCTYPE html>\r\n")
					.append("<html><head><meta charset='utf-8' /><title>").append("Listing of: ").append(dirPath)
					.append("</title></head><body>\r\n").append("<h3>Listing of: ").append(dirPath).append("</h3>\r\n")
					.append("<ul>").append("<li><a href=\"../\">..</a></li>\r\n");

			File[] files = dir.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isHidden() || !f.canRead()) {
						continue;
					}

					String name = f.getName();
					if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
						continue;
					}

					buf.append("<li><a href=\"").append(name).append("\">").append(name).append("</a></li>\r\n");
				}
			}

			buf.append("</ul></body></html>\r\n");

			ByteBuf buffer = ctx.alloc().buffer(buf.length());
			buffer.writeCharSequence(buf.toString(), UTF_8);

			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

			sendAndCleanupConnection(ctx, request, response);
		}

		@SuppressWarnings("unused")
		private void sendRedirect(ChannelHandlerContext ctx, String newUri, FullHttpRequest request) {
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND, Unpooled.EMPTY_BUFFER);
			response.headers().set(HttpHeaderNames.LOCATION, newUri);

			sendAndCleanupConnection(ctx, request, response);
		}

		private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, FullHttpRequest request) {
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
					Unpooled.copiedBuffer("Failure: " + status + "\r\n", UTF_8));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

			sendAndCleanupConnection(ctx, request, response);
		}

		/**
		 * When file timestamp is the same as what the browser is sending up,
		 * send a "304 Not Modified"
		 *
		 * @param ctx
		 *            Context
		 */
		private void sendNotModified(ChannelHandlerContext ctx, FullHttpRequest request) {
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
			setDateHeader(response);

			sendAndCleanupConnection(ctx, request, response);
		}

		/**
		 * If Keep-Alive is disabled, attaches "Connection: close" header to the
		 * response and closes the connection after the response being sent.
		 */
		private void sendAndCleanupConnection(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
			if (isNull(request)) {
				return;
			}

			final boolean keepAlive = HttpUtil.isKeepAlive(request);
			HttpUtil.setContentLength(response, response.content().readableBytes());
			if (!keepAlive) {
				// We're going to close the connection as soon as the response
				// is sent,
				// so we should also make it clear for the client.
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			} else if (request.protocolVersion().equals(HTTP_1_0)) {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}

			ChannelFuture flushPromise = ctx.writeAndFlush(response);

			if (!keepAlive) {
				// Close the connection as soon as the response is sent.
				flushPromise.addListener(ChannelFutureListener.CLOSE);
			}
		}

		/**
		 * Sets the Date header for the HTTP response
		 *
		 * @param response
		 *            HTTP response
		 */
		private static void setDateHeader(FullHttpResponse response) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
			dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

			Calendar time = new GregorianCalendar();
			response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
		}

		/**
		 * Sets the Date and Cache headers for the HTTP Response
		 *
		 * @param response
		 *            HTTP response
		 * @param fileToCache
		 *            file to extract content type
		 */
		private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
			dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

			// Date header
			Calendar time = new GregorianCalendar();
			response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

			// Add cache headers
			time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
			response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
			response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
			response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
		}

		/**
		 * Sets the content type header for the HTTP Response
		 *
		 * @param response
		 *            HTTP response
		 * @param file
		 *            file to extract content type
		 */
		private static void setContentTypeHeader(HttpResponse response, File file) {
			MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
		}

	}

	final public static String HOST = "127.0.0.1";
	final public static int PORT = 60000;
	final public static File sampleFile = createSampleFile0();

}