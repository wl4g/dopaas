package com.wl4g.devops.components.tools.common.http;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.components.tools.common.reflect.ParameterizedTypeReference;
import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.CollectionUtils2;
import com.wl4g.devops.components.tools.common.http.parse.ByteArrayHttpMessageParser;
import com.wl4g.devops.components.tools.common.http.parse.GenericHttpMessageParser;
import com.wl4g.devops.components.tools.common.http.parse.HttpMessageParser;
import com.wl4g.devops.components.tools.common.http.parse.HttpMessageParserExtractor;
import com.wl4g.devops.components.tools.common.http.parse.MappingJackson2HttpMessageParser;
import com.wl4g.devops.components.tools.common.http.parse.ResourceHttpMessageParser;
import com.wl4g.devops.components.tools.common.http.parse.StringHttpMessageParser;
import com.wl4g.devops.components.tools.common.http.uri.AbstractUriTemplateHandler;
import com.wl4g.devops.components.tools.common.http.uri.DefaultResponseErrorHandler;
import com.wl4g.devops.components.tools.common.http.uri.DefaultUriBuilderFactory;
import com.wl4g.devops.components.tools.common.http.uri.ResourceAccessException;
import com.wl4g.devops.components.tools.common.http.uri.UriTemplateHandler;
import com.wl4g.devops.components.tools.common.http.uri.DefaultUriBuilderFactory.EncodingMode;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.lang.ClassUtils2;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Synchronous client to perform HTTP requests, exposing a simple, template
 * method API over underlying HTTP client libraries such as the JDK
 * {@code HttpURLConnection}, Apache HttpComponents, and others.
 *
 * <p>
 * The RestTemplate offers templates for common scenarios by HTTP method, in
 * addition to the generalized {@code exchange} and {@code execute} methods that
 * support of less frequent cases.
 *
 * @see HttpMessageParser
 * @see RequestCallback
 * @see ResponseExtractor
 * @see ResponseErrorHandler
 */
public class RestClient {

	final protected Log log = LogFactory.getLog(getClass());

	final private List<HttpMessageParser<?>> messageConverters = new ArrayList<>(4);
	final private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(4);
	final private ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();
	final private ClientHttpRequestFactory requestFactory;
	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
	private UriTemplateHandler uriTemplateHandler;

	/**
	 * Create a new instance of the {@link RestClient} using default settings.
	 * Default {@link HttpMessageParser HttpMessageConverters} are initialized.
	 */
	@SuppressWarnings("serial")
	public RestClient() {
		this(new Netty4ClientHttpRequestFactory(), new ArrayList<HttpMessageParser<?>>() {
			{
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
	 * @param messageConverters
	 *            the list of {@link HttpMessageParser} to use
	 */
	public RestClient(ClientHttpRequestFactory requestFactory, List<HttpMessageParser<?>> messageConverters) {
		notNullOf(requestFactory, "requestFactory");
		this.requestFactory = requestFactory;
		validateConverters(messageConverters);
		this.messageConverters.addAll(messageConverters);
		this.uriTemplateHandler = initUriTemplateHandler();
	}

	private static DefaultUriBuilderFactory initUriTemplateHandler() {
		DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
		uriFactory.setEncodingMode(EncodingMode.URI_COMPONENT); // for backwards
		return uriFactory;
	}

	/**
	 * Set the message body converters to use.
	 * <p>
	 * These converters are used to convert from and to HTTP requests and
	 * responses.
	 */
	public RestClient setMessageConverters(List<HttpMessageParser<?>> messageConverters) {
		validateConverters(messageConverters);
		// Take getMessageConverters() List as-is when passed in here
		if (this.messageConverters != messageConverters) {
			this.messageConverters.clear();
			this.messageConverters.addAll(messageConverters);
		}
		return this;
	}

	private void validateConverters(List<HttpMessageParser<?>> messageConverters) {
		Assert2.notEmpty(messageConverters, "At least one HttpMessageConverter is required");
	}

	/**
	 * Return the list of message body converters.
	 * <p>
	 * The returned {@link List} is active and may get appended to.
	 */
	public List<HttpMessageParser<?>> getMessageConverters() {
		return this.messageConverters;
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
	public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
		// Take getInterceptors() List as-is when passed in here
		if (this.interceptors != interceptors) {
			this.interceptors.clear();
			this.interceptors.addAll(interceptors);
		}
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
	 * By default, RestTemplate uses a {@link DefaultResponseErrorHandler}.
	 */
	public void setErrorHandler(ResponseErrorHandler errorHandler) {
		Assert2.notNull(errorHandler, "ResponseErrorHandler must not be null");
		this.errorHandler = errorHandler;
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
	 * RestTemplate restTemplate = new RestTemplate();
	 * restTemplate.setUriTemplateHandler(handler);
	 * </pre>
	 * 
	 * @param uriVars
	 *            the default URI variable values
	 * @since 4.3
	 */
	public void setDefaultUriVariables(Map<String, ?> uriVars) {
		if (this.uriTemplateHandler instanceof DefaultUriBuilderFactory) {
			((DefaultUriBuilderFactory) this.uriTemplateHandler).setDefaultUriVariables(uriVars);
		} else if (this.uriTemplateHandler instanceof AbstractUriTemplateHandler) {
			((AbstractUriTemplateHandler) this.uriTemplateHandler).setDefaultUriVariables(uriVars);
		} else {
			throw new IllegalArgumentException("This property is not supported with the configured UriTemplateHandler.");
		}
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
	public void setUriTemplateHandler(UriTemplateHandler handler) {
		Assert2.notNull(handler, "UriTemplateHandler must not be null");
		this.uriTemplateHandler = handler;
	}

	/**
	 * Return the configured URI template handler.
	 */
	public UriTemplateHandler getUriTemplateHandler() {
		return this.uriTemplateHandler;
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
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageConverters(),
				log);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
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
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageConverters(),
				log);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
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
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageConverters(),
				log);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
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

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
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

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables));
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
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, HttpMethod.GET, requestCallback, responseExtractor));
	}

