package com.wl4g.devops.scm.config;

import java.lang.annotation.Annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.context.ConfigContextHandler;
import com.wl4g.devops.scm.context.NothingConfigSourceHandler;
import com.wl4g.devops.scm.endpoint.ScmServerEndpoint;
import com.wl4g.devops.scm.publish.ConfigSourcePublisher;
import com.wl4g.devops.scm.publish.DefaultConfigSourcePublisher;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;

public class ScmAutoConfiguration extends AbstractOptionalControllerConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ConfigContextHandler configSourceHandler() {
		return new NothingConfigSourceHandler();
	}

	@Bean
	public ConfigSourcePublisher configSourcePublisher() {
		return new DefaultConfigSourcePublisher();
	}

	//
	// Endpoint's
	//

	@Bean
	public ScmServerEndpoint scmServerEnndpoint() {
		return new ScmServerEndpoint();
	}

	@Bean
	public PrefixHandlerMapping scmServerEndpointPrefixHandlerMapping() {
		return createPrefixHandlerMapping();
	}

	@Override
	protected String getMappingPrefix() {
		return URI_S_BASE;
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return ScmEndpoint.class;
	}

}
