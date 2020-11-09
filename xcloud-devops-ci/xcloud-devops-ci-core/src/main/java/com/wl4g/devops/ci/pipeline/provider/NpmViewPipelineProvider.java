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
package com.wl4g.devops.ci.pipeline.provider;

import com.wl4g.components.core.bean.ci.PipelineHistory;
import com.wl4g.components.core.bean.ci.Project;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.support.cli.command.DestroableCommand;
import com.wl4g.components.support.cli.command.LocalDestroableCommand;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.pipeline.deploy.NpmViewPipeDeployer;

import java.io.File;

import static java.lang.String.format;

/**
 * Pipeline provider for deployment NPM/(VUE/AngularJS/ReactJS...) standard
 * project.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public class NpmViewPipelineProvider extends RestorableDeployPipelineProvider {

	/**
	 * NPM default building command.
	 */
	final public static String DEFAULT_NPM_CMD = "cd %s\nrm -Rf dist\nnpm install\nnpm run build\n";

	public NpmViewPipelineProvider(PipelineContext context) {
		super(context);
	}

	// --- NPM building. ---

	@Override
	protected Runnable newPipeDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getPipelineHistoryInstances() };
		return beanFactory.getBean(NpmViewPipeDeployer.class, args);
	}

	@Override
	protected void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Long taskId) throws Exception {
		Project project = getContext().getProject();
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();
		File tmpCmdFile = config.getJobTmpCommandFile(pipelineHistory.getId(), project.getId());

		// Execution command.
		String defaultNpmBuildCmd = format(DEFAULT_NPM_CMD, projectDir);
		log.info(writeBuildLog("Building with npm default command: %s", defaultNpmBuildCmd));
		// TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(pipelineHistory.getId()), defaultNpmBuildCmd,
				tmpCmdFile, 300000L).setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

	/**
	 * Handling the NPM built installation package asset file, and convert the
	 * dist directory to a formal tar compressed package.
	 * 
	 * </br>
	 * For example:
	 * 
	 * <pre>
	 * $ cd /root/.ci-workspace/sources/portal-view/dist
	 * $ mkdir portal-view-1.0.0-bin
	 * $ mv `ls -A|grep -v portal-view-1.0.0-bin` portal-view-1.0.0-bin/
	 * $ tar -cvf /Users/vjay/.ci-workspace/sources/safecloud-view-portal/dist/portal-view-master-bin.tar *
	 * </pre>
	 */
	@Override
	protected void postModuleBuiltCommand() throws Exception {
		Project project = getContext().getProject();
		String prgramInstallFileName = config.getPrgramInstallFileName(getContext().getAppCluster().getName());
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		File tmpCmdFile = config.getJobTmpCommandFile(pipelineHistory.getId(), project.getId());
		File jobLogFile = config.getJobLog(pipelineHistory.getId());

		String tarCommand = format("cd %s/dist\nmkdir %s\nmv `ls -A|grep -v %s` %s/\ntar -cvf %s/dist/%s.tar *", projectDir,
				prgramInstallFileName, prgramInstallFileName, prgramInstallFileName, projectDir, prgramInstallFileName);
		log.info(writeBuildLog("Npm built, packing the assets file command: %s", tarCommand));

		// Execution command.
		// TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(pipelineHistory.getId()), tarCommand, tmpCmdFile,
				300000L).setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

}