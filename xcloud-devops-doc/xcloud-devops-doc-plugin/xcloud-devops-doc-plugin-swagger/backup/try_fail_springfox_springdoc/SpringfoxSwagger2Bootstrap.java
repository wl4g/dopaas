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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWithAny;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import static com.wl4g.components.common.collection.Collections2.safeArrayToList;
import static com.wl4g.devops.doc.plugin.swagger.util.SpringUtils.SCAN_BASE_PACKAGES;
//import static com.wl4g.devops.doc.plugin.swagger.springfox.SpringfoxSwagger2Bootstrap.SpringfoxConfigurerExcludeFilter;
//import static com.wl4g.devops.doc.plugin.swagger.springfox.SpringfoxSwagger2Bootstrap.SpringfoxConfigurerIncludeFilter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import com.wl4g.devops.doc.plugin.swagger.util.SpringUtils;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

/**
 * {@link SpringfoxSwagger2Bootstrap}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-09
 * @sine v1.0
 * @see {@link Swagger2ControllerWebMvc}
 * @see {@link Swagger2Controller}
 */
// @SpringBootConfiguration
// @EnableAutoConfiguration
// @ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes =
// { TypeExcludeFilter.class,
// SpringfoxConfigurerExcludeFilter.class }) }, includeFilters = {
// @Filter(type = FilterType.CUSTOM, classes = {
// SpringfoxConfigurerIncludeFilter.class }) })
@SpringBootApplication(scanBasePackages = "${" + SCAN_BASE_PACKAGES + ":com.wl4g.devops}")
@EnableSwagger2
public class SpringfoxSwagger2Bootstrap {

	@Bean
	public Docket springfoxSwaggerDocket() {
		ApiInfo apiInfo = new ApiInfoBuilder().title("Demo API文档").description("").license("")
				.contact(new Contact("Wanglsir", "#", "Wanglsir")).version("v1.0.0").build();
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select()
				.apis(RequestHandlerSelectors.basePackage("com.wl4g"))
				// .apis(RequestHandlerSelectors.withMethodAnnotation(Api.class))
				.paths(PathSelectors.any()).build();
	}

	// public static class SpringfoxConfigurerExcludeFilter implements
	// TypeFilter, BeanClassLoaderAware {
	//
	// private ClassLoader beanClassLoader;
	// private volatile List<String> autoConfigurations;
	//
	// @Override
	// public void setBeanClassLoader(ClassLoader beanClassLoader) {
	// this.beanClassLoader = beanClassLoader;
	// }
	//
	// @Override
	// public boolean match(MetadataReader metadataReader, MetadataReaderFactory
	// metadataReaderFactory) throws IOException {
	// return isConfiguration(metadataReader) &&
	// isAutoConfiguration(metadataReader);
	// }
	//
	// private boolean isConfiguration(MetadataReader metadataReader) {
	// return
	// metadataReader.getAnnotationMetadata().isAnnotated(Configuration.class.getName());
	// }
	//
	// private boolean isAutoConfiguration(MetadataReader metadataReader) {
	// String className = metadataReader.getClassMetadata().getClassName();
	// if (!startsWithAny(className, "org.spring", "springfox") &&
	// !startsWithAny(className, "com.wl4g.devops")) {
	// return false;
	// }
	// return getAutoConfigurations().contains(className);
	// }
	//
	// protected List<String> getAutoConfigurations() {
	// if (this.autoConfigurations == null) {
	// this.autoConfigurations =
	// SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,
	// this.beanClassLoader);
	// }
	// return this.autoConfigurations;
	// }
	//
	// }
	//
	// public static class SpringfoxConfigurerIncludeFilter implements
	// TypeFilter {
	//
	// @Override
	// public boolean match(MetadataReader metadataReader, MetadataReaderFactory
	// metadataReaderFactory) throws IOException {
	// return startsWithAny(metadataReader.getClassMetadata().getClassName(),
	// "com.wl4g", "com.github");
	// }
	//
	// }

}
