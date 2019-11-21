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
import com.wl4g.devops.common.bean.share.AppInstance;

/**
 * Docker native integrate pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-10-25
 */
public class DockerNativePipelineProvider extends AbstractPipelineProvider implements ContainerPipelineProvider {

	public DockerNativePipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	public void buildImage(String remoteHost, String user, String sshkey, String projectDir) throws Exception {
		String cmd = "mvn -f " + projectDir + "/pom.xml -Pdocker:push dockerfile:build  dockerfile:push -Ddockerfile.username="
				+ config.getDeploy().getDockerNative().getDockerPushUsername() + " -Ddockerfile.password="
				+ config.getDeploy().getDockerNative().getDockerPushPasswd();
		processManager.execSync(cmd, config.getJobLog(getContext().getTaskHistory().getId()), 300000);
	}

	@Override
	public void imagePull(String remoteHost, String user, String sshkey, String image) throws Exception {
		String command = "docker pull " + image;
		doRemoteCommand(remoteHost, user, command, sshkey);
	}

	@Override
	public void stopContainer(String remoteHost, String user, String sshkey, String container) throws Exception {
		String command = "docker stop " + container;
		doRemoteCommand(remoteHost, user, command, sshkey);
	}

	@Override
	public void destroyContainer(String remoteHost, String user, String sshkey, String container) throws Exception {
		String command = "docker rm " + container;
		doRemoteCommand(remoteHost, user, command, sshkey);
	}

	@Override
	public void startContainer(String remoteHost, String user, String sshkey, String runContainerCommands) throws Exception {
		doRemoteCommand(remoteHost, user, runContainerCommands, sshkey);
	}

	@Override
	protected Runnable newDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getTaskHistoryInstances() };
		return beanFactory.getBean(DockerNativePipeDeployer.class, args);
	}

}