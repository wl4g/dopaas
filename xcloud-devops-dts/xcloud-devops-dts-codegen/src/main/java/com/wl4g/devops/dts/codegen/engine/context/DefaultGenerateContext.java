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
package com.wl4g.devops.dts.codegen.engine.context;

import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;

import java.io.File;

import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.lang.Assert2.notNullOf;

/**
 * {@link DefaultGenerateContext}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class DefaultGenerateContext implements GenerateContext {

	/** {@link CodegenProperties} */
	protected final CodegenProperties config;

	/**
	 * {@link GenTemplateLocator}
	 */
	protected final GenTemplateLocator locator;

	/** Generating job workspace directory. */
	protected final File jobDir;

	/** {@link GenProject} */
	protected final GenProject genProject;

	/** {@link GenTable} */
	protected GenTable genTable;

	public DefaultGenerateContext(@NotNull CodegenProperties config, @NotNull GenTemplateLocator locator,
			@NotNull GenProject project) {
		this.config = notNullOf(config, "config");
		this.locator = notNullOf(locator, "locator");
		this.genProject = notNullOf(project, "genProject");
		this.jobDir = config.getJobDir(project.getId());
	}

	@Override
	public GenTemplateLocator getLocator() {
		return locator;
	}

	@Override
	public File getJobDir() {
		return jobDir;
	}

	@Override
	public GenProject getGenProject() {
		return genProject;
	}

	@Override
	public GenTable getGenTable() {
		return genTable;
	}

	@Override
	public void setGenTable(GenTable genTable) {
		this.genTable = notNullOf(genTable, "genTable");
	}

}