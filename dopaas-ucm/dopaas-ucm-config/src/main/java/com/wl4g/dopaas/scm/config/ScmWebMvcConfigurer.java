/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.scm.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * SCM web mvc configurer
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月4日
 * @since
 */
public class ScmWebMvcConfigurer extends WebMvcConfigurerAdapter {

	final private ScmProperties config;

	final private ThreadPoolTaskExecutor executor;

	public ScmWebMvcConfigurer(ScmProperties config, ThreadPoolTaskExecutor executor) {
		super();
		this.config = config;
		this.executor = executor;
	}

	/**
	 * Configure asynchronous support, set up a work-threads pool for
	 * asynchronous execution of business logic, and set the default timeout
	 * time to 60 seconds
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor(executor);
		configurer.setDefaultTimeout(config.getLongPollTimeout());
	}

}