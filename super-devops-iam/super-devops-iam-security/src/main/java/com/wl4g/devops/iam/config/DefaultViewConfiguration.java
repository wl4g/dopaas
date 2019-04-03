package com.wl4g.devops.iam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.Annotation;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.iam.web.DefaultViewController;

/**
 * Default view configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月8日
 * @since
 */
@AutoConfigureAfter({ IamConfiguration.class })
public class DefaultViewConfiguration extends AbstractOptionalControllerConfiguration {

	@Autowired
	private IamProperties config;

	@Bean
	public DefaultViewController defaultViewController() {
		return new DefaultViewController();
	}

	@Override
	protected String getMappingPrefix() {
		return this.config.getDefaultViewBaseUri();
	}

	@Bean
	public PrefixHandlerMapping defaultViewControllerPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return com.wl4g.devops.iam.annotation.DefaultViewController.class;
	}

}
