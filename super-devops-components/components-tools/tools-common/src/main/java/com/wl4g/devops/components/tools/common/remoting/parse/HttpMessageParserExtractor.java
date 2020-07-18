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
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.reflect.ResolvableType;
import com.wl4g.devops.components.tools.common.remoting.ClientHttpResponse;
import com.wl4g.devops.components.tools.common.remoting.RestClient.ResponseProcessor;
import com.wl4g.devops.components.tools.common.remoting.exception.RestClientException;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;

/**
 * Response extractor that uses the given {@linkplain HttpMessageParser entity
 * converters} to convert the response into a type {@code T}.
 *
 * @param <T>
 *            the data type
 * @see RestTemplate
 */
public class HttpMessageParserExtractor<T> implements ResponseProcessor<T> {

	private final Type responseType;

	@Nullable
	private final Class<T> responseClass;

	private final List<HttpMessageParser<?>> messageParsers;

	private final Log log;

	/**
	 * Create a new instance of the {@code HttpMessageParserExtractor} with the
	 * given response type and message converters. The given converters must
	 * support the response type.
	 */
	public HttpMessageParserExtractor(Class<T> responseType, List<HttpMessageParser<?>> messageConverters) {
		this((Type) responseType, messageConverters);
	}

	/**
	 * Creates a new instance of the {@code HttpMessageParserExtractor} with the
	 * given response type and message converters. The given converters must
	 * support the response type.
	 */
	public HttpMessageParserExtractor(Type responseType, List<HttpMessageParser<?>> messageConverters) {
		this(responseType, messageConverters, LogFactory.getLog(HttpMessageParserExtractor.class));
	}

	@SuppressWarnings("unchecked")
	public HttpMessageParserExtractor(Type responseType, List<HttpMessageParser<?>> messageConverters, Log logger) {
		Assert2.notNull(responseType, "'responseType' must not be null");
		Assert2.notEmpty(messageConverters, "'messageConverters' must not be empty");
		this.responseType = responseType;
		this.responseClass = (responseType instanceof Class ? (Class<T>) responseType : null);
		this.messageParsers = messageConverters;
		this.log = logger;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public T extractData(ClientHttpResponse response) throws IOException {
		MessageBodyClientHttpResponseWrapper wrapper = new MessageBodyClientHttpResponseWrapper(response);
		if (!wrapper.hasMessageBody() || wrapper.hasEmptyMessageBody()) {
			return null;
		}
		HttpMediaType contentType = getContentType(wrapper);

		try {
			for (HttpMessageParser<?> parser : messageParsers) {
				if (parser instanceof GenericHttpMessageParser) {
					GenericHttpMessageParser<?> genericMessageConverter = (GenericHttpMessageParser<?>) parser;
					if (genericMessageConverter.canRead(responseType, null, contentType)) {
						if (log.isDebugEnabled()) {
							ResolvableType type = ResolvableType.forType(responseType);
							log.debug("Reading to [" + type + "]");
						}
						return (T) genericMessageConverter.read(responseType, null, wrapper);
					}
				}
				if (responseClass != null) {
					if (parser.canRead(responseClass, contentType)) {
						if (log.isDebugEnabled()) {
							String className = responseClass.getName();
							log.debug("Reading to [" + className + "] as \"" + contentType + "\"");
						}
						return (T) parser.read((Class) responseClass, wrapper);
					}
				}
			}
		} catch (IOException | HttpMessageNotReadableException ex) {
			throw new RestClientException(
					"Error while extracting response for type [" + responseType + "] and content type [" + contentType + "]", ex);
		}

		throw new RestClientException("Could not extract response: no suitable HttpMessageParser found " + "for response type ["
				+ responseType + "] and content type [" + contentType + "]");
	}

	/**
	 * Determine the Content-Type of the response based on the "Content-Type"
	 * header or otherwise default to
	 * {@link HttpMediaType#APPLICATION_OCTET_STREAM}.
	 * 
	 * @param response
	 *            the response
	 * @return the HttpMediaType, possibly {@code null}.
	 */
	@Nullable
	protected HttpMediaType getContentType(MessageBodyClientHttpResponseWrapper response) {
		HttpMediaType contentType = response.getHeaders().getContentType();
		if (contentType == null) {
			if (log.isTraceEnabled()) {
				log.trace("No content-type, using 'application/octet-stream'");
			}
			contentType = HttpMediaType.APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}

}