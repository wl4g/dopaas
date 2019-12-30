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
import com.wl4g.devops.ci.pipeline.deploy.NpmViewPipeDeployer;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.support.cli.command.LocalDestroableCommand;

import java.io.File;

import static com.wl4g.devops.tool.common.io.FileIOUtils.writeALineFile;

/**
 * Pipeline provider for deployment NPM/(VUE/AngularJS/ReactJS...) standard
 * project.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public class NpmViewPipelineProvider extends RestorableDeployPipelineProvider {

	public NpmViewPipelineProvider(PipelineContext context) {
		super(context);
	}

	// --- NPM building. ---

	@Override
	protected Runnable newPipeDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getTaskHistoryInstances() };
		return beanFactory.getBean(NpmViewPipeDeployer.class, args);
	}

	@Override
	protected void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Integer taskId) throws Exception {
		Project project = getContext().getProject();
		TaskHistory taskHistory = getContext().getTaskHistory();
		File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());

		// Execution command. TODO timeoutMs?
		String defaultNpmBuildCmd = String.format("cd %s\nrm -Rf dist\nnpm install\nnpm run build\n", projectDir);
		writeALineFile(config.getJobLog(taskId).getAbsoluteFile(),"execute cmd:"+defaultNpmBuildCmd);
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), defaultNpmBuildCmd,
				tmpCmdFile, 300000L).setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

	/**
	 * Handling the NPM built installation package asset file, and convert the dist
	 * directory to a formal tar compressed package.
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
		TaskHistory taskHistory = getContext().getTaskHistory();
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());
		File jobLogFile = config.getJobLog(getContext().getTaskHistory().getId());

		String tarCommand = String.format("cd %s/dist\nmkdir %s\nmv `ls -A|grep -v %s` %s/\ntar -cvf %s/dist/%s.tar *",
				projectDir, prgramInstallFileName, prgramInstallFileName, prgramInstallFileName,
				projectDir, prgramInstallFileName);

		writeALineFile(config.getJobLog(taskHistory.getId()).getAbsoluteFile(),"execute cmd:"+tarCommand);

		// Execution command. TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), tarCommand, tmpCmdFile,
				300000L).setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

}