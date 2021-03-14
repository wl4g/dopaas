/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.urm.operator;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.common.bean.uci.Vcs;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

/**
 * Abstract VCS API operator.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
@SuppressWarnings("deprecation")
public abstract class AbstractVcsOperator implements VcsOperator, InitializingBean {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * HTTP for {@link RestTemplate}
	 */
	protected RestTemplate http;

	/**
	 * SSL/TLS for {@link RestTemplate}
	 */
	protected RestTemplate https;

	@Override
	public SmartLogger getLog() {
		return log;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// HTTP rest client.
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(10_000);
		factory.setReadTimeout(60_000);
		factory.setMaxResponseSize(1024 * 1024 * 10);
		this.http = new RestTemplate(factory);
		this.http.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		// SSL/HTTPS rest client.
		this.https = new RestTemplate(createSSLClientRequestFactory());
	}

	/**
	 * Do request to remote VCS provider servers.
	 *
	 * @param mthod
	 * @param credentials
	 * @param url
	 * @param headers
	 * @param ref
	 * @return
	 */
	protected final <T> ResponseEntity<T> doRemoteRequest(HttpMethod method, Vcs credentials, String url, HttpHeaders headers,
			ParameterizedTypeReference<T> ref) {
		notNullOf(method, "method");
		notNullOf(credentials, "credentials");
		notNullOf(ref, "typeReference");

		// Create httpEntity.
		HttpEntity<String> entity = createRequestEntity(credentials);
		if (!isNull(headers)) {
			entity.getHeaders().putAll(headers); // Overrade
		}

		// Do request.
		ResponseEntity<T> resp = http.exchange(url, method, entity, ref);
		if (null == resp || HttpStatus.OK != resp.getStatusCode()) {
			throw new IllegalStateException(
					format("Failed to request vcs remote, status: %s, body: %s", resp.getStatusCodeValue(), resp.getBody()));
		}
		log.info("Receiving VCS server response <= {}", resp.getBody());

		return resp;
	}

	/**
	 * Create vcs APIs http request entity.
	 * 
	 * @param credentials
	 * @return
	 */
	protected abstract HttpEntity<String> createRequestEntity(Vcs credentials);

	/**
	 * Create SSL {@link ClientHttpRequestFactory}
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 */
	private HttpComponentsClientHttpRequestFactory createSSLClientRequestFactory()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

		HttpClientBuilder builder = HttpClients.custom();
		builder.setSSLSocketFactory(socketFactory);
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(builder.build());
		return factory;
	}

}