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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.notNull;

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
		// build
		build();
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
		// step1: git clone/pull
		getSources(false);
		// step2: npm install & npm run build ==> run build command
		npmBuild();
		// step3: tar -c
		pkg();
		// step4 scp ==> tar -x

		// Startup pipeline jobs.
		executeRemoteDeploying();

		if (log.isInfoEnabled()) {
			log.info("Npm pipeline execution successful!");
		}
	}

	private void getSources(boolean isRollback) throws Exception {
		log.info("Pipeline building for projectId={}", getContext().getProject().getId());
		Project project = getContext().getProject();
		notNull(project, "project not exist");

		String branchName = getContext().getTaskHistory().getBranchName();
		// Project source directory.
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();

		if (isRollback) {
			String sha = getContext().getTaskHistory().getShaGit();
			if (getVcsOperator(project).ensureRepository(projectDir)) {
				getVcsOperator(project).rollback(project.getVcs(), projectDir, sha);
			} else {
				getVcsOperator(project).clone(project.getVcs(), project.getHttpUrl(), projectDir, branchName);
				getVcsOperator(project).rollback(project.getVcs(), projectDir, sha);
			}
		} else {
			if (getVcsOperator(project).ensureRepository(projectDir)) {// 若果目录存在则chekcout分支并pull
				getVcsOperator(project).checkoutAndPull(project.getVcs(), projectDir,
						getContext().getTaskHistory().getBranchName());
			} else { // 若目录不存在: 则clone 项目并 checkout 对应分支
				getVcsOperator(project).clone(project.getVcs(), project.getHttpUrl(), projectDir, branchName);
			}
		}
	}

	private void npmBuild() throws Exception {
		Project project = getContext().getProject();
		TaskHistory taskHistory = getContext().getTaskHistory();
		File jobLogFile = config.getJobLog(getContext().getTaskHistory().getId());
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();

		// Building.
		if (isBlank(taskHistory.getBuildCommand())) {
			doBuildWithDefaultCommand(projectDir, jobLogFile, taskHistory.getId());
		} else {
			// Obtain temporary command file.
			File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());
			// Resolve placeholder variables.
			String buildCommand = resolveCmdPlaceholderVariables(taskHistory.getBuildCommand());
			// Execution command.
			// TODO timeoutMs?
			DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), buildCommand, tmpCmdFile,
					300000L).setStdout(jobLogFile).setStderr(jobLogFile);
			pm.execWaitForComplete(cmd);
		}
	}

	private void doBuildWithDefaultCommand(String projectDir, File jobLogFile, Integer taskId) throws Exception {
		Project project = getContext().getProject();
		TaskHistory taskHistory = getContext().getTaskHistory();
		File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());
		String buildCommand = "cd " + projectDir + "\nrm -Rf dist\nnpm install\nnpm run build\n";
		// Execution command.
		// TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), buildCommand, tmpCmdFile, 300000L)
				.setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

	/**
	 * tar -cvf ***.tar -C /home/ci/view * tar -xvf ***.tar -C /opt/apps/view
	 */
	private void pkg() throws Exception {
		Project project = getContext().getProject();
		String appClusterName = getContext().getAppCluster().getName();
		String prgramInstallFileName = config.getPrgramInstallFileName(getContext().getAppCluster().getName());
		TaskHistory taskHistory = getContext().getTaskHistory();
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		File tmpCmdFile = config.getJobTmpCommandFile(taskHistory.getId(), project.getId());
		File jobLogFile = config.getJobLog(getContext().getTaskHistory().getId());
		
		// cd /root/.ci-workspace/sources/super-devops-view/dist
		// mkdir super-devops-view-master-bin
		// mv `ls -A|grep -v super-devops-view-master-bin` super-devops-view-master-bin/
		// tar -cvf /root/.ci-workspace/jobs/job.936/super-devops-view-master-bin.tar *
		String tarCommand = String.format("cd %s/dist\nmkdir %s\nmv `ls -A|grep -v %s` %s/\ntar -cvf %s/%s.tar *", projectDir,
				prgramInstallFileName, prgramInstallFileName, prgramInstallFileName,
				config.getJobBackup(getContext().getTaskHistory().getId()), prgramInstallFileName);
		// Execution command.
		// TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHistory.getId()), tarCommand, tmpCmdFile, 300000L)
				.setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

	@Override
	protected void doBuildWithDefaultCommands(String projectDir, File jobLogFile, Integer taskId) throws Exception {
		String defaultCommand = "cd " + projectDir + " && npm install";
		// Execution command.
		// TODO timeoutMs/pwdDir?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskId), defaultCommand, null, 300000L)
				.setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

}