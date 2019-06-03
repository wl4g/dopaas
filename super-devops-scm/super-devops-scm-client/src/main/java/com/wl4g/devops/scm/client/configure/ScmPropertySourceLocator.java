/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.client.configure;

import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.scm.client.config.InstanceInfo;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties.Credentials;
import com.wl4g.devops.scm.client.enviroment.ScmEnvironment;
import com.wl4g.devops.scm.client.enviroment.ScmPropertySource;

/**
 * Abstract SCM application context initializer instructions.</br>
 * See:https://blog.csdn.net/leileibest_437147623/article/details/81074174
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月22日
 * @since
 */
@Order(0)
public abstract class ScmPropertySourceLocator implements PropertySourceLocator {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	final protected RestTemplate restTemplate;

	final protected ScmClientProperties config;

	final protected RetryProperties retryConfig;

	final protected InstanceInfo info;

	public ScmPropertySourceLocator(ScmClientProperties config, RetryProperties retryConfig, InstanceInfo info) {
		Assert.notNull(config, "Scm client properties must not be null");
		Assert.notNull(retryConfig, "Retry properties must not be null");
		Assert.notNull(info, "Instance info must not be null");
		this.config = config;
		this.retryConfig = retryConfig;
		this.info = info;
		this.restTemplate = createSecureRestTemplate(config);
	}

	protected void putPropertyValue(Map<String, Object> map, String key, String value) {
		if (StringUtils.hasText(value)) {
			map.put(key, value);
		}
	}

	protected ScmEnvironment pullRemoteEnvironment(RestTemplate restTemplate, ScmClientProperties properties, String label,
			String state) {
		String path = "/{name}/{profile}";
		String name = properties.getName();
		String profile = properties.getProfile();
		String token = properties.getToken();
		int noOfUrls = properties.getUri().length;
		if (noOfUrls > 1) {
			log.info("Multiple Config Server Urls found listed.");
		}

		Object[] args = new String[] { name, profile };
		if (StringUtils.hasText(label)) {
			if (label.contains("/")) {
				label = label.replace("/", "(_)");
			}
			args = new String[] { name, profile, label };
			path = path + "/{label}";
		}
		ResponseEntity<ScmEnvironment> response = null;

		for (int i = 0; i < noOfUrls; i++) {
			Credentials credentials = properties.getCredentials(i);
			String uri = credentials.getUri();
			String username = credentials.getUsername();
			String password = credentials.getPassword();
			if (log.isInfoEnabled()) {
				log.info("Fetching config from server at : " + uri);
			}

			try {
				HttpHeaders headers = new HttpHeaders();
				addAuthorizationToken(properties, headers, username, password);
				if (StringUtils.hasText(token)) {
					headers.add(TOKEN_HEADER, token);
				}
				if (StringUtils.hasText(state) && properties.isSendState()) {
					headers.add(STATE_HEADER, state);
				}
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

				final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);
				response = restTemplate.exchange(uri + path, HttpMethod.GET, entity, ScmEnvironment.class, args);
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
					throw e;
				}
			} catch (ResourceAccessException e) {
				log.info("Connect Timeout Exception on Url - " + uri + ". Will be trying the next url if available");
				if (i == noOfUrls - 1) {
					throw e;
				} else {
					continue;
				}
			}

			if (response == null || response.getStatusCode() != HttpStatus.OK) {
				return null;
			}

			ScmEnvironment result = response.getBody();
			return result;
		}

		return null;
	}

	protected void addAuthorizationToken(ScmClientProperties configClientProperties, HttpHeaders httpHeaders, String username,
			String password) {
		String authorization = configClientProperties.getHeaders().get(AUTHORIZATION);
		if (password != null && authorization != null) {
			throw new IllegalStateException("You must set either 'password' or 'authorization'");
		}

		if (password != null) {
			byte[] token = Base64Utils.encode((username + ":" + password).getBytes());
			httpHeaders.add("Authorization", "Basic " + new String(token));
		} else if (authorization != null) {
			httpHeaders.add("Authorization", authorization);
		}
	}

	protected void printfLog(ScmEnvironment result) {
		if (log.isInfoEnabled()) {
			log.info(String.format("Located environment: name=%s, profiles=%s, label=%s, version=%s, state=%s", result.getName(),
					result.getProfiles() == null ? "" : Arrays.asList(result.getProfiles()), result.getLabel(),
					result.getVersion(), result.getState()));
		}
		if (log.isDebugEnabled()) {
			List<ScmPropertySource> propertySourceList = result.getPropertySources();
			if (propertySourceList != null) {
				int propertyCount = 0;
				for (ScmPropertySource propertySource : propertySourceList) {
					propertyCount += propertySource.getSource().size();
				}
				log.debug(String.format("Environment %s has %d property sources with %d properties.", result.getName(),
						result.getPropertySources().size(), propertyCount));
			}
		}
	}

	protected RestTemplate createSecureRestTemplate(ScmClientProperties client) {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		if (client.getRequestReadTimeout() < 0) {
			throw new IllegalStateException("Invalid Value for Read Timeout set.");
		}
		factory.setReadTimeout(client.getRequestReadTimeout());

		RestTemplate template = new RestTemplate(factory);
		Map<String, String> headers = new HashMap<>(client.getHeaders());
		if (headers.containsKey(AUTHORIZATION)) {
			// To avoid redundant addition of header
			headers.remove(AUTHORIZATION);
		}
		if (!headers.isEmpty()) {
			template.setInterceptors(Arrays.asList(new GenericRequestHeaderInterceptor(headers)));
		}

		return template;
	}

	/**
	 * Adds the provided headers to the request.
	 */
	class GenericRequestHeaderInterceptor implements ClientHttpRequestInterceptor {

		final private Map<String, String> headers;

		public GenericRequestHeaderInterceptor(Map<String, String> headers) {
			this.headers = headers;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			for (Entry<String, String> h : headers.entrySet()) {
				request.getHeaders().add(h.getKey(), h.getValue());
			}
			return execution.execute(request, body);
		}

	}

}