	// POST

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the value of the {@code Location} header. This header
	 * typically indicates where the new resource is stored.
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
	 * @return the value for the {@code Location} header
	 * @see HttpEntity
	 */
	@Nullable
	public URI postForLocation(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
		return (headers != null ? headers.getLocation() : null);
	}

	/**
	 * Create a new resource by POSTing the given object to the URI template,
	 * and returns the value of the {@code Location} header. This header
	 * typically indicates where the new resource is stored.
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
	 * @return the value for the {@code Location} header
	 * @see HttpEntity
	 */
	@Nullable
	public URI postForLocation(String url, @Nullable Object request, Map<String, ?> uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
		return (headers != null ? headers.getLocation() : null);
	}

	/**
	 * Create a new resource by POSTing the given object to the URL, and returns
	 * the value of the {@code Location} header. This header typically indicates
	 * where the new resource is stored.
	 * <p>
	 * The {@code request} parameter can be a {@link HttpEntity} in order to add
	 * additional HTTP headers to the request.
	 * 
	 * @param url
	 *            the URL
	 * @param request
	 *            the Object to be POSTed (may be {@code null})
	 * @return the value for the {@code Location} header
	 * @see HttpEntity
	 */
	@Nullable
	public URI postForLocation(URI url, @Nullable Object request) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor());
		return (headers != null ? headers.getLocation() : null);
	}

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

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageConverters(),
				log);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
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

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageConverters(),
				log);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
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
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageParserExtractor<T> responseExtractor = new HttpMessageParserExtractor<>(responseType, getMessageConverters());
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
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

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
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
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables));
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

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, HttpMethod.POST, requestCallback, responseExtractor));
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

		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
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

		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
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
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null);
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
		execute(url, HttpMethod.DELETE, null, null, uriVariables);
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
		execute(url, HttpMethod.DELETE, null, null, uriVariables);
	}

	/**
	 * Delete the resources at the specified URL.
	 * 
	 * @param url
	 *            the URL
	 */
	public void delete(URI url) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null);
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

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
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

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
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

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(execute(url, method, requestCallback, responseExtractor));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}. The given {@link ParameterizedTypeReference} is
	 * used to pass generic type information:
	 * 
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {
	 * };
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;, HttpMethod.GET, null, myBean);
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
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}. The given {@link ParameterizedTypeReference} is
	 * used to pass generic type information:
	 * 
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {
	 * };
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;, HttpMethod.GET, null, myBean);
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
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
	}

	/**
	 * Execute the HTTP method to the given URI template, writing the given
	 * request entity to the request, and returns the response as
	 * {@link HttpResponseEntity}. The given {@link ParameterizedTypeReference} is
	 * used to pass generic type information:
	 * 
	 * <pre class="code">
	 * ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt; myBean = new ParameterizedTypeReference&lt;List&lt;MyBean&gt;&gt;() {
	 * };
	 * ResponseEntity&lt;List&lt;MyBean&gt;&gt; response = template.exchange(&quot;http://example.com&quot;, HttpMethod.GET, null, myBean);
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
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return nonNull(execute(url, method, requestCallback, responseExtractor));
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
	public <T> HttpResponseEntity<T> exchange(HttpRequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return nonNull(doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor));
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
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<HttpResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return nonNull(doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor));
	}

	// General execution

	/**
	 * {@inheritDoc}
	 * <p>
	 * To provide a {@code RequestCallback} or {@code ResponseExtractor} only,
	 * but not both, consider using:
	 * <ul>
	 * <li>{@link #acceptHeaderRequestCallback(Class)}
	 * <li>{@link #httpEntityCallback(Object)}
	 * <li>{@link #httpEntityCallback(Object, Type)}
	 * <li>{@link #responseEntityExtractor(Type)}
	 * </ul>
	 */
	@Nullable
	public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback,
			@Nullable ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * To provide a {@code RequestCallback} or {@code ResponseExtractor} only,
	 * but not both, consider using:
	 * <ul>
	 * <li>{@link #acceptHeaderRequestCallback(Class)}
	 * <li>{@link #httpEntityCallback(Object)}
	 * <li>{@link #httpEntityCallback(Object, Type)}
	 * <li>{@link #responseEntityExtractor(Type)}
	 * </ul>
	 */
	@Nullable
	public <T> T execute(String url, HttpMethod method, @Nullable RequestCallback requestCallback,
			@Nullable ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * To provide a {@code RequestCallback} or {@code ResponseExtractor} only,
	 * but not both, consider using:
	 * <ul>
	 * <li>{@link #acceptHeaderRequestCallback(Class)}
	 * <li>{@link #httpEntityCallback(Object)}
	 * <li>{@link #httpEntityCallback(Object, Type)}
	 * <li>{@link #responseEntityExtractor(Type)}
	 * </ul>
	 */
	@Nullable
	public <T> T execute(URI url, HttpMethod method, @Nullable RequestCallback requestCallback,
			@Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

		return doExecute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI.
	 * <p>
	 * The {@link Netty4ClientHttpRequest} is processed using the
	 * {@link RequestCallback}; the response with the {@link ResponseExtractor}.
	 * 
	 * @param url
	 *            the fully-expanded URL to connect to
	 * @param method
	 *            the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback
	 *            object that prepares the request (can be {@code null})
	 * @param responseExtractor
	 *            object that extracts the return value from the response (can
	 *            be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	@Nullable
	protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
			@Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert2.notNull(url, "URI is required");
		Assert2.notNull(method, "HttpMethod is required");
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			response = request.execute();
			handleResponse(url, method, response);
			return (responseExtractor != null ? responseExtractor.extractData(response) : null);
		} catch (IOException ex) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
			throw new ResourceAccessException(
					"I/O error on " + method.name() + " request for \"" + resource + "\": " + ex.getMessage(), ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

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
	public <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
		return new AcceptHeaderRequestCallback(responseType);
	}

	/**
	 * Return a {@code RequestCallback} implementation that writes the given
	 * object to the request stream.
	 */
	public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody) {
		return new HttpEntityRequestCallback(requestBody);
	}

	/**
	 * Return a {@code RequestCallback} implementation that:
	 * <ol>
	 * <li>Sets the request {@code Accept} header based on the given response
	 * type, cross-checked against the configured message converters.
	 * <li>Writes the given object to the request stream.
	 * </ol>
	 */
	public <T> RequestCallback httpEntityCallback(@Nullable Object requestBody, Type responseType) {
		return new HttpEntityRequestCallback(requestBody, responseType);
	}

	/**
	 * Return a {@code ResponseExtractor} that prepares a
	 * {@link HttpResponseEntity}.
	 */
	public <T> ResponseExtractor<HttpResponseEntity<T>> responseEntityExtractor(Type responseType) {
		return new ResponseEntityResponseExtractor<>(responseType);
	}

	/**
	 * Return a response extractor for {@link HttpHeaders}.
	 */
	protected ResponseExtractor<HttpHeaders> headersExtractor() {
		return this.headersExtractor;
	}

	private static <T> T nonNull(@Nullable T result) {
		Assert2.state(result != null, "No result");
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
	 * @return the created request
	 * @throws IOException
	 *             in case of I/O errors
	 * @see #getRequestFactory()
	 * @see ClientHttpRequestFactory#createRequest(URI, HttpMethod)
	 */
	private ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
		ClientHttpRequest request = getRequestFactory().createRequest(url, method);
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

	/**
	 * Callback interface for code that operates on a
	 * {@link Netty4ClientHttpRequest}. Allows to manipulate the request
	 * headers, and write to the request body.
	 */
	public interface RequestCallback {

		/**
		 * Gets called by {@link RestTemplate#execute} with an opened
		 * {@code ClientHttpRequest}. Does not need to care about closing the
		 * request or about handling errors: this will all be handled by the
		 * {@code RestTemplate}.
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
	private class AcceptHeaderRequestCallback implements RequestCallback {

		@Nullable
		private final Type responseType;

		public AcceptHeaderRequestCallback(@Nullable Type responseType) {
			this.responseType = responseType;
		}

		@Override
		public void doWithRequest(ClientHttpRequest request) throws IOException {
			if (this.responseType != null) {
				List<HttpMediaType> allSupportedMediaTypes = getMessageConverters().stream()
						.filter(converter -> canReadResponse(this.responseType, converter)).flatMap(this::getSupportedMediaTypes)
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
	private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {

		private final HttpEntity<?> requestEntity;

		public HttpEntityRequestCallback(@Nullable Object requestBody) {
			this(requestBody, null);
		}

		public HttpEntityRequestCallback(@Nullable Object requestBody, @Nullable Type responseType) {
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
		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
			super.doWithRequest(httpRequest);
			Object requestBody = this.requestEntity.getBody();
			if (requestBody == null) {
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = this.requestEntity.getHeaders();
				if (!requestHeaders.isEmpty()) {
					requestHeaders.forEach((key, values) -> httpHeaders.put(key, new LinkedList<>(values)));
				}
				if (httpHeaders.getContentLength() < 0) {
					httpHeaders.setContentLength(0L);
				}
			} else {
				Class<?> requestBodyClass = requestBody.getClass();
				Type requestBodyType = (this.requestEntity instanceof HttpRequestEntity
						? ((HttpRequestEntity<?>) this.requestEntity).getType() : requestBodyClass);
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = this.requestEntity.getHeaders();
				HttpMediaType requestContentType = requestHeaders.getContentType();
				for (HttpMessageParser<?> messageConverter : getMessageConverters()) {
					if (messageConverter instanceof GenericHttpMessageParser) {
						GenericHttpMessageParser<Object> genericConverter = (GenericHttpMessageParser<Object>) messageConverter;
						if (genericConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
							if (!requestHeaders.isEmpty()) {
								requestHeaders.forEach((key, values) -> httpHeaders.put(key, new LinkedList<>(values)));
							}
							logBody(requestBody, requestContentType, genericConverter);
							genericConverter.write(requestBody, requestBodyType, requestContentType, httpRequest);
							return;
						}
					} else if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
						if (!requestHeaders.isEmpty()) {
							requestHeaders.forEach((key, values) -> httpHeaders.put(key, new LinkedList<>(values)));
						}
						logBody(requestBody, requestContentType, messageConverter);
						((HttpMessageParser<Object>) messageConverter).write(requestBody, requestContentType, httpRequest);
						return;
					}
				}
				String message = "No HttpMessageConverter for " + requestBodyClass.getName();
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

	/**
	 * Generic callback interface used by {@link RestTemplate}'s retrieval
	 * methods Implementations of this interface perform the actual work of
	 * extracting data from a {@link ClientHttpResponse}, but don't need to
	 * worry about exception handling or closing resources.
	 *
	 * <p>
	 * Used internally by the {@link RestTemplate}, but also useful for
	 * application code.
	 *
	 * @see {@link RestClient#execute}
	 */
	public interface ResponseExtractor<T> {

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
	private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<HttpResponseEntity<T>> {

		@Nullable
		private final HttpMessageParserExtractor<T> delegate;

		public ResponseEntityResponseExtractor(@Nullable Type responseType) {
			if (responseType != null && Void.class != responseType) {
				this.delegate = new HttpMessageParserExtractor<>(responseType, getMessageConverters(), log);
			} else {
				this.delegate = null;
			}
		}

		@Override
		public HttpResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
			if (this.delegate != null) {
				T body = this.delegate.extractData(response);
				return HttpResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).body(body);
			} else {
				return HttpResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).build();
			}
		}
	}

	/**
	 * Response extractor that extracts the response {@link HttpHeaders}.
	 */
	private static class HeadersExtractor implements ResponseExtractor<HttpHeaders> {

		@Override
		public HttpHeaders extractData(ClientHttpResponse response) {
			return response.getHeaders();
		}
	}

	/**
	 * Strategy interface used by the {@link RestTemplate} to determine whether
	 * a particular response has an error or not.
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
