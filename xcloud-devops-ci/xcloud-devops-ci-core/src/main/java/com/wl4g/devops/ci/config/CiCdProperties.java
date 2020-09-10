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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.wl4g.components.common.log.SmartLogger;

import java.io.File;
import java.util.Objects;

import static com.wl4g.components.common.lang.SystemUtils2.cleanSystemPath;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;
import static org.springframework.util.Assert.hasText;

/**
 * CICD configuration properties.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public class CiCdProperties implements InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Global workspace directory path.
	 */
	private String workspace = USER_HOME + File.separator + ".ci-workspace"; // By-default.

	/**
	 * Pipeline executor configuration properties.
	 */
	private ExecutorProperties executor = new ExecutorProperties();

	/**
	 * VCS configuration properties.
	 */
	private VcsSourceProperties vcs = new VcsSourceProperties();

	/**
	 * Pipeline build configuration properties.
	 */
	private BuildProperties build = new BuildProperties();

	/**
	 * Pipeline deploy configuration properties.
	 */
	private DeployProperties deploy = new DeployProperties();

	/**
	 * Test and inspection report configuration.
	 */
	private TestedReportProperties testedReport = new TestedReportProperties();

	/**
	 * Docker Properties
	 */
	private DockerProperties docker = new DockerProperties();

	/**
	 * project collaboration management configuration.
	 */
	private PcmProperties pcm = new PcmProperties();

	/**
	 * Pipeline log records cleaner configuration.
	 */
	private LogCleanerProperties logCleaner = new LogCleanerProperties();

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

	public BuildProperties getBuild() {
		return build;
	}

	public void setBuild(BuildProperties build) {
		if (Objects.nonNull(build)) {
			this.build = build;
		}
	}

	public DeployProperties getDeploy() {
		return deploy;
	}

	public void setDeploy(DeployProperties deploy) {
		if (Objects.nonNull(deploy)) {
			this.deploy = deploy;
		}
	}

	public TestedReportProperties getTestedReport() {
		return testedReport;
	}

	public void setTestedReport(TestedReportProperties testReport) {
		this.testedReport = testReport;
	}

	public DockerProperties getDocker() {
		return docker;
	}

	public void setDocker(DockerProperties docker) {
		this.docker = docker;
	}

	public PcmProperties getPcm() {
		return pcm;
	}

	public void setPcm(PcmProperties pcm) {
		this.pcm = pcm;
	}

	public LogCleanerProperties getLogCleaner() {
		return logCleaner;
	}

	public void setLogCleaner(LogCleanerProperties logCleaner) {
		this.logCleaner = logCleaner;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		applyDefaultProperties();
	}

	/**
	 * Apply default properties values.
	 */
	protected void applyDefaultProperties() {
		if (isNull(getBuild().getSharedDependencyTryTimeoutMs())) {
			// Default to one third of the full job timeout.
			getBuild().setSharedDependencyTryTimeoutMs(getBuild().getJobTimeoutMs() / 3);
			log.info("Use sharedDependencyTryTimeoutMs of default value: {}", getBuild().getSharedDependencyTryTimeoutMs());
		}

		if (isNull(getDeploy().getTransferTimeoutMs())) {
			// Default to one fifth of the full job timeout.
			getDeploy().setTransferTimeoutMs(getBuild().getJobTimeoutMs() / 5);
			log.info("Use transferTimeoutMs of default value: {}", getDeploy().getTransferTimeoutMs());
		}
	}

	//
	// Function's.
	//

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/jobs/job.11/
	 * 
	 * @param taskHisyId
	 * @return
	 */
	public File getJobBaseDir(Integer taskHisyId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		return new File(getWorkspace() + "/" + DEFUALT_JOB_BASEDIR + "/job." + taskHisyId);
	}

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/jobs/job.11/build.out.log
	 * 
	 * @param taskHisyId
	 * @return
	 */
	public File getJobLog(Integer taskHisyId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		return new File(getJobBaseDir(taskHisyId).getAbsolutePath() + "/build.out.log");
	}

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/jobs/job.11/deploy.234.out.log
	 *
	 * @param taskHisyId
	 * @param instanceId
	 * @return
	 */
	public File getJobDeployerLog(Integer taskHisyId, Integer instanceId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		Assert.notNull(instanceId, "Task history instanceId ID must not be null.");
		return new File(getJobBaseDir(taskHisyId).getAbsolutePath() + "/deploy." + instanceId + ".out.log");
	}

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/jobs/job.11/{PROJECT_NAME}
	 * 
	 * @param taskHisId
	 * @return
	 */
	public File getJobBackupDir(Integer taskHisId) {
		Assert.notNull(taskHisId, "Rollback task history ref ID must not be null.");
		return new File(getJobBaseDir(taskHisId).getAbsolutePath());
	}

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/jobs/job.11/tmp.build.2.sh
	 * 
	 * @param taskHisyId
	 * @param projectId
	 * @return
	 */
	public File getJobTmpCommandFile(Integer taskHisyId, Integer projectId) {
		Assert.notNull(taskHisyId, "Task history ID must not be null.");
		Assert.notNull(projectId, "Task project ID must not be null.");
		return new File(getJobBaseDir(taskHisyId).getAbsolutePath() + "/" + "tmp.build." + projectId + ".sh");
	}

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/sources/example-web/[pom.xml]
	 * 
	 * @param projectName
	 * @return
	 */
	public File getProjectSourceDir(String projectName) {
		Assert.hasText(projectName, "ProjectName must not be empty.");
		return new File(getWorkspace() + "/" + DEFUALT_VCS_SOURCEDIR + "/" + projectName);
	}

	/**
	 * Timeout for execution of each remote command during the distribution
	 * deployment phase.
	 * 
	 * @param instances
	 * @return
	 */
	public long getRemoteCommandTimeoutMs(int instances) {
		// isTrue(instances > 0, "Job instance count must greater than or equal
		// to 0");
		return (long) (getDeploy().getTransferTimeoutMs() * 0.76);
	}

	/**
	 * e.g. </br>
	 * ~/.ci-workspace/jobs/job.11/{PROJECT_NAME}/{PROJECT_NAME}.{SUFFIX}
	 * 
	 * @param projectName
	 * @param suffix
	 * @return
	 */
	public File getTransferLocalTmpFile(Integer taskHisId, String projectName, String suffix) {
		hasText(projectName, "Transfer project name must not be empty.");
		hasText(suffix, "Transfer project file suffix must not be empty.");
		return new File(getJobBackupDir(taskHisId).getAbsolutePath() + "/" + projectName + "." + suffix);
	}

	/**
	 * e.g. </br>
	 * ~/.ci-temporary/{PROJECT_NAME}.{SUFFIX}
	 * 
	 * @param projectName
	 * @param suffix
	 * @return
	 */
	public String getTransferRemoteHomeTmpFile(String projectName, String suffix) {
		hasText(projectName, "Transfer project name must not be empty.");
		hasText(suffix, "Transfer project file suffix must not be empty.");
		return getDeploy().getRemoteHomeTmpDir() + "/" + projectName + "." + suffix;
	}

	/**
	 * e.g. </br>
	 * portal-master-bin
	 * 
	 * @param clusterName
	 * @return
	 */
	public String getPrgramInstallFileName(String clusterName) {
		return String.format("%s-%s-%s", clusterName, DEFUALT_VERSION, DEFUALT_ASSETS_TYPE);
	}

	public String getTarFileNameWithTar(String clusterName) {
		return String.format("%s-%s-%s.tar", clusterName, DEFUALT_VERSION, DEFUALT_ASSETS_TYPE);
	}

	public String getAssetsFullFilename(String assetsPath, String clusterName) {
		return assetsPath + "/" + getPrgramInstallFileName(clusterName) + ".tar";
	}

	final public static String DEFUALT_JOB_BASEDIR = "jobs";
	final public static String DEFUALT_VCS_SOURCEDIR = "sources";

	/**
	 * The default version number(alias), which is used for pipeline
	 * construction of production installation package
	 */
	final public static String DEFUALT_VERSION = "master";

	/**
	 * Default pipeline build production installation package type.(e.g.
	 * portal-master-bin.tar)
	 */
	final public static String DEFUALT_ASSETS_TYPE = "bin";

}