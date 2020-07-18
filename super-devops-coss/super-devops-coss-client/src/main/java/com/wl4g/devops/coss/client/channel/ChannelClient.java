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
package com.wl4g.devops.coss.client.channel;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.wl4g.devops.coss.client.config.ClientCossConfiguration;
import com.wl4g.devops.coss.client.utils.HttpUtil;
import com.wl4g.devops.coss.common.auth.RequestSigner;
import com.wl4g.devops.coss.common.exception.ClientCossException;
import com.wl4g.devops.coss.common.exception.ServiceException;
import com.wl4g.devops.coss.common.internal.ExecutionContext;
import com.wl4g.devops.coss.common.internal.HttpMesssage;
import com.wl4g.devops.coss.common.internal.RequestHandler;
import com.wl4g.devops.coss.common.internal.RequestMessage;
import com.wl4g.devops.coss.common.internal.ResponseHandler;
import com.wl4g.devops.coss.common.internal.ResponseMessage;
import com.wl4g.devops.coss.common.internal.RetryStrategy;
import com.wl4g.devops.coss.common.internal.define.COSSConstants;
import com.wl4g.devops.coss.common.utils.HttpMethod;
import com.wl4g.devops.coss.common.utils.LogUtils;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.coss.common.utils.LogUtils.getLog;
import static com.wl4g.devops.coss.client.utils.COSSUtils.COMMON_RESOURCE_MANAGER;
import static com.wl4g.devops.coss.common.utils.LogUtils.logException;

/**
 * Abstract channel client that provides interfaces to access COSS services.
 */
public abstract class ChannelClient {

	final protected ClientCossConfiguration config;

	protected ChannelClient(ClientCossConfiguration config) {
		this.config = config;
	}

	public ClientCossConfiguration getClientCossConfiguration() {
		return config;
	}

	/**
	 * Send HTTP request with specified context to COSS and wait for HTTP
	 * response.
	 */
	public ResponseMessage handleRequest(RequestMessage request, ExecutionContext context)
			throws ServiceException, ClientCossException {
		notNull(request, "request");
		notNull(context, "context");

		try {
			return doRequest(request, context);
		} finally {
			// Close the request stream as well after the request is completed.
			try {
				request.close();
			} catch (IOException ex) {
				logException("Unexpected io exception when trying to close http request: ", ex);
				throw new ClientCossException("Unexpected io exception when trying to close http request: ", ex);
			}
		}
	}

	/**
	 * Do implements the core logic to send requests to COSS services.
	 * 
	 * @param request
	 * @param context
	 * @return
	 * @throws ClientCossException
	 * @throws ServiceException
	 */
	private ResponseMessage doRequest(RequestMessage request, ExecutionContext context)
			throws ClientCossException, ServiceException {

		RetryStrategy retryStrategy = nonNull(context.getRetryStrategy()) ? context.getRetryStrategy()
				: getDefaultRetryStrategy();

		// Sign the request if a signer provided.
		if (nonNull(context.getSigner()) && !request.isUseUrlSignature()) {
			context.getSigner().sign(request);
		}

		for (RequestSigner signer : context.getSignerHandlers()) {
			signer.sign(request);
		}

		InputStream requestContent = request.getContent();
		if (requestContent != null && requestContent.markSupported()) {
			requestContent.mark(COSSConstants.DEFAULT_STREAM_BUFFER_SIZE);
		}

		int retries = 0;
		ResponseMessage response = null;
		while (true) {
			try {
				if (retries > 0) {
					pause(retries, retryStrategy);
					if (requestContent != null && requestContent.markSupported()) {
						try {
							requestContent.reset();
						} catch (IOException ex) {
							logException("Failed to reset the request input stream: ", ex);
							throw new ClientCossException("Failed to reset the request input stream: ", ex);
						}
					}
				}

				/*
				 * The key four steps to send HTTP requests and receive HTTP
				 * responses.
				 */

				// Step 1. Preprocess HTTP request.
				beforeRequestHandle(request, context.getResquestHandlers());

				// Step 2. Build HTTP request with specified request parameters
				// and context.
				Request httpRequest = buildRequest(request, context);

				// Step 3. Send HTTP request to COSS.
				long startTime = System.currentTimeMillis();
				response = doRequestInternal(httpRequest, context);
				long duration = currentTimeMillis() - startTime;
				if (duration > config.getSlowRequestsThreshold()) {
					LogUtils.getLog().warn(formatSlowRequestLog(request, response, duration));
				}

				// Step 4. Preprocess HTTP response.
				afterResponseHandle(response, context.getResponseHandlers());

				return response;
			} catch (ServiceException sex) {
				logException("[Server]Unable to execute HTTP request: ", sex, request.getOriginalRequest().isLogEnabled());

				// Notice that the response should not be closed in the
				// finally block because if the request is successful,
				// the response should be returned to the callers.
				closeQuietly(response);

				if (!shouldRetry(sex, request, response, retries, retryStrategy)) {
					throw sex;
				}
			} catch (ClientCossException cex) {
				logException("[Client]Unable to execute HTTP request: ", cex, request.getOriginalRequest().isLogEnabled());
				closeQuietly(response);

				if (!shouldRetry(cex, request, response, retries, retryStrategy)) {
					throw cex;
				}
			} catch (Exception ex) {
				logException("[Unknown]Unable to execute HTTP request: ", ex, request.getOriginalRequest().isLogEnabled());
				closeQuietly(response);
				throw new ClientCossException(COMMON_RESOURCE_MANAGER.getFormattedString("ConnectionError", ex.getMessage()), ex);
			} finally {
				retries++;
			}
		}
	}

