///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.devops.uos.client.channel;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Method;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//
//import javax.net.ssl.SSLContext;
//
//import org.apache.commons.codec.binary.Base64;
//
//import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
//import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;
//import com.wl4g.devops.uos.client.config.ClientCossConfiguration;
//import com.wl4g.devops.uos.common.exception.ClientCossException;
//import com.wl4g.devops.uos.common.internal.ExecutionContext;
//import com.wl4g.devops.uos.common.internal.RequestMessage;
//import com.wl4g.devops.uos.common.internal.ResponseMessage;
//import com.wl4g.devops.uos.common.internal.RetryStrategy;
//import com.wl4g.devops.uos.common.internal.define.UOSResultCode;
//import com.wl4g.devops.uos.common.internal.define.ClientResultCode;
//
///**
// * Netty implementation of {@link ChannelClient}.
// *
// * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
// * @version v1.0 2020年7月2日
// * @since
// */
//public class DefaultChannelClient extends ChannelClient {
//	private static Method setNormalizeUriMethod = null;
//
//	protected CloseableHttpClient httpClient;
//	protected RequestConfig requestConfig;
//	protected CredentialsProvider credentialsProvider;
//	protected HttpHost proxyHttpHost;
//	protected AuthCache authCache;
//
//	public DefaultChannelClient(ClientCossConfiguration config) {
//		super(config);
//		this.connectionManager = createHttpClientConnectionManager();
//		this.httpClient = createHttpClient(this.connectionManager);
//		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
//		requestConfigBuilder.setConnectTimeout(config.getConnectionTimeout());
//		requestConfigBuilder.setSocketTimeout(config.getSocketTimeout());
//		requestConfigBuilder.setConnectionRequestTimeout(config.getConnectionRequestTimeout());
//
//		String proxyHost = config.getProxyHost();
//		int proxyPort = config.getProxyPort();
//		if (proxyHost != null && proxyPort > 0) {
//			this.proxyHttpHost = new HttpHost(proxyHost, proxyPort);
//			requestConfigBuilder.setProxy(proxyHttpHost);
//
//			String proxyUsername = config.getProxyUsername();
//			String proxyPassword = config.getProxyPassword();
//			String proxyDomain = config.getProxyDomain();
//			String proxyWorkstation = config.getProxyWorkstation();
//			if (proxyUsername != null && proxyPassword != null) {
//				this.credentialsProvider = new BasicCredentialsProvider();
//				this.credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
//						new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
//
//				this.authCache = new BasicAuthCache();
//				authCache.put(this.proxyHttpHost, new BasicScheme());
//			}
//		}
//
//		// Compatible with HttpClient 4.5.9 or later
//		if (setNormalizeUriMethod != null) {
//			try {
//				setNormalizeUriMethod.invoke(requestConfigBuilder, false);
//			} catch (Exception e) {
//			}
//		}
//
//		this.requestConfig = requestConfigBuilder.build();
//	}
//
//	@Override
//	public ResponseMessage doRequestInternal(Request request, ExecutionContext context) throws IOException {
//		HttpRequestBase httpRequest = httpRequestFactory.createHttpRequest(request, context);
//		setProxyAuthorizationIfNeed(httpRequest);
//		HttpClientContext httpContext = createHttpContext();
//		httpContext.setRequestConfig(this.requestConfig);
//
//		CloseableHttpResponse httpResponse = null;
//		try {
//			httpResponse = httpClient.execute(httpRequest, httpContext);
//		} catch (IOException ex) {
//			httpRequest.abort();
//			throw ExceptionFactory.createNetworkException(ex);
//		}
//
//		return buildResponse(request, httpResponse);
//	}
//
//	protected static ResponseMessage buildResponse(Request request, CloseableHttpResponse httpResponse) throws IOException {
//
//		assert (httpResponse != null);
//
//		ResponseMessage response = new ResponseMessage(request);
//		response.setUrl(request.getUri());
//		response.setHttpResponse(httpResponse);
//
//		if (httpResponse.getStatusLine() != null) {
//			response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
//		}
//
//		if (httpResponse.getEntity() != null) {
//			if (response.isSuccessful()) {
//				response.setContent(httpResponse.getEntity().getContent());
//			} else {
//				readAndSetErrorResponse(httpResponse.getEntity().getContent(), response);
//			}
//		}
//
//		for (Header header : httpResponse.getAllHeaders()) {
//			if (HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(header.getName())) {
//				response.setContentLength(Long.parseLong(header.getValue()));
//			}
//			response.addHeader(header.getName(), header.getValue());
//		}
//
//		HttpUtil.convertHeaderCharsetFromIso88591(response.getHeaders());
//
//		return response;
//	}
//
//	private static void readAndSetErrorResponse(InputStream originalContent, ResponseMessage response) throws IOException {
//		byte[] contentBytes = IOUtils.readStreamAsByteArray(originalContent);
//		response.setErrorResponseAsString(new String(contentBytes));
//		response.setContent(new ByteArrayInputStream(contentBytes));
//	}
//
//	@Override
//	protected RetryStrategy getDefaultRetryStrategy() {
//		return new DefaultRetryStrategy();
//	}
//
//	private static class DefaultRetryStrategy extends RetryStrategy {
//
//		@Override
//		public boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries) {
//			if (ex instanceof ClientCossException) {
//				String errorCode = ((ClientCossException) ex).getErrorCode();
//				if (errorCode.equals(ClientResultCode.CONNECTION_TIMEOUT) || errorCode.equals(ClientResultCode.SOCKET_TIMEOUT)
//						|| errorCode.equals(ClientResultCode.CONNECTION_REFUSED)
//						|| errorCode.equals(ClientResultCode.UNKNOWN_HOST)
//						|| errorCode.equals(ClientResultCode.SOCKET_EXCEPTION)) {
//					return true;
//				}
//
//				// Don't retry when request input stream is non-repeatable
//				if (errorCode.equals(ClientResultCode.NONREPEATABLE_REQUEST)) {
//					return false;
//				}
//			}
//
//			if (ex instanceof UOSException) {
//				String errorCode = ((UOSException) ex).getErrorCode();
//				// No need retry for invalid responses
//				if (errorCode.equals(UOSResultCode.INVALID_RESPONSE)) {
//					return false;
//				}
//			}
//
//			if (response != null) {
//				int statusCode = response.getStatusCode();
//				if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
//					return true;
//				}
//			}
//
//			return false;
//		}
//	}
//
//}