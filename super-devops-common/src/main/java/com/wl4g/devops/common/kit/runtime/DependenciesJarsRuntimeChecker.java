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
package com.wl4g.devops.common.kit.runtime;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import com.wl4g.devops.common.exception.framework.InvalidDependsSpecificationException;

/**
 * Dependencies jars checker.
 * 
 * @author Wangl.sir
 * @version v1.0.0 2019-09-24
 * @since
 */
public class DependenciesJarsRuntimeChecker implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		/*
		 * Check: hibernate-validate-6.x.jar/validation-api-2.x.jar
		 */
		if (!ClassUtils.isPresent("javax.validation.constraints.NotBlank", Thread.currentThread().getContextClassLoader())) {
			throw new InvalidDependsSpecificationException(
					"Incompatible version dependency errors, requiring: validation-api-2.x and hibernate-validate-6.x, because which may result in runtime unknown errors");
		}

	}

	@Configuration
	@ConditionalOnProperty(value = "spring.cloud.devops.kit.runtime.enabled", matchIfMissing = true)
	public static class DependencyJarRuntimeCheckerAutoConfiguration {

		@Bean
		public DependenciesJarsRuntimeChecker dependencyJarRuntimeChecker() {
			return new DependenciesJarsRuntimeChecker();
		}

	}

}