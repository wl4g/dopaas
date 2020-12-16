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
package com.wl4g.devops.doc.plugin.swagger.springfox.swagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;

import java.util.List;

import static java.util.Optional.ofNullable;

import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.wl4g.devops.doc.plugin.swagger.config.DocumentionHolder;
import com.wl4g.devops.doc.plugin.swagger.config.swagger2.Swagger2Properties;
import com.wl4g.devops.doc.plugin.swagger.springfox.plugin.ApiVersionPathsRequestHandlerCombiner;
import com.wl4g.devops.doc.plugin.swagger.springfox.plugin.AuthorOperationBuilderPlugin;
import com.wl4g.devops.doc.plugin.swagger.springfox.plugin.OrderOperationBuilderPlugin;
import com.wl4g.devops.doc.plugin.swagger.springfox.plugin.VersionedApiListingPlugin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Info;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;

/**
 * {@link SpringfoxSwagger2Configuration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-09
 * @sine v1.0
 * @see {@link Swagger2ControllerWebMvc}
 * @see {@link Swagger2Controller}
 */
@EnableSwagger2
public class SpringfoxSwagger2Configuration {

	@SuppressWarnings("unchecked")
	@Bean
	public Docket springfoxSwagger2Docket() {
		Swagger2Properties config = (Swagger2Properties) DocumentionHolder.get().getConfig();

		ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).groupName(config.getGroupName())
				.apiInfo(buildApiInfo(config)).select().paths(PathSelectors.any());

		// Scanning apis conditions.
		builder.apis(withClassAnnotation(Api.class));
		builder.apis(withMethodAnnotation(ApiOperation.class));

		/**
		 * Note: since there are multiple scan package paths, must use the 'or'
		 * combination condition. refer to
		 * {@link springfox.documentation.spring.web.scanners.ApiListingReferenceScanner#scan(DocumentationContext)}
		 */
		List<String> resourcePackages = DocumentionHolder.get().getResourcePackages();
		Predicate<RequestHandler> allOrPredicate = Predicates.or(resourcePackages.stream()
				.map(scanPackage -> createRequestHandlerPredicate(scanPackage)).toArray(Predicate[]::new));
		builder.apis(allOrPredicate);

		return builder.build();
	}

	@Bean
	public VersionedApiListingPlugin versionedApiListingPlugin(TypeResolver resolver) {
		return new VersionedApiListingPlugin(resolver);
	}

	@Bean
	public AuthorOperationBuilderPlugin authorOperationBuilderPlugin() {
		return new AuthorOperationBuilderPlugin();
	}

	@Bean
	public OrderOperationBuilderPlugin orderOperationBuilderPlugin() {
		return new OrderOperationBuilderPlugin();
	}

	@Bean
	public ApiVersionPathsRequestHandlerCombiner apiVersionPathsRequestHandlerCombiner() {
		return new ApiVersionPathsRequestHandlerCombiner();
	}

	private ApiInfo buildApiInfo(Swagger2Properties config) {
		Info info = config.getInfo();
		io.swagger.models.Contact contact = info.getContact();
		return new ApiInfoBuilder().title(info.getTitle()).description(info.getDescription()).license(info.getLicense().getName())
				.licenseUrl(info.getLicense().getUrl())
				.contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail())).version(info.getVersion()).build();
	}

	/**
	 * Refer to {@link RequestHandlerSelectors#basePackage(String)}
	 * 
	 * @param scanPackage
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Predicate<RequestHandler> createRequestHandlerPredicate(String scanPackage) {
		return handler -> ofNullable(handler.declaringClass())
				.map(clazz -> ClassUtils.getPackageName(clazz).startsWith(scanPackage)).orElse(true);
	}

}
