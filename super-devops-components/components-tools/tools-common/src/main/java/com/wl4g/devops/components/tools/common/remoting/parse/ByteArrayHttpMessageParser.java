package com.wl4g.devops.components.tools.common.remoting.parse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.remoting.HttpMediaType;

/**
 * Implementation of {@link HttpMessageParser} that can read and write byte
 * arrays.
 *
 * <p>
 * By default, this converter supports all media types
 * ({@code &#42;&#47;&#42;}), and writes with a {@code Content-Type} of
 * {@code application/octet-stream}. This can be overridden by setting the
 * {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ByteArrayHttpMessageParser extends AbstractHttpMessageParser<byte[]> {

	/**
	 * Create a new instance of the {@code ByteArrayHttpMessageConverter}.
	 */
	public ByteArrayHttpMessageParser() {
		super(new HttpMediaType("application", "octet-stream"), HttpMediaType.ALL);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return byte[].class == clazz;
	}

	@Override
	public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException {
		long contentLength = inputMessage.getHeaders().getContentLength();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				contentLength >= 0 ? (int) contentLength : ByteStreamUtils.BUFFER_SIZE);
		ByteStreamUtils.copy(inputMessage.getBody(), bos);
		return bos.toByteArray();
	}

	@Override
	protected Long getContentLength(byte[] bytes, HttpMediaType contentType) {
		return (long) bytes.length;
	}

	@Override
	protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
		ByteStreamUtils.copy(bytes, outputMessage.getBody());
	}

}
