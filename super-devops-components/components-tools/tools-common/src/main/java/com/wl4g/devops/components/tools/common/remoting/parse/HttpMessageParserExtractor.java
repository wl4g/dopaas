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
import com.wl4g.devops.components.tools.common.remoting.HttpMediaType;
import com.wl4g.devops.components.tools.common.remoting.RestClientException;
import com.wl4g.devops.components.tools.common.remoting.RestClient.ResponseExtractor;

/**
 * Response extractor that uses the given {@linkplain HttpMessageParser entity
 * converters} to convert the response into a type {@code T}.
 *
 * @param <T>
 *            the data type
 * @see RestTemplate
 */
public class HttpMessageParserExtractor<T> implements ResponseExtractor<T> {

	private final Type responseType;

	@Nullable
	private final Class<T> responseClass;

	private final List<HttpMessageParser<?>> messageConverters;

	private final Log logger;

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
		this.messageConverters = messageConverters;
		this.logger = logger;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public T extractData(ClientHttpResponse response) throws IOException {
		MessageBodyClientHttpResponseWrapper responseWrapper = new MessageBodyClientHttpResponseWrapper(response);
		if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
			return null;
		}
		HttpMediaType contentType = getContentType(responseWrapper);

		try {
			for (HttpMessageParser<?> messageConverter : this.messageConverters) {
				if (messageConverter instanceof GenericHttpMessageParser) {
					GenericHttpMessageParser<?> genericMessageConverter = (GenericHttpMessageParser<?>) messageConverter;
					if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
						if (logger.isDebugEnabled()) {
							ResolvableType resolvableType = ResolvableType.forType(this.responseType);
							logger.debug("Reading to [" + resolvableType + "]");
						}
						return (T) genericMessageConverter.read(this.responseType, null, responseWrapper);
					}
				}
				if (this.responseClass != null) {
					if (messageConverter.canRead(this.responseClass, contentType)) {
						if (logger.isDebugEnabled()) {
							String className = this.responseClass.getName();
							logger.debug("Reading to [" + className + "] as \"" + contentType + "\"");
						}
						return (T) messageConverter.read((Class) this.responseClass, responseWrapper);
					}
				}
			}
		} catch (IOException | HttpMessageNotReadableException ex) {
			throw new RestClientException(
					"Error while extracting response for type [" + this.responseType + "] and content type [" + contentType + "]",
					ex);
		}

		throw new RestClientException("Could not extract response: no suitable HttpMessageParser found " + "for response type ["
				+ this.responseType + "] and content type [" + contentType + "]");
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
			if (logger.isTraceEnabled()) {
				logger.trace("No content-type, using 'application/octet-stream'");
			}
			contentType = HttpMediaType.APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}

}