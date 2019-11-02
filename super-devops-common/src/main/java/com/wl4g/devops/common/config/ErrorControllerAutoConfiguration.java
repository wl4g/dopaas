package com.wl4g.devops.common.config;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
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
	public SmartGlobalErrorController smartGlobalErrorController(ErrorAttributes errorAttributes) {
		return new SmartGlobalErrorController(errorAttributes);
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

}
