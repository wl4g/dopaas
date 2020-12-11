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
package com.wl4g.devops.doc.plugin.swagger.springfox.oas3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;

import static com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder.PROPERTY_OAS3;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;
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
@Configuration
@ConditionalOnProperty(name = PROPERTY_OAS3, matchIfMissing = false)
@EnableOpenApi
@EnableWebMvc
public class SpringfoxOas3Configuration {

	@Bean
	public Docket springfoxOas3Docket() {
		ApiInfo apiInfo = new ApiInfoBuilder().title("Demo API文档").description("").license("")
				.contact(new Contact("Wanglsir", "#", "Wanglsir")).version("v1.0.0").build();

		ApiSelectorBuilder builder = new Docket(DocumentationType.OAS_30).apiInfo(apiInfo).select().paths(PathSelectors.any());
		// .apis(withMethodAnnotation(Api.class))
		builder.apis(withMethodAnnotation(ApiOperation.class));
		for (String scanBasePackage : DocumentHolder.get().getResourcePackages()) {
			builder.apis(RequestHandlerSelectors.basePackage(scanBasePackage));
		}

		return builder.build();
	}

}
