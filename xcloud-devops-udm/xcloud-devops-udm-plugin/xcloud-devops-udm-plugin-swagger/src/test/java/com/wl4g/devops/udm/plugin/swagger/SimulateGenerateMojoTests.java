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
package com.wl4g.devops.udm.plugin.swagger;

import static com.wl4g.component.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.component.common.reflect.ReflectionUtils2.setField;
import static com.wl4g.devops.udm.plugin.swagger.util.OutputFormater.JSON;
import static com.wl4g.devops.udm.plugin.swagger.util.OutputFormater.YAML;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.junit.Test;

import com.wl4g.devops.udm.plugin.swagger.jaxrs2.GenerateJaxrs2Oas3Mojo;
import com.wl4g.devops.udm.plugin.swagger.springdoc.oas3.GenerateSpringdocOas3Mojo;
import com.wl4g.devops.udm.plugin.swagger.springfox.oas3.GenerateSpringfoxOas3Mojo;
import com.wl4g.devops.udm.plugin.swagger.springfox.swagger2.GenerateSpringfoxSwagger2Mojo;

/**
 * Simulate mvn plugin execution tests.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-09
 * @sine v1.0
 * @see
 */
public class SimulateGenerateMojoTests {

	@Test
	public void generateSpringfoxSwagger2MojoTest() throws Exception {
		createMavenMojoInstance(GenerateSpringfoxSwagger2Mojo.class,
				singleton("com.wl4g.devops.udm.plugin.swagger.example.swagger2"), "swagger-swagger2-by-springfox").execute();
	}

	@Test
	public void generateSpringfoxOas3MojoTest() throws Exception {
		createMavenMojoInstance(GenerateSpringfoxOas3Mojo.class, singleton("com.wl4g.devops.udm.plugin.swagger.example.oas3"),
				"swagger-oas3-by-springfox").execute();
	}

	// @Test
	public void generateSpringdocOas3MojoTest() throws Exception {
		createMavenMojoInstance(GenerateSpringdocOas3Mojo.class, singleton("com.wl4g.devops.udm.plugin.swagger.example.oas3"),
				"swagger-oas3-by-springdoc").execute();
	}

	// @Test
	public void generateJaxrs2Oas3MojoTest() throws Exception {
		createMavenMojoInstance(GenerateJaxrs2Oas3Mojo.class, singleton("com.wl4g.devops.udm.plugin.swagger.example.jaxrs2"),
				"swagger-oas3-by-jaxrs").execute();
	}

	public static AbstractMojo createMavenMojoInstance(Class<? extends AbstractMojo> mojoClass, Set<String> resourcePackages,
			String outputFilename) throws Exception {
		AbstractMojo mojo = mojoClass.newInstance();
		setField(findField(mojoClass, "project"), mojo, createMavenProject(), true);
		setField(findField(mojoClass, "projectHelper"), mojo, createMavenProjectHelper(), true);
		setField(findField(mojoClass, "skip"), mojo, false, true);
		setField(findField(mojoClass, "resourcePackages"), mojo, resourcePackages, true);
		setField(findField(mojoClass, "prettyPrint"), mojo, true, true);
		setField(findField(mojoClass, "outputFormats"), mojo, asList(JSON, YAML), true);
		setField(findField(mojoClass, "outputDirectory"), mojo, new File(USER_DIR + "/target/generated-docs"), true);
		setField(findField(mojoClass, "outputFilename"), mojo, outputFilename, true);
		setField(findField(mojoClass, "attachSwaggerArtifact"), mojo, true, true);
		return mojo;
	}

	public static MavenProject createMavenProject() {
		MavenProject project = new MavenProject();
		// project.setBuild(new Build());
		return project;
	}

	public static MavenProjectHelper createMavenProjectHelper() {
		return new MavenProjectHelper() {

			@Override
			public void attachArtifact(MavenProject project, File artifactFile, String artifactClassifier) {
				// TODO Auto-generated method stub

			}

			@Override
			public void attachArtifact(MavenProject project, String artifactType, File artifactFile) {
				// TODO Auto-generated method stub

			}

			@Override
			public void attachArtifact(MavenProject project, String artifactType, String artifactClassifier, File artifactFile) {
				out.println(format("[[attachArtifact]] - project: {}, artifactType: {}, artifactClassifier: {}, artifactFile: {}",
						project, artifactType, artifactClassifier, artifactFile));
			}

			@Override
			public void addResource(MavenProject project, String resourceDirectory, List<String> includes,
					List<String> excludes) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addTestResource(MavenProject project, String resourceDirectory, List<String> includes,
					List<String> excludes) {
				// TODO Auto-generated method stub

			}

		};
	}

}