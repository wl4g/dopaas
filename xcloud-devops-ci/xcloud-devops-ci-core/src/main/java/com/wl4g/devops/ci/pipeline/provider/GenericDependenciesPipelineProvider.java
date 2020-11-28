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
package com.wl4g.devops.ci.pipeline.provider;

import com.wl4g.devops.common.exception.ci.DependencyCurrentlyInBuildingException;
import com.wl4g.components.support.cli.command.DestroableCommand;
import com.wl4g.components.support.cli.command.LocalDestroableCommand;
import com.wl4g.devops.ci.bean.ActionControl;
import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.common.bean.ci.PipeStageBuilding;
import com.wl4g.devops.common.bean.ci.PipeStageBuildingProject;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.vcs.operator.VcsOperator.RefType;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.ci.bean.RunModel.Pipeline.ModulesPorject;
import static com.wl4g.devops.ci.pipeline.flow.FlowManager.FlowStatus.RUNNING_BUILD;
import static com.wl4g.devops.ci.pipeline.flow.FlowManager.FlowStatus.RUNNING_DEPLOY;
import static com.wl4g.devops.common.constant.CiConstants.LOCK_DEPENDENCY_BUILD;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.notNull;

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
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();

		File jobLog = config.getJobLog(pipelineHistory.getId());
		log.info(writeBuildLog("Analyzing pipeline building appcluster dependencies... stdout to '%s'",
				getContext().getAppCluster().getName(), jobLog.getAbsolutePath()));

		// Resolve project dependencies.
		List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeStepBuildingProjectDao
				.selectByPipeId(getContext().getPipeline().getId());
		// LinkedHashSet<Dependency> dependencies =
		// dependencyService.getHierarchyDependencys(project.getId(), null);
		log.info(writeBuildLog("Analyzed pipeline for hierarchy of appcluster: %s, dependencies: %s",
				getContext().getAppCluster().getName(), pipeStepBuildingProjects));

		// If has Action Control, user this first
		String branchForce = null;
		ActionControl actionControl = getContext().getActionControl();
		if (Objects.nonNull(actionControl) && StringUtils.isNotBlank(actionControl.getBranch())) {
			branchForce = actionControl.getBranch();
		}

		// Pipeline State Change
		List<ModulesPorject> modulesPorjects = new ArrayList<>();
		for (PipeStageBuildingProject depd : pipeStepBuildingProjects) {
			ModulesPorject modulesPorject = new ModulesPorject();
			modulesPorject.setProjectId(depd.getProjectId());
			modulesPorject.setRef(StringUtils.isNotBlank(branchForce) ? branchForce : depd.getRef());
			modulesPorjects.add(modulesPorject);
		}
		PipelineModel pipelineModel = getContext().getPipelineModel();
		pipelineModel.setStatus(RUNNING_BUILD.toString());
		pipelineModel.setModulesPorjects(modulesPorjects);
		flowManager.pipelineStateChange(pipelineModel);

		log.info(writeBuildLog("Analyzed pipelineModel=%s", toJSONString(pipelineModel)));

		// Build of dependencies sub-modules.
		for (PipeStageBuildingProject buildingProject : pipeStepBuildingProjects) {
			// Is dependency Already build
			if (flowManager.isDependencyBuilded(buildingProject.getProjectId())) {
				continue;
			}
			pipelineModel.setCurrent(buildingProject.getProjectId());
			flowManager.pipelineStateChange(pipelineModel);

			doMutexBuildModuleInDependencies(buildingProject.getProjectId(),
					StringUtils.isNotBlank(branchForce) ? branchForce : buildingProject.getRef(),
					buildingProject.getBuildCommand());
		}

		// Build Success
		pipelineModel.setCurrent(null);
		// build complete ==? pipeline complete
		pipelineModel.setStatus(RUNNING_DEPLOY.toString());
		flowManager.pipelineStateChange(pipelineModel);

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
	 * Building module in dependencies with mutually.
	 * 
	 * @param projectId
	 * @param dependencyId
	 * @param branch
	 * @param isDependency
	 * @param buildCommand
	 * @throws Exception
	 */
	private final void doMutexBuildModuleInDependencies(Long projectId, String branch, String buildCommand) throws Exception {
		Lock lock = lockManager.getLock(LOCK_DEPENDENCY_BUILD + projectId, config.getBuild().getSharedDependencyTryTimeoutMs(),
				TimeUnit.MILLISECONDS);
		if (lock.tryLock()) { // Dependency build wait?
			try {
				pullSourceAndBuild(projectId, branch, buildCommand);
			} catch (Exception e) {
				throw e;
			} finally {
				lock.unlock();
			}
		} else {
			String buildWaitMsg = writeBuildLog("Waiting to build dependency, for timeout: %sms,  projectId: %s ...",
					config.getBuild().getJobTimeoutMs(), projectId);
			log.info(buildWaitMsg);

			try {
				long begin = currentTimeMillis();
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
	private void pullSourceAndBuild(Long projectId, String branch, String buildCommand) throws Exception {
		log.info("Pipeline building for projectId: {}", projectId);

		PipeStageBuilding pipeStepBuilding = getContext().getPipeStepBuilding();

		Project project = projectDao.selectByPrimaryKey(projectId);
		notNull(project, format("Not found project by %s", projectId));

		// Obtain project source from VCS.
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();

		// Checked out? pull and merge.
		if (getVcsOperator(project).hasLocalRepository(projectDir)) {
			log.info(writeBuildLog("Pulling project source to '%s:%s' ...", branch, projectDir));
			getVcsOperator(project).checkoutAndPull(project.getVcs(), projectDir, branch,
					RefType.of(pipeStepBuilding.getRefType().toString()));
		} else { // Unchecked out? new clone & checkout.
			log.info(writeBuildLog("New checkout project source to '%s:%s' ...", branch, projectDir));
			getVcsOperator(project).clone(project.getVcs(), project.getHttpUrl(), projectDir, branch);
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
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();
		File jobLogFile = config.getJobLog(pipelineHistory.getId());

		// Building.
		if (isBlank(buildCommand)) {
			doBuildWithDefaultCommand(projectDir, jobLogFile, pipelineHistory.getId());
		} else {
			// Temporary command file.
			File tmpCmdFile = config.getJobTmpCommandFile(pipelineHistory.getId(), project.getId());

			// Resolve placeholder variables.
			buildCommand = resolveCmdPlaceholderVariables(buildCommand);
			log.info(writeBuildLog("Building with customizes command: '%s' ...", buildCommand));

			// Execute shell file.
			// TODO timeoutMs?
			DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(pipelineHistory.getId()), buildCommand, tmpCmdFile,
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
	protected abstract void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Long taskId) throws Exception;

	/**
	 * Customized handing after building the module.
	 * 
	 * @throws Exception
	 */
	protected void postModuleBuiltCommand() throws Exception {
		// Nothing do
	}

}