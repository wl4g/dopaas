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
package com.wl4g.devops.doc.plugin.swagger.jaxrs2;

import javax.ws.rs.core.Application;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.DependencyResolutionRequiredException;

import com.wl4g.devops.doc.plugin.swagger.jaxrs2.model.SwaggerConfig;

/**
 * Maven mojo to generate OpenAPI documentation document based on Swagger.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-09
 * @sine v1.0
 * @see
 */
@Mojo(name = "gendoc-jaxrs2", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateJaxrs2Mojo extends AbstractMojo {

	/**
	 * Skip the execution.
	 */
	@Parameter(name = "skip", property = "openapi.generation.skip", required = false, defaultValue = "false")
	private Boolean skip;

	/**
	 * Static information to provide for the generation.
	 */
	@Parameter
	private SwaggerConfig swaggerConfig;

	/**
	 * List of packages which contains API resources. This is <i>not</i>
	 * recursive.
	 */
	@Parameter
	private Set<String> resourcePackages;

	/**
	 * Recurse into resourcePackages child packages.
	 */
	@Parameter(required = false, defaultValue = "false")
	private Boolean useResourcePackagesChildren;

	/**
	 * Directory to contain generated documentation.
	 */
	@Parameter(defaultValue = "${project.build.directory}")
	private File outputDirectory;

	/**
	 * Filename to use for the generated documentation.
	 */
	@Parameter
	private String outputFilename = "swagger";

	/**
	 * Choosing the output format. Supports JSON or YAML.
	 */
	@Parameter
	private Set<OutputFormat> outputFormats = Collections.singleton(OutputFormat.JSON);

	/**
	 * Attach generated documentation as artifact to the Maven project. If true
	 * documentation will be deployed along with other artifacts.
	 */
	@Parameter(defaultValue = "false")
	private boolean attachSwaggerArtifact;

	/**
	 * Specifies the implementation of {@link Application}. If the class is not
	 * specified, the resource packages are scanned for the {@link Application}
	 * implementations automatically.
	 */
	@Parameter(name = "applicationClass", defaultValue = "")
	private String applicationClass;

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	/**
	 * When true, the plugin produces a pretty-printed JSON Swagger
	 * specification. Note that this parameter doesn't have any effect on the
	 * generation of the YAML version because YAML is pretty-printed by nature.
	 */
	@Parameter(defaultValue = "false")
	private boolean prettyPrint;

	@Component
	private MavenProjectHelper projectHelper;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip != null && skip) {
			getLog().info("OpenApi generation is skipped.");
			return;
		}

		ClassLoader origClzLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader clzLoader = createClassLoader(origClzLoader);

		try {
			// set the TCCL before everything else
			Thread.currentThread().setContextClassLoader(clzLoader);

			Reader reader = new Reader(swaggerConfig == null ? new OpenAPI() : swaggerConfig.createSwaggerModel());

			JaxRSScanner reflectiveScanner = new JaxRSScanner(getLog(), resourcePackages, useResourcePackagesChildren);

			Application application = resolveApplication(reflectiveScanner);
			reader.setApplication(application);

			OpenAPI swagger = OpenAPISorter.sort(reader.read(reflectiveScanner.classes()));

			if (outputDirectory.mkdirs()) {
				getLog().debug("Created output directory " + outputDirectory);
			}

			for (OutputFormat format : outputFormats) {
				try {
					File outputFile = new File(outputDirectory, outputFilename + "." + format.name().toLowerCase());
					format.write(swagger, outputFile, prettyPrint);
					if (attachSwaggerArtifact) {
						projectHelper.attachArtifact(project, format.name().toLowerCase(), "swagger", outputFile);
					}
				} catch (IOException e) {
					throw new RuntimeException("Unable write " + outputFilename + " document", e);
				}
			}
		} finally {
			// reset the TCCL back to the original class loader
			Thread.currentThread().setContextClassLoader(origClzLoader);
		}
	}

	private Application resolveApplication(JaxRSScanner reflectiveScanner) {
		if (applicationClass == null || applicationClass.isEmpty()) {
			return reflectiveScanner.applicationInstance();
		}

		Class<?> clazz = ClassUtils.loadClass(applicationClass, Thread.currentThread().getContextClassLoader());

		if (clazz == null || !Application.class.isAssignableFrom(clazz)) {
			getLog().warn("Provided application class does not implement javax.ws.rs.core.Application, skipping");
			return null;
		}

		@SuppressWarnings("unchecked")
		Class<? extends Application> appClazz = (Class<? extends Application>) clazz;
		return ClassUtils.createInstance(appClazz);
	}

	private URLClassLoader createClassLoader(ClassLoader parent) {
		try {
			Collection<String> dependencies = getDependentClasspathElements();
			URL[] urls = new URL[dependencies.size()];
			int index = 0;
			for (String dependency : dependencies) {
				urls[index++] = Paths.get(dependency).toUri().toURL();
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
		dependencies.add(project.getBuild().getOutputDirectory());
		Collection<String> compileClasspathElements = project.getCompileClasspathElements();
		if (compileClasspathElements != null) {
			dependencies.addAll(compileClasspathElements);
		}
		Collection<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
		if (runtimeClasspathElements != null) {
			dependencies.addAll(runtimeClasspathElements);
		}
		return dependencies;
	}

}