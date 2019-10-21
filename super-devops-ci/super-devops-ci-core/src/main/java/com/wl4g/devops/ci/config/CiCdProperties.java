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
package com.wl4g.devops.ci.config;

import org.springframework.util.Assert;

import java.io.File;
import java.util.Objects;

import static com.wl4g.devops.common.utils.lang.SystemUtils2.cleanSystemPath;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

/**
 * CICD configuration properties.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public class CiCdProperties {

	final public static String DEFUALT_JOB_BASEDIR = "jobs";
	final public static String DEFUALT_VCS_SOURCEDIR = "sources";

	/**
	 * Global workspace directory path.
	 */
	private String workspace = USER_HOME + File.separator + ".ci-workspace"; // By-default.

	/**
	 * Pipeline executor properties.
	 */
	private ExecutorProperties executor = new ExecutorProperties();

	/**
	 * Pipeline VCS properties.
	 */
	private VcsSourceProperties vcs = new VcsSourceProperties();

	/**
	 * Pipeline job properties.
	 */
	private JobProperties job = new JobProperties();

	/**
	 * Pipeline transform properties.
	 */
	private TranformProperties tranform = new TranformProperties();

	public void setWorkspace(String workspace) {
		if (!isBlank(workspace)) {
			// Clean invalid suffix separator.
			this.workspace = cleanSystemPath(workspace);
		}
	}

	public String getWorkspace() {
		return workspace;
	}

	public ExecutorProperties getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorProperties executor) {
		if (Objects.nonNull(executor)) {
			this.executor = executor;
		}
	}

	public VcsSourceProperties getVcs() {
		return vcs;
	}

	public void setVcs(VcsSourceProperties vcs) {
		if (Objects.nonNull(vcs)) {
			this.vcs = vcs;
		}
	}

	public JobProperties getJob() {
		return job;
	}

	public void setJob(JobProperties job) {
		if (Objects.nonNull(job)) {
			this.job = job;
		}
	}

	public TranformProperties getTranform() {
		return tranform;
	}

	public void setTranform(TranformProperties tranform) {
		if (Objects.nonNull(tranform)) {
			this.tranform = tranform;
		}
	}

	//
	// Functions.
	//

	public File getJobBaseDir(Integer taskHisyId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		return new File(getWorkspace() + "/" + DEFUALT_JOB_BASEDIR + "/job." + taskHisyId);
	}

	public File getJobLog(Integer taskHisyId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		return new File(getJobBaseDir(taskHisyId).getAbsolutePath() + "/build.out.log");
	}

	public File getJobBackup(Integer taskHisId) {
		Assert.notNull(taskHisId, "Rollback task history ref ID must not be null.");
		return new File(getJobBaseDir(taskHisId).getAbsolutePath());
	}

	public File getJobTmpCommandFile(Integer taskHisyId, Integer projectId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		Assert.notNull(projectId, "Task project ID must not be null.");
		return new File(getJobBaseDir(taskHisyId).getAbsolutePath() + "/" + "tmp.build." + projectId + ".sh");
	}

	public File getProjectDir(String projectName) {
		Assert.hasText(projectName, "ProjectName must not be empty.");
		return new File(getWorkspace() + "/" + DEFUALT_VCS_SOURCEDIR + "/" + projectName);
	}

}