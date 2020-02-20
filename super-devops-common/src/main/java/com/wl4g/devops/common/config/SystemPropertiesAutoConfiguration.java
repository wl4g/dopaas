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
package com.wl4g.devops.common.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.wl4g.devops.common.web.RespBase.ErrorPromptMessageBuilder;

/**
 * System properties auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月20日
 * @since
 */
@Configuration
public class SystemPropertiesAutoConfiguration implements EnvironmentAware {

	/**
	 * API prompt max length.
	 */
	final private static int PROMPT_MAX_LEN = 4;

	@Override
	public void setEnvironment(Environment environment) {
		initSystemProperties(environment);
	}

	/**
	 * Initializaing system global properties.
	 * 
	 * @param env
	 */
	protected void initSystemProperties(Environment env) {
		// Setup api message prompt
		initErrorPrompt(env);
	}

	/**
	 * Initializing error prompt.
	 * 
	 * @param env
	 */
	protected void initErrorPrompt(Environment env) {
		String appName = env.getRequiredProperty("spring.application.name");
		if (appName.length() < PROMPT_MAX_LEN) {
			ErrorPromptMessageBuilder.setPrompt(appName);
		} else {
			ErrorPromptMessageBuilder.setPrompt(appName.substring(0, 4));
		}

	}

}