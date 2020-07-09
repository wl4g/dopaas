package com.wl4g.devops.components.tools.common.http;

import java.io.IOException;

import com.wl4g.devops.components.tools.common.http.parse.HttpOutputMessage;

/**
 * Represents a client-side HTTP request. Created via an implementation of the
 * {@link ClientHttpRequestFactory}.
 *
 * <p>
 * A {@code ClientHttpRequest} can be {@linkplain #execute() executed},
 * receiving a {@link ClientHttpResponse} which can be read from.
 * 
 * @see ClientHttpRequestFactory#createRequest(java.net.URI, HttpMethod)
 */
public interface ClientHttpRequest extends HttpRequest, HttpOutputMessage {

	/**
	 * Execute this request, resulting in a {@link ClientHttpResponse} that can
	 * be read.
	 * 
	 * @return the response result of the execution
	 * @throws IOException
	 *             in case of I/O errors
	 */
	ClientHttpResponse execute() throws IOException;

}
