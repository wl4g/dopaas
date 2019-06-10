/*

 * Copyright 2017 ~ 2025 the original author or authors.
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

import java.io.IOException;

import java.util.*;
import java.util.Map.Entry;
import static java.util.Collections.*;

import com.wl4g.devops.common.bean.scm.model.GenericInfo;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage.ReleasePropertySource;
import com.wl4g.devops.common.exception.scm.ScmException;
import com.wl4g.devops.common.utils.bean.BeanMapConvert;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.scm.client.config.InstanceInfo;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.MediaType.*;
import static org.springframework.http.HttpMethod.*;

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

	/** Rest template */
	final protected RestTemplate restTemplate;

	/** SCM client configuration */
	final protected ScmClientProperties config;

	/** SCM client retry configuration */
	final protected RetryProperties retryConfig;

	/** SCM client local instance info */
	final protected InstanceInfo info;

	/** SCM encrypted field identification prefix */
	final private static String CIPHER_PREFIX = "{cipher}";

	/** SCM configuration server base URI. */
	@Value("${spring.cloud.devops.scm.client.base-uri:http://localhost:6400/devops}")
	private String baseUri;

	public ScmPropertySourceLocator(ScmClientProperties config, RetryProperties retryConfig, InstanceInfo info) {
		Assert.notNull(config, "Scm client properties must not be null");
		Assert.notNull(retryConfig, "Retry properties must not be null");
		Assert.notNull(info, "Instance info must not be null");
		this.config = config;
		this.retryConfig = retryConfig;
		this.info = info;
		this.restTemplate = createSecureRestTemplate(config);
	}

	public ReleaseMessage pullRemoteReleaseConfig(GenericInfo.ReleaseMeta targetReleaseMeta) {
		// Get pull release URL.
		String uri = baseUri + URI_S_BASE + "/" + URI_S_SOURCE_GET;

		// Create release get
		GetRelease get = new GetRelease(info.getAppName(), info.getProfilesActive(), targetReleaseMeta, info.getBindInstance());

		// To parameters
		String params = new BeanMapConvert(get).toUriParmaters();
		String url = uri + "?" + params;
		if (log.isDebugEnabled()) {
			log.debug("Get release config url: {}", url);
		}

		HttpHeaders headers = new HttpHeaders();
		attachHeaders(headers);
		final HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		// Attach headers
		if (log.isDebugEnabled()) {
			log.debug("Adding header for: {}", headers);
		}

		// Do get request source
		RespBase<ReleaseMessage> resp = restTemplate
				.exchange(url, GET, entity, new ParameterizedTypeReference<RespBase<ReleaseMessage>>() {
				}).getBody();
		if (!RespBase.isSuccess(resp)) {
			throw new ScmException(String.format("Get remote source error. %s, %s", url, resp.getMessage()));
		}

		// Release payload
		ReleaseMessage release = resp.getData().get(KEY_RELEASE);
		Assert.notNull(release, "Release message is required, it must not be null");
		release.validation(true, true);

		// Print sources
		printfSources(release);

		if (log.isDebugEnabled()) {
			log.debug("Get remote release config : {}", release);
		}
		return release;
	}

	/**
	 * Resolver cipher configuration source.
	 * 
	 * @param release
	 */
	public void resolvesCipherSource(ReleaseMessage release) {
		if (log.isTraceEnabled()) {
			log.trace("Resolver cipher configuration propertySource ...");
		}

		for (ReleaseMessage.ReleasePropertySource ps : release.getPropertySources()) {
			ps.getSource().forEach((key, value) -> {
				String cipher = String.valueOf(value);
				if (cipher.startsWith(CIPHER_PREFIX)) {
					try {
						String plain = new AES().decrypt(cipher.substring(CIPHER_PREFIX.length()));
						ps.getSource().put(key, plain);

						if (log.isDebugEnabled()) {
							log.debug("Decryption property-key: {}, cipherText: {}, plainText: {}", key, cipher, plain);
						}
					} catch (Exception e) {
						throw new ScmException("Cipher decryption error.", e);
					}
				}
			});
		}
	}

	/**
	 * Attach headers, e.g. authentication token information
	 * 
	 * @param headers
	 */
	protected void attachHeaders(HttpHeaders headers) {
		headers.setAccept(singletonList(APPLICATION_JSON));
	}

	protected void printfSources(ReleaseMessage release) {
		if (log.isInfoEnabled()) {
			log.info("Located environment: group: {}, namespace: {}, profile: {}, release meta: {}", release.getGroup(),
					release.getNamespace(), release.getProfile(), release.getMeta());
		}

		if (log.isDebugEnabled()) {
			List<ReleasePropertySource> propertySources = release.getPropertySources();
			if (propertySources != null) {
				int propertyCount = 0;
				for (ReleasePropertySource ps : propertySources) {
					propertyCount += ps.getSource().size();
				}
				log.debug(String.format("Environment has %d property sources with %d properties.", propertySources.size(),
						propertyCount));
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