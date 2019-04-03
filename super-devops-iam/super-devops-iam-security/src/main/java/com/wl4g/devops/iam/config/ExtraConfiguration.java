package com.wl4g.devops.iam.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.iam.annotation.ExtraController;
import com.wl4g.devops.iam.web.DiabloExtraController;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_BASE;

import java.lang.annotation.Annotation;

/**
 * IAM extra configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月8日
 * @since
 */
@AutoConfigureAfter({ IamConfiguration.class })
public class ExtraConfiguration extends AbstractOptionalControllerConfiguration {

	@Bean
	public DiabloExtraController diabloExtraController() {
		return new DiabloExtraController();
	}

	@Override
	protected String getMappingPrefix() {
		return URI_S_EXT_BASE;
	}

	@Bean
	public PrefixHandlerMapping extraControllerPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return ExtraController.class;
	}

}
