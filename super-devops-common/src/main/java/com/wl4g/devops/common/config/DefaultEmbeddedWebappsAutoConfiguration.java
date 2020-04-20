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
package com.wl4g.devops.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.web.embedded.GenericEmbeddedWebappsEndpoint;

import static com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties.*;

import java.util.Properties;

/**
 * Embedded webapps site configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月20日
 * @since
 */
@Configuration
public class DefaultEmbeddedWebappsAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	final private static String BEAN_DEFAULT_EMBEDDED_WEBAPPS_PROPERTIES = "defaultGenericEmbeddedWebappsProperties";
	final private static String BEAN_DEFAULT_EMBEDDED_WEBAPPS_ENDPOINT = "defaultGenericEmbeddedWebappsEndpoint";

	@Bean(BEAN_DEFAULT_EMBEDDED_WEBAPPS_PROPERTIES)
	@ConfigurationProperties(prefix = KEY_EMBEDDED_WEBAPP_BASE)
	@ConditionalOnProperty(value = KEY_EMBEDDED_WEBAPP_BASE + ".enabled", matchIfMissing = false)
	public GenericEmbeddedWebappsProperties defaultEmbeddedWebappsEndpointProperties() {
		return new GenericEmbeddedWebappsProperties() {
		};
	}

	@Bean(BEAN_DEFAULT_EMBEDDED_WEBAPPS_ENDPOINT)
	@ConditionalOnBean(GenericEmbeddedWebappsProperties.class)
	public GenericEmbeddedWebappsEndpoint defaultEmbeddedWebappsEndpoint(
			@Qualifier(BEAN_DEFAULT_EMBEDDED_WEBAPPS_PROPERTIES) GenericEmbeddedWebappsProperties config) {
		return new GenericEmbeddedWebappsEndpoint(config) {
		};
	}

	@Bean
	@ConditionalOnBean(GenericEmbeddedWebappsProperties.class)
	public PrefixHandlerMapping defaultEmbeddedWebappsEndpointPrefixHandlerMapping(
			@Qualifier(BEAN_DEFAULT_EMBEDDED_WEBAPPS_PROPERTIES) GenericEmbeddedWebappsProperties config,
			@Qualifier(BEAN_DEFAULT_EMBEDDED_WEBAPPS_ENDPOINT) GenericEmbeddedWebappsEndpoint endpoint) {
		return super.newPrefixHandlerMapping(config.getBaseUri(), endpoint);
	}

	/**
	 * {@link GenericEmbeddedWebappsProperties}
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年2月20日
	 * @since
	 */
	public static abstract class GenericEmbeddedWebappsProperties {
		final public static String KEY_EMBEDDED_WEBAPP_BASE = "spring.cloud.devops.embedded-webapps";

		/**
		 * Basic controller mapping access URI of default web application
		 */
		private String baseUri = "/default-view";

		/**
		 * The static file publishing directory of the default web application,
		 * such as: classpath*:/default-webapps
		 */
		private String webappLocation = "classpath*:/default-webapps";

		/**
		 * Media mapping
		 */
		private Properties mimeMapping = new Properties() {
			private static final long serialVersionUID = 6601944358361144649L;
			{
				put("html", "text/html");
				put("shtml", "text/html");
				put("htm", "text/html");
				put("css", "text/css");
				put("js", "application/javascript");

				put("icon", "image/icon");
				put("ico", "image/icon");
				put("gif", "image/gif");
				put("jpg", "image/jpeg");
				put("jpeg", "image/jpeg");
				put("bmp", "image/jpeg");

				put("doc", "application/msword");
				put("dot", "application/msword");
				put("docx", "  application/vnd.openxmlformats-officedocument.wordprocessingml.document");
				put("dotx", "  application/vnd.openxmlformats-officedocument.wordprocessingml.template");
				put("docm", "  application/vnd.ms-word.document.macroEnabled.12");
				put("dotm", "  application/vnd.ms-word.template.macroEnabled.12");

				put("xls", "application/vnd.ms-excel");
				put("xlt", "application/vnd.ms-excel");
				put("xla", "application/vnd.ms-excel");

				put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
				put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
				put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
				put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
				put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");

				put("ppt", "application/vnd.ms-powerpoint");
				put("pot", "application/vnd.ms-powerpoint");
				put("pps", "application/vnd.ms-powerpoint");
				put("ppa", "application/vnd.ms-powerpoint");

				put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
				put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
				put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
				put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
				put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
				put("potm", "application/vnd.ms-powerpoint.template.macroEnabled.12");
				put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");

				put("mdb", "application/vnd.ms-access");
			}
		};

		public GenericEmbeddedWebappsProperties() {
			super();
		}

		public GenericEmbeddedWebappsProperties(String baseUri, String webappLocation) {
			setBaseUri(baseUri);
			setWebappLocation(webappLocation);
		}

		public String getBaseUri() {
			return baseUri;
		}

		public void setBaseUri(String baseUri) {
			this.baseUri = baseUri;
		}

		public String getWebappLocation() {
			return webappLocation;
		}

		public void setWebappLocation(String webappLocation) {
			this.webappLocation = webappLocation;
		}

		public Properties getMimeMapping() {
			return mimeMapping;
		}

		public void setMimeMapping(Properties mimeMapping) {
			this.mimeMapping = mimeMapping;
		}

	}

}