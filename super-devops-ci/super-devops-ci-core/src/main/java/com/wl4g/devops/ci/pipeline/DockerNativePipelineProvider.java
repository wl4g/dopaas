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

import com.wl4g.devops.ci.pipeline.job.DockerNativePipelineJob;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.FileCodec;
import com.wl4g.devops.support.task.GenericTaskRunner.NamedIdJob;

import java.io.File;

/**
 * Docker native integrate pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class DockerNativePipelineProvider extends MavenPipelineProvider {

	public DockerNativePipelineProvider(PipelineInfo deployProviderBean) {
		super(deployProviderBean);
	}

	/**
	 * execute -- build , push , pull , run
	 *
	 * @throws Exception
	 */
	@Override
	public void execute() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getPipelineInfo().getProject().getId());
		build(getPipelineInfo().getTaskHistory(), false);

		// get sha and md5
		setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));

		// docker build
		dockerBuild(getPipelineInfo().getPath());

		// Startup pipeline jobs.
		doStartJobsExecute0();

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

	/**
	 * Roll-back
	 *
	 * @throws Exception
	 */
	@Override
	public void rollback() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getPipelineInfo().getProject().getId());

		build(getPipelineInfo().getTaskHistory(), true);
		setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));

		setShaLocal(FileCodec.getFileMD5(new File(getPipelineInfo().getPath() + getPipelineInfo().getProject().getTarPath())));

		// Startup pipeline jobs.
		doStartJobsExecute0();

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

	/**
	 * Docker build
	 */
	public void dockerBuild(String path) throws Exception {
		String command = "mvn -f " + path + "/pom.xml -Pdocker:push dockerfile:build  dockerfile:push -Ddockerfile.username="
				+ config.getTranform().getDockerNative().getDockerPushUsername() + " -Ddockerfile.password="
				+ config.getTranform().getDockerNative().getDockerPushPasswd();
		processManager.exec(command, config.getJobLog(getPipelineInfo().getTaskHistory().getId()), 300000);
	}

	/**
	 * Docker pull
	 */
	public String dockerPull(String targetHost, String userName, String imageName, String rsa) throws Exception {
		String command = "docker pull " + imageName;
		return exceCommand(targetHost, userName, command, rsa);
	}

	/**
	 * Docker stop
	 */
	public String dockerStop(String targetHost, String userName, String groupName, String rsa) throws Exception {
		String command = "docker stop " + groupName;
		return exceCommand(targetHost, userName, command, rsa);
	}

	/**
	 * Docker remove container
	 */
	public String dockerRemoveContainer(String targetHost, String userName, String groupName, String rsa) throws Exception {
		String command = "docker rm " + groupName;
		return exceCommand(targetHost, userName, command, rsa);
	}

	/**
	 * Docker Run
	 */
	public String dockerRun(String targetHost, String userName, String runCommand, String rsa) throws Exception {
		return exceCommand(targetHost, userName, runCommand, rsa);
	}

	@Override
	protected NamedIdJob newPipelineJob(AppInstance instance) {
		// TODO namedId
		return new DockerNativePipelineJob("", config, this, getPipelineInfo().getProject(), instance,
				getPipelineInfo().getTaskHistoryDetails());
	}

}