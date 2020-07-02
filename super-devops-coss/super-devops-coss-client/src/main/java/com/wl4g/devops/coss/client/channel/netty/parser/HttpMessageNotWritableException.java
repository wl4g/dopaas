package com.wl4g.devops.coss.client.channel.netty.parser;

/**
 * Thrown by {@link HttpMessageParser} implementations when the
 * {@link HttpMessageParser#read} method fails.
 */
@SuppressWarnings("serial")
public class HttpMessageNotWritableException extends RuntimeException {

	/**
	 * Create a new HttpMessageNotReadableException.
	 * 
	 * @param msg
	 *            the detail message
	 */
	public HttpMessageNotWritableException(String msg) {
		super(msg);
	}

	/**
	 * Create a new HttpMessageNotReadableException.
	 * 
	 * @param msg
	 *            the detail message
	 * @param cause
	 *            the root cause (if any)
	 */
	public HttpMessageNotWritableException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
