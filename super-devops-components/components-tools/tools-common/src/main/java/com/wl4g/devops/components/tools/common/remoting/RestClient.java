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
package com.wl4g.devops.components.tools.common.remoting;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import com.wl4g.devops.components.tools.common.reflect.ParameterizedTypeReference;
import com.wl4g.devops.components.tools.common.remoting.exception.RestClientException;
import com.wl4g.devops.components.tools.common.remoting.parse.AllEncompassingFormHttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.parse.ByteArrayHttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.parse.GenericHttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.parse.HttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.parse.HttpMessageParserExtractor;
import com.wl4g.devops.components.tools.common.remoting.parse.MappingJackson2HttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.parse.ResourceHttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.parse.StringHttpMessageParser;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;
import com.wl4g.devops.components.tools.common.remoting.uri.AbstractUriTemplateHandler;
import com.wl4g.devops.components.tools.common.remoting.uri.DefaultResponseErrorHandler;
import com.wl4g.devops.components.tools.common.remoting.uri.DefaultUriBuilderFactory;
import com.wl4g.devops.components.tools.common.remoting.uri.ResourceAccessException;
import com.wl4g.devops.components.tools.common.remoting.uri.UriTemplateHandler;

import com.wl4g.devops.components.tools.common.remoting.uri.DefaultUriBuilderFactory.EncodingMode;
import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.CollectionUtils2;
import com.wl4g.devops.components.tools.common.lang.ClassUtils2;

import static io.netty.handler.codec.http.HttpMethod.*;
import io.netty.handler.codec.http.HttpMethod;

/**
 * Synchronous client to perform HTTP requests, exposing a simple, template
 * method API over underlying HTTP client libraries such as the JDK
 * {@code HttpURLConnection}, Apache HttpComponents, and others.
 *
 * <p>
 * The {@link RestClient} offers templates for common scenarios by HTTP method,
 * in addition to the generalized {@code exchange} and {@code execute} methods
 * that support of less frequent cases.
 *
 * @see HttpMessageParser
 * @see RequestProcessor
 * @see ResponseProcessor
 * @see ResponseErrorHandler
 */
public class RestClient {

	final protected Log log = LogFactory.getLog(getClass());

	final private ClientHttpRequestFactory requestFactory;
	final private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(4);
	final private ResponseProcessor<HttpHeaders> headersExtractor = new ResponseHeadersProcessor();
	final private List<HttpMessageParser<?>> messageParsers = new ArrayList<>(4);
	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
	private UriTemplateHandler uriTemplateHandler;

	/**
	 * Create a new instance of the {@link RestClient} using default settings.
	 * Default {@link HttpMessageParser} are initialized.
	 */
	public RestClient() {
		this(false);
	}

	/**
	 * Create a new instance of the {@link RestClient} using default settings.
	 * Default {@link HttpMessageParser} are initialized.
	 * 
	 * @param debug
	 */
	@SuppressWarnings("serial")
	public RestClient(boolean debug) {
		this(new Netty4ClientHttpRequestFactory(debug), new ArrayList<HttpMessageParser<?>>() {
			{
				add(new AllEncompassingFormHttpMessageParser());
				add(new ByteArrayHttpMessageParser());
				add(new StringHttpMessageParser());
				add(new ResourceHttpMessageParser(false));
				if (jackson2Present) {
					add(new MappingJackson2HttpMessageParser());
				}
			}
		});
	}

	/**
	 * Create a new instance of the {@link RestClient} using the given list of
	 * {@link HttpMessageParser} to use.
	 * 
	 * @param requestFactory
	 *            the HTTP request factory to use
	 * @param messageParsers
	 *            the list of {@link HttpMessageParser} to use
	 */
	public RestClient(ClientHttpRequestFactory requestFactory, List<HttpMessageParser<?>> messageParsers) {
		notNullOf(requestFactory, "requestFactory");
		this.requestFactory = requestFactory;
		notEmpty(messageParsers, "At least one HttpMessageParser is required");
		this.messageParsers.addAll(messageParsers);
		this.uriTemplateHandler = new DefaultUriBuilderFactory(EncodingMode.URI_COMPONENT);
	}

	/**
	 * Set the message body parsers to use.
	 * <p>
	 * These converters are used to convert from and to HTTP requests and
	 * responses.
	 */
	public RestClient setMessageParsers(List<HttpMessageParser<?>> messageParsers) {
		notEmpty(messageParsers, "At least one HttpMessageParser is required");
		// Take getMessageParsers() List as-is when passed in here
		if (this.messageParsers != messageParsers) {
			this.messageParsers.clear();
			this.messageParsers.addAll(messageParsers);
		}
		return this;
	}

