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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.core.PipelineContext;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.exception.ci.DependencyCurrentlyInBuildingException;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.ci.utils.GitUtils.*;
import static com.wl4g.devops.ci.utils.PipelineUtils.ensureDirectory;
import static com.wl4g.devops.ci.utils.PipelineUtils.subPackname;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Abstract based MAVEN pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
public abstract class BasedMavenPipelineProvider extends AbstractPipelineProvider {

	public BasedMavenPipelineProvider(PipelineContext info) {
		super(info);
	}

	// --- MAVEN building. ---

	/**
	 * Do maven building.
	 * 
	 * @param taskHistory
	 * @param isRollback
	 * @throws Exception
	 */
	protected void mvnBuild(TaskHistory taskHistory, boolean isRollback) throws Exception {
		File jobLog = config.getJobLog(taskHistory.getId());
		if (log.isInfoEnabled()) {
			log.info("Building starting, stdout to {}", jobLog.getAbsolutePath());
		}

		// Resolve project dependencies.
		LinkedHashSet<Dependency> dependencies = dependencyService.getHierarchyDependencys(taskHistory.getProjectId(), null);
		if (log.isInfoEnabled()) {
			log.info("Resolved hierarchy dependencies: {}", dependencies);
		}

		// Custom dependency commands.
		List<TaskBuildCommand> commands = taskHisBuildCommandDao.selectByTaskHisId(taskHistory.getId());
		for (Dependency depd : dependencies) {
			String depCmd = extractDependencyBuildCommand(commands, depd.getDependentId());
			// Do MAVEN building.
			doMvnBuildDependencies(taskHistory, depd.getDependentId(), depd.getDependentId(), depd.getBranch(), true, isRollback,
					depCmd);
		}

		// Do MAVEN building.
		doMvnBuildDependencies(taskHistory, taskHistory.getProjectId(), null, taskHistory.getBranchName(), false, isRollback,
				taskHistory.getBuildCommand());
	}

	/**
	 * Local backup
	 */
	protected void backupLocal() throws Exception {
		Integer taskHisId = getContext().getTaskHistory().getId();
		String targetPath = getContext().getProjectSourceDir() + getContext().getProject().getAssetsPath();
		String backupPath = config.getJobBackup(taskHisId).getAbsolutePath() + "/"
				+ subPackname(getContext().getProject().getAssetsPath());

		ensureDirectory(config.getJobBackup(taskHisId).getAbsolutePath());

		String command = "cp -Rf " + targetPath + " " + backupPath;
		processManager.exec(command, config.getJobLog(taskHisId), 300000);
	}

	/**
	 * Roll-back backup file.
	 * 
	 * @throws Exception
	 */
	protected void rollbackBackupFile() throws Exception {
		Integer taskHisRefId = getContext().getRefTaskHistory().getId();
		String backupPath = config.getJobBackup(taskHisRefId).getAbsolutePath()
				+ subPackname(getContext().getProject().getAssetsPath());

		String target = getContext().getProjectSourceDir() + getContext().getProject().getAssetsPath();
		String command = "cp -Rf " + backupPath + " " + target;
		processManager.exec(command, config.getJobLog(taskHisRefId), 300000);
	}

	/**
	 * Extract dependency project custom command.
	 * 
	 * @param buildCommands
	 * @param projectId
	 * @return
	 */
	private String extractDependencyBuildCommand(List<TaskBuildCommand> buildCommands, Integer projectId) {
		if (isEmpty(buildCommands)) {
			return null;
		}
		notNull(projectId, "Building dependency projectId is null");

		Optional<TaskBuildCommand> buildCmdOp = safeList(buildCommands).stream()
				.filter(cmd -> cmd.getProjectId().intValue() == projectId.intValue()).findFirst();
		return buildCmdOp.isPresent() ? buildCmdOp.get().getCommand() : null;
	}

