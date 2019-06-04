package com.wl4g.devops.scm.example.config;

import com.wl4g.devops.scm.example.service.ExampleService;
import com.wl4g.devops.scm.example.service.ExampleService2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleConfiguration {

	@Bean
	@RefreshScope
	@ConfigurationProperties(prefix = "example")
	public ExampleService2 exampleService2() {
		System.out.println("@Bean create exampleService2 ...");
		return new ExampleService2();
	}


	@Bean
	@RefreshScope
	@ConfigurationProperties(prefix = "example")
	public ExampleService exampleService() {
		System.out.println("@Bean create exampleService ...");
		return new ExampleService();
	}

}
