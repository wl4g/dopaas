package com.wl4g.devops.esm.config;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.Bean;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.esm.annotation.EnableEsmScalingController;
import com.wl4g.devops.esm.rest.EsmScalingController;

public class EsmServerAutoConfiguration extends AbstractOptionalControllerConfiguration {

	//
	// Server controller.
	//

	@Override
	public String getMappingPrefix() {
		return "/scaling";
	}

	@Override
	public Class<? extends Annotation> annotationClass() {
		return EnableEsmScalingController.class;
	}

	@Bean
	public PrefixHandlerMapping esmScalingControllerPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Bean
	public EsmScalingController esmScalingController() {
		return new EsmScalingController();
	}

	//
	// ESM service.
	//

}
