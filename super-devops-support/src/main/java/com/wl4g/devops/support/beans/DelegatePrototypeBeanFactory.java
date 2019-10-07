/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.support.beans;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.lang.OnceModifiableMap;

/**
 * Delegate prototype bean factory.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 * @see {@link MapperScannerRegistrar} struct implements.
 */
public class DelegatePrototypeBeanFactory<T> implements ImportBeanDefinitionRegistrar {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Delegate prototype bean class registry.
	 */
	final protected Map<String, Class<? extends T>> beanClassRegistry = new OnceModifiableMap<>(new HashMap<>());

	@Autowired
	protected BeanFactory beanFactory;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		for (String bdn : registry.getBeanDefinitionNames()) {
			BeanDefinition bd = registry.getBeanDefinition(bdn);
			if (bd.isPrototype()) {
				if (bd instanceof AnnotatedBeanDefinition) {
					if (log.isDebugEnabled()) {
						log.debug("Register prototype bean class with annotatedBeanDefinition ... - {}", bd);
					}

					AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) bd;
					String prototypeBeanClassName = null;
					if (bd instanceof ScannedGenericBeanDefinition) {
						/*
						 * Using with @Service/@Component...
						 */
						prototypeBeanClassName = abd.getMetadata().getClassName();
					} else {
						/*
						 * See:ConfigurationClassBeanDefinition, using
						 * with @Configuration
						 */
						if (Objects.nonNull(abd.getFactoryMethodMetadata())) {
							prototypeBeanClassName = abd.getFactoryMethodMetadata().getReturnTypeName();
						}
					}
					if (!isBlank(prototypeBeanClassName)) {
						try {
							@SuppressWarnings("unchecked")
							Class<? extends T> beanClass = (Class<? extends T>) forName(prototypeBeanClassName,
									getDefaultClassLoader());
							if (Objects.isNull(beanClassRegistry.putIfAbsent("", beanClass))) {
								if (log.isDebugEnabled()) {
									log.debug("Registed prototype bean class - {}", beanClass);
								}
							}
						} catch (LinkageError | ClassNotFoundException e) {
							throw new IllegalStateException(e);
						}
					}
				}
			}
		}
	}

	/**
	 * Get new prototype bean instance.
	 * 
	 * @param type
	 * @return
	 */
	public T getBean(@NotNull T type, @NotNull Object... args) {
		Class<? extends T> beanClass = this.beanClassRegistry.get(type);
		Assert.notNull(beanClass, String.format("Unsupported prototype beanClass for '%s'", type));
		return this.beanFactory.getBean(beanClass, args);
	}

}