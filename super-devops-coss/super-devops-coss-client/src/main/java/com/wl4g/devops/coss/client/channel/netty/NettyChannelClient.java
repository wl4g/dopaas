package com.wl4g.devops.coss.client.channel.netty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;

import com.wl4g.devops.coss.client.channel.ChannelClient;
import com.wl4g.devops.coss.client.channel.ChannelClient.Request;
import com.wl4g.devops.coss.client.config.ClientCossConfiguration;
import com.wl4g.devops.coss.common.exception.ClientCossException;
import com.wl4g.devops.coss.common.internal.ExecutionContext;
import com.wl4g.devops.coss.common.internal.RequestMessage;
import com.wl4g.devops.coss.common.internal.ResponseMessage;
import com.wl4g.devops.coss.common.internal.RetryStrategy;
import com.wl4g.devops.coss.common.internal.define.COSSResultCode;
import com.wl4g.devops.coss.common.internal.define.ClientResultCode;

/**
 * Netty implementation of {@link ChannelClient}.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月2日
 * @since
 */
public class NettyChannelClient extends ChannelClient {
	private static Method setNormalizeUriMethod = null;

	protected CloseableHttpClient httpClient;
	protected RequestConfig requestConfig;
	protected CredentialsProvider credentialsProvider;
	protected HttpHost proxyHttpHost;
	protected AuthCache authCache;

	public NettyChannelClient(ClientCossConfiguration config) {
		super(config);
		this.connectionManager = createHttpClientConnectionManager();
		this.httpClient = createHttpClient(this.connectionManager);
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		requestConfigBuilder.setConnectTimeout(config.getConnectionTimeout());
		requestConfigBuilder.setSocketTimeout(config.getSocketTimeout());
		requestConfigBuilder.setConnectionRequestTimeout(config.getConnectionRequestTimeout());

		String proxyHost = config.getProxyHost();
		int proxyPort = config.getProxyPort();
		if (proxyHost != null && proxyPort > 0) {
			this.proxyHttpHost = new HttpHost(proxyHost, proxyPort);
			requestConfigBuilder.setProxy(proxyHttpHost);

			String proxyUsername = config.getProxyUsername();
			String proxyPassword = config.getProxyPassword();
			String proxyDomain = config.getProxyDomain();
			String proxyWorkstation = config.getProxyWorkstation();
			if (proxyUsername != null && proxyPassword != null) {
				this.credentialsProvider = new BasicCredentialsProvider();
				this.credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
						new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));

				this.authCache = new BasicAuthCache();
				authCache.put(this.proxyHttpHost, new BasicScheme());
			}
		}

		// Compatible with HttpClient 4.5.9 or later
		if (setNormalizeUriMethod != null) {
			try {
				setNormalizeUriMethod.invoke(requestConfigBuilder, false);
			} catch (Exception e) {
			}
		}

		this.requestConfig = requestConfigBuilder.build();
	}

	@Override
	public ResponseMessage doRequestInternal(Request request, ExecutionContext context) throws IOException {
		HttpRequestBase httpRequest = httpRequestFactory.createHttpRequest(request, context);
		setProxyAuthorizationIfNeed(httpRequest);
		HttpClientContext httpContext = createHttpContext();
		httpContext.setRequestConfig(this.requestConfig);

		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpRequest, httpContext);
		} catch (IOException ex) {
			httpRequest.abort();
			throw ExceptionFactory.createNetworkException(ex);
		}

		return buildResponse(request, httpResponse);
	}

	protected static ResponseMessage buildResponse(Request request, CloseableHttpResponse httpResponse) throws IOException {

		assert (httpResponse != null);

		ResponseMessage response = new ResponseMessage(request);
		response.setUrl(request.getUri());
		response.setHttpResponse(httpResponse);

		if (httpResponse.getStatusLine() != null) {
			response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		}

		if (httpResponse.getEntity() != null) {
			if (response.isSuccessful()) {
				response.setContent(httpResponse.getEntity().getContent());
			} else {
				readAndSetErrorResponse(httpResponse.getEntity().getContent(), response);
			}
		}

		for (Header header : httpResponse.getAllHeaders()) {
			if (HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(header.getName())) {
				response.setContentLength(Long.parseLong(header.getValue()));
			}
			response.addHeader(header.getName(), header.getValue());
		}

		HttpUtil.convertHeaderCharsetFromIso88591(response.getHeaders());

		return response;
	}

	private static void readAndSetErrorResponse(InputStream originalContent, ResponseMessage response) throws IOException {
		byte[] contentBytes = IOUtils.readStreamAsByteArray(originalContent);
		response.setErrorResponseAsString(new String(contentBytes));
		response.setContent(new ByteArrayInputStream(contentBytes));
	}

	@Override
	protected RetryStrategy getDefaultRetryStrategy() {
		return new DefaultRetryStrategy();
	}

	private static class DefaultRetryStrategy extends RetryStrategy {

		@Override
		public boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries) {
			if (ex instanceof ClientCossException) {
				String errorCode = ((ClientCossException) ex).getErrorCode();
				if (errorCode.equals(ClientResultCode.CONNECTION_TIMEOUT) || errorCode.equals(ClientResultCode.SOCKET_TIMEOUT)
						|| errorCode.equals(ClientResultCode.CONNECTION_REFUSED)
						|| errorCode.equals(ClientResultCode.UNKNOWN_HOST)
						|| errorCode.equals(ClientResultCode.SOCKET_EXCEPTION)) {
					return true;
				}

				// Don't retry when request input stream is non-repeatable
				if (errorCode.equals(ClientResultCode.NONREPEATABLE_REQUEST)) {
					return false;
				}
			}

			if (ex instanceof COSSException) {
				String errorCode = ((COSSException) ex).getErrorCode();
				// No need retry for invalid responses
				if (errorCode.equals(COSSResultCode.INVALID_RESPONSE)) {
					return false;
				}
			}

			if (response != null) {
				int statusCode = response.getStatusCode();
				if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR || statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
					return true;
				}
			}

			return false;
		}
	}

}
