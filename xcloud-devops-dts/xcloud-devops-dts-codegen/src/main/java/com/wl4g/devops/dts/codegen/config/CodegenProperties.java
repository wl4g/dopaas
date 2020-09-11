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
package com.wl4g.devops.dts.codegen.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

import java.io.File;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.components.common.log.SmartLogger;
import static com.wl4g.iam.common.utils.IamSecurityHolder.getPrincipal;

/**
 * {@link CodegenProperties}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public class CodegenProperties implements InitializingBean {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * Global workspace directory path.
	 */
	private String workspace = USER_HOME + File.separator + ".codegen-workspace"; // By-default.

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		hasTextOf(workspace, "workspace");
		this.workspace = workspace;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		applyDefaultProperties();
	}

	/**
	 * Apply default properties values.
	 */
	protected void applyDefaultProperties() {

	}

	//
	// Function's.
	//

	/**
	 * e.g. </br>
	 * ~/.codegen-workspace/jobs/job.root-1000001-1599726930000/
	 * 
	 * @param genConfId
	 * @return
	 */
	public File getJobDir(Integer genConfId) {
		notNullOf(genConfId, "genConfigId");
		String principalName = getPrincipal();
		return new File(getWorkspace().concat("/").concat(DEFUALT_JOB_BASEDIR).concat("/job.").concat(principalName).concat("-")
				.concat(valueOf(genConfId)).concat("-").concat(valueOf(currentTimeMillis())));
	}

	final public static String DEFUALT_JOB_BASEDIR = "jobs";

}