/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.scm.example.service;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

@RefreshScope
public class ExampleTask {

	@Scheduled(initialDelayString = "${devops.example.watch.init-delay:3000}", fixedDelayString = "${devops.example.watch.delay:2000}")
	public void run() {
		System.out.println("ExampleWatch#run..." + Thread.currentThread() + ", " + this);
	}

	// @org.springframework.context.annotation.Configuration
	public static class ExampleWatchConfiguration {

		@Bean
		public ExampleTask exampleWatch() {
			return new ExampleTask();
		}

	}

}