package com.wl4g.devops.components.tools.common.http.parse;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.wl4g.devops.components.tools.common.http.HttpHeaders;
import com.wl4g.devops.components.tools.common.http.MediaType;

/**
 * Abstract base class for most {@link GenericHttpMessageParser}
 * implementations.
 */
public abstract class AbstractGenericHttpMessageParser<T> extends AbstractHttpMessageParser<T>
		implements GenericHttpMessageParser<T> {

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with no
	 * supported media types.
	 * 
	 * @see #setSupportedMediaTypes
	 */
	protected AbstractGenericHttpMessageParser() {
	}

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with one
	 * supported media type.
	 * 
	 * @param supportedMediaType
	 *            the supported media type
	 */
	protected AbstractGenericHttpMessageParser(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with multiple
	 * supported media type.
	 * 
	 * @param supportedMediaTypes
	 *            the supported media types
	 */
	protected AbstractGenericHttpMessageParser(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return (type instanceof Class ? canRead((Class<?>) type, mediaType) : canRead(mediaType));
	}

	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		return canWrite(clazz, mediaType);
	}

	/**
	 * This implementation sets the default headers by calling
	 * {@link #addDefaultHeaders}, and then calls {@link #writeInternal}.
	 */
	public final void write(final T t, final Type type, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		final HttpHeaders headers = outputMessage.getHeaders();
		addDefaultHeaders(headers, t, contentType);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
				@Override
				public void writeTo(final OutputStream outputStream) throws IOException {
					writeInternal(t, type, new HttpOutputMessage() {
						@Override
						public OutputStream getBody() throws IOException {
							return outputStream;
						}

						@Override
						public HttpHeaders getHeaders() {
							return headers;
						}
					});
				}
			});
		} else {
			writeInternal(t, type, outputMessage);
			outputMessage.getBody().flush();
		}
	}

	@Override
	protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

		writeInternal(t, null, outputMessage);
	}

	/**
	 * Abstract template method that writes the actual body. Invoked from
	 * {@link #write}.
	 * 
	 * @param t
	 *            the object to write to the output message
	 * @param type
	 *            the type of object to write (may be {@code null})
	 * @param outputMessage
	 *            the HTTP output message to write to
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws HttpMessageNotWritableException
	 *             in case of conversion errors
	 */
	protected abstract void writeInternal(T t, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}
