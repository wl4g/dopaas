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
package com.wl4g.devops.doc.plugin.swagger.springdoc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * {@link GenerateSpringdocMojo}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@Mojo(name = "gendoc-springdoc", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateSpringdocMojo extends AbstractMojo {

	/**
	 * Current Maven project, read only.
	 */
	@Parameter(readonly = true, required = true, defaultValue = "${project}")
	private MavenProject mvnProject;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			doExecute();
		} catch (Throwable e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void doExecute() {
		// TODO
		throw new UnsupportedOperationException("No implements export documention for springdoc");
	}

}