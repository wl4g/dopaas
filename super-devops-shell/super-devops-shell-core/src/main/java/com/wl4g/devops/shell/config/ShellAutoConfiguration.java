package com.wl4g.devops.shell.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.shell.processor.ServerProcessor;

/**
 * Shell component services auto configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月30日
 * @since
 */
public class ShellAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.shell")
	public ShellProperties shellProperties() {
		return new ShellProperties();
	}

	@Bean
	public AnnotationBeanRegistry annotationBeanRegistry() {
		return new AnnotationBeanRegistry();
	}

	@Bean
	@ConditionalOnMissingBean
	public ServerProcessor serverProcessor(ShellProperties config, AnnotationBeanRegistry registry) {
		return new ServerProcessor(config, registry);
	}

}
