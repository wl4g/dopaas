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
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

/**
 * Delegate prototype bean factory.</br>
 * 
 * @author Wangl.sir <Wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0.0 2019-10-09
 * @since
 */
public class DelegateAliasPrototypeBeanFactory {

	/**
	 * Global delegate alias prototype bean class registry.
	 */
	final private static Map<String, Class<DelegateAliasPrototypeBean>> globalAliasRegistry = Collections
			.synchronizedMap(new HashMap<>());

	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Get and create prototype bean instance by alias.
	 * 
	 * @param alias
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends DelegateAliasPrototypeBean> T getPrototypeBean(@NotNull String alias, @NotNull Object... args) {
		Class<?> beanClass = globalAliasRegistry.get(alias);
		Assert.notNull(beanClass, String.format("No such prototype bean class for '%s'", alias));
		return (T) beanFactory.getBean(beanClass, args);
	}

	/**
	 * Delegate alias prototype bean importing auto registrar.
	 * 
	 * @author Wangl.sir <Wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0.0 2019-10-09
	 * @since
	 * @see {@link MapperScannerRegistrar} struct implements.
	 */
	public static class DelegateAliasPrototypeBeanAutoRegistrar implements ImportBeanDefinitionRegistrar {

		final private Logger log = LoggerFactory.getLogger(getClass());

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
						String[] beanAliass = null;
						if (bd instanceof ScannedGenericBeanDefinition) {
							/*
							 * Using with @Service/@Component...
							 */
							AnnotationMetadata metadata = abd.getMetadata();
							if (Objects.nonNull(metadata)) {
								prototypeBeanClassName = metadata.getClassName();
								beanAliass = getAnnotationDelegateAliasValue(metadata);
							}
						} else {
							/*
							 * Using with @Configuration
							 * See:ConfigurationClassBeanDefinition
							 */
							MethodMetadata metadata = abd.getFactoryMethodMetadata();
							if (Objects.nonNull(metadata)) {
								prototypeBeanClassName = metadata.getReturnTypeName();
								beanAliass = getAnnotationDelegateAliasValue(metadata);
							}
						}
						if (!isBlank(prototypeBeanClassName)) {
							try {
								Class<?> beanClass = forName(prototypeBeanClassName, getDefaultClassLoader());
								if (DelegateAliasPrototypeBean.class.isAssignableFrom(beanClass)) {
									putPrototypeBeanClassAlias((Class<DelegateAliasPrototypeBean>) beanClass,
											ArrayUtils.add(beanAliass, beanName));
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
		 * Get annotation delegate alias value.
		 * 
		 * @param metadata
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		private String[] getAnnotationDelegateAliasValue(AnnotatedTypeMetadata metadata) {
			MultiValueMap<String, Object> annotationPropertyValues = metadata
					.getAllAnnotationAttributes(DelegateAlias.class.getName());
			if (!CollectionUtils.isEmpty(annotationPropertyValues)) {
				/**
				 * See:{@link DelegateAlias}
				 */
				Object values = annotationPropertyValues.get("value");
				if (Objects.nonNull(values) && values instanceof List) {
					List _values = ((List) values);
					if (!isEmpty(_values)) {
						return (String[]) _values.get(0);
					}
				}
			}
			return null;
		}

		/**
		 * Saved prototype bean class alias to factory registry.
		 * 
		 * @param beanClass
		 * @param beanAliass
		 */
		@SuppressWarnings("unchecked")
		private void putPrototypeBeanClassAlias(Class<? extends DelegateAliasPrototypeBean> beanClass, String... beanAliass) {
			if (Objects.nonNull(beanAliass)) {
				for (String alias : beanAliass) {
					if (Objects.isNull(globalAliasRegistry.putIfAbsent(alias, (Class<DelegateAliasPrototypeBean>) beanClass))) {
						if (log.isDebugEnabled()) {
							log.debug("Registed prototype bean class - {}", beanClass);
						}
					}
				}
			}
		}

	}

	/**
	 * Delegate alias prototype bean auto configuration.
	 * 
	 * @author Wangl.sir <Wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0.0 2019-10-09
	 * @since
	 */
	@Configuration
	@Import(DelegateAliasPrototypeBeanAutoRegistrar.class)
	public static class DelegateAliasPrototypeBeanAutoConfiguration {

		@Bean
		public DelegateAliasPrototypeBeanFactory delegateAliasPrototypeBeanFactory() {
			return new DelegateAliasPrototypeBeanFactory();
		}

	}

}