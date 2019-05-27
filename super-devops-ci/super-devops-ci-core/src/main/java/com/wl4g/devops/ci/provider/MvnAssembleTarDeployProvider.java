/*
 * Copyright 2015 the original author or authors.
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
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.shell.utils.ShellContextHolder;

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

	public MvnAssembleTarDeployProvider( Integer projectId,
			String path, String url, String branch, String alias, String tarPath, List<AppInstance> instances,
			List<TaskDetail> taskDetails) {
		super( projectId, path, url, branch, alias, tarPath, instances, taskDetails);
	}

	@Override
	public void execute() throws Exception {
		/*
		 * //chekcout if(checkGitPahtExist()){ checkOut(path,branch); }else{
		 * clone(path,url,branch); }
		 * 
		 * //build build(path);
		 */

		Dependency dependency = new Dependency();
		dependency.setProjectId(getProjectId());

		getDependencyService().build(running,dependency, getBranch());

		// backup in local
		backupLocal(getPath() + getTarPath());

		// scp to server
		/*
		 * for(AppInstance instance : instances){ //scp to server and tar
		 * //scp(path+"/"+tarPath,instance.getServerAccount()+"@"+instance.
		 * getHost(),instance.getWebappsPath());
		 * scpAndTar(path+"/"+tarPath,instance.getHost(),instance.
		 * getServerAccount(),instance.getWebappsPath()); //stop server
		 * //stop(instance.getHost(),instance.getServerAccount(),alias);
		 * reLink(instance.getHost(),instance.getWebappsPath(),instance.
		 * getServerAccount(),path+"/"+tarPath); //decompression the tar package
		 * //tar(instance.getHost(),instance.getServerAccount(),instance.
		 * getWebappsPath(),tarName); //restart server
		 * restart(instance.getHost(),instance.getServerAccount());
		 * //start(instance.getHost(),instance.getServerAccount(),alias,tarName)
		 * ; }
		 */
		// scp to server
		for (AppInstance instance : getInstances()) {
			Runnable task = new MvnAssembleTarDeployTask(this, getPath(), instance, getTarPath(), getTaskDetails(), getAlias(),running);
			Thread thread = new Thread(task);
			thread.start();
			thread.join();
		}

		ShellContextHolder.getContext().setEventListener(() -> running.set(false));

		log.info("Done");
	}

	public String restart(String host, String userName, String rsa) throws Exception {
		String command = ". /etc/profile && . /etc/bashrc && . ~/.bash_profile && . ~/.bashrc && sc " + getAlias() + " restart";
		return doExecute(host, userName, command, rsa);
	}

}