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
package com.wl4g.devops.ci.pipeline.container;

import com.github.dockerjava.api.DockerClient;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.pipeline.AbstractPipelineProvider;
import com.wl4g.devops.ci.pipeline.deploy.DockerNativePipeDeployer;
import com.wl4g.devops.ci.utils.DockerJavaUtil;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppInstance;

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
	 * Docker hub server url. TODO just for now, need move to config file
	 */
	final private static String SERVER_URL = "tcp://localhost:2376";

	/**
	 * Docker app bin name
	 */
	final private static String APP_BIN_NAME = "APP_BIN_NAME";

	final private static String MAIN_CLASS = "MAIN_CLASS";

	final private static String ACTIVE = "ACTIVE";


	public DockerNativePipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	public void buildImage() throws Exception {
		DockerClient dockerClient = DockerJavaUtil.sampleConnect(SERVER_URL);//"tcp://10.0.0.161:2375"
		try {
			AppCluster appCluster = getContext().getAppCluster();
			Pipeline pipeline = getContext().getPipeline();
			PipelineHistory pipelineHistory = getContext().getPipelineHistory();


			Map<String, String> args = new HashMap<>();
			args.put(APP_BIN_NAME, appCluster.getName()+"-master-bin");
			//args.put("APP_PORT", "14040");
			args.put(MAIN_CLASS, "com.wl4g.devops.IamServer");//TODO 启动需要，如何获取
			args.put(ACTIVE, pipeline.getEnvironment());

			Set<String> tags = new HashSet<>();
			tags.add(appCluster.getName());//冒号前面为名字，冒号后面为版本，版本为空则为latest

			String containerId = DockerJavaUtil.buildImage(dockerClient, tags,
					new File(config.getJobBackupDir(pipelineHistory.getId()).getAbsolutePath()),
					new File("./Dockerfile"),
					args);

			log.info("create container success. containerId = {}", containerId);
		}finally {
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