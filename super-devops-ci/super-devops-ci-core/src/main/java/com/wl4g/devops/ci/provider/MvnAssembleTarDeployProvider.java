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
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
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
 * @since
 */
public class MvnAssembleTarDeployProvider extends BasedDeployProvider {

	public MvnAssembleTarDeployProvider(Project project, String path, String branch, String alias, List<AppInstance> instances, Task task,Task refTask,
			List<TaskDetail> taskDetails) {
		super(project, path, branch, alias, instances,task,refTask, taskDetails);
	}

	@Override
	public void execute() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getProject().getId());
		getDependencyService().build(getTask(),dependency, getBranch(),isSuccess,result,false);

		//get sha and md5
		setShaGit(GitUtils.getOldestCommitSha(getPath()));
		setShaLocal(FileCodec.getFileMD5(new File(getPath()+getProject().getTarPath())));
		// backup in local
		backupLocal(getPath() + getProject().getTarPath(),getTask().getId().toString());

		// scp to server
		for (AppInstance instance : getInstances()) {
			Runnable task = new MvnAssembleTarDeployTask(this, getProject(), getPath(), instance, getProject().getTarPath(),
					getTaskDetails());
			Thread t = new Thread(task);
			t.start();
			t.join();
		}

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

	@Override
	public void rollback() throws Exception{
		Dependency dependency = new Dependency();
		dependency.setProjectId(getProject().getId());

		//TODO check bakup file isExist
		String oldFilePath = getPath() + getProject().getTarPath()+"#"+getTask().getRefId();
		File oldFile = new File(oldFilePath);
		if(oldFile.exists()){
			getBackupLocal(oldFilePath,getPath() + getProject().getTarPath());
			setShaGit(getRefTask().getShaGit());
		}else{
			getDependencyService().rollback(getTask(),dependency, getBranch(),isSuccess,result,false);
			setShaGit(GitUtils.getOldestCommitSha(getPath()));
		}


		setShaLocal(FileCodec.getFileMD5(new File(getPath()+getProject().getTarPath())));
		// backup in local
		backupLocal(getPath() + getProject().getTarPath(),getTask().getId().toString());

		// scp to server
		for (AppInstance instance : getInstances()) {
			Runnable task = new MvnAssembleTarDeployTask(this, getProject(), getPath(), instance, getProject().getTarPath(),
					getTaskDetails());
			Thread t = new Thread(task);
			t.start();
			t.join();
		}

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

	public String restart(String host, String userName, String rsa) throws Exception {
		String command = ". /etc/profile && . /etc/bashrc && . ~/.bash_profile && . ~/.bashrc && sc " + getAlias() + " restart";
		return doExecute(host, userName, command, rsa);
	}

}