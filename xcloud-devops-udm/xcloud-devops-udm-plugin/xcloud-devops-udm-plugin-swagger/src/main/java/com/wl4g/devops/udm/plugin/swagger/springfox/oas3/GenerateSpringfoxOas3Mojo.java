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
package com.wl4g.devops.udm.plugin.swagger.springfox.oas3;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.wl4g.devops.udm.plugin.swagger.AbstractGenDocMojo;
import com.wl4g.devops.udm.plugin.swagger.config.DocumentionHolder;
import com.wl4g.devops.udm.plugin.swagger.config.DocumentionHolder.DocumentionProvider;
import com.wl4g.devops.udm.plugin.swagger.config.oas3.Oas3Properties;
import com.wl4g.devops.udm.plugin.swagger.springfox.EmbeddedSpringfoxBootstrap;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

/**
 * {@link GenerateSpringfoxOas3Mojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@Mojo(name = "gendoc-springfox-oas3", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateSpringfoxOas3Mojo extends AbstractGenDocMojo<Oas3Properties, OpenAPI> {

	@Parameter
	private Oas3Properties swaggerConfig;

	@Override
	protected DocumentionProvider provider() {
		return DocumentionProvider.SPRINGFOX_OAS3;
	}

	@Override
	protected Oas3Properties loadSwaggerConfig() {
		return swaggerConfig;
	}

	@Override
	protected OpenAPI doGenerateDocumentInternal() throws Exception {
		return resolveOAS3Documention();
	}

	private OpenAPI resolveOAS3Documention() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(EmbeddedSpringfoxBootstrap.class)
				/* .web(SERVLET) // auto-detection */
				.bannerMode(Mode.OFF).headless(true).run();) {

			String apiDocUri = DEFAULT_SWAGGER3_API_URI + DocumentionHolder.get().getConfig().getSwaggerGroup();
			return new OpenAPIV3Parser().read(apiDocUri);
		}
	}

	public static final String DEFAULT_SWAGGER3_API_URI = "http://localhost:8080/v3/api-docs?group=";

}