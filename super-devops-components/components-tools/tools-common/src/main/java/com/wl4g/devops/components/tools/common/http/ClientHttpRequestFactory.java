package com.wl4g.devops.components.tools.common.http;

import java.io.IOException;
import java.net.URI;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Factory for {@link ClientHttpRequest} objects. Requests are created by the
 * {@link #createRequest(URI, HttpMethod)} method.
 */
public interface ClientHttpRequestFactory {

	/**
	 * Create a new {@link ClientHttpRequest} for the specified URI and HTTP
	 * method.
	 * <p>
	 * The returned request can be written to, and then executed by calling
	 * {@link ClientHttpRequest#execute()}.
	 * 
	 * @param uri
	 *            the URI to create a request for
	 * @param httpMethod
	 *            the HTTP method to execute
	 * @return the created request
	 * @throws IOException
	 *             in case of I/O errors
	 */
	ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException;

}
