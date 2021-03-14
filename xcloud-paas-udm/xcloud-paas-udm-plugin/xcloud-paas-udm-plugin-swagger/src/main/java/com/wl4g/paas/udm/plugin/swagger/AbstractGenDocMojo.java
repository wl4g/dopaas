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
package com.wl4g.paas.udm.plugin.swagger;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.springframework.core.ResolvableType;

import com.wl4g.component.common.collection.CollectionUtils2;
import com.wl4g.paas.udm.plugin.swagger.config.DocumentionHolder;
import com.wl4g.paas.udm.plugin.swagger.config.SwaggerConfig;
import com.wl4g.paas.udm.plugin.swagger.config.DocumentionHolder.DocumentionProvider;
import com.wl4g.paas.udm.plugin.swagger.util.OutputFormater;

import static com.wl4g.paas.udm.plugin.swagger.util.OutputFormater.JSON;

/**
 * {@link AbstractGenDocMojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-10
 * @sine v1.0
 * @see
 */
public abstract class AbstractGenDocMojo<C extends SwaggerConfig, D> extends AbstractMojo {

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
	 * Skip the execution.
	 */
	@Parameter(name = "skip", property = "xcloud.gendoc.skip", required = false, defaultValue = "false")
	protected Boolean skip = false;

	/**
	 * List of packages which contains API resources. This is <i>not</i>
	 * recursive.
	 */
	@Parameter
	protected Set<String> resourcePackages;

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
		// Skip mojo execution?
		if (nonNull(skip) && skip) {
			getLog().info("Swagger documention generation is skipped.");
			return;
		}

		try {
			// Init config properties.
			initPropertiesSet();

			// Init directorys.
			if (!outputDirectory.exists() && outputDirectory.mkdirs()) {
				getLog().debug("Created output directory " + outputDirectory);
			}

			// Generate apis document.
			D document = generateDocument();

			// Output apis documents.
			for (OutputFormater format : outputFormats) {
				try {
					File outputFile = new File(outputDirectory, outputFilename + "." + format.name().toLowerCase());
					format.write(document, outputFile, prettyPrint);
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
	 * Load swagger protocol documention initialization configuration
	 * properties.
	 */
	protected abstract C loadSwaggerConfig();

	/**
	 * Generate apis document.
	 * 
	 * @return
	 * @throws MojoExecutionException
	 */
	private final D generateDocument() throws MojoExecutionException {
		D document = null;

		ClassLoader origClzLoader = Thread.currentThread().getContextClassLoader();
		try {
			ClassLoader clzLoader = createClassLoader(origClzLoader);

			// set the TCCL before everything else
			Thread.currentThread().setContextClassLoader(clzLoader);

			// Do generate apis document.
			document = doGenerateDocumentInternal();
			if (getLog().isDebugEnabled()) {
				getLog().debug(format("Generated documention. - %s", toJSONString(document)));
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			// reset the TCCL back to the original class loader
			Thread.currentThread().setContextClassLoader(origClzLoader);
		}

		return document;
	}

	/**
	 * Do generate api document.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract D doGenerateDocumentInternal() throws Exception;

	/**
	 * Sets initialization config properties.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void initPropertiesSet() throws Exception {
		// Load initialization swagger config.
		SwaggerConfig swaggerConfig = loadSwaggerConfig();

		if (isNull(swaggerConfig)) {
			Class<C> clazz = (Class<C>) ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
			notNull(clazz, "Mojo super class generaic types is requires.");
			// New default config instance.
			swaggerConfig = clazz.newInstance();

			StringWriter out = new StringWriter(128);
			OutputFormater.JSON.write(swaggerConfig, out, false);
			getLog().warn(format("Use default swagger config properties: %s", out));
		}

		DocumentionHolder.get().setConfig(swaggerConfig);
		DocumentionHolder.get().setProvider(provider());
		DocumentionHolder.get().setResourcePackages(resourcePackages);
	}

	private URLClassLoader createClassLoader(ClassLoader parent) {
		try {
			Collection<String> dependencies = getDependentClasspathElements();
			URL[] urls = new URL[dependencies.size()];
			int index = 0;
			for (String dependency : dependencies) {
				if (nonNull(dependency)) {
					urls[index++] = Paths.get(dependency).toUri().toURL();
				}
			}
			return new URLClassLoader(urls, parent);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unable to create class loader with compiled classes", e);
		} catch (DependencyResolutionRequiredException e) {
			throw new RuntimeException("Dependency resolution (runtime + compile) is required");
		}
	}

	private Collection<String> getDependentClasspathElements() throws DependencyResolutionRequiredException {
		Set<String> dependencies = new LinkedHashSet<>();
		String outputDir = project.getBuild().getOutputDirectory();
		if (!isBlank(outputDir)) {
			dependencies.add(outputDir);
		}
		Collection<String> compileClasspathElements = project.getCompileClasspathElements();
		if (!CollectionUtils2.isEmpty(compileClasspathElements)) {
			dependencies.addAll(compileClasspathElements);
		}
		Collection<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
		if (!CollectionUtils2.isEmpty(runtimeClasspathElements)) {
			dependencies.addAll(runtimeClasspathElements);
		}
		return dependencies;
	}

}
