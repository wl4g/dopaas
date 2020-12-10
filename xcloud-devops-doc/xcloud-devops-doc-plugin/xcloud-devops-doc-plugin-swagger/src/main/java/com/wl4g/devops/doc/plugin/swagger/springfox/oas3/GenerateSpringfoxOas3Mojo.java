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

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;

import java.net.URI;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.wl4g.components.common.remoting.RestClient;
import com.wl4g.devops.doc.plugin.swagger.springfox.Bootstrap;
import com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * {@link GenerateSpringfoxOas3Mojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@Mojo(name = "gendoc-springfox-oas3", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateSpringfoxOas3Mojo extends AbstractMojo {

	/**
	 * Current Maven project, read only.
	 */
	@Parameter(readonly = true, required = true, defaultValue = "${project}")
	private MavenProject mvnProject;

	@Parameter(required = true)
	private String scanBasePackages;

	@Override
	public void execute() throws MojoExecutionException {
		DocumentHolder.get().setScanBasePackages(scanBasePackages);

		OpenAPI document = resolveOAS3Documention();
		if (getLog().isDebugEnabled()) {
			getLog().debug(format("Exported OAS3 documention: %s", toJSONString(document)));
		}

	}

	private OpenAPI resolveOAS3Documention() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(Bootstrap.class)
				/* .web(SERVLET) // auto-detection */
				.bannerMode(Mode.OFF).headless(true).run(DocumentHolder.get().toSpringArgs());) {

			RestClient rest = new RestClient();
			Object res = rest.getForObject(URI.create(DEFAULT_SWAGGER3_API_URL), String.class);
			System.out.println(">>>>" + res);
			return null;
		}
	}

	public static final String DEFAULT_SWAGGER3_API_URL = "http://localhost:8080/v3/api-docs";

}