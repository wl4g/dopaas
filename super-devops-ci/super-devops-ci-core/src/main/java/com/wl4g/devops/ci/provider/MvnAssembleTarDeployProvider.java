/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.ci.provider;

import com.wl4g.devops.ci.task.MvnAssembleTarDeployTask;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.utils.codec.FileCodec;

import java.io.File;
import java.util.List;

/**
 * Maven assemble tar provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class MvnAssembleTarDeployProvider extends BasedDeployProvider {

	public MvnAssembleTarDeployProvider(Project project, String path, String branch, String alias, List<AppInstance> instances,
			TaskHistory taskHistory, TaskHistory refTaskHistory, List<TaskHistoryDetail> taskHistoryDetails) {
		super(project, path, branch, alias, instances, taskHistory, refTaskHistory, taskHistoryDetails);
	}

	/**
	 * excute build and deploy
	 */
	@Override
	public void execute() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getProject().getId());
		// maven install , include dependency
		getDependencyService().build(getTaskHistory(), dependency, getBranch(), taskResult, false);
		if (!taskResult.isSuccess()) {
			return;
		}
		// get git sha
		setShaGit(GitUtils.getOldestCommitSha(getPath()));
		deploy();
	}

	/**
	 * rollback
	 */
	@Override
	public void rollback() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getProject().getId());
		// Old file
		String oldFilePath = config.getBackupPath() + "/" + subPackname(getProject().getTarPath()) + "#"
				+ getTaskHistory().getRefId();
		File oldFile = new File(oldFilePath);
		if (oldFile.exists()) {// Check bakup file isExist , if not -- check out
								// from git
			getBackupLocal(oldFilePath, getPath() + getProject().getTarPath());
			setShaGit(getRefTaskHistory().getShaGit());
		} else {
			getDependencyService().rollback(getTaskHistory(), getRefTaskHistory(), dependency, getBranch(), taskResult, false);
			setShaGit(GitUtils.getOldestCommitSha(getPath()));
		}
		deploy();
	}

	/**
	 * Deploy
	 */
	private void deploy() throws Exception {
		// get local sha
		setShaLocal(FileCodec.getFileMD5(new File(getPath() + getProject().getTarPath())));
		// backup in local
		backupLocal(getPath() + getProject().getTarPath(), getTaskHistory().getId().toString());
		// scp to server
		for (AppInstance instance : getInstances()) {
			// create deploy task
			Runnable task = new MvnAssembleTarDeployTask(this, getProject(), getPath(), instance, getProject().getTarPath(),
					getTaskHistoryDetails());
			Thread t = new Thread(task);
			t.start();
			t.join();
		}

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}
}