	/**
	 * Implements the core logic to send requests to COSS services.
	 */
	protected abstract ResponseMessage doRequestInternal(Request request, ExecutionContext context) throws IOException;

	private Request buildRequest(RequestMessage requestMessage, ExecutionContext context) throws ClientCossException {
		Request request = new Request();
		request.setMethod(requestMessage.getMethod());
		request.setUseChunkEncoding(requestMessage.isUseChunkEncoding());

		if (requestMessage.isUseUrlSignature()) {
			request.setUrl(requestMessage.getAbsoluteUrl().toString());
			request.setUseUrlSignature(true);
			request.setContent(requestMessage.getContent());
			request.setContentLength(requestMessage.getContentLength());
			request.setHeaders(requestMessage.getHeaders());
			return request;
		}

		request.setHeaders(requestMessage.getHeaders());
		// The header must be converted after the request is signed,
		// otherwise the signature will be incorrect.
		if (request.getHeaders() != null) {
			HttpUtil.convertHeaderCharsetToIso88591(request.getHeaders());
		}

		final String delimiter = "/";
		String uri = requestMessage.getEndpoint().toString();
		if (!uri.endsWith(delimiter)
				&& (requestMessage.getResourcePath() == null || !requestMessage.getResourcePath().startsWith(delimiter))) {
			uri += delimiter;
		}

		if (requestMessage.getResourcePath() != null) {
			uri += requestMessage.getResourcePath();
		}

		String paramString = HttpUtil.paramToQueryString(requestMessage.getParameters(), context.getCharset());

		/*
		 * For all non-POST requests, and any POST requests that already have a
		 * payload, we put the encoded params directly in the URI, otherwise,
		 * we'll put them in the POST request's payload.
		 */
		boolean requestHasNoPayload = requestMessage.getContent() != null;
		boolean requestIsPost = requestMessage.getMethod() == HttpMethod.POST;
		boolean putParamsInUri = !requestIsPost || requestHasNoPayload;
		if (paramString != null && putParamsInUri) {
			uri += "?" + paramString;
		}
		request.setUrl(uri);

		if (requestIsPost && requestMessage.getContent() == null && paramString != null) {
			// Put the param string to the request body if POSTing and
			// no content.
			try {
				byte[] buf = paramString.getBytes(context.getCharset());
				ByteArrayInputStream content = new ByteArrayInputStream(buf);
				request.setContent(content);
				request.setContentLength(buf.length);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(COMMON_RESOURCE_MANAGER.getFormattedString("EncodingFailed", e.getMessage()));
			}
		} else {
			request.setContent(requestMessage.getContent());
			request.setContentLength(requestMessage.getContentLength());
		}

		return request;
	}

	private void afterResponseHandle(ResponseMessage response, List<ResponseHandler> responseHandlers)
			throws ServiceException, ClientCossException {
		for (ResponseHandler h : responseHandlers) {
			h.handle(response);
		}
	}

	private void beforeRequestHandle(RequestMessage message, List<RequestHandler> resquestHandlers)
			throws ServiceException, ClientCossException {
		for (RequestHandler h : resquestHandlers) {
			h.handle(message);
		}
	}

	private void pause(int retries, RetryStrategy retryStrategy) throws ClientCossException {
		long delay = retryStrategy.getPauseDelay(retries);
		getLog().debug("An retriable error request will be retried after " + delay + "(ms) with attempt times: " + retries);

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new ClientCossException(e.getMessage(), e);
		}
	}

	private boolean shouldRetry(Exception exception, RequestMessage request, ResponseMessage response, int retries,
			RetryStrategy retryStrategy) {

		if (retries >= config.getMaxErrorRetry()) {
			return false;
		}

		if (!request.isRepeatable()) {
			return false;
		}

		if (retryStrategy.shouldRetry(exception, request, response, retries)) {
			getLog().debug("Retrying on " + exception.getClass().getName() + ": " + exception.getMessage());
			return true;
		}
		return false;
	}

	private void closeQuietly(ResponseMessage response) {
		if (response != null) {
			try {
				response.close();
			} catch (IOException ioe) {
				/* silently close the response. */
			}
		}
	}

	private String formatSlowRequestLog(RequestMessage request, ResponseMessage response, long useTimesMs) {
		return format("Request cost %d seconds, endpoint %s, resourcePath %s, " + "method %s, statusCode %d, requestId %s.",
				useTimesMs / 1000, request.getEndpoint(), request.getResourcePath(), request.getMethod(),
				response.getStatusCode(), response.getRequestId());
	}

	protected abstract RetryStrategy getDefaultRetryStrategy();

	public abstract void shutdown();

	/**
	 * Wrapper class based on {@link HttpMessage} that represents HTTP request
	 * message to COSS.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年7月1日
	 * @since
	 */
	public static class Request extends HttpMesssage {
		private String uri;
		private HttpMethod method;
		private boolean useUrlSignature = false;
		private boolean useChunkEncoding = false;

		public String getUri() {
			return uri;
		}

		public void setUrl(String uri) {
			this.uri = uri;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public void setMethod(HttpMethod method) {
			this.method = method;
		}

		public boolean isUseUrlSignature() {
			return useUrlSignature;
		}

		public void setUseUrlSignature(boolean useUrlSignature) {
			this.useUrlSignature = useUrlSignature;
		}

		public boolean isUseChunkEncoding() {
			return useChunkEncoding;
		}

		public void setUseChunkEncoding(boolean useChunkEncoding) {
			this.useChunkEncoding = useChunkEncoding;
		}
	}

}