/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.ci.pipeline.provider.container;

import com.github.dockerjava.api.DockerClient;
import com.wl4g.components.support.cli.command.DestroableCommand;
import com.wl4g.components.support.cli.command.LocalDestroableCommand;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.pipeline.deploy.DockerNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.provider.AbstractPipelineProvider;
import com.wl4g.devops.ci.utils.DockerJavaUtil;
import com.wl4g.devops.common.bean.ci.PipeStageBuilding;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppEnvironment;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.bean.erm.DockerRepository;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Docker native integrate pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-10-25
 */
public class DockerNativePipelineProvider extends AbstractPipelineProvider implements ContainerPipelineProvider {

	/**
	 * Docker app bin name
	 */
	final private static String APP_BIN_NAME = "APP_BIN_NAME";

	/**
	 * Docker run command
	 */
	final private static String RUN_COM = "RUN_COM";

	public DockerNativePipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	public void buildImage() throws Exception {
		DockerClient dockerClient = DockerJavaUtil.sampleConnect(config.getDocker().getMakeImageAddr());// "tcp://10.0.0.161:2375"
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();
		AppCluster appCluster = getContext().getAppCluster();
		PipeStageBuilding pipeStepBuilding = getContext().getPipeStepBuilding();
		AppEnvironment environment = getContext().getEnvironment();
		DockerRepository dockerRepository = environment.getDockerRepository();
		DockerRepository.AuthConfigModel authConfigModel = dockerRepository.getAuthConfigModel();
		File jobLogFile = config.getJobLog(pipelineHistory.getId());
		String installFileName = config.getPrgramInstallFileName(appCluster.getName());
		String tarFileName = config.getTarFileNameWithTar(appCluster.getName());
		File jobBackDir = config.getJobBackupDir(pipelineHistory.getId());
		try {
			Map<String, String> args = new HashMap<>();
			args.put(APP_BIN_NAME, installFileName);
			args.put(RUN_COM, environment.getRunCommand());
			// args.put("APP_PORT", "14040");
			// args.put(MAIN_CLASS, "com.wl4g.devops.IamServer");
			// args.put(ACTIVE, pipeline.getEnvironment());

			Set<String> tags = new HashSet<>();
			String tag;
			if (StringUtils.isNotBlank(pipeStepBuilding.getRef())) {
				tag = dockerRepository.getRegistryAddress() + "/" + environment.getRepositoryNamespace() + "/"
						+ appCluster.getName() + ":" + pipeStepBuilding.getRef();
				tags.add(tag);// 冒号前面为名字，冒号后面为版本，版本为空则为latest
			} else {
				tag = dockerRepository.getRegistryAddress() + "/" + environment.getRepositoryNamespace() + "/"
						+ appCluster.getName();
				tags.add(tag);
			}

			// tar
			DestroableCommand tarCmd = new LocalDestroableCommand(String.format("cd %s\ntar -xvf %s", jobBackDir, tarFileName),
					jobBackDir, 300000L).setStdout(jobLogFile).setStderr(jobLogFile);
			pm.execWaitForComplete(tarCmd);

			String containerId = DockerJavaUtil.buildImage(dockerClient, tags, jobBackDir, args);
			DockerJavaUtil.pushImage(dockerClient, tag, dockerRepository.getRegistryAddress(), authConfigModel.getUsername(),
					authConfigModel.getPassword());

			// remove dir
			if (StringUtils.isNotBlank(installFileName) && !StringUtils.equals(installFileName, "/")) {
				DestroableCommand rmCmd = new LocalDestroableCommand(
						String.format("cd %s\nrm -Rf %s", jobBackDir, installFileName), jobBackDir, 300000L).setStdout(jobLogFile)
								.setStderr(jobLogFile);
				pm.execWaitForComplete(rmCmd);
			}

			log.info("create container success. containerId = {}", containerId);
		} finally {
			dockerClient.close();
		}
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
	protected Runnable newPipeDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getPipelineHistoryInstances() };
		return beanFactory.getBean(DockerNativePipeDeployer.class, args);
	}

}