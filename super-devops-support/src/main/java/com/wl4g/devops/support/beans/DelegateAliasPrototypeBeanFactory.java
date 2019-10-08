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
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.lang.OnceModifiableMap;

/**
 * Delegate prototype bean factory.</br>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 * @see {@link MapperScannerRegistrar} struct implements.
 */
public class DelegateAliasPrototypeBeanFactory implements ImportBeanDefinitionRegistrar {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Delegate prototype bean class registry.
	 */
	final protected Map<String, Class<? extends DelegateAliasPrototypeBean>> beanClassRegistry = new OnceModifiableMap<>(
			new HashMap<>());

	@Autowired
	protected BeanFactory beanFactory;

	@SuppressWarnings("unchecked")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		for (String beanName : registry.getBeanDefinitionNames()) {
			BeanDefinition bd = registry.getBeanDefinition(beanName);
			if (Objects.nonNull(beanName) && bd.isPrototype()) {
				if (bd instanceof AnnotatedBeanDefinition) {
					if (log.isDebugEnabled()) {
						log.debug("Register prototype bean class with annotatedBeanDefinition ... - {}", bd);
					}

					AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) bd;
					String prototypeBeanClassName = null;
					// Bean alias, Used to get prototype bean
					// instance.
					String beanAlias = beanName;
					if (bd instanceof ScannedGenericBeanDefinition) {
						/*
						 * Using with @Service/@Component...
						 */
						AnnotationMetadata metadata = abd.getMetadata();
						if (Objects.nonNull(metadata)) {
							prototypeBeanClassName = metadata.getClassName();
							// TODO get custom alias
						}
					} else {
						/*
						 * Using with @Configuration
						 * See:ConfigurationClassBeanDefinition
						 */
						MethodMetadata metadata = abd.getFactoryMethodMetadata();
						if (Objects.nonNull(metadata)) {
							prototypeBeanClassName = metadata.getReturnTypeName();
							// TODO get custom alias
						}
					}
					if (!isBlank(prototypeBeanClassName)) {
						try {
							Class<?> beanClass = forName(prototypeBeanClassName, getDefaultClassLoader());
							if (DelegateAliasPrototypeBean.class.isAssignableFrom(beanClass)) {
								if (Objects.isNull(beanClassRegistry.putIfAbsent(beanAlias,
										(Class<? extends DelegateAliasPrototypeBean>) beanClass))) {
									if (log.isDebugEnabled()) {
										log.debug("Registed prototype bean class - {}", beanClass);
									}
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
	 * Get and create prototype bean instance by alias.
	 * 
	 * @param alias
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends DelegateAliasPrototypeBean> T getPrototypeBean(@NotNull String alias, @NotNull Object... args) {
		Class<?> beanClass = this.beanClassRegistry.get(alias);
		Assert.notNull(beanClass, String.format("Unsupported prototype beanClass for '%s'", alias));
		return (T) this.beanFactory.getBean(beanClass, args);
	}

}