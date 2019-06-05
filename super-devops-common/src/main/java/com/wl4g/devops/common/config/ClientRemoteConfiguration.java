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
package com.wl4g.devops.common.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

@Configuration
@ConditionalOnClass({ RestTemplate.class, Netty4ClientHttpRequestFactory.class })
public class ClientRemoteConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	@ConditionalOnMissingBean
	public ClientHttpRequestFactory netty4ClientHttpRequestFactory(
			RemoteProperties props/* , SslContext sslContext */) {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setReadTimeout(props.getReadTimeout());
		factory.setConnectTimeout(props.getConnectTimeout());
		factory.setMaxResponseSize(props.getMaxResponseSize());
		// factory.setSslContext(sslContext);
		return factory;
	}

	/**
	 * Clearly specify OpenSSL, because jdk8 may have performance problems, See:
	 * https://www.cnblogs.com/wade-luffy/p/6019743.html#_label1
	 * {@link io.netty.handler.ssl.ReferenceCountedOpenSslContext
	 * ReferenceCountedOpenSslContext}
	 * 
	 * @return
	 * @throws SSLException
	 */
	// @Bean
	// @ConditionalOnMissingBean
	public SslContext sslContext(RemoteProperties props) throws SSLException {
		SslProperties ssl = props.getSslProperties();
		List<String> ciphers = ssl.getCiphers() == null ? SslProperties.DEFAULT_CIPHERS : ssl.getCiphers();
		return SslContextBuilder.forServer(new File(ssl.getKeyCertChainFile()), new File(ssl.getKeyFile()))
				.sslProvider(SslProvider.OPENSSL).ciphers(ciphers).clientAuth(ClientAuth.REQUIRE)
				.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
	}

	@Bean
	public RemoteProperties remoteProperties() {
		return new RemoteProperties();
	}

	/**
	 * Verifies a SSL peer host name based on an explicit whitelist of allowed
	 * hosts.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月20日
	 * @since
	 */
	public final static class WhitelistHostnameVerifier implements HostnameVerifier {

		/** Allowed hosts */
		private String[] allowedHosts;

		/**
		 * Creates a new instance using the given array of allowed hosts.
		 * 
		 * @param allowed
		 *            Array of allowed hosts.
		 */
		public WhitelistHostnameVerifier(final String[] allowed) {
			this.allowedHosts = allowed;
		}

		/**
		 * Creates a new instance using the given list of allowed hosts.
		 * 
		 * @param allowedList
		 *            Comma-separated list of allowed hosts.
		 */
		public WhitelistHostnameVerifier(final String allowedList) {
			this.allowedHosts = allowedList.split(",\\s*");
		}

		/** {@inheritDoc} */
		public boolean verify(final String hostname, final SSLSession session) {

			for (final String allowedHost : this.allowedHosts) {
				if (hostname.equalsIgnoreCase(allowedHost)) {
					return true;
				}
			}
			return false;
		}

	}

	/**
	 * Validates an SSL peer's hostname using a regular expression that a
	 * candidate host must match in order to be verified.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月20日
	 * @since
	 */
	public final static class RegexHostnameVerifier implements HostnameVerifier {

		/** Allowed hostname pattern */
		private Pattern pattern;

		/**
		 * Creates a new instance using the given regular expression.
		 * 
		 * @param regex
		 *            Regular expression describing allowed hosts.
		 */
		public RegexHostnameVerifier(final String regex) {
			this.pattern = Pattern.compile(regex);
		}

		/** {@inheritDoc} */
		public boolean verify(final String hostname, final SSLSession session) {
			return pattern.matcher(hostname).matches();
		}
	}

	/**
	 * Hostname verifier that performs no host name verification for an SSL peer
	 * such that all hosts are allowed.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月20日
	 * @since
	 */
	public final static class AnyHostnameVerifier implements HostnameVerifier {

		/** {@inheritDoc} */
		public boolean verify(final String hostname, final SSLSession session) {
			return true;
		}

	}

	/**
	 * Remote rest template properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月20日
	 * @since
	 */
	@ConfigurationProperties(prefix = "spring.web.remote")
	public static class RemoteProperties {

		private Integer readTimeout = 10000;
		private Integer connectTimeout = 6000;
		private Integer maxResponseSize = 1024 * 1024 * 10;
		private SslProperties sslProperties = new SslProperties();

		public Integer getReadTimeout() {
			return readTimeout;
		}

		public void setReadTimeout(Integer readTimeout) {
			this.readTimeout = readTimeout;
		}

		public Integer getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(Integer connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		public Integer getMaxResponseSize() {
			return maxResponseSize;
		}

		public void setMaxResponseSize(Integer maxResponseSize) {
			this.maxResponseSize = maxResponseSize;
		}

		public SslProperties getSslProperties() {
			return sslProperties;
		}

		public void setSslProperties(SslProperties sslProperties) {
			this.sslProperties = sslProperties;
		}

	}

	/**
	 * Remote SSL context properties.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月21日
	 * @since
	 */
	public static class SslProperties {
		/*
		 * Make sure to sync this list with JdkSslEngineFactory.
		 */
		final public static List<String> DEFAULT_CIPHERS = Collections.unmodifiableList(Arrays.asList(
				new String[] { "ECDHE-RSA-AES128-SHA", "ECDHE-RSA-AES256-SHA", "AES128-SHA", "AES256-SHA", "DES-CBC3-SHA" }));

		private String keyCertChainFile;
		private String keyFile;
		/**
		 * Clearly specify OpenSSL, because jdk8 may have performance problems,
		 * See: https://www.cnblogs.com/wade-luffy/p/6019743.html#_label1
		 * {@link io.netty.handler.ssl.ReferenceCountedOpenSslContext
		 * ReferenceCountedOpenSslContext}
		 */
		private List<String> ciphers;

		public String getKeyCertChainFile() {
			return keyCertChainFile;
		}

		public void setKeyCertChainFile(String keyCertChainFile) {
			this.keyCertChainFile = keyCertChainFile;
		}

		public String getKeyFile() {
			return keyFile;
		}

		public void setKeyFile(String keyFile) {
			this.keyFile = keyFile;
		}

		public List<String> getCiphers() {
			return this.ciphers;
		}

		public void setCiphers(List<String> ciphers) {
			this.ciphers = ciphers;
		}
	}

}