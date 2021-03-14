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
package com.wl4g.dopaas.udc.codegen.engine.context;

import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.udc.codegen.bean.GenDataSource;
import com.wl4g.dopaas.udc.codegen.bean.GenProject;

/**
 * {@link GeneratedResult}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-10-01
 * @sine v1.0.0
 * @see
 */
public class GeneratedResult {

	/** {@link GenProject} */
	@NotNull
	protected final GenProject project;

	/** {@link GenDataSource} */
	@NotNull
	protected final GenDataSource dataSource;

	/** Generating job ID. */
	protected final String jobId;

	public GeneratedResult(@NotNull GenProject project, @NotNull GenDataSource dataSource, @NotBlank String jobId) {
		this.project = notNullOf(project, "project");
		this.dataSource = notNullOf(dataSource, "dataSource");
		this.jobId = hasTextOf(jobId, "jobId");
	}

	public GenProject getProject() {
		return project;
	}

	public GenDataSource getDataSource() {
		return dataSource;
	}

	public String getJobId() {
		return jobId;
	}

}