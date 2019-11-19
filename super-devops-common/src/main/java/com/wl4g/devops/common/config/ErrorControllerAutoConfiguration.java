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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.annotation.DevopsErrorController;
import com.wl4g.devops.common.web.error.CompositeErrorConfiguringAdapter;
import com.wl4g.devops.common.web.error.DefaultBasicErrorConfiguring;
import com.wl4g.devops.common.web.error.ErrorConfiguring;
import com.wl4g.devops.common.web.error.SmartGlobalErrorController;

/**
 * Smart DevOps error controller auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月10日
 * @since
 */
@Configuration
@ConditionalOnProperty(value = "spring.cloud.devops.error.enabled", matchIfMissing = true)
public class ErrorControllerAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	@Bean
	public ErrorConfiguring defaultBasicErrorConfiguring() {
		return new DefaultBasicErrorConfiguring();
	}

	@Bean
	public CompositeErrorConfiguringAdapter compositeErrorConfiguringAdapter(List<ErrorConfiguring> configures) {
		return new CompositeErrorConfiguringAdapter(configures);
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.error")
	public ErrorControllerProperties errorControllerProperties() {
		return new ErrorControllerProperties();
	}

	@Bean
	public SmartGlobalErrorController smartGlobalErrorController(ErrorAttributes errorAttrs,
			CompositeErrorConfiguringAdapter adapter, ErrorControllerProperties config) {
		return new SmartGlobalErrorController(errorAttrs, config, adapter);
	}

	@Bean
	public PrefixHandlerMapping errorControllerPrefixHandlerMapping() {
		// Fixed to Spring-MVC default: /
		return super.newPrefixHandlerMapping("/", DevopsErrorController.class);
	}

	/**
	 * Error controller properties.</br>
	 * <font color=red>Note: When {@link @ConfigurationProperties} is used, the
	 * field name cannot contain numbers, otherwise</font>
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-11-02
	 * @since
	 */
	public static class ErrorControllerProperties {
		final public static String DEFAULT_DIR_VIEW = "/default-error-view/";

		// <<Alarm: Field name cannot contain numbers>>.
		private String basePath = DEFAULT_DIR_VIEW;
		private String notFountUriOrTpl = "404.tpl.html"; //
		private String unauthorizedUriOrTpl = "403.tpl.html";
		private String errorUriOrTpl = "50x.tpl.html";

		/**
		 * Error return previous page URI.</br>
		 * Default for browser location origin.
		 */
		private String homeUri = "javascript:location.href = location.origin";

		public String getBasePath() {
			return basePath;
		}

		public void setBasePath(String basePath) {
			if (!isBlank(basePath)) {
				this.basePath = basePath;
			}
		}

		public String getNotFountUriOrTpl() {
			return notFountUriOrTpl;
		}

		public void setNotFountUriOrTpl(String notFountUriOrTpl) {
			if (!isBlank(notFountUriOrTpl)) {
				this.notFountUriOrTpl = notFountUriOrTpl;
			}
		}

		public String getUnauthorizedUriOrTpl() {
			return unauthorizedUriOrTpl;
		}

		public void setUnauthorizedUriOrTpl(String unauthorizedUriOrTpl) {
			if (!isBlank(unauthorizedUriOrTpl)) {
				this.unauthorizedUriOrTpl = unauthorizedUriOrTpl;
			}
		}

		public String getErrorUriOrTpl() {
			return errorUriOrTpl;
		}

		public void setErrorUriOrTpl(String errorUriOrTpl) {
			if (!isBlank(errorUriOrTpl)) {
				this.errorUriOrTpl = errorUriOrTpl;
			}
		}

		public String getHomeUri() {
			return homeUri;
		}

		public void setHomeUri(String homeUri) {
			if (!isBlank(homeUri)) {
				this.homeUri = homeUri;
			}
		}

	}

}