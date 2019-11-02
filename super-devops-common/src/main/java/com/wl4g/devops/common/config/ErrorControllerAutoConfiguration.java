package com.wl4g.devops.common.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.annotation.Annotation;
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
public class ErrorControllerAutoConfiguration extends AbstractOptionalControllerAutoConfiguration {

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
		return super.createPrefixHandlerMapping();
	}

	@Override
	protected String getMappingPrefix() {
		return "/"; // Fixed to Spring-MVC default: /
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return DevopsErrorController.class;
	}

	/**
	 * Error controller properties.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-11-02
	 * @since
	 */
	public static class ErrorControllerProperties {
		final public static String DEFAULT_DIR_VIEW = "/default-error-view/";

		private String basePath = DEFAULT_DIR_VIEW;
		private String errorTplOrUri404 = "1404.tpl.html";
		private String errorTplOrUri403 = "403.tpl.html";
		private String errorTplOrUri50x = "50x.tpl.html";

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

		public String getErrorTplOrUri404() {
			return errorTplOrUri404;
		}

		public void setErrorTplOrUri404(String errorTplOrUri404) {
			if (!isBlank(errorTplOrUri404)) {
				this.errorTplOrUri404 = errorTplOrUri404;
			}
		}

		public String getErrorTplOrUri403() {
			return errorTplOrUri403;
		}

		public void setErrorTplOrUri403(String errorTplOrUri403) {
			if (!isBlank(errorTplOrUri403)) {
				this.errorTplOrUri403 = errorTplOrUri403;
			}
		}

		public String getErrorTplOrUri50x() {
			return errorTplOrUri50x;
		}

		public void setErrorTplOrUri50x(String errorTplOrUri50x) {
			if (!isBlank(errorTplOrUri50x)) {
				this.errorTplOrUri50x = errorTplOrUri50x;
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
