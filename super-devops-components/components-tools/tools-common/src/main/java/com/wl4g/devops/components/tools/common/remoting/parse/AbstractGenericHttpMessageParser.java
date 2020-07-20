/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.components.tools.common.remoting.parse;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;

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
	protected AbstractGenericHttpMessageParser(HttpMediaType supportedMediaType) {
		super(supportedMediaType);
	}

	/**
	 * Construct an {@code AbstractGenericHttpMessageConverter} with multiple
	 * supported media type.
	 * 
	 * @param supportedMediaTypes
	 *            the supported media types
	 */
	protected AbstractGenericHttpMessageParser(HttpMediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, HttpMediaType mediaType) {
		return (type instanceof Class ? canRead((Class<?>) type, mediaType) : canRead(mediaType));
	}

	@Override
	public boolean canWrite(Type type, Class<?> clazz, HttpMediaType mediaType) {
		return canWrite(clazz, mediaType);
	}

	/**
	 * This implementation sets the default headers by calling
	 * {@link #addDefaultHeaders}, and then calls {@link #writeInternal}.
	 */
	public final void write(final T t, final Type type, HttpMediaType contentType, HttpOutputMessage outputMessage)
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