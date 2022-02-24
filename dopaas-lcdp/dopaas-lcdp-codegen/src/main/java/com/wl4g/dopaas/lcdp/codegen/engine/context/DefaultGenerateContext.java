/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.lcdp.codegen.engine.context;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.valueOf;

import java.io.File;

import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.lcdp.GenDataSource;
import com.wl4g.dopaas.common.bean.lcdp.GenProject;
import com.wl4g.dopaas.common.bean.lcdp.GenTable;
import com.wl4g.dopaas.lcdp.codegen.config.CodegenProperties;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.template.GenTemplateLocator;

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

	/**
	 * {@link GenTemplateLocator}
	 */
	protected final MetadataResolver resolver;

	/** Generating job ID. */
	protected final String jobId;

	/** Generating job workspace directory. */
	protected final File jobDir;

	/** {@link GenProject} */
	protected final GenProject project;

	/** {@link GenDataSource} */
	protected final GenDataSource dataSource;

	/** Currently rendering of {@link GenTable} */
	protected GenTable genTable;

	public DefaultGenerateContext(@NotNull CodegenProperties config, @NotNull GenTemplateLocator locator,
			@NotNull MetadataResolver resolver, @NotNull GenProject project, @NotNull GenDataSource datasource) {
		this.config = notNullOf(config, "config");
		this.locator = notNullOf(locator, "locator");
		this.resolver = notNullOf(resolver, "resolver");
		this.project = notNullOf(project, "genProject");
		this.jobId = config.generateJobId(valueOf(project.getId()));
		this.jobDir = config.generateJobDir(this.jobId);
		this.dataSource = notNullOf(datasource, "genDataSource");
	}

	@Override
	public CodegenProperties getConfiguration() {
		return config;
	}

	@Override
	public final GenTemplateLocator getLocator() {
		return locator;
	}

	@Override
	public final MetadataResolver getMetaResolver() {
		return resolver;
	}

	@Override
	public String getJobId() {
		return jobId;
	}

	@Override
	public final File getJobDir() {
		return jobDir;
	}

	@Override
	public final GenProject getGenProject() {
		return project;
	}

	@Override
	public final GenDataSource getGenDataSource() {
		return dataSource;
	}

	@Override
	public final GenTable getGenTable() {
		return genTable;
	}

	@Override
	public final void setGenTable(GenTable genTable) {
		this.genTable = notNullOf(genTable, "genTable");
	}

}