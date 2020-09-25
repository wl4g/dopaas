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
package com.wl4g.devops.dts.codegen.engine.generator;

import static java.lang.System.out;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.context.DefaultGenerateContext;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateResourceWrapper;

/**
 * {@link GeneratorProviderTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-24
 * @sine v1.0.0
 * @see
 */
public class GeneratorProviderTests {

	@Test
	@SuppressWarnings("serial")
	public void crossForeachRenderingCase() {
		GenProject project = new GenProject();
		project.setOrganType("com");
		project.setOrganName("wl4g");
		project.setProjectName("ElectronicStore");
		List<GenTable> genTables = new ArrayList<>();
		genTables.add(new GenTable().withEntityName("OrderBean").withModuleName("marketModule"));
		genTables.add(new GenTable().withEntityName("StoreBean").withModuleName("marketModule"));
		genTables.add(new GenTable().withEntityName("UserBean").withModuleName("systemModule"));
		genTables.add(new GenTable().withEntityName("RoleBean").withModuleName("systemModule"));
		project.setGenTables(genTables);

		List<String> generatedFiles = Lists.newArrayList();

		// For case1
		GenTemplateLocator locator1 = newGenTemplateLocator(new ArrayList<TemplateResourceWrapper>() {
			{
				add(new TemplateResourceWrapper(
						"src/main/java/#{organType}/#{organName}/#{projectName}/common/#{moduleName}/#{javaSpecs.pkgToPath(beanSubModulePackageName)}/#{javaSpecs.firstUCase(entityName)}.java.ftl",
						"test template content"));
			}
		});

		new SpringCloudMvnGeneratorProvider(new DefaultGenerateContext(config, locator1, resolver, project, datasource)) {
			@Override
			protected void postRenderingComplete(@NotNull TemplateResourceWrapper resource, @NotBlank String renderedString,
					@NotBlank String writePath) {
				generatedFiles.add(writePath);
			}
		}.run();

		// For case2
		GenTemplateLocator locator2 = newGenTemplateLocator(new ArrayList<TemplateResourceWrapper>() {
			{
				add(new TemplateResourceWrapper(
						"#{vueSpecs.lCase(organName)}-#{vueSpecs.lCase(projectName)}-view/src/views/#{moduleName}/#{vueSpecs.lCase(entityName)}/#{vueSpecs.firstUCase(entityName)}.vue.ftl",
						"test template content"));
			}
		});

		new VueGeneratorProvider(new DefaultGenerateContext(config, locator2, resolver, project, datasource)) {
			@Override
			protected void postRenderingComplete(@NotNull TemplateResourceWrapper resource, @NotBlank String renderedString,
					@NotBlank String writePath) {
				generatedFiles.add(writePath);
			}
		}.run();

		out.println("======= For test generated files: ======");
		generatedFiles.forEach(f -> out.println(f));

	}

	static GenTemplateLocator newGenTemplateLocator(List<TemplateResourceWrapper> templates) {
		return new GenTemplateLocator() {
			@Override
			public List<TemplateResourceWrapper> locate(String provider) throws Exception {
				return templates;
			}

			@Override
			public boolean cleanAll() {
				return false;
			}
		};
	}

	static CodegenProperties config = new CodegenProperties() {
		@Override
		public File getJobDir(Integer confId) {
			return new File("test_job_dir");
		}
	};

	static MetadataResolver resolver = new MetadataResolver() {
		@Override
		public String findDBVersion() throws Exception {
			return "test_db_v0.0.0";
		}
	};

	static GenDataSource datasource = new GenDataSource();

}
