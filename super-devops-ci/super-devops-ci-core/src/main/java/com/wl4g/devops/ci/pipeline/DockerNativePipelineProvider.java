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
import com.wl4g.devops.ci.pipeline.deploy.DockerNativePipeDeployer;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.FingerprintCodec;

import java.io.File;

/**
 * Docker native integrate pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class DockerNativePipelineProvider extends BasedMavenPipelineProvider {

	public DockerNativePipelineProvider(PipelineContext deployProviderBean) {
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
		dependency.setProjectId(getContext().getProject().getId());
		mvnBuild(getContext().getTaskHistory(), false);

		// get sha and md5
		setupSourceFingerprint(GitUtils.getLatestCommitted(getContext().getProjectSourceDir()));

		// docker build
		dockerBuild(getContext().getProjectSourceDir());

		// Startup pipeline jobs.
		doExecuteTransferToRemoteInstances();

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
		dependency.setProjectId(getContext().getProject().getId());

		mvnBuild(getContext().getTaskHistory(), true);
		setupSourceFingerprint(GitUtils.getLatestCommitted(getContext().getProjectSourceDir()));

		setupAssetsFingerprint(FingerprintCodec.getMd5Fingerprint(new File(getContext().getProjectSourceDir() + getContext().getProject().getAssetsPath())));

		// Startup pipeline jobs.
		doExecuteTransferToRemoteInstances();

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

	/**
	 * Docker build
	 */
	public void dockerBuild(String path) throws Exception {
		String command = "mvn -f " + path + "/pom.xml -Pdocker:push dockerfile:build  dockerfile:push -Ddockerfile.username="
				+ config.getDeploy().getDockerNative().getDockerPushUsername() + " -Ddockerfile.password="
				+ config.getDeploy().getDockerNative().getDockerPushPasswd();
		processManager.exec(command, config.getJobLog(getContext().getTaskHistory().getId()), 300000);
	}

	/**
	 * Docker pull
	 */
	public void dockerPull(String remoteHost, String user, String imageName, String rsa) throws Exception {
		String command = "docker pull " + imageName;
		doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * Docker stop
	 */
	public void dockerStop(String remoteHost, String user, String groupName, String rsa) throws Exception {
		String command = "docker stop " + groupName;
		doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * Docker remove container
	 */
	public void dockerRemoveContainer(String remoteHost, String user, String groupName, String rsa) throws Exception {
		String command = "docker rm " + groupName;
		doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * Docker Run
	 */
	public void dockerRun(String remoteHost, String user, String runCommand, String rsa) throws Exception {
		doRemoteCommand(remoteHost, user, runCommand, rsa);
	}

	@Override
	protected Runnable newDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getTaskHistoryDetails() };
		return beanFactory.getBean(DockerNativePipeDeployer.class, args);
	}

}