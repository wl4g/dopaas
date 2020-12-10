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
import com.wl4g.devops.doc.plugin.swagger.springfox.EmbeddedBootstrap;
import com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder;
import static com.wl4g.devops.doc.plugin.swagger.util.DocumentHolder.DocumentionProvider.SWAGGER2;

import io.swagger.models.Swagger;

/**
 * {@link GenerateSpringfoxSwagger2Mojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@Mojo(name = "gendoc-springfox-swagger2", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateSpringfoxSwagger2Mojo extends AbstractMojo {

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

		Swagger document = resolveSwagger2Documention();
		if (getLog().isDebugEnabled()) {
			getLog().debug(format("Exported swagger2 documention: %s", toJSONString(document)));
		}

	}

	private Swagger resolveSwagger2Documention() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(EmbeddedBootstrap.class)
				/* .web(SERVLET) // auto-detection */
				.bannerMode(Mode.OFF).headless(true).run(DocumentHolder.get().toSpringArgs(SWAGGER2));) {

			RestClient rest = new RestClient();
			Object res = rest.getForObject(URI.create(DEFAULT_SWAGGER2_API_URL), String.class);
			System.out.println(">>>>" + res);
			return null;
		}
	}

	public static final String DEFAULT_SWAGGER2_API_URL = "http://localhost:8080/v2/api-docs";

}