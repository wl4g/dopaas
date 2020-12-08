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
package com.wl4g.devops.doc.plugin.swagger.export;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.wl4g.components.common.remoting.RestClient;

import java.net.URI;

import org.apache.maven.plugin.logging.Log;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;

import io.swagger.models.Swagger;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * {@link SpringfoxSwagger2DocExporter}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@SpringBootApplication(exclude = { FreeMarkerAutoConfiguration.class, MustacheAutoConfiguration.class,
		DataSourceAutoConfiguration.class })
@EnableSwagger2
public class SpringfoxSwagger2DocExporter extends AbstractDocumentionExporter<Swagger> {

	public SpringfoxSwagger2DocExporter(Log log) {
		super(log);
	}

	@Bean
	public Docket springfoxSwaggerDocket() {
		ApiInfo apiInfo = new ApiInfoBuilder().title("epiot API文档").description("").license("")
				.contact(new Contact("Wanglsir", "#", "Wanglsir")).version("v1.0.0").build();

		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select()
				.apis(RequestHandlerSelectors.basePackage("com.wl4g"))
				// .apis(RequestHandlerSelectors.withMethodAnnotation(Api.class))
				.paths(PathSelectors.any()).build();
	}

	@Override
	public Swagger export() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(SpringfoxSwagger2DocExporter.class)
				/* .web(SERVLET) // auto-detection */
				.bannerMode(Mode.OFF).headless(true).run(new String[0]);) {

			RestClient rest = new RestClient();
			return rest.getForObject(URI.create(DEFAULT_SWAGGER2_API_URL), Swagger.class);
		}
	}

	public static final String DEFAULT_SWAGGER2_API_URL = "http://localhost:8080/v2/api-docs";

}
