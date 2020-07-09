package com.wl4g.devops.components.tools.common.http;

import java.net.URI;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.http.parse.HttpMessage;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Represents an HTTP request message, consisting of {@linkplain #getMethod()
 * method} and {@linkplain #getURI() uri}.
 *
 */
public interface HttpRequest extends HttpMessage {

	/**
	 * Return the HTTP method of the request.
	 * 
	 * @return the HTTP method as an HttpMethod enum value, or {@code null} if
	 *         not resolvable (e.g. in case of a non-standard HTTP method)
	 * @see #getMethodValue()
	 * @see HttpMethod#resolve(String)
	 */
	@Nullable
	default HttpMethod getMethod() {
		return new HttpMethod(getMethodValue());
	}

	/**
	 * Return the HTTP method of the request as a String value.
	 * 
	 * @return the HTTP method as a plain String
	 * @since 5.0
	 * @see #getMethod()
	 */
	String getMethodValue();

	/**
	 * Return the URI of the request (including a query string if any, but only
	 * if it is well-formed for a URI representation).
	 * 
	 * @return the URI of the request (never {@code null})
	 */
	URI getURI();

}
