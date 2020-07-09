package com.wl4g.devops.components.tools.common.http;

import java.io.IOException;
import java.io.OutputStream;

import com.wl4g.devops.components.tools.common.lang.Assert2;

/**
 * Abstract base for {@link ClientHttpRequest} that makes sure that headers and
 * body are not written multiple times.
 */
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {

	private final HttpHeaders headers = new HttpHeaders();

	private boolean executed = false;

	@Override
	public final HttpHeaders getHeaders() {
		return (this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
	}

	@Override
	public final OutputStream getBody() throws IOException {
		Assert2NotExecuted();
		return getBodyInternal(this.headers);
	}

	@Override
	public final ClientHttpResponse execute() throws IOException {
		Assert2NotExecuted();
		ClientHttpResponse result = executeInternal(this.headers);
		this.executed = true;
		return result;
	}

	/**
	 * Assert2 that this request has not been {@linkplain #execute() executed}
	 * yet.
	 * 
	 * @throws IllegalStateException
	 *             if this request has been executed
	 */
	protected void Assert2NotExecuted() {
		Assert2.state(!this.executed, "ClientHttpRequest already executed");
	}

	/**
	 * Abstract template method that returns the body.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @return the body output stream
	 */
	protected abstract OutputStream getBodyInternal(HttpHeaders headers) throws IOException;

	/**
	 * Abstract template method that writes the given headers and content to the
	 * HTTP request.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @return the response object for the executed request
	 */
	protected abstract ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException;

}