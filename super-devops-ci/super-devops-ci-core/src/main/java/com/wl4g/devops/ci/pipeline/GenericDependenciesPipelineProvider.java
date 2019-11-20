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

import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.exception.ci.DependencyCurrentlyInBuildingException;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Generic modular dependencies pipeline provider.</br>
 * Purpose: Because any programming language or framework built project must be
 * structure dependent.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月12日
 * @since
 */
public abstract class GenericDependenciesPipelineProvider extends AbstractPipelineProvider {

	public GenericDependenciesPipelineProvider(PipelineContext context) {
		super(context);
	}

	/**
	 * The building of generic modularization.
	 * 
	 * @param isRollback
	 * @throws Exception
	 */
	protected void buildModular(boolean isRollback) throws Exception {
		TaskHistory taskHisy = getContext().getTaskHistory();
		File jobLog = config.getJobLog(taskHisy.getId());
		if (log.isInfoEnabled()) {
			log.info("Generic building... stdout to {}", jobLog.getAbsolutePath());
		}

		// Resolve project dependencies.
		LinkedHashSet<Dependency> dependencies = dependencyService.getHierarchyDependencys(taskHisy.getProjectId(), null);
		if (log.isInfoEnabled()) {
			log.info("Resolved hierarchy dependencies: {}", dependencies);
		}

		// Custom dependency commands.
		List<TaskBuildCommand> commands = taskHistoryBuildCommandDao.selectByTaskHisId(taskHisy.getId());

		// Build of dependencies sub-modules.
		for (Dependency depd : dependencies) {
			String depCmd = extractDependencyBuildCommand(commands, depd.getDependentId());
			doBuildModuleInDependencies(depd.getDependentId(), depd.getDependentId(), depd.getBranch(), true, isRollback, depCmd);
		}

		// Build for primary(self).
		doBuildModuleInDependencies(taskHisy.getProjectId(), null, taskHisy.getBranchName(), false, isRollback,
				taskHisy.getBuildCommand());
	}

	// --- Dependencies. ---

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
		notNull(projectId, "Mvn building dependency projectId is null");

		Optional<TaskBuildCommand> buildCmdOp = safeList(buildCommands).stream()
				.filter(cmd -> cmd.getProjectId().intValue() == projectId.intValue()).findFirst();
		return buildCmdOp.isPresent() ? buildCmdOp.get().getCommand() : null;
	}

	/**
	 * Building module in dependencies.
	 * 
	 * @param projectId
	 * @param dependencyId
	 * @param branch
	 * @param isDependency
	 * @param isRollback
	 * @param buildCommand
	 * @throws Exception
	 */
	private void doBuildModuleInDependencies(Integer projectId, Integer dependencyId, String branch, boolean isDependency,
			boolean isRollback, String buildCommand) throws Exception {
		Lock lock = lockManager.getLock(LOCK_DEPENDENCY_BUILD + projectId, config.getBuild().getSharedDependencyTryTimeoutMs(),
				TimeUnit.MILLISECONDS);
		if (lock.tryLock()) { // Dependency build idle?
			try {
				updatingSourceAndBuild(projectId, dependencyId, branch, isDependency, isRollback, buildCommand);
			} finally {
				lock.unlock();
			}
		} else {
			if (log.isInfoEnabled()) {
				log.info("Waiting to build dependency, timeout for {}sec ...", config.getBuild().getJobTimeoutMs());
			}
			try {
				long begin = System.currentTimeMillis();
				// Waiting for other job builds to completed.
				if (lock.tryLock(config.getBuild().getSharedDependencyTryTimeoutMs(), TimeUnit.MILLISECONDS)) {
					if (log.isInfoEnabled()) {
						long cost = System.currentTimeMillis() - begin;
						log.info("Wait for dependency build to be skipped successful! cost: {}ms", cost);
					}
				} else {
					throw new DependencyCurrentlyInBuildingException("Failed to build, timeout waiting for dependency building.");
				}
			} finally {
				lock.unlock();
			}
		}

	}

	// --- VCS source's. ---

	/**
	 * Updating(pull & merge) source and module generic build.
	 * 
	 * @param projectId
	 * @param dependencyId
	 * @param branch
	 * @param isDependency
	 * @param isRollback
	 * @param buildCommand
	 * @throws Exception
	 */
	private void updatingSourceAndBuild(Integer projectId, Integer dependencyId, String branch, boolean isDependency,
			boolean isRollback, String buildCommand) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Pipeline building for projectId: {}", projectId);
		}

		TaskHistory taskHisy = getContext().getTaskHistory();
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
			if (getVcsOperator(project).ensureRepository(projectDir)) {
				getVcsOperator(project).rollback(project.getVcs(), projectDir, sign);
			} else {
				getVcsOperator(project).clone(project.getVcs(), project.getHttpUrl(), projectDir, branch);
				getVcsOperator(project).rollback(project.getVcs(), projectDir, sign);
			}
		} else {
			if (getVcsOperator(project).ensureRepository(projectDir)) {// 若果目录存在则chekcout分支并pull
				getVcsOperator(project).checkoutAndPull(project.getVcs(), projectDir, branch);
			} else { // 若目录不存在: 则clone 项目并 checkout 对应分支
				getVcsOperator(project).clone(project.getVcs(), project.getHttpUrl(), projectDir, branch);
			}
		}

		// Save the SHA of the dependency project.
		if (isDependency) {
			TaskSign taskSign = new TaskSign();
			taskSign.setTaskId(taskHisy.getId());
			taskSign.setDependenvyId(dependencyId);
			taskSign.setShaGit(getVcsOperator(project).getLatestCommitted(projectDir));
			taskSignDao.insertSelective(taskSign);
		}

		// Resolving placeholder & execution.
		doResolvedBuildCommands(project, projectDir, buildCommand);
	}

	// --- Building's. ---

	/**
	 * Execution resolves commands build.
	 * 
	 * @param project
	 * @param projectDir
	 * @param buildCommand
	 * @throws Exception
	 */
	private void doResolvedBuildCommands(Project project, String projectDir, String buildCommand) throws Exception {
		TaskHistory taskHisy = getContext().getTaskHistory();
		File logFile = config.getJobLog(taskHisy.getId());

		// Building.
		if (isBlank(buildCommand)) {
			doBuildWithDefaultCommands(projectDir, logFile, taskHisy.getId());
		} else {
			// Temporary command file.
			File tmpCmdFile = config.getJobTmpCommandFile(taskHisy.getId(), project.getId());
			// Resolve placeholder variables.
			buildCommand = resolveCmdPlaceholderVariables(buildCommand);
			// Execute shell file.
			processManager.execFileWaitFor(String.valueOf(taskHisy.getId()), buildCommand, tmpCmdFile, logFile, 300000);
		}

	}

	/**
	 * Execution default commands build.
	 * 
	 * @param projectDir
	 * @param logPath
	 * @throws Exception
	 */
	protected abstract void doBuildWithDefaultCommands(String projectDir, File logPath, Integer taskId) throws Exception;

}