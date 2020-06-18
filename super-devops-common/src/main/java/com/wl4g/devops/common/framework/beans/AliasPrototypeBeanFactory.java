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
package com.wl4g.devops.common.framework.beans;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

/**
 * Delegate prototype bean factory.</br>
 * 
 * @author Wangl.sir <Wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0.0 2019-10-09
 * @since
 */
public class AliasPrototypeBeanFactory {

	/**
	 * Global delegate alias prototype bean class registry.
	 */
	final private static Map<String, Class<PrototypeBean>> aliasRegistry = synchronizedMap(new HashMap<>());

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
	public <T extends PrototypeBean> T getPrototypeBean(@NotNull String alias, @NotNull Object... args) {
		Class<?> beanClass = aliasRegistry.get(alias);
		notNull(beanClass, "No such prototype bean class for '%s'", alias);
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
	public static class AliasPrototypeBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
		final protected SmartLogger log = getLogger(getClass());

		@SuppressWarnings("unchecked")
		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
			for (String beanName : registry.getBeanDefinitionNames()) {
				BeanDefinition bd = registry.getBeanDefinition(beanName);
				if (nonNull(beanName) && bd.isPrototype()) {
					if (bd instanceof AnnotatedBeanDefinition) {
						log.debug("Register prototype bean with AnnotatedBeanDefinition... - {}", bd);

						AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) bd;
						String prototypeBeanClassName = null;
						// Bean alias, Used to get prototype bean instance.
						String[] beanAliass = null;
						if (bd instanceof ScannedGenericBeanDefinition) {
							// Using with @Service/@Component...
							AnnotationMetadata metadata = abd.getMetadata();
							if (nonNull(metadata)) {
								prototypeBeanClassName = metadata.getClassName();
								beanAliass = getAnnotationDelegateAliasValue(metadata);
							}
						} else {
							/**
							 * Using with {@link Configuration}
							 * See:ConfigurationClassBeanDefinition
							 */
							MethodMetadata metadata = abd.getFactoryMethodMetadata();
							if (nonNull(metadata)) {
								prototypeBeanClassName = metadata.getReturnTypeName();
								beanAliass = getAnnotationDelegateAliasValue(metadata);
							}
						}
						if (!isBlank(prototypeBeanClassName) && nonNull(beanAliass)) {
							try {
								Class<?> beanClass = forName(prototypeBeanClassName, getDefaultClassLoader());
								if (PrototypeBean.class.isAssignableFrom(beanClass)) {
									registerPrototypeBean((Class<PrototypeBean>) beanClass, ArrayUtils.add(beanAliass, beanName));
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
					.getAllAnnotationAttributes(PrototypeAlias.class.getName());
			if (!CollectionUtils.isEmpty(annotationPropertyValues)) {
				/**
				 * See:{@link DelegateAlias}
				 */
				Object values = annotationPropertyValues.get("value");
				if (nonNull(values) && values instanceof List) {
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
		private void registerPrototypeBean(Class<? extends PrototypeBean> beanClass, String... beanAliass) {
			if (nonNull(beanAliass)) {
				for (String alias : beanAliass) {
					if (isNull(aliasRegistry.putIfAbsent(alias, (Class<PrototypeBean>) beanClass))) {
						log.debug("Registered prototype bean for alias: {}, class: {}", alias, beanClass);
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
	@Import(AliasPrototypeBeanDefinitionRegistrar.class)
	public static class AliasPrototypeBeanFactoryAutoConfiguration {

		@Bean
		public AliasPrototypeBeanFactory delegateAliasPrototypeBeanFactory() {
			return new AliasPrototypeBeanFactory();
		}

	}

}