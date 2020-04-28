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
package com.wl4g.devops.components.shell.config;

import static java.util.Objects.nonNull;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import static org.springframework.core.annotation.AnnotationUtils.*;

import com.wl4g.devops.components.shell.annotation.ShellComponent;
import com.wl4g.devops.components.shell.registry.ShellHandlerRegistrar;

/**
 * Annotation shell command handler registrar.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class AnnotationShellHandlerRegistrar extends ShellHandlerRegistrar implements BeanPostProcessor {
	private static final long serialVersionUID = 1281712204663635026L;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		ShellComponent component = findAnnotation(bean.getClass(), ShellComponent.class);
		if (nonNull(component)) {
			register(bean);
		}
		return bean;
	}

}