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
import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.support.cli.command.LocalDestroableCommand;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static java.lang.String.format;
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

	// --- Build & dependencies. ---

	@Override
	public void execute() throws Exception {
		// Building of generic module dependencies.
		buildModular();
	}

	/**
	 * The building of generic modularization.
	 * 
	 * @throws Exception
	 */
	protected void buildModular() throws Exception {
		TaskHistory taskHisy = getContext().getTaskHistory();
		File jobLog = config.getJobLog(taskHisy.getId());
		log.info(writeBuildLog("Analyzing pipeline building appcluster dependencies... stdout to '%s'",
				getContext().getAppCluster().getName(), jobLog.getAbsolutePath()));

		// Resolve project dependencies.
		LinkedHashSet<Dependency> dependencies = dependencyService.getHierarchyDependencys(taskHisy.getProjectId(), null);
		log.info(writeBuildLog("Analyzed pipeline for hierarchy of appcluster: %s, dependencies: %s",
				getContext().getAppCluster().getName(), dependencies));

		// Custom dependency commands.
		List<TaskBuildCommand> commands = taskHistoryBuildCommandDao.selectByTaskHisId(taskHisy.getId());

		// Build of dependencies sub-modules.
		for (Dependency depd : dependencies) {
			String depCmd = extractDependencyBuildCommand(commands, depd.getDependentId());
			doMutexBuildModuleInDependencies(depd.getDependentId(), depd.getDependentId(), depd.getBranch(), true, depCmd);
		}

		// Build for primary(self).
		doMutexBuildModuleInDependencies(taskHisy.getProjectId(), null, taskHisy.getBranchName(), false,
				taskHisy.getBuildCommand());

		// Call after all built dependencies completed handling.
		postBuiltModulesDependencies();
	}

	/**
	 * Handing after all dependency modules are built, For example, set the
	 * fingerprint of the source code or asset installation file, publish the
	 * installation package to remote, deploy rollback and so on.
	 * 
	 * @throws Exception
	 */
	protected abstract void postBuiltModulesDependencies() throws Exception;

	/**
	 * Extract dependencies project custom command.
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
	 * Building module in dependencies with mutually.
	 * 
	 * @param projectId
	 * @param dependencyId
	 * @param branch
	 * @param isDependency
	 * @param buildCommand
	 * @throws Exception
	 */
	private final void doMutexBuildModuleInDependencies(Integer projectId, Integer dependencyId, String branch,
			boolean isDependency, String buildCommand) throws Exception {
		Lock lock = lockManager.getLock(LOCK_DEPENDENCY_BUILD + projectId, config.getBuild().getSharedDependencyTryTimeoutMs(),
				TimeUnit.MILLISECONDS);
		if (lock.tryLock()) { // Dependency build wait?
			try {
				pullSourceAndBuild(projectId, dependencyId, branch, isDependency, buildCommand);
			} catch (Exception e) {
				throw e;
			} finally {
				lock.unlock();
			}
		} else {
			String buildWaitMsg = writeBuildLog(
					"Waiting to build dependency, for timeout: %sms, dependencyId: %s, projectId: %s ...",
					config.getBuild().getJobTimeoutMs(), dependencyId, projectId);
			log.info(buildWaitMsg);

			try {
				long begin = System.currentTimeMillis();
				// Waiting for other job builds to completed.
				if (lock.tryLock(config.getBuild().getSharedDependencyTryTimeoutMs(), TimeUnit.MILLISECONDS)) {
					long cost = System.currentTimeMillis() - begin;
					String waitCostMsg = writeBuildLog("Wait for dependency build to be skipped successful! cost: %sms", cost);
					log.info(waitCostMsg);
				} else {
					throw new DependencyCurrentlyInBuildingException(
							format("Failed to build, timeout waiting for dependency building, for projectId: %s", projectId));
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
	 * @param buildCommand
	 * @throws Exception
	 */
	private final void pullSourceAndBuild(Integer projectId, Integer dependencyId, String branch, boolean isDependency,
			String buildCommand) throws Exception {
		log.info("Pipeline building for projectId: {}", projectId);

		TaskHistory taskHisy = getContext().getTaskHistory();
		Project project = projectDao.selectByPrimaryKey(projectId);
		notNull(project, String.format("Not found project by %s", projectId));

		// Obtain project source from VCS.
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();

		// Checked out? pull and merge.
		if (getVcsOperator(project).hasLocalRepository(projectDir)) {
			log.info(writeBuildLog("Pulling project source to '%s:%s' ...", branch, projectDir));
			getVcsOperator(project).checkoutAndPull(project.getVcs(), projectDir, branch);
		} else { // Unchecked out? new clone & checkout.
			log.info(writeBuildLog("New checkout project source to '%s:%s' ...", branch, projectDir));
			getVcsOperator(project).clone(project.getVcs(), project.getHttpUrl(), projectDir, branch);
		}

		// Save the SHA of the dependency project.
		if (isDependency) {
			TaskSign sign = new TaskSign();
			sign.setTaskId(taskHisy.getId());
			sign.setDependenvyId(dependencyId);
			sign.setShaGit(getVcsOperator(project).getLatestCommitted(projectDir));
			taskSignDao.insertSelective(sign);
		}

		// Resolving placeholder & execution.
		doResolvedBuildCommand(project, projectDir, buildCommand);
	}

	// --- Building's. ---

	/**
	 * Execution resolves commands & build.
	 * 
	 * @param project
	 * @param projectDir
	 * @param buildCommand
	 * @throws Exception
	 */
	private final void doResolvedBuildCommand(Project project, String projectDir, String buildCommand) throws Exception {
		TaskHistory taskHisy = getContext().getTaskHistory();
		File jobLogFile = config.getJobLog(taskHisy.getId());

		// Building.
		if (isBlank(buildCommand)) {
			doBuildWithDefaultCommand(projectDir, jobLogFile, taskHisy.getId());
		} else {
			// Temporary command file.
			File tmpCmdFile = config.getJobTmpCommandFile(taskHisy.getId(), project.getId());

			// Resolve placeholder variables.
			buildCommand = resolveCmdPlaceholderVariables(buildCommand);
			log.info(writeBuildLog("Building with customizes command: '%s' ...", buildCommand));

			// Execute shell file.
			// TODO timeoutMs?
			DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHisy.getId()), buildCommand, tmpCmdFile,
					300000L).setStdout(jobLogFile).setStderr(jobLogFile);
			pm.execWaitForComplete(cmd);
		}

		// Call after built command.
		postModuleBuiltCommand();
	}

	/**
	 * Execution default build commands.
	 * 
	 * @param projectDir
	 * @param jobLogFile
	 * @throws Exception
	 */
	protected abstract void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Integer taskId) throws Exception;

	/**
	 * Customized handing after building the module.
	 * 
	 * @throws Exception
	 */
	protected void postModuleBuiltCommand() throws Exception {
		// Nothing do
	}

}