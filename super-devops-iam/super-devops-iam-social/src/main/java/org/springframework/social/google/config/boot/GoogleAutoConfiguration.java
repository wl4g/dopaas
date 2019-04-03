/**
 * Copyright 2011-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.google.config.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Social
 * connectivity with Google.
 *
 * @author Leon Radley
 * @since 1.0.1
 */
@Configuration
@ConditionalOnClass({ SocialConfigurerAdapter.class, GoogleConnectionFactory.class })
@ConditionalOnProperty(prefix = "spring.social.google", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class GoogleAutoConfiguration {

	@Configuration
	@EnableSocial
	@EnableConfigurationProperties(GoogleProperties.class)
	@ConditionalOnWebApplication
	protected static class GoogleConfigurerAdapter extends SocialConfigurerAdapter {

		@Autowired
		private GoogleProperties properties;

		@Bean
		@ConditionalOnMissingBean
		@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
		public Google google(final ConnectionRepository repository) {
			final Connection<Google> connection = repository.findPrimaryConnection(Google.class);
			return connection != null ? connection.getApi() : null;
		}

		@Bean(name = { "connect/googleConnect", "connect/googleConnected" })
		@ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
		public GenericConnectionStatusView googleConnectView() {
			return new GenericConnectionStatusView("google", "Google");
		}

		@Override
		public void addConnectionFactories(final ConnectionFactoryConfigurer configurer, final Environment environment) {
			final GoogleConnectionFactory factory = new GoogleConnectionFactory(this.properties.getAppId(),
					this.properties.getAppSecret());
			factory.setScope("email profile");
			configurer.addConnectionFactory(factory);
		}
	}

}