	/**
	 * Return the list of message body parsers.
	 * <p>
	 * The returned {@link List} is active and may get appended to.
	 */
	public List<HttpMessageParser<?>> getMessageParsers() {
		return this.messageParsers;
	}

	/**
	 * Set the request interceptors that this accessor should use.
	 * <p>
	 * The interceptors will get immediately sorted according to their
	 * {@linkplain AnnotationAwareOrderComparator#sort(List) order}.
	 * 
	 * @see #getRequestFactory()
	 * @see AnnotationAwareOrderComparator
	 */
	public RestClient setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
		// Take getInterceptors() List as-is when passed in here
		if (this.interceptors != interceptors) {
			this.interceptors.clear();
			this.interceptors.addAll(interceptors);
		}
		return this;
	}

	/**
	 * Get the request interceptors that this accessor uses.
	 * <p>
	 * The returned {@link List} is active and may be modified. Note, however,
	 * that the interceptors will not be resorted according to their
	 * {@linkplain AnnotationAwareOrderComparator#sort(List) order} before the
	 * {@link ClientHttpRequestFactory} is built.
	 */
	public List<ClientHttpRequestInterceptor> getInterceptors() {
		return this.interceptors;
	}

	/**
	 * Set the error handler.
	 * <p>
	 * By default, RestClient uses a {@link DefaultResponseErrorHandler}.
	 */
	public RestClient setErrorHandler(ResponseErrorHandler errorHandler) {
		notNull(errorHandler, "ResponseErrorHandler must not be null");
		this.errorHandler = errorHandler;
		return this;
	}

	/**
	 * Return the error handler.
	 */
	public ResponseErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	/**
	 * Configure default URI variable values. This is a shortcut for:
	 * 
	 * <pre class="code">
	 * DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
	 * handler.setDefaultUriVariables(...);
	 *
	 * RestClient restTemplate = new RestClient();
	 * restTemplate.setUriTemplateHandler(handler);
	 * </pre>
	 * 
	 * @param uriVars
	 *            the default URI variable values
	 * @since 4.3
	 */
	public RestClient setDefaultUriVariables(Map<String, ?> uriVars) {
		if (this.uriTemplateHandler instanceof DefaultUriBuilderFactory) {
			((DefaultUriBuilderFactory) this.uriTemplateHandler).setDefaultUriVariables(uriVars);
		} else if (this.uriTemplateHandler instanceof AbstractUriTemplateHandler) {
			((AbstractUriTemplateHandler) this.uriTemplateHandler).setDefaultUriVariables(uriVars);
		} else {
			throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
		}
		return this;
	}

	/**
	 * Configure a strategy for expanding URI templates.
	 * <p>
	 * By default, {@link DefaultUriBuilderFactory} is used and for backwards
	 * compatibility, the encoding mode is set to
	 * {@link EncodingMode#URI_COMPONENT URI_COMPONENT}. As of 5.0.8, prefer
	 * using {@link EncodingMode#TEMPLATE_AND_VALUES TEMPLATE_AND_VALUES}.
	 * <p>
	 * 
	 * @param handler
	 *            the URI template handler to use
	 */
	public RestClient setUriTemplateHandler(UriTemplateHandler handler) {
		notNull(handler, "UriTemplateHandler must not be null");
		this.uriTemplateHandler = handler;
		return this;
	}

	/**
	 * Return the configured URI template handler.
	 */
	public UriTemplateHandler getUriTemplateHandler() {
		return this.uriTemplateHandler;
	}

	// Request and response handler.

	/**
	 * Handle the given response, performing appropriate logging and invoking
	 * the {@link ResponseErrorHandler} if necessary.
	 * <p>
	 * Can be overridden in subclasses.
	 * 
	 * @param url
	 *            the fully-expanded URL to connect to
	 * @param method
	 *            the HTTP method to execute (GET, POST, etc.)
	 * @param response
	 *            the resulting {@link Netty4ClientHttpResponse}
	 * @throws IOException
	 *             if propagated from {@link ResponseErrorHandler}
	 * @since 4.1.6
	 * @see #setErrorHandler
	 */
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		ResponseErrorHandler errorHandler = getErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (log.isDebugEnabled()) {
			try {
				int code = response.getRawStatusCode();
				HttpStatus status = HttpStatus.resolve(code);
				log.debug("Response " + (status != null ? status : code));
			} catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(url, method, response);
		}
	}

	/**
	 * Return a {@code RequestCallback} that sets the request {@code Accept}
	 * header based on the given response type, cross-checked against the
	 * configured message converters.
	 */
	public <T> RequestProcessor acceptHeaderRequestProcessor(Class<T> responseType) {
		return new AcceptHeaderRequestProcessor(responseType);
	}

	/**
	 * Return a {@code RequestCallback} implementation that writes the given
	 * object to the request stream.
	 */
	public <T> RequestProcessor requestEntityProcessor(@Nullable Object requestBody) {
		return new HttpEntityRequestProcessor(requestBody);
	}

	/**
	 * Return a {@code RequestCallback} implementation that:
	 * <ol>
	 * <li>Sets the request {@code Accept} header based on the given response
	 * type, cross-checked against the configured message converters.
	 * <li>Writes the given object to the request stream.
	 * </ol>
	 */
	public <T> RequestProcessor requestEntityProcessor(@Nullable Object requestBody, Type responseType) {
		return new HttpEntityRequestProcessor(requestBody, responseType);
	}

	/**
	 * Return a {@code ResponseExtractor} that prepares a
	 * {@link HttpResponseEntity}.
	 */
	public <T> ResponseProcessor<HttpResponseEntity<T>> responseEntityProcessor(Type responseType) {
		return new ResponseEntityProcessor<>(responseType);
	}

	/**
	 * Return a response extractor for {@link HttpHeaders}.
	 */
	protected ResponseProcessor<HttpHeaders> responseHeadersProcessor() {
		return this.headersExtractor;
	}

	private static <T> T nonNull(@Nullable T result) {
		state(result != null, "No result");
		return result;
	}

	/**
	 * Create a new {@link ClientHttpRequest} via this template's
	 * {@link ClientHttpRequestFactory}.
	 * 
	 * @param url
	 *            the URL to connect to
	 * @param method
	 *            the HTTP method to execute (GET, POST, etc)
	 * @param requestHeaders
	 *            Request headers
	 * @return the created request
	 * @throws IOException
	 *             in case of I/O errors
	 * @see #getRequestFactory()
	 * @see ClientHttpRequestFactory#createRequest(URI, HttpMethod)
	 */
	private ClientHttpRequest createRequest(URI url, HttpMethod method, HttpHeaders requestHeaders) throws IOException {
		ClientHttpRequest request = getRequestFactory().createRequest(url, method, requestHeaders);
		if (log.isDebugEnabled()) {
			log.debug("HTTP " + method.name() + " " + url);
		}
		return request;
	}

	/**
	 * Overridden to expose an {@link InterceptingClientHttpRequestFactory} if
	 * necessary.
	 * 
	 * @see #getInterceptors()
	 */
	private ClientHttpRequestFactory getRequestFactory() {
		List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
		if (!CollectionUtils2.isEmpty(interceptors)) {
			return new InterceptingClientHttpRequestFactory(requestFactory, interceptors);
		} else {
			return requestFactory;
		}
	}

	// GET

	/**
	 * Retrieve a representation by doing a GET on the specified URL. The
	 * response (if any) is converted and returned.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * 
	 * @param url
	 *            the URL
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand the template
	 * @return the converted object
	 */
	@Nullable
	public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
		RequestProcessor requestProcessor = acceptHeaderRequestProcessor(responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageParsers(),
				log);
		return execute(url, GET, requestProcessor, responseExtractor, uriVariables);
	}

	/**
	 * Retrieve a representation by doing a GET on the URI template. The
	 * response (if any) is converted and returned.
	 * <p>
	 * URI Template variables are expanded using the given map.
	 * 
	 * @param url
	 *            the URL
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the map containing variables for the URI template
	 * @return the converted object
	 */
	@Nullable
	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
		RequestProcessor requestProcessor = acceptHeaderRequestProcessor(responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageParsers(),
				log);
		return execute(url, GET, requestProcessor, responseExtractor, uriVariables);
	}

	/**
	 * Retrieve a representation by doing a GET on the URL . The response (if
	 * any) is converted and returned.
	 * 
	 * @param url
	 *            the URL
	 * @param responseType
	 *            the type of the return value
	 * @return the converted object
	 */
	@Nullable
	public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
		RequestProcessor requestProcessor = acceptHeaderRequestProcessor(responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageParsers(),
				log);
		return execute(url, GET, requestProcessor, responseExtractor);
	}

	/**
	 * Retrieve an entity by doing a GET on the specified URL. The response is
	 * converted and stored in an {@link HttpResponseEntity}.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * 
	 * @param url
	 *            the URL
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand the template
	 * @return the entity
	 * @since 3.0.2
	 */
	public <T> HttpResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestProcessor requestProcessor = acceptHeaderRequestProcessor(responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, GET, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Retrieve a representation by doing a GET on the URI template. The
	 * response is converted and stored in an {@link HttpResponseEntity}.
	 * <p>
	 * URI Template variables are expanded using the given map.
	 * 
	 * @param url
	 *            the URL
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the map containing variables for the URI template
	 * @return the converted object
	 * @since 3.0.2
	 */
	public <T> HttpResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestProcessor requestProcessor = acceptHeaderRequestProcessor(responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, GET, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Retrieve a representation by doing a GET on the URL . The response is
	 * converted and stored in an {@link HttpResponseEntity}.
	 * 
	 * @param url
	 *            the URL
	 * @param responseType
	 *            the type of the return value
	 * @return the converted object
	 * @since 3.0.2
	 */
	public <T> HttpResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		RequestProcessor requestProcessor = acceptHeaderRequestProcessor(responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, GET, requestProcessor, responseExtractor));
	}

	// POST

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the representation found in the response.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand the template
	 * @return the converted object
	 * @see HttpEntity
	 */
	@Nullable
	public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(request, responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageParsers(),
				log);
		return execute(url, POST, requestProcessor, responseExtractor, uriVariables);
	}

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the representation found in the response.
	 * <p>
	 * URI Template variables are expanded using the given map.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand the template
	 * @return the converted object
	 * @see HttpEntity
	 */
	@Nullable
	public <T> T postForObject(String url, @Nullable Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(request, responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageParsers(),
				log);
		return execute(url, POST, requestProcessor, responseExtractor, uriVariables);
	}

	/**
	 * Create a new resource by POSTing the given object to the URL, and returns
	 * the representation found in the response.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @return the converted object
	 * @see HttpEntity
	 */
	@Nullable
	public <T> T postForObject(URI url, @Nullable Object request, Class<T> responseType) throws RestClientException {
		RequestProcessor requestProcessor = requestEntityProcessor(request, responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageParsers());
		return execute(url, POST, requestProcessor, responseExtractor);
	}

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the response as {@link HttpResponseEntity}.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @param uriVariables
	 *            the variables to expand the template
	 * @return the converted object
	 * @since 3.0.2
	 * @see HttpEntity
	 */
	public <T> HttpResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType,
			Object... uriVariables) throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(request, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, POST, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the response as {@link HttpEntity}.
	 * <p>
	 * URI Template variables are expanded using the given map.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @param uriVariables
	 *            the variables to expand the template
	 * @return the converted object
	 * @since 3.0.2
	 * @see HttpEntity
	 */
	public <T> HttpResponseEntity<T> postForEntity(String url, @Nullable Object request, Class<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException {
		RequestProcessor requestProcessor = requestEntityProcessor(request, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, POST, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Create a new resource by POSTing the given object to the URL, and returns
	 * the response as {@link HttpResponseEntity}.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @return the converted object
	 * @since 3.0.2
	 * @see HttpEntity
	 */
	public <T> HttpResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType)
			throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(request, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, POST, requestProcessor, responseExtractor));
	}

	// PUT

	/**
	 * Create or update a resource by PUTting the given object to the URI.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be PUT (may be {@code null})
	 * @param uriVariables
	 *            the variables to expand the template
	 * @see HttpEntity
	 */
	public void put(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(request);
		execute(url, PUT, requestProcessor, null, uriVariables);
	}

	/**
	 * Creates a new resource by PUTting the given object to URI template.
	 * <p>
	 * URI Template variables are expanded using the given map.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be PUT (may be {@code null})
	 * @param uriVariables
	 *            the variables to expand the template
	 * @see HttpEntity
	 */
	public void put(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(request);
		execute(url, PUT, requestProcessor, null, uriVariables);
	}

	/**
	 * Creates a new resource by PUTting the given object to URL.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be PUT (may be {@code null})
	 * @see HttpEntity
	 */
	public void put(URI url, @Nullable Object request) throws RestClientException {
		RequestProcessor requestProcessor = requestEntityProcessor(request);
		execute(url, PUT, requestProcessor, null);
	}

	// DELETE

	/**
	 * Delete the resources at the specified URI.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * 
	 * @param url
	 *            the URL
	 * @param uriVariables
	 *            the variables to expand in the template
	 */
	public void delete(String url, Object... uriVariables) throws RestClientException {
		execute(url, DELETE, null, null, uriVariables);
	}

	/**
	 * Delete the resources at the specified URI.
	 * <p>
	 * URI Template variables are expanded using the given map.
	 *
	 * @param url
	 *            the URL
	 * @param uriVariables
	 *            the variables to expand the template
	 */
	public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
		execute(url, DELETE, null, null, uriVariables);
	}

	/**
	 * Delete the resources at the specified URL.
	 * 
	 * @param url
	 *            the URL
	 */
	public void delete(URI url) throws RestClientException {
		execute(url, DELETE, null, null);
	}

	// exchange

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param requestEntity
	 *            the entity (headers and/or body) to write to the request may
	 *            be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand in the template
	 * @return the response as entity
	 * @since 3.0.2
	 */
	public <T> HttpResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, method, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}.
	 * <p>
	 * URI Template variables are expanded using the given URI variables, if
	 * any.
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param requestEntity
	 *            the entity (headers and/or body) to write to the request (may
	 *            be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand in the template
	 * @return the response as entity
	 * @since 3.0.2
	 */
	public <T> HttpResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, method, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}.
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param requestEntity
	 *            the entity (headers and/or body) to write to the request (may
	 *            be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @return the response as entity
	 * @since 3.0.2
	 */
	public <T> HttpResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(execute(url, method, requestProcessor, responseExtractor));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}. The given {@link ParameterizedTypeReference}
	 * is used to pass generic type information:
	 * 
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {
	 * };
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;, GET, null, myBean);
	 * </pre>
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param requestEntity
	 *            the entity (headers and/or body) to write to the request (may
	 *            be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand in the template
	 * @return the response as entity
	 * @since 3.2
	 */
	public <T> HttpResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, type);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(type);
		return nonNull(execute(url, method, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}. The given {@link ParameterizedTypeReference}
	 * is used to pass generic type information:
	 * 
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {
	 * };
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;, GET, null, myBean);
	 * </pre>
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param requestEntity
	 *            the entity (headers and/or body) to write to the request (may
	 *            be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @param uriVariables
	 *            the variables to expand in the template
	 * @return the response as entity
	 * @since 3.2
	 */
	public <T> HttpResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, type);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(type);
		return nonNull(execute(url, method, requestProcessor, responseExtractor, uriVariables));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}. The given {@link ParameterizedTypeReference}
	 * is used to pass generic type information:
	 * 
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {
	 * };
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;, GET, null, myBean);
	 * </pre>
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param requestEntity
	 *            the entity (headers and/or body) to write to the request (may
	 *            be {@code null})
	 * @param responseType
	 *            the type of the return value
	 * @return the response as entity
	 * @since 3.2
	 */
	public <T> HttpResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, type);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(type);
		return nonNull(execute(url, method, requestProcessor, responseExtractor));
	}

	/**
	 * Execute the request specified in the given {@link HttpRequestEntity} and
	 * return the response as {@link HttpResponseEntity}. Typically used in
	 * combination with the static builder methods on {@code RequestEntity}, for
	 * instance:
	 * 
	 * <pre class="code">
	 * MyRequest body = ...
	 * RequestEntity request = RequestEntity.post(new URI(&quot;http://example.com/foo&quot;)).accept(MediaType.APPLICATION_JSON).body(body);
	 * ResponseEntity&lt;MyResponse&gt; response = template.exchange(request, MyResponse.class);
	 * </pre>
	 * 
	 * @param requestEntity
	 *            the entity to write to the request
	 * @param responseType
	 *            the type of the return value
	 * @return the response as entity
	 * @since 4.1
	 */
	public <T> HttpResponseEntity<T> exchange(HttpRequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {

		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, responseType);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(responseType);
		return nonNull(doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestProcessor, responseExtractor));
	}

	/**
	 * Execute the request specified in the given {@link HttpRequestEntity} and
	 * return the response as {@link HttpResponseEntity}. The given
	 * {@link ParameterizedTypeReference} is used to pass generic type
	 * information:
	 * 
	 * <pre class="code">
	 * MyRequest body = ...
	 * RequestEntity request = RequestEntity.post(new URI(&quot;http://example.com/foo&quot;)).accept(MediaType.APPLICATION_JSON).body(body);
	 * ParameterizedTypeReference&lt;List&lt;MyResponse&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyResponse&gt;&gt;() {};
	 * ResponseEntity&lt;List&lt;MyResponse&gt;&gt; response = template.exchange(request, myBean);
	 * </pre>
	 * 
	 * @param requestEntity
	 *            the entity to write to the request
	 * @param responseType
	 *            the type of the return value
	 * @return the response as entity
	 * @since 4.1
	 */
	public <T> HttpResponseEntity<T> exchange(HttpRequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException {

		Type type = responseType.getType();
		RequestProcessor requestProcessor = requestEntityProcessor(requestEntity, type);
		ResponseProcessor<HttpResponseEntity<T>> responseExtractor = responseEntityProcessor(type);
		return nonNull(doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestProcessor, responseExtractor));
	}

	// General execution

	/**
	 * {@inheritDoc}
	 * <p>
	 * To provide a {@code RequestCallback} or {@code ResponseExtractor} only,
	 * but not both, consider using:
	 * <ul>
	 * <li>{@link #acceptHeaderRequestProcessor(Class)}
	 * <li>{@link #requestEntityProcessor(Object)}
	 * <li>{@link #requestEntityProcessor(Object, Type)}
	 * <li>{@link #responseEntityProcessor(Type)}
	 * </ul>
	 */
	@Nullable
	public <T> T execute(String url, HttpMethod method, @Nullable RequestProcessor requestProcessor,
			@Nullable ResponseProcessor<T> responseExtractor, Object... uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestProcessor, responseExtractor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * To provide a {@code RequestCallback} or {@code ResponseExtractor} only,
	 * but not both, consider using:
	 * <ul>
	 * <li>{@link #acceptHeaderRequestProcessor(Class)}
	 * <li>{@link #requestEntityProcessor(Object)}
	 * <li>{@link #requestEntityProcessor(Object, Type)}
	 * <li>{@link #responseEntityProcessor(Type)}
	 * </ul>
	 */
	@Nullable
	public <T> T execute(String url, HttpMethod method, @Nullable RequestProcessor requestProcessor,
			@Nullable ResponseProcessor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestProcessor, responseExtractor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * To provide a {@code RequestCallback} or {@code ResponseExtractor} only,
	 * but not both, consider using:
	 * <ul>
	 * <li>{@link #acceptHeaderRequestProcessor(Class)}
	 * <li>{@link #requestEntityProcessor(Object)}
	 * <li>{@link #requestEntityProcessor(Object, Type)}
	 * <li>{@link #responseEntityProcessor(Type)}
	 * </ul>
	 */
	@Nullable
	public <T> T execute(URI url, HttpMethod method, @Nullable RequestProcessor requestProcessor,
			@Nullable ResponseProcessor<T> responseExtractor) throws RestClientException {

		return doExecute(url, method, requestProcessor, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI.
	 * <p>
	 * The {@link Netty4ClientHttpRequest} is processed using the
	 * {@link RequestProcessor}; the response with the
	 * {@link ResponseProcessor}.
	 * 
	 * @param url
	 *            the fully-expanded URL to connect to
	 * @param method
	 *            the HTTP method to execute (GET, POST, etc.)
	 * @param requestProcessor
	 *            object that prepares the request (can be {@code null})
	 * @param responseProcessor
	 *            object that extracts the return value from the response (can
	 *            be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseProcessor}
	 */
	@Nullable
	protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestProcessor requestProcessor,
			@Nullable ResponseProcessor<T> responseProcessor) throws RestClientException {

		notNull(url, "URI is required");
		notNull(method, "HttpMethod is required");
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = createRequest(url, method,
					Objects.nonNull(requestProcessor) ? requestProcessor.getRequestHeaders() : null);
			if (Objects.nonNull(requestProcessor)) {
				requestProcessor.doWithRequest(request);
			}
			response = request.execute();
			handleResponse(url, method, response);
			return (Objects.nonNull(responseProcessor) ? responseProcessor.extractData(response) : null);
		} catch (IOException ex) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (Objects.nonNull(query) ? resource.substring(0, resource.indexOf('?')) : resource);
			throw new ResourceAccessException(
					"I/O error on " + method.name() + " request for \"" + resource + "\": " + ex.getMessage(), ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	// Request parameters processor.

	/**
	 * Callback interface for code that operates on a
	 * {@link Netty4ClientHttpRequest}. Allows to manipulate the request
	 * headers, and write to the request body.
	 */
	public interface RequestProcessor {

		/**
		 * Gets request headers.
		 * 
		 * @return
		 */
		HttpHeaders getRequestHeaders();

		/**
		 * Gets called by {@link RestClient#execute} with an opened
		 * {@code ClientHttpRequest}. Does not need to care about closing the
		 * request or about handling errors: this will all be handled by the
		 * {@code RestClient}.
		 * 
		 * @param request
		 *            the active HTTP request
		 * @throws IOException
		 *             in case of I/O errors
		 */
		void doWithRequest(ClientHttpRequest request) throws IOException;

	}

	/**
	 * Request callback implementation that prepares the request's accept
	 * headers.
	 */
	private class AcceptHeaderRequestProcessor implements RequestProcessor {

		@Nullable
		private final Type responseType;

		public AcceptHeaderRequestProcessor(@Nullable Type responseType) {
			this.responseType = responseType;
		}

		@Override
		public HttpHeaders getRequestHeaders() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void doWithRequest(ClientHttpRequest request) throws IOException {
			if (this.responseType != null) {
				List<HttpMediaType> allSupportedMediaTypes = getMessageParsers().stream()
						.filter(converter -> canReadResponse(responseType, converter)).flatMap(this::getSupportedMediaTypes)
						.distinct().sorted(HttpMediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
				if (log.isDebugEnabled()) {
					log.debug("Accept=" + allSupportedMediaTypes);
				}
				request.getHeaders().setAccept(allSupportedMediaTypes);
			}
		}

		private boolean canReadResponse(Type responseType, HttpMessageParser<?> converter) {
			Class<?> responseClass = (responseType instanceof Class ? (Class<?>) responseType : null);
			if (responseClass != null) {
				return converter.canRead(responseClass, null);
			} else if (converter instanceof GenericHttpMessageParser) {
				GenericHttpMessageParser<?> genericConverter = (GenericHttpMessageParser<?>) converter;
				return genericConverter.canRead(responseType, null, null);
			}
			return false;
		}

		private Stream<HttpMediaType> getSupportedMediaTypes(HttpMessageParser<?> messageConverter) {
			return messageConverter.getSupportedMediaTypes().stream().map(mediaType -> {
				if (mediaType.getCharset() != null) {
					return new HttpMediaType(mediaType.getType(), mediaType.getSubtype());
				}
				return mediaType;
			});
		}

	}

	/**
	 * Request callback implementation that writes the given object to the
	 * request stream.
	 */
	private class HttpEntityRequestProcessor extends AcceptHeaderRequestProcessor {

		private final HttpEntity<?> requestEntity;

		public HttpEntityRequestProcessor(@Nullable Object requestBody) {
			this(requestBody, null);
		}

		public HttpEntityRequestProcessor(@Nullable Object requestBody, @Nullable Type responseType) {
			super(responseType);
			if (requestBody instanceof HttpEntity) {
				this.requestEntity = (HttpEntity<?>) requestBody;
			} else if (requestBody != null) {
				this.requestEntity = new HttpEntity<>(requestBody);
			} else {
				this.requestEntity = HttpEntity.EMPTY;
			}
		}

		@Override
		public HttpHeaders getRequestHeaders() {
			return requestEntity.getHeaders();
		}

		@Override
		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
			super.doWithRequest(httpRequest);
			Object requestBody = requestEntity.getBody();
			if (requestBody == null) {
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = requestEntity.getHeaders();
				if (!requestHeaders.isEmpty()) {
					requestHeaders.forEach((key, values) -> httpHeaders.put(key, new LinkedList<>(values)));
				}
				if (httpHeaders.getContentLength() < 0) {
					httpHeaders.setContentLength(0L);
				}
			} else {
				Class<?> requestBodyClass = requestBody.getClass();
				Type requestBodyType = (requestEntity instanceof HttpRequestEntity
						? ((HttpRequestEntity<?>) requestEntity).getType() : requestBodyClass);
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = requestEntity.getHeaders();
				HttpMediaType requestContentType = requestHeaders.getContentType();
				for (HttpMessageParser<?> parser : getMessageParsers()) {
					if (parser instanceof GenericHttpMessageParser) {
						GenericHttpMessageParser<Object> genericParser = (GenericHttpMessageParser<Object>) parser;
						if (genericParser.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
							if (!requestHeaders.isEmpty()) {
								requestHeaders.forEach((key, values) -> httpHeaders.put(key, new LinkedList<>(values)));
							}
							logBody(requestBody, requestContentType, genericParser);
							genericParser.write(requestBody, requestBodyType, requestContentType, httpRequest);
							return;
						}
					} else if (parser.canWrite(requestBodyClass, requestContentType)) {
						if (!requestHeaders.isEmpty()) {
							requestHeaders.forEach((key, values) -> httpHeaders.put(key, new LinkedList<>(values)));
						}
						logBody(requestBody, requestContentType, parser);
						((HttpMessageParser<Object>) parser).write(requestBody, requestContentType, httpRequest);
						return;
					}
				}
				String message = "No HttpMessageParser for " + requestBodyClass.getName();
				if (requestContentType != null) {
					message += " and content type \"" + requestContentType + "\"";
				}
				throw new RestClientException(message);
			}
		}

		private void logBody(Object body, @Nullable HttpMediaType mediaType, HttpMessageParser<?> converter) {
			if (log.isDebugEnabled()) {
				if (mediaType != null) {
					log.debug("Writing [" + body + "] as \"" + mediaType + "\"");
				} else {
					log.debug("Writing [" + body + "] with " + converter.getClass().getName());
				}
			}
		}

	}

	// Response data processor.

	/**
	 * Generic callback interface used by {@link RestClient}'s retrieval methods
	 * Implementations of this interface perform the actual work of extracting
	 * data from a {@link ClientHttpResponse}, but don't need to worry about
	 * exception handling or closing resources.
	 *
	 * <p>
	 * Used internally by the {@link RestClient}, but also useful for
	 * application code.
	 *
	 * @see {@link RestClient#execute}
	 */
	public interface ResponseProcessor<T> {

		/**
		 * Extract data from the given {@code ClientHttpResponse} and return it.
		 * 
		 * @param response
		 *            the HTTP response
		 * @return the extracted data
		 * @throws IOException
		 *             in case of I/O errors
		 */
		T extractData(ClientHttpResponse response) throws IOException;

	}

	/**
	 * Response extractor for {@link HttpEntity}.
	 */
	private class ResponseEntityProcessor<T> implements ResponseProcessor<HttpResponseEntity<T>> {

		@Nullable
		private final HttpMessageParserExtractor<T> delegate;

		public ResponseEntityProcessor(@Nullable Type responseType) {
			if (responseType != null && Void.class != responseType) {
				this.delegate = new HttpMessageParserExtractor<>(responseType, getMessageParsers(), log);
			} else {
				this.delegate = null;
			}
		}

		@Override
		public HttpResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
			if (delegate != null) {
				T body = delegate.extractData(response);
				return HttpResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).body(body);
			} else {
				return HttpResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).build();
			}
		}
	}

	/**
	 * Response extractor that extracts the response {@link HttpHeaders}.
	 */
	private class ResponseHeadersProcessor implements ResponseProcessor<HttpHeaders> {

		@Override
		public HttpHeaders extractData(ClientHttpResponse response) {
			return response.getHeaders();
		}
	}

	// Response error handler.

	/**
	 * Strategy interface used by the {@link RestClient} to determine whether a
	 * particular response has an error or not.
	 */
	public interface ResponseErrorHandler {

		/**
		 * Indicate whether the given response has any errors.
		 * <p>
		 * Implementations will typically inspect the
		 * {@link ClientHttpResponse#getStatusCode() HttpStatus} of the
		 * response.
		 * 
		 * @param response
		 *            the response to inspect
		 * @return {@code true} if the response indicates an error;
		 *         {@code false} otherwise
		 * @throws IOException
		 *             in case of I/O errors
		 */
		boolean hasError(ClientHttpResponse response) throws IOException;

		/**
		 * Handle the error in the given response.
		 * <p>
		 * This method is only called when {@link #hasError(ClientHttpResponse)}
		 * has returned {@code true}.
		 * 
		 * @param response
		 *            the response with the error
		 * @throws IOException
		 *             in case of I/O errors
		 */
		void handleError(ClientHttpResponse response) throws IOException;

		/**
		 * Alternative to {@link #handleError(ClientHttpResponse)} with extra
		 * information providing access to the request URL and HTTP method.
		 * 
		 * @param url
		 *            the request URL
		 * @param method
		 *            the HTTP method
		 * @param response
		 *            the response with the error
		 * @throws IOException
		 *             in case of I/O errors
		 * @since 5.0
		 */
		default void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
			handleError(response);
		}

	}

	private static final boolean jackson2Present;

	static {
		ClassLoader classLoader = RestClient.class.getClassLoader();
		jackson2Present = ClassUtils2.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)
				&& ClassUtils2.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
	}

}