package com.wl4g.devops.scm.example.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

import com.wl4g.devops.scm.client.configure.RefreshBean;

@RefreshBean
public class ExampleWatch {

	@Scheduled(initialDelayString = "${devops.example.watch.init-delay:8000}", fixedDelayString = "${devops.example.watch.delay:2000}")
	public void run() {
		System.out.println("ExampleWatch.run..." + Thread.currentThread() + ", " + this);
	}

	// @org.springframework.context.annotation.Configuration
	@ConditionalOnBean({ ExampleService.class })
	public static class ExampleWatchConfiguration {

		@Bean
		public ExampleWatch exampleWatch() {
			return new ExampleWatch();
		}

	}

}
