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

/**
 * Pipeline provider for deployment NPM/(VUE/AngularJS/ReactJS...) standard
 * project.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public class NpmViewPipelineProvider extends BasedPhysicalBackupPipelineProvider {

	public NpmViewPipelineProvider(PipelineContext context) {
		super(context);
	}

	// --- NPM building. ---

	@Override
	public void execute() throws Exception {
		// Building maven of modules dependencies.
		buildModular(false);
	}

	@Override
	public void rollback() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Runnable newDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getTaskHistoryInstances() };
		return beanFactory.getBean(NpmViewPipeDeployer.class, args);
	}

	private void build() throws Exception {
		// TODO
		// step3: tar -c
		pkg();
		// step4 scp ==> tar -x

		// Startup pipeline jobs.
		executeRemoteDeploying();

		if (log.isInfoEnabled()) {
			log.info("Npm pipeline execution successful!");
		}
	}

	/**
	 * tar -cvf ***.tar -C /home/ci/view * tar -xvf ***.tar -C /opt/apps/view
	 */
	private void pkg() throws Exception {
		Project project = getContext().getProject();
		String prgramInstallFileName = config.getPrgramInstallFileName(getContext().getAppCluster().getName());
		TaskHistory taskHistory = getContext().getTaskHistory();
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());
		File jobLogFile = config.getJobLog(getContext().getTaskHistory().getId());

		/**
		 * For example:
		 * 
		 * <pre>
		 * $ cd /root/.ci-workspace/sources/super-devops-view/dist
		 * $ mkdir super-devops-view-master-bin
		 * $ mv `ls -A|grep -v super-devops-view-master-bin`
		 * $ super-devops-view-master-bin/
		 * $ tar -cvf /root/.ci-workspace/jobs/job.936/super-devops-view-master-bin.tar *
		 * </pre>
		 */
		String tarCommand = String.format("cd %s/dist\nmkdir %s\nmv `ls -A|grep -v %s` %s/\ntar -cvf %s/%s.tar *", projectDir,
				prgramInstallFileName, prgramInstallFileName, prgramInstallFileName,
				config.getJobBackup(getContext().getTaskHistory().getId()), prgramInstallFileName);
		// Execution command. TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), tarCommand, tmpCmdFile, 300000L)
				.setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

	@Override
	protected void doBuildWithDefaultCommands(String projectDir, File jobLogFile, Integer taskId) throws Exception {
		Project project = getContext().getProject();
		TaskHistory taskHistory = getContext().getTaskHistory();
		File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());

		String defaultBuildCommand = String.format("cd %s\nrm -Rf dist\nnpm install\nnpm run build\n", projectDir);
		// Execution command. TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), defaultBuildCommand, tmpCmdFile,
				300000L).setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

}