package com.wl4g.devops.components.tools.common.remoting;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Common base class for exceptions that contain actual HTTP response data.
 *
 */
public class RestClientResponseException extends RestClientException {
	private static final long serialVersionUID = -8803556342728481792L;

	private static final String DEFAULT_CHARSET = "UTF-8";

	private final int rawStatusCode;

	private final String statusText;

	private final byte[] responseBody;

	private final HttpHeaders responseHeaders;

	private final String responseCharset;

	/**
	 * Construct a new instance of with the given response data.
	 * 
	 * @param statusCode
	 *            the raw status code value
	 * @param statusText
	 *            the status text
	 * @param responseHeaders
	 *            the response headers (may be {@code null})
	 * @param responseBody
	 *            the response body content (may be {@code null})
	 * @param responseCharset
	 *            the response body charset (may be {@code null})
	 */
	public RestClientResponseException(String message, int statusCode, String statusText, HttpHeaders responseHeaders,
			byte[] responseBody, Charset responseCharset) {

		super(message);
		this.rawStatusCode = statusCode;
		this.statusText = statusText;
		this.responseHeaders = responseHeaders;
		this.responseBody = (responseBody != null ? responseBody : new byte[0]);
		this.responseCharset = (responseCharset != null ? responseCharset.name() : DEFAULT_CHARSET);
	}

	/**
	 * Return the raw HTTP status code value.
	 */
	public int getRawStatusCode() {
		return this.rawStatusCode;
	}

	/**
	 * Return the HTTP status text.
	 */
	public String getStatusText() {
		return this.statusText;
	}

	/**
	 * Return the HTTP response headers.
	 */
	public HttpHeaders getResponseHeaders() {
		return this.responseHeaders;
	}

	/**
	 * Return the response body as a byte array.
	 */
	public byte[] getResponseBodyAsByteArray() {
		return this.responseBody;
	}

	/**
	 * Return the response body as a string.
	 */
	public String getResponseBodyAsString() {
		try {
			return new String(this.responseBody, this.responseCharset);
		} catch (UnsupportedEncodingException ex) {
			// should not occur
			throw new IllegalStateException(ex);
		}
	}

}