	/**
	 * Execution MVN dependencies building.
	 * 
	 * @param taskHisy
	 * @param projectId
	 * @param dependencyId
	 * @param branch
	 * @param taskResult
	 * @param isDependency
	 * @param isRollback
	 * @param buildCommand
	 * @throws Exception
	 */
	private void doMvnBuildDependencies(TaskHistory taskHisy, Integer projectId, Integer dependencyId, String branch,
			boolean isDependency, boolean isRollback, String buildCommand) throws Exception {
		Lock lock = lockManager.getLock(LOCK_DEPENDENCY_BUILD + projectId, config.getJob().getSharedDependencyTryTimeoutMs(),
				TimeUnit.MILLISECONDS);
		if (lock.tryLock()) { // Dependency build idle?
			try {
				pullSourceAndBuild(taskHisy, projectId, dependencyId, branch, isDependency, isRollback, buildCommand);
			} finally {
				lock.unlock();
			}
		} else {
			if (log.isInfoEnabled()) {
				log.info("Waiting to build dependency, timeout for {}sec ...", config.getJob().getJobTimeoutMs());
			}
			try {
				long begin = System.currentTimeMillis();
				// Waiting for other job builds to completed.
				if (lock.tryLock(config.getJob().getSharedDependencyTryTimeoutMs(), TimeUnit.MILLISECONDS)) {
					if (log.isInfoEnabled()) {
						long cost = System.currentTimeMillis() - begin;
						log.info("Wait for dependency build to be skipped successfully! cost: {}ms", cost);
					}
				} else {
					throw new DependencyCurrentlyInBuildingException("Failed to build, timeout waiting for dependency building.");
				}
			} finally {
				lock.unlock();
			}
		}

	}

	/**
	 * Pull merge source and MVN building.
	 * 
	 * @param taskHisy
	 * @param projectId
	 * @param dependencyId
	 * @param branch
	 * @param taskRet
	 * @param isDependency
	 * @param isRollback
	 * @param buildCommand
	 * @throws Exception
	 */
	private void pullSourceAndBuild(TaskHistory taskHisy, Integer projectId, Integer dependencyId, String branch,
			boolean isDependency, boolean isRollback, String buildCommand) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Pipeline building for projectId: {}", projectId);
		}
		Project project = projectDao.selectByPrimaryKey(projectId);
		notNull(project, String.format("Not found project by %s", projectId));

		// Obtain project source from VCS.
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		if (isRollback) {
			String sign;
			if (isDependency) {
				TaskSign taskSign = taskSignDao.selectByDependencyIdAndTaskId(dependencyId, taskHisy.getRefId());
				notNull(taskSign, String.format("Not found taskSign for dependencyId:%s, taskHistoryRefId:%s", dependencyId,
						taskHisy.getRefId()));
				sign = taskSign.getShaGit();
			} else {
				sign = taskHisy.getShaGit();
			}
			if (checkGitPath(projectDir)) {
				GitUtils.rollback(config.getVcs().getGitlab().getCredentials(), projectDir, sign);
			} else {
				GitUtils.clone(config.getVcs().getGitlab().getCredentials(), project.getGitUrl(), projectDir, branch);
				GitUtils.rollback(config.getVcs().getGitlab().getCredentials(), projectDir, sign);
			}
		} else {
			if (checkGitPath(projectDir)) {// 若果目录存在则chekcout分支并pull
				checkout(config.getVcs().getGitlab().getCredentials(), projectDir, branch);
			} else { // 若目录不存在: 则clone 项目并 checkout 对应分支
				GitUtils.clone(config.getVcs().getGitlab().getCredentials(), project.getGitUrl(), projectDir, branch);
			}
		}

		// Save the SHA of the dependency project for roll-back。
		if (isDependency) {
			TaskSign taskSign = new TaskSign();
			taskSign.setTaskId(taskHisy.getId());
			taskSign.setDependenvyId(dependencyId);
			taskSign.setShaGit(getLatestCommitted(projectDir));
			taskSignDao.insertSelective(taskSign);
		}

		File logFile = config.getJobLog(taskHisy.getId());
		// Building.
		if (isBlank(buildCommand)) {
			doBuildWithDefaultCommand(projectDir, logFile, taskHisy.getId());
		} else {
			// Temporary command file.
			File tmpCmdFile = config.getJobTmpCommandFile(taskHisy.getId(), project.getId());
			// Resolve placeholder variables.
			buildCommand = resolvePlaceholderVariables(buildCommand);
			// Execute shell file.
			processManager.execFile(String.valueOf(taskHisy.getId()), buildCommand, tmpCmdFile, logFile, 300000);
		}

	}

	/**
	 * Build with default commands.
	 * 
	 * @param projectDir
	 * @param logPath
	 * @throws Exception
	 */
	private void doBuildWithDefaultCommand(String projectDir, File logPath, Integer taskId) throws Exception {
		String defaultCommand = "mvn -f " + projectDir + "/pom.xml clean install -Dmaven.test.skip=true -DskipTests";
		// SSHTool.exec(defaultCommand, logPath.getAbsolutePath(), taskId);
		processManager.exec(String.valueOf(taskId), defaultCommand, null, logPath, 300000);
	}

}