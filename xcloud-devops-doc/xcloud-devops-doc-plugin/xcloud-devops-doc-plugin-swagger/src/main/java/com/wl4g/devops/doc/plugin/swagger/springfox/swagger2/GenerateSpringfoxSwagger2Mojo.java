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

import java.net.URI;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.wl4g.components.common.remoting.RestClient;
import com.wl4g.devops.doc.plugin.swagger.AbstractGenDocMojo;
import com.wl4g.devops.doc.plugin.swagger.springfox.EmbeddedBootstrap;
import com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder;

import static com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder.DocumentionProvider.SPRINGFOX_SWAGGER2;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

/**
 * {@link GenerateSpringfoxSwagger2Mojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@Mojo(name = "gendoc-springfox-swagger2", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateSpringfoxSwagger2Mojo extends AbstractGenDocMojo<Swagger> {

	@Override
	protected Swagger generateDocument() throws Exception {
		return resolveSwagger2Documention();
	}

	private Swagger resolveSwagger2Documention() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(EmbeddedBootstrap.class)
				/* .web(SERVLET) // auto-detection */
				.bannerMode(Mode.OFF).headless(true).run(DocumentHolder.get().toSpringArgs(SPRINGFOX_SWAGGER2));) {

			RestClient rest = new RestClient();
			String swagger = rest.getForObject(URI.create(DEFAULT_SWAGGER2_API_URL), String.class);
			return new SwaggerParser().parse(swagger);
		}
	}

	public static final String DEFAULT_SWAGGER2_API_URL = "http://localhost:8080/v2/api-docs";

}