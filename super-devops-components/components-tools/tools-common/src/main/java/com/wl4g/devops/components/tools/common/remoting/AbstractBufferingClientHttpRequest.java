package com.wl4g.devops.components.tools.common.remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;

/**
 * Base implementation of {@link ClientHttpRequest} that buffers output in a
 * byte array before sending it over the wire.
 */
abstract class AbstractBufferingClientHttpRequest extends AbstractClientHttpRequest {

	private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

	@Override
	protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
		return this.bufferedOutput;
	}

	@Override
	protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
		byte[] bytes = this.bufferedOutput.toByteArray();
		if (headers.getContentLength() < 0) {
			headers.setContentLength(bytes.length);
		}
		ClientHttpResponse result = executeInternal(headers, bytes);
		this.bufferedOutput = null;
		return result;
	}

	/**
	 * Abstract template method that writes the given headers and content to the
	 * HTTP request.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @param bufferedOutput
	 *            the body content
	 * @return the response object for the executed request
	 */
	protected abstract ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException;

}
