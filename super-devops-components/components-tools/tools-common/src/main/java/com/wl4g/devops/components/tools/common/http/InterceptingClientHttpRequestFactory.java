package com.wl4g.devops.components.tools.common.http;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import io.netty.handler.codec.http.HttpMethod;

/**
 * {@link ClientHttpRequestFactory} wrapper with support for
 * {@link ClientHttpRequestInterceptor}s.
 *
 * @see ClientHttpRequestFactory
 * @see ClientHttpRequestInterceptor
 */
public class InterceptingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {

	private final List<ClientHttpRequestInterceptor> interceptors;

	/**
	 * Create a new instance of the {@code InterceptingClientHttpRequestFactory}
	 * with the given parameters.
	 * 
	 * @param requestFactory
	 *            the request factory to wrap
	 * @param interceptors
	 *            the interceptors that are to be applied (can be {@code null})
	 */
	public InterceptingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory,
			List<ClientHttpRequestInterceptor> interceptors) {

		super(requestFactory);
		this.interceptors = (interceptors != null ? interceptors : Collections.<ClientHttpRequestInterceptor> emptyList());
	}

	@Override
	protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) {
		return new InterceptingClientHttpRequest(requestFactory, this.interceptors, uri, httpMethod);
	}

}
