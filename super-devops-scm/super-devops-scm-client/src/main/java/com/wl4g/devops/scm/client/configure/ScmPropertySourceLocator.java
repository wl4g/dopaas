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
package com.wl4g.devops.scm.client.configure;

import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage.ReleasePropertySource;
import com.wl4g.devops.common.exception.scm.ScmException;
import com.wl4g.devops.common.utils.bean.BeanMapConvert;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.scm.client.config.InstanceHolder;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.crypto.symmetric.AESCryptor;
import com.wl4g.devops.tool.common.log.SmartLogger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.AUTHORIZATION;
import static com.wl4g.devops.scm.client.configure.RefreshConfigHolder.getReleaseMeta;
import static com.wl4g.devops.scm.client.configure.RefreshConfigHolder.pollReleaseMeta;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
public abstract class ScmPropertySourceLocator implements PropertySourceLocator, InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/** SCM client configuration */
	final protected ScmClientProperties config;

	/** SCM client local instance info */
	final protected InstanceHolder info;

	/** SCM encrypted field identification prefix */
	final private static String CIPHER_PREFIX = "{cipher}";

	/** Rest template */
	protected RestTemplate restTemplate;

	public ScmPropertySourceLocator(ScmClientProperties config, InstanceHolder info) {
		Assert.notNull(config, "Scm client properties must not be null");
		Assert.notNull(info, "Instance info must not be null");
		this.config = config;
		this.info = info;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.restTemplate = createRestTemplate(config.getFetchReadTimeout());
	}

	/**
	 * Fetch release configuration from SCM server.
	 * 
	 * @return
	 */
	public ReleaseMessage fetchRemoteReleaseConfig() {
		try {
			// Fetch release URL.
			String uri = config.getBaseUri() + URI_S_BASE + "/" + URI_S_SOURCE_GET;
			// Create release get
			ReleaseMeta meta = getReleaseMeta(false);
			GetRelease get = new GetRelease(info.getAppName(), config.getNamespaces(), meta, info.getInstance());

			// To parameters
			String kvs = new BeanMapConvert(get).toUriParmaters();
			String url = uri + "?" + kvs;
			if (log.isDebugEnabled()) {
				log.debug("Fetch release config url: {}", url);
			}

			HttpHeaders headers = new HttpHeaders();
			attachHeaders(headers);
			final HttpEntity<Void> entity = new HttpEntity<>(null, headers);
			// Attach headers
			if (log.isDebugEnabled()) {
				log.debug("Adding header for: {}", headers);
			}

			RespBase<ReleaseMessage> resp = restTemplate
					.exchange(url, GET, entity, new ParameterizedTypeReference<RespBase<ReleaseMessage>>() {
					}).getBody();
			if (!RespBase.isSuccess(resp)) {
				throw new ScmException(String.format("Locate remote config error! %s, %s", url, resp.getMessage()));
			}

			// Extract release
			ReleaseMessage release = resp.getData();
			Assert.notNull(release, "Release message is required, it must not be null");
			release.validation(true, true);

			// Print sources
			printfSources(release);

			if (log.isDebugEnabled()) {
				log.debug("Fetch release config <= {}", release);
			}
			return release;
		} finally {
			pollReleaseMeta();
		}
	}

	/**
	 * Resolver cipher configuration source.
	 * 
	 * @param release
	 */
	public void resolvesCipherSource(ReleaseMessage release) {
		log.debug("Resolver cipher configuration propertySource ...");

		for (ReleasePropertySource ps : release.getPropertySources()) {
			ps.getSource().forEach((key, value) -> {
				String cipher = String.valueOf(value);
				if (cipher.startsWith(CIPHER_PREFIX)) {
					try {
						// TODO using dynamic cipherKey??
						byte[] cipherKey = AESCryptor.getEnvCipherKey("DEVOPS_CIPHER_KEY");
						String cipherText = cipher.substring(CIPHER_PREFIX.length());
						// TODO fromHex()??
						String plain = new AESCryptor().decrypt(cipherKey, CrypticSource.fromHex(cipherText)).toString();
						ps.getSource().put(key, plain);

						log.debug("Decryption property key: {}, cipherText: {}, plainText: {}", key, cipher, plain);
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

	/**
	 * Print property sources.
	 * 
	 * @param release
	 */
	protected void printfSources(ReleaseMessage release) {
		if (log.isInfoEnabled()) {
			log.info("Fetched from scm config <= group({}), namespace({}), release meta({})", release.getCluster(),
					release.getNamespaces(), release.getMeta());
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

	/**
	 * Create rest template.
	 * 
	 * @param readTimeout
	 * @return
	 */
	public RestTemplate createRestTemplate(long readTimeout) {
		Assert.state(readTimeout > 0, String.format("Invalid value for read timeout for %s", readTimeout));

		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(config.getConnectTimeout());
		factory.setReadTimeout((int) readTimeout);
		factory.setMaxResponseSize(config.getMaxResponseSize());
		RestTemplate restTemplate = new RestTemplate(factory);

		Map<String, String> headers = new HashMap<>(config.getHeaders());
		if (headers.containsKey(AUTHORIZATION)) {
			// To avoid redundant addition of header
			headers.remove(AUTHORIZATION);
		}
		if (!headers.isEmpty()) {
			restTemplate.setInterceptors(asList(new GenericRequestHeaderInterceptor(headers)));
		}
		return restTemplate;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public ScmClientProperties getConfig() {
		return config;
	}

	public InstanceHolder getInfo() {
		return info;
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