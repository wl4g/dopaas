///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.devops.udm.plugin.swagger;
//
//import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
//import static java.lang.String.format;
//
//import org.apache.maven.plugin.AbstractMojo;
//import org.apache.maven.plugin.MojoExecutionException;
//import org.apache.maven.plugins.annotations.LifecyclePhase;
//import org.apache.maven.plugins.annotations.Mojo;
//import org.apache.maven.plugins.annotations.Parameter;
//import org.apache.maven.project.MavenProject;
//
//import com.wl4g.devops.udm.plugin.swagger.export.springfox.SpringfoxSwagger2DocExporter;
//
//import io.swagger.models.Swagger;
//
///**
// * {@link DocumentionGenMojo}
// * 
// * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
// * @version v1.0 2020-12-08
// * @sine v1.0
// * @see
// */
////@Mojo(name = "gendoc", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
//public class DocumentionGenMojo extends AbstractMojo {
//
//	/**
//	 * Current Maven project, read only.
//	 */
//	@Parameter(readonly = true, required = true, defaultValue = "${project}")
//	private MavenProject mvnProject;
//
//	@Parameter(name = "inputSpec", property = "inputSpec", required = false)
//	private String inputSpec;
//
//	@Override
//	public void execute() throws MojoExecutionException {
//		try {
//			doExecute();
//		} catch (Throwable e) {
//			throw new MojoExecutionException(e.getMessage(), e);
//		}
//	}
//
//	protected void doExecute() {
//		Object documention = new SpringfoxSwagger2DocExporter(getLog(), mvnProject).export();
//		if (getLog().isDebugEnabled()) {
//			getLog().debug(format("Exported documention: %s", toJSONString(documention)));
//		}
//
//		writeDocumention(documention);
//	}
//
//	protected void writeDocumention(Object documention) {
//		if (documention instanceof Swagger) {
//			writeSwaggerDocumention((Swagger) documention);
//		}
//	}
//
//	protected void writeSwaggerDocumention(Swagger swagger) {
//
//	}
//
//}