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
package com.wl4g.dopaas.esm.config;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.Bean;

import com.wl4g.dopaas.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.dopaas.esm.annotation.EnableEsmScalingController;
import com.wl4g.dopaas.esm.console.EsmScalingConsole;
import com.wl4g.dopaas.esm.rest.EsmScalingController;

public class EsmAutoConfiguration extends AbstractOptionalControllerConfiguration {

	//
	// ESM server RESTful.
	//

	@Override
	public String getMappingPrefix() {
		return "/scaling";
	}

	@Override
	public Class<? extends Annotation> annotationClass() {
		return EnableEsmScalingController.class;
	}

	@Bean
	public PrefixHandlerMapping esmScalingControllerPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Bean
	public EsmScalingController esmScalingController() {
		return new EsmScalingController();
	}

	//
	// ESM console.
	//

	@Bean
	public EsmScalingConsole esmScalingConsole() {
		return new EsmScalingConsole();
	}

}