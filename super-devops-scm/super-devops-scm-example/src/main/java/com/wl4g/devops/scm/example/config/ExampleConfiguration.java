package com.wl4g.devops.scm.example.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.scm.example.service.ExampleService2;

@Configuration
public class ExampleConfiguration {

	@Bean
	@RefreshScope
	public ExampleService2 exampleService2() {
		System.out.println("@Bean create exampleService2 ...");
		return new ExampleService2();
	}

}
