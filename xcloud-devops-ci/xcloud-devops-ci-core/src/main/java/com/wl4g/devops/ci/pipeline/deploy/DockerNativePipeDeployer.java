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
package com.wl4g.devops.ci.pipeline.deploy;

import com.github.dockerjava.api.DockerClient;
import com.wl4g.components.common.cli.ssh2.SSH2Holders;
import com.wl4g.components.common.io.FileIOUtils;
import com.wl4g.devops.ci.pipeline.provider.PipelineProvider;
import com.wl4g.devops.ci.utils.DockerJavaUtil;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppEnvironment;
import com.wl4g.devops.common.bean.erm.AppInstance;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Docker native deployer.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class DockerNativePipeDeployer extends GenericHostPipeDeployer<PipelineProvider> {

	final private static String ymlFileName = "/swarm_config.yml";

	public DockerNativePipeDeployer(PipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		super(provider, instance, pipelineHistoryInstances);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {

		// String dockerMasterAddr =
		// instance.getDockerCluster().getMasterAddr();
		// DockerClient dockerClient =
		// DockerJavaUtil.sampleConnect(dockerMasterAddr);
		String ref = getContext().getPipeStepBuilding().getRef();
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();
		AppCluster appCluster = getContext().getAppCluster();
		AppEnvironment environment = getContext().getEnvironment();

		if (StringUtils.isNotBlank(environment.getConfigContent())) {
			File ymlFile = new File(config.getJobBaseDir(pipelineHistory.getId()).getCanonicalPath() + ymlFileName);
			createServiceWithStack(remoteHost, user, sshkey, ymlFile, environment.getConfigContent(), appCluster.getName());
		} else {
			createServiceWithDockerJavaClient(environment.getDockerRepository().getRegistryAddress() + "/"
					+ environment.getRepositoryNamespace() + "/" + appCluster.getName() + ":" + ref, appCluster.getName());
		}

		log.info("docker stop & pull & restart container");
	}

	// TODO need move to docker client expand
	private void createServiceWithStack(String remoteHost, String user, String sshkey, File ymlFile, String configContent,
			String stackName) throws Exception {
		// write config file to job dir
		FileIOUtils.writeFile(ymlFile, configContent, false);

		// transfer yml file
		transferYmlFileToRemote(ymlFile, remoteHost, user, sshkey);

		// run
		String remoteYmlFileName = config.getDeploy().getRemoteHomeTmpDir() + ymlFileName;
		String cmd = "docker stack deploy -c " + remoteYmlFileName + " " + stackName;
		provider.doRemoteCommand(remoteHost, user, cmd, sshkey);

		// clean temp file
		cleanupRemoteYmlTmpFile(remoteHost, user, sshkey);
	}

	private void createServiceWithDockerJavaClient(String imageName, String name) {
		String dockerMasterAddr = instance.getDockerCluster().getMasterAddr();
		DockerClient dockerClient = DockerJavaUtil.sampleConnect(dockerMasterAddr);
		DockerJavaUtil.removeService(dockerClient, name);
		DockerJavaUtil.pullImage(dockerClient, imageName);
		DockerJavaUtil.createService(dockerClient, imageName, name);
	}

	private void transferYmlFileToRemote(File file, String remoteHost, String user, String sshkey) throws Exception {
		String remoteTmpDir = config.getDeploy().getRemoteHomeTmpDir();
		writeDeployLog(String.format("Transfer to remote tmpdir: %s@%s [%s]", user, remoteHost, file));
		SSH2Holders.getDefault().scpPutFile(remoteHost, user, provider.getUsableCipherSshKey(sshkey), null, file, remoteTmpDir);
	}

	private void cleanupRemoteYmlTmpFile(String remoteHost, String user, String sshkey) throws Exception {
		String remoteYmlFileName = config.getDeploy().getRemoteHomeTmpDir() + ymlFileName;
		if (StringUtils.isNotBlank(remoteYmlFileName) && StringUtils.equals(remoteYmlFileName, "/")) {
			String command = "rm -Rf " + remoteYmlFileName;
			writeDeployLog("Cleanup remote temporary program file: %s@%s [%s]", user, remoteHost, command);
			doRemoteCommand(remoteHost, user, command, sshkey);
		}

	}

}