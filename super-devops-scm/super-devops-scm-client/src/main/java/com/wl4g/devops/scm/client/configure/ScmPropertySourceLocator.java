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

import com.wl4g.devops.common.bean.scm.model.GenericInfo;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;
import com.wl4g.devops.common.exception.scm.ScmException;
import com.wl4g.devops.common.utils.bean.BeanMapConvert;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.scm.client.config.InstanceInfo;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties.*;
import com.wl4g.devops.scm.client.enviroment.ScmEnvironment;
import com.wl4g.devops.scm.client.enviroment.ScmPropertySource;
import com.wl4g.devops.scm.common.bean.ScmMetaInfo;
import com.wl4g.devops.scm.common.utils.ScmUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
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
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;

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

	final private static String CIPHER_PREFIX = "{cipher}";

	public static String token = "";

	@Value("${spring.cloud.devops.scm.client.base-uri:http://localhost:6400/devops}")
	String baseUri;

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

	public ReleaseMessage getRemoteReleaseConfig(GenericInfo.ReleaseMeta targetReleaseMeta) {
		// Get pull release URL.
		String uri = this.baseUri + SCMDevOpsConstants.URI_S_BASE + "/" + SCMDevOpsConstants.URI_S_SOURCE_GET;

		// Create request bean.
		GetRelease req = new GetRelease(info.getAppName(), info.getProfilesActive(), targetReleaseMeta, info.getBindInstance());

		// Bean to map.
		String params = new BeanMapConvert(req).toUriParmaters();
		String url = uri + "?" + params;
		if (log.isDebugEnabled()) {
			log.debug("Get remote release config url: {}", url);
		}

		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.hasText(token)) {
			headers.add(TOKEN_HEADER, token);
		}
		log.info("scm token:"+token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);

		RespBase<ReleaseMessage> resp = this.restTemplate
				.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<RespBase<ReleaseMessage>>() {
				}).getBody();
		if (!RespBase.isSuccess(resp)) {
			throw new ScmException(String.format("Get remote source error. %s, %s", url, resp.getMessage()));
		}

		// Get release payload
		ReleaseMessage release = resp.getData().get(SCMDevOpsConstants.KEY_RELEASE);
		Assert.notNull(release, "'releaseMessage' is required, it must not be null");
		release.validation(true, true);

		if (log.isDebugEnabled()) {
			log.debug("Get remote release config : {}", release);
		}
		return release;
	}

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

	public void receiveToken(){
		CuratorFramework client = null;
		try {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
			client = CuratorFrameworkFactory.builder().connectString(config.getZookeeperUrl())
					//.sessionTimeoutMs(10000)
					.retryPolicy(retryPolicy)
					//.namespace("admin")
					.build();
			client.start();
			String path = ScmUtils.genMetaPath(new GenericInfo(info.getAppName(), info.getProfilesActive()),
					info.getBindInstance());

			byte[] b = client.getData().forPath(path);
			String t = new String(b, StandardCharsets.UTF_8);
			log.info("get token from zookeeper:"+config.getZookeeperUrl()+",path:"+path);
			ScmMetaInfo scmMetaInfo = JacksonUtils.parseJSON(t, ScmMetaInfo.class);
			token = scmMetaInfo.getToken();
		}catch (Exception e){
			log.error(e.getMessage());
		} finally {
			if (client != null) {
				client.close();
			}
		}

	}

}