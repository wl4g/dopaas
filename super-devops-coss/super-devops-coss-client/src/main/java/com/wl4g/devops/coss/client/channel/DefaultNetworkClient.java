///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//package com.wl4g.devops.coss.client.channel;
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
//import com.wl4g.devops.coss.common.internal.ResponseMessage;
//import com.wl4g.devops.coss.common.internal.RetryStrategy;
//
///**
// * Default implementation of {@link ServiceClient}.
// */
//public class DefaultNetworkClient extends NetworkClient {
//	private static Method setNormalizeUriMethod = null;
//
//	protected CloseableHttpClient httpClient;
//	protected RequestConfig requestConfig;
//	protected CredentialsProvider credentialsProvider;
//	protected HttpHost proxyHttpHost;
//	protected AuthCache authCache;
//
//	public DefaultNetworkClient(ClientConfiguration config) {
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
//	public ResponseMessage sendRequestCore(Request request, ExecutionContext context) throws IOException {
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
//	protected static ResponseMessage buildResponse(ServiceClient.Request request, CloseableHttpResponse httpResponse)
//			throws IOException {
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
//	private static class DefaultRetryStrategy extends RetryStrategy {
//
//		@Override
//		public boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries) {
//			if (ex instanceof ClientException) {
//				String errorCode = ((ClientException) ex).getErrorCode();
//				if (errorCode.equals(ClientErrorCode.CONNECTION_TIMEOUT) || errorCode.equals(ClientErrorCode.SOCKET_TIMEOUT)
//						|| errorCode.equals(ClientErrorCode.CONNECTION_REFUSED) || errorCode.equals(ClientErrorCode.UNKNOWN_HOST)
//						|| errorCode.equals(ClientErrorCode.SOCKET_EXCEPTION)) {
//					return true;
//				}
//
//				// Don't retry when request input stream is non-repeatable
//				if (errorCode.equals(ClientErrorCode.NONREPEATABLE_REQUEST)) {
//					return false;
//				}
//			}
//
//			if (ex instanceof OSSException) {
//				String errorCode = ((OSSException) ex).getErrorCode();
//				// No need retry for invalid responses
//				if (errorCode.equals(OSSErrorCode.INVALID_RESPONSE)) {
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
//	@Override
//	protected RetryStrategy getDefaultRetryStrategy() {
//		return new DefaultRetryStrategy();
//	}
//
//}
