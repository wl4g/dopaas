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
package com.wl4g.devops.dts.codegen.engine.context;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.annotation.Reserved;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * {@link GenerateContext}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public interface GenerateContext {

	/**
	 * {@link CodegenProperties}
	 * 
	 * @return
	 */
	CodegenProperties getConfiguration();

	/**
	 * Gets generate template locator {@link GenTemplateLocator}
	 * 
	 * @return
	 */
	@NotNull
	GenTemplateLocator getLocator();

	/**
	 * Gets {@link MetadataResolver}
	 * 
	 * @return
	 */
	@Reserved
	@NotNull
	MetadataResolver getMetadataResolver();

	/**
	 * Gets generate job ID.
	 * 
	 * @return
	 */
	@NotBlank
	String getJobId();

	/**
	 * Gets generate workspace job directory.
	 * 
	 * @return
	 */
	@NotNull
	File getJobDir();

	/**
	 * Gets generate for {@link GenProject}
	 * 
	 * @return
	 */
	@NotNull
	GenProject getGenProject();

	/**
	 * Gets current generating for {@link GenDataSource}
	 *
	 * @return
	 */
	@NotNull
	GenDataSource getGenDataSource();

	/**
	 * Gets current generating for {@link GenTable}
	 * 
	 * @return
	 */
	@Nullable
	GenTable getGenTable();

	/**
	 * Sets current generating for {@link GenTable}
	 * 
	 * @param genTable
	 */
	void setGenTable(@NotNull GenTable genTable);

}