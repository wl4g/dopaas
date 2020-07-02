package com.wl4g.devops.coss.client.channel.netty.codec;

/**
 * Thrown by {@link HttpMessageCodec} implementations when the
 * {@link HttpMessageCodec#read} method fails.
 */
@SuppressWarnings("serial")
public class HttpMessageNotReadableException extends RuntimeException {

	/**
	 * Create a new HttpMessageNotReadableException.
	 * 
	 * @param msg
	 *            the detail message
	 */
	public HttpMessageNotReadableException(String msg) {
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
	public HttpMessageNotReadableException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
