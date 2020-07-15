package com.wl4g.devops.components.tools.common.remoting;

import java.io.Closeable;
import java.io.IOException;

import com.wl4g.devops.components.tools.common.remoting.parse.HttpInputMessage;

/**
 * Represents a client-side HTTP response. Obtained via an calling of the
 * {@link ClientHttpRequest#execute()}.
 *
 * <p>
 * A {@code ClientHttpResponse} must be {@linkplain #close() closed}, typically
 * in a {@code finally} block.
 *
 */
public interface ClientHttpResponse extends HttpInputMessage, Closeable {

	/**
	 * Return the HTTP status code as an {@link HttpStatus} enum value.
	 * 
	 * @return the HTTP status as an HttpStatus enum value (never {@code null})
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws IllegalArgumentException
	 *             in case of an unknown HTTP status code
	 * @since #getRawStatusCode()
	 * @see HttpStatus#valueOf(int)
	 */
	HttpStatus getStatusCode() throws IOException;

	/**
	 * Return the HTTP status code (potentially non-standard and not resolvable
	 * through the {@link HttpStatus} enum) as an integer.
	 * 
	 * @return the HTTP status as an integer value
	 * @throws IOException
	 *             in case of I/O errors
	 * @since 3.1.1
	 * @see #getStatusCode()
	 * @see HttpStatus#resolve(int)
	 */
	int getRawStatusCode() throws IOException;

	/**
	 * Return the HTTP status text of the response.
	 * 
	 * @return the HTTP status text
	 * @throws IOException
	 *             in case of I/O errors
	 */
	String getStatusText() throws IOException;

	/**
	 * Close this response, freeing any resources created.
	 */
	@Override
	void close();

}
