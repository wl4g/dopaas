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

import static com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder.PROPERTY_SWAGGER2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder;

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
@Configuration
@ConditionalOnProperty(name = PROPERTY_SWAGGER2, matchIfMissing = false)
@EnableSwagger2
public class SpringfoxSwagger2Configuration {

	@Bean
	public Docket springfoxSwagger2Docket() {
		ApiInfo apiInfo = new ApiInfoBuilder().title("Demo API文档").description("").license("")
				.contact(new Contact("Wanglsir", "#", "Wanglsir")).version("v1.0.0").build();

		ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select().paths(PathSelectors.any());
		// .apis(RequestHandlerSelectors.withMethodAnnotation(Api.class))
		for (String scanBasePackage : DocumentHolder.get().getScanBasePackages()) {
			builder.apis(RequestHandlerSelectors.basePackage(scanBasePackage));
		}

		return builder.build();
	}

}
