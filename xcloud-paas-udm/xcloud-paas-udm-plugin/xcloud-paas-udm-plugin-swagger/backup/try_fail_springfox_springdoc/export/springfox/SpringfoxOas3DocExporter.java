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
package com.wl4g.paas.udm.plugin.swagger.export.springfox;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.wl4g.component.common.remoting.RestClient;
import com.wl4g.paas.udm.plugin.swagger.export.AbstractDocumentionExporter;

import java.net.URI;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.Banner.Mode;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * {@link SpringfoxOas3DocExporter}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
public class SpringfoxOas3DocExporter extends AbstractDocumentionExporter<OpenAPI> {

	public SpringfoxOas3DocExporter(Log log, MavenProject mvnProject) {
		super(log, mvnProject);
	}

	@Override
	public OpenAPI export() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(SpringfoxSwagger2Bootstrap.class)
				/* .web(SERVLET) // auto-detection */
				.bannerMode(Mode.OFF).headless(true).run(new String[0]);) {

			RestClient rest = new RestClient();
			Object res = rest.getForObject(URI.create(DEFAULT_SWAGGER3_API_URL), String.class);
			System.out.println(">>>>" + res);
			return null;
		}
	}

	public static final String DEFAULT_SWAGGER3_API_URL = "http://localhost:8080/v3/api-docs";

}
