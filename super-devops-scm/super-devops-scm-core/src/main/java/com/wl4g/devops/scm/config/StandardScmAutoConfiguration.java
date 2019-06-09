package com.wl4g.devops.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.scm.StandardConfigContextHandler;
import com.wl4g.devops.scm.context.ConfigContextHandler;

@Configuration
public class StandardScmAutoConfiguration {

	@Bean
	public ConfigContextHandler configContextHandler() {
		return new StandardConfigContextHandler();
	}

}
