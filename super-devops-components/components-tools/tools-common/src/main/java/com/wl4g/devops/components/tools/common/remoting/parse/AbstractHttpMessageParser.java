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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;

/**
 * Abstract base class for most {@link HttpMessageParser} implementations.
 *
 * <p>
 * This base class adds support for setting supported {@code MediaTypes},
 * through the {@link #setSupportedMediaTypes(List) supportedMediaTypes} bean
 * property. It also adds support for {@code Content-Type} and
 * {@code Content-Length} when writing to output messages.
 */
public abstract class AbstractHttpMessageParser<T> implements HttpMessageParser<T> {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private List<HttpMediaType> supportedMediaTypes = Collections.emptyList();

	private Charset defaultCharset;

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with no supported media
	 * types.
	 * 
	 * @see #setSupportedMediaTypes
	 */
	protected AbstractHttpMessageParser() {
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with one supported
	 * media type.
	 * 
	 * @param supportedMediaType
	 *            the supported media type
	 */
	protected AbstractHttpMessageParser(HttpMediaType supportedMediaType) {
		setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with multiple supported
	 * media types.
	 * 
	 * @param supportedMediaTypes
	 *            the supported media types
	 */
	protected AbstractHttpMessageParser(HttpMediaType... supportedMediaTypes) {
		setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with a default charset
	 * and multiple supported media types.
	 * 
	 * @param defaultCharset
	 *            the default character set
	 * @param supportedMediaTypes
	 *            the supported media types
	 * @since 4.3
	 */
	protected AbstractHttpMessageParser(Charset defaultCharset, HttpMediaType... supportedMediaTypes) {
		this.defaultCharset = defaultCharset;
		setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
	}

	/**
	 * Set the list of {@link HttpMediaType} objects supported by this
	 * converter.
	 */
	public void setSupportedMediaTypes(List<HttpMediaType> supportedMediaTypes) {
		Assert2.notEmpty(supportedMediaTypes, "MediaType List must not be empty");
		this.supportedMediaTypes = new ArrayList<HttpMediaType>(supportedMediaTypes);
	}

	@Override
	public List<HttpMediaType> getSupportedMediaTypes() {
		return Collections.unmodifiableList(this.supportedMediaTypes);
	}

	/**
	 * Set the default character set, if any.
	 * 
	 * @since 4.3
	 */
	public void setDefaultCharset(Charset defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	/**
	 * Return the default character set, if any.
	 * 
	 * @since 4.3
	 */
	public Charset getDefaultCharset() {
		return this.defaultCharset;
	}

	/**
	 * This implementation checks if the given class is
	 * {@linkplain #supports(Class) supported}, and if the
	 * {@linkplain #getSupportedMediaTypes() supported media types}
	 * {@linkplain HttpMediaType#includes(HttpMediaType) include} the given
	 * media type.
	 */
	@Override
	public boolean canRead(Class<?> clazz, HttpMediaType mediaType) {
		return supports(clazz) && canRead(mediaType);
	}

	/**
	 * Returns {@code true} if any of the
	 * {@linkplain #setSupportedMediaTypes(List) supported} media types
	 * {@link HttpMediaType#includes(HttpMediaType) include} the given media
	 * type.
	 * 
	 * @param mediaType
	 *            the media type to read, can be {@code null} if not specified.
	 *            Typically the value of a {@code Content-Type} header.
	 * @return {@code true} if the supported media types include the media type,
	 *         or if the media type is {@code null}
	 */
	protected boolean canRead(HttpMediaType mediaType) {
		if (mediaType == null) {
			return true;
		}
		for (HttpMediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.includes(mediaType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This implementation checks if the given class is
	 * {@linkplain #supports(Class) supported}, and if the
	 * {@linkplain #getSupportedMediaTypes() supported} media types
	 * {@linkplain HttpMediaType#includes(HttpMediaType) include} the given
	 * media type.
	 */
	@Override
	public boolean canWrite(Class<?> clazz, HttpMediaType mediaType) {
		return supports(clazz) && canWrite(mediaType);
	}

	/**
	 * Returns {@code true} if the given media type includes any of the
	 * {@linkplain #setSupportedMediaTypes(List) supported media types}.
	 * 
	 * @param mediaType
	 *            the media type to write, can be {@code null} if not specified.
	 *            Typically the value of an {@code Accept} header.
	 * @return {@code true} if the supported media types are compatible with the
	 *         media type, or if the media type is {@code null}
	 */
	protected boolean canWrite(HttpMediaType mediaType) {
		if (mediaType == null || HttpMediaType.ALL.equals(mediaType)) {
			return true;
		}
		for (HttpMediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.isCompatibleWith(mediaType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This implementation simple delegates to
	 * {@link #readInternal(Class, HttpInputMessage)}. Future implementations
	 * might add some default behavior, however.
	 */
	@Override
	public final T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
		return readInternal(clazz, inputMessage);
	}

	/**
	 * This implementation sets the default headers by calling
	 * {@link #addDefaultHeaders}, and then calls {@link #writeInternal}.
	 */
	@Override
	public final void write(final T t, HttpMediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		final HttpHeaders headers = outputMessage.getHeaders();
		addDefaultHeaders(headers, t, contentType);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
				@Override
				public void writeTo(final OutputStream outputStream) throws IOException {
					writeInternal(t, new HttpOutputMessage() {
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
			writeInternal(t, outputMessage);
			outputMessage.getBody().flush();
		}
	}

	/**
	 * Add default headers to the output message.
	 * <p>
	 * This implementation delegates to {@link #getDefaultContentType(Object)}
	 * if a content type was not provided, set if necessary the default
	 * character set, calls {@link #getContentLength}, and sets the
	 * corresponding headers.
	 * 
	 * @since 4.2
	 */
	protected void addDefaultHeaders(HttpHeaders headers, T t, HttpMediaType contentType) throws IOException {
		if (headers.getContentType() == null) {
			HttpMediaType contentTypeToUse = contentType;
			if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
				contentTypeToUse = getDefaultContentType(t);
			} else if (HttpMediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
				HttpMediaType mediaType = getDefaultContentType(t);
				contentTypeToUse = (mediaType != null ? mediaType : contentTypeToUse);
			}
			if (contentTypeToUse != null) {
				if (contentTypeToUse.getCharset() == null) {
					Charset defaultCharset = getDefaultCharset();
					if (defaultCharset != null) {
						contentTypeToUse = new HttpMediaType(contentTypeToUse, defaultCharset);
					}
				}
				headers.setContentType(contentTypeToUse);
			}
		}
		if (headers.getContentLength() < 0 && !headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
			Long contentLength = getContentLength(t, headers.getContentType());
			if (contentLength != null) {
				headers.setContentLength(contentLength);
			}
		}
	}

	/**
	 * Returns the default content type for the given type. Called when
	 * {@link #write} is invoked without a specified content type parameter.
	 * <p>
	 * By default, this returns the first element of the
	 * {@link #setSupportedMediaTypes(List) supportedMediaTypes} property, if
	 * any. Can be overridden in subclasses.
	 * 
	 * @param t
	 *            the type to return the content type for
	 * @return the content type, or {@code null} if not known
	 */
	protected HttpMediaType getDefaultContentType(T t) throws IOException {
		List<HttpMediaType> mediaTypes = getSupportedMediaTypes();
		return (!mediaTypes.isEmpty() ? mediaTypes.get(0) : null);
	}

	/**
	 * Returns the content length for the given type.
	 * <p>
	 * By default, this returns {@code null}, meaning that the content length is
	 * unknown. Can be overridden in subclasses.
	 * 
	 * @param t
	 *            the type to return the content length for
	 * @return the content length, or {@code null} if not known
	 */
	protected Long getContentLength(T t, HttpMediaType contentType) throws IOException {
		return null;
	}

	/**
	 * Indicates whether the given class is supported by this converter.
	 * 
	 * @param clazz
	 *            the class to test for support
	 * @return {@code true} if supported; {@code false} otherwise
	 */
	protected abstract boolean supports(Class<?> clazz);

	/**
	 * Abstract template method that reads the actual object. Invoked from
	 * {@link #read}.
	 * 
	 * @param clazz
	 *            the type of object to return
	 * @param inputMessage
	 *            the HTTP input message to read from
	 * @return the converted object
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws HttpMessageNotReadableException
	 *             in case of conversion errors
	 */
	protected abstract T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException;

	/**
	 * Abstract template method that writes the actual body. Invoked from
	 * {@link #write}.
	 * 
	 * @param t
	 *            the object to write to the output message
	 * @param outputMessage
	 *            the HTTP output message to write to
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws HttpMessageNotWritableException
	 *             in case of conversion errors
	 */
	protected abstract void writeInternal(T t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}