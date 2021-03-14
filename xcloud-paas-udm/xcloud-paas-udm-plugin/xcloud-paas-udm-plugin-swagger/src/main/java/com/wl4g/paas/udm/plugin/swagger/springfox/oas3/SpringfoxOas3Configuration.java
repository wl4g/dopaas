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
package com.wl4g.paas.udm.plugin.swagger.springfox.oas3;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.classmate.TypeResolver;
import com.wl4g.paas.udm.plugin.swagger.config.DocumentionHolder;
import com.wl4g.paas.udm.plugin.swagger.config.oas3.Oas3Properties;
import com.wl4g.paas.udm.plugin.swagger.springfox.plugin.ApiVersionPathsRequestHandlerCombiner;
import com.wl4g.paas.udm.plugin.swagger.springfox.plugin.AuthorOperationBuilderPlugin;
import com.wl4g.paas.udm.plugin.swagger.springfox.plugin.OrderOperationBuilderPlugin;
import com.wl4g.paas.udm.plugin.swagger.springfox.plugin.VersionedApiListingPlugin;
import com.wl4g.paas.udm.plugin.swagger.config.oas3.Oas3Contact;
import com.wl4g.paas.udm.plugin.swagger.config.oas3.Oas3Info;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;

import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * {@link SpringfoxOas3Configuration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-09
 * @sine v1.0
 * @see {@link springfox.documentation.oas.web.OpenApiControllerWebMvc}
 */
@EnableOpenApi
@EnableWebMvc
public class SpringfoxOas3Configuration {

	@Bean
	public Docket springfoxOas3Docket() {
		Oas3Properties config = (Oas3Properties) DocumentionHolder.get().getConfig();

		ApiSelectorBuilder builder = new Docket(DocumentationType.OAS_30).apiInfo(buildApiInfo(config)).select()
				.paths(PathSelectors.any());

		// Scanning apis conditions.
		builder.apis(withClassAnnotation(Api.class));
		builder.apis(withMethodAnnotation(ApiOperation.class).or(withMethodAnnotation(Operation.class)));
		for (String scanPackage : DocumentionHolder.get().getResourcePackages()) {
			builder.apis(basePackage(scanPackage));
		}

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

	private ApiInfo buildApiInfo(Oas3Properties config) {
		Oas3Info info = config.getInfo();
		Oas3Contact contact = info.getContact();
		return new ApiInfoBuilder().title(info.getTitle()).description(info.getDescription()).license(info.getLicense().getName())
				.licenseUrl(info.getLicense().getUrl())
				.contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail())).version(info.getVersion()).build();
	}

}
