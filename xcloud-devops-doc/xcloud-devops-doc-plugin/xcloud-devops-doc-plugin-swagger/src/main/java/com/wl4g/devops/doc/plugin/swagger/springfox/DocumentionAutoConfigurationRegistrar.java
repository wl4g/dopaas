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
package com.wl4g.devops.doc.plugin.swagger.springfox;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static java.util.Objects.isNull;

import javax.ws.rs.Path;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wl4g.devops.doc.plugin.swagger.config.DocumentionHolder;

import io.swagger.annotations.Api;

/**
 * {@link DocumentionAutoConfigurationRegistrar}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-14
 * @sine v1.0
 * @see
 */
public class DocumentionAutoConfigurationRegistrar
		implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

	private Environment environment;

	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry,
			BeanNameGenerator beanNameGenerator) {
		AnnotationAttributes annoAttrs = AnnotationAttributes
				.fromMap(metadata.getAnnotationAttributes(EnableDocumentionAutoConfiguration.class.getName()));
		if (!isNull(annoAttrs)) {
			Class<?> autoConfigClass = DocumentionHolder.get().getProvider().getAutoConfigClass();
			notNull(autoConfigClass, "Documention auto configuration class is requires.");

			registerDocumentionBean(registry, beanNameGenerator, autoConfigClass);
			registerDocumentionApis(registry, beanNameGenerator);
		}
	}

	private void registerDocumentionApis(BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false, environment);
		scanner.setBeanNameGenerator(beanNameGenerator);
		scanner.setResourceLoader(resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(RequestMapping.class, true, true));
		scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class, true, true));
		scanner.addIncludeFilter(new AnnotationTypeFilter(ControllerAdvice.class, true, true));
		scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class, true, true));
		scanner.addIncludeFilter(new AnnotationTypeFilter(RestControllerAdvice.class, true, true));
		scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class, true, true));
		scanner.addIncludeFilter(new AnnotationTypeFilter(Api.class, true, true));

		for (String scanPackage : DocumentionHolder.get().getResourcePackages()) {
			// Registers scanned bean
			scanner.scan(scanPackage);
		}

	}

	private void registerDocumentionBean(BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator,
			Class<?> beanClass) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
