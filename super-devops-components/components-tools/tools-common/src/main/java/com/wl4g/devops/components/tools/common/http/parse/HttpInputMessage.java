package com.wl4g.devops.components.tools.common.http.parse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an HTTP input message, consisting of {@linkplain #getHeaders()
 * headers} and a readable {@linkplain #getBody() body}.
 *
 * <p>
 * Typically implemented by an HTTP request handle on the server side, or an
 * HTTP response handle on the client side.
 */
public interface HttpInputMessage extends HttpMessage {

	/**
	 * Return the body of the message as an input stream.
	 * 
	 * @return the input stream body (never {@code null})
	 * @throws IOException
	 *             in case of I/O errors
	 */
	InputStream getBody() throws IOException;

}
