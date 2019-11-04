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
package com.wl4g.devops.ci.pipeline.job;

import com.wl4g.devops.ci.pipeline.NpmViewPipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.io.File;
import java.util.List;

/**
 * NPM view deployments pipeline handler tasks.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class NpmViewPipeTransferJob extends GenericHostPipeTransferJob<NpmViewPipelineProvider> {

	public NpmViewPipeTransferJob(NpmViewPipelineProvider provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		super(provider, project, instance, taskHistoryDetails);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		// Create replace remote home temporary directory.
		createReplaceRemoteDirectory(remoteHost, user, config.getTranform().getRemoteHomeTmpDir(), sshkey);

		// Transfer to remote temporary directory.
		String localFile = config.getJobBackup(provider.getPipelineInfo().getTaskHistory().getId()) + "/"
				+ provider.getPipelineInfo().getProject().getProjectName() + ".tar.gz";
		transferToRemoteTmpDir(remoteHost, user, sshkey, localFile);

		// Create replace remote appHome directory.
		createReplaceRemoteDirectory(remoteHost, user, provider.getPipelineInfo().getProject().getParentAppHome(), sshkey);

		// Uncompress program.
		decompressRemoteProgram(remoteHost, user, sshkey);

		// Cleanup temporary program file.
		cleanupRemoteTmpProgramFile(remoteHost, user, sshkey, "tar.gz");
	}

	/**
	 * Decompression executable program assets file.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected void decompressRemoteProgram(String remoteHost, String user, String sshkey) throws Exception {
		File remoteTmpFile = config.getTransferRemoteProgramTmpFile(provider.getPipelineInfo().getProject().getProjectName(),
				"tar.gz");
		String command = "tar -zxvf " + remoteTmpFile.getAbsolutePath() + " -C "
				+ provider.getPipelineInfo().getProject().getParentAppHome();
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

}