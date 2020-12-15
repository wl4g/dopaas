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
package com.wl4g.devops.doc.plugin.swagger;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.springframework.core.ResolvableType;

import com.wl4g.devops.doc.plugin.swagger.config.DocumentionHolder;
import com.wl4g.devops.doc.plugin.swagger.config.DocumentionProperties;
import com.wl4g.devops.doc.plugin.swagger.config.DocumentionHolder.DocumentionProvider;
import com.wl4g.devops.doc.plugin.swagger.util.OutputFormater;

import static com.wl4g.devops.doc.plugin.swagger.util.OutputFormater.JSON;

/**
 * {@link AbstractGenDocMojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-10
 * @sine v1.0
 * @see
 */
public abstract class AbstractGenDocMojo<C extends DocumentionProperties, D> extends AbstractMojo {

	/**
	 * Current maven project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true)
	protected MavenProject project;

	/**
	 * Maven building helper.
	 */
	@Component
	protected MavenProjectHelper projectHelper;

	/**
	 * Documention init configuration properties.
	 */
	@Parameter
	protected C config;

	/**
	 * Skip the execution.
	 */
	@Parameter(name = "skip", property = "xcloud.gendoc.skip", required = false, defaultValue = "false")
	protected Boolean skip = false;

	/**
	 * List of packages which contains API resources. This is <i>not</i>
	 * recursive.
	 */
	@Parameter
	protected List<String> resourcePackages;

	/**
	 * When true, the plugin produces a pretty-printed JSON Swagger
	 * specification. Note that this parameter doesn't have any effect on the
	 * generation of the YAML version because YAML is pretty-printed by nature.
	 */
	@Parameter(defaultValue = "false")
	protected boolean prettyPrint = false;

	/**
	 * Choosing the output format. Supports JSON or YAML.
	 */
	@Parameter
	protected List<OutputFormater> outputFormats = singletonList(JSON);

	/**
	 * Directory to contain generated documentation.
	 */
	@Parameter(defaultValue = "${project.build.directory}/generated-docs")
	protected File outputDirectory;

	/**
	 * Filename to use for the generated documentation.
	 */
	@Parameter(defaultValue = "swagger")
	protected String outputFilename = "swagger";

	/**
	 * Attach generated documentation as artifact to the Maven project. If true
	 * documentation will be deployed along with other artifacts.
	 */
	@Parameter(defaultValue = "false")
	protected boolean attachSwaggerArtifact = false;

	protected abstract DocumentionProvider provider();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (nonNull(skip) && skip) {
			getLog().info("Swagger documention generation is skipped.");
			return;
		}
		try {
			if (!outputDirectory.exists() && outputDirectory.mkdirs()) {
				getLog().debug("Created output directory " + outputDirectory);
			}

			// Init config properties.
			initPropertiesSet();

			// Generation document.
			D doc = generateDocument();

			if (getLog().isDebugEnabled()) {
				getLog().debug(format("Generated documention. - %s", toJSONString(doc)));
			}

			// Output documents.
			for (OutputFormater format : outputFormats) {
				try {
					File outputFile = new File(outputDirectory, outputFilename + "." + format.name().toLowerCase());
					format.write(doc, outputFile, prettyPrint);
					getLog().info(format("Written generate document to %s", outputFile));

					if (attachSwaggerArtifact) {
						projectHelper.attachArtifact(project, format.name().toLowerCase(), "swagger", outputFile);
					}
				} catch (IOException e) {
					throw new IOException("Unable write " + outputFilename + " document", e);
				}
			}

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	/**
	 * Do execution generation documents.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract D generateDocument() throws Exception;

	/**
	 * Sets initialization config properties.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void initPropertiesSet() throws Exception {
		if (isNull(config)) {
			Class<C> clazz = (Class<C>) ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
			notNull(clazz, "Mojo super class generaic types is requires.");
			// New default config instance.
			this.config = clazz.newInstance();

			StringWriter out = new StringWriter(128);
			OutputFormater.JSON.write(config, out, false);
			getLog().warn(format("Use default swagger config properties: %s", out));
		}

		DocumentionHolder.get().setConfig(config);
		DocumentionHolder.get().setProvider(provider());
		DocumentionHolder.get().setResourcePackages(resourcePackages);
	}

}
