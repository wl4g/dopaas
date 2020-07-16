package com.wl4g.devops.components.tools.common.remoting.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import com.wl4g.devops.components.tools.common.remoting.ClientHttpResponse;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;

/**
 * Implementation of {@link Netty4ClientHttpResponse} that can not only check if
 * the response has a message body, but also if its length is 0 (i.e. empty) by
 * actually reading the input stream.
 *
 * @see <a href="http://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230
 *      Section 3.3.3</a>
 */
class MessageBodyClientHttpResponseWrapper implements ClientHttpResponse {

	private final ClientHttpResponse response;

	private PushbackInputStream pushbackInputStream;

	public MessageBodyClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
		this.response = response;
	}

	/**
	 * Indicates whether the response has a message body.
	 * <p>
	 * Implementation returns {@code false} for:
	 * <ul>
	 * <li>a response status of {@code 1XX}, {@code 204} or {@code 304}</li>
	 * <li>a {@code Content-Length} header of {@code 0}</li>
	 * </ul>
	 * 
	 * @return {@code true} if the response has a message body, {@code false}
	 *         otherwise
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public boolean hasMessageBody() throws IOException {
		try {
			HttpStatus status = getStatusCode();
			if (status != null && status.is1xxInformational() || status == HttpStatus.NO_CONTENT
					|| status == HttpStatus.NOT_MODIFIED) {
				return false;
			}
		} catch (IllegalArgumentException ex) {
			// Ignore - unknown HTTP status code...
		}
		if (getHeaders().getContentLength() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Indicates whether the response has an empty message body.
	 * <p>
	 * Implementation tries to read the first bytes of the response stream:
	 * <ul>
	 * <li>if no bytes are available, the message body is empty</li>
	 * <li>otherwise it is not empty and the stream is reset to its start for
	 * further reading</li>
	 * </ul>
	 * 
	 * @return {@code true} if the response has a zero-length message body,
	 *         {@code false} otherwise
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public boolean hasEmptyMessageBody() throws IOException {
		InputStream body = this.response.getBody();
		if (body == null) {
			return true;
		} else if (body.markSupported()) {
			body.mark(1);
			if (body.read() == -1) {
				return true;
			} else {
				body.reset();
				return false;
			}
		} else {
			this.pushbackInputStream = new PushbackInputStream(body);
			int b = this.pushbackInputStream.read();
			if (b == -1) {
				return true;
			} else {
				this.pushbackInputStream.unread(b);
				return false;
			}
		}
	}

	public HttpHeaders getHeaders() {
		return this.response.getHeaders();
	}

	public InputStream getBody() throws IOException {
		return (this.pushbackInputStream != null ? this.pushbackInputStream : this.response.getBody());
	}

	public HttpStatus getStatusCode() throws IOException {
		return this.response.getStatusCode();
	}

	public int getRawStatusCode() throws IOException {
		return this.response.getRawStatusCode();
	}

	public String getStatusText() throws IOException {
		return this.response.getStatusText();
	}

	public void close() {
		this.response.close();
	}

}