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

import com.wl4g.devops.ci.pipeline.MvnAssembleTarPipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

import static org.springframework.util.Assert.hasText;

/**
 * MAVEN assemble tar deployments task.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class MvnAssembleTarPipeTransferJob extends GenericHostPipeTransferJob<MvnAssembleTarPipelineProvider> {

	final protected String path;
	final protected String tarPath;

	public MvnAssembleTarPipeTransferJob(MvnAssembleTarPipelineProvider provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails, String tarPath, String path) {
		super(provider, project, instance, taskHistoryDetails);
		hasText(path, "path must not be empty.");
		hasText(tarPath, "tarPath must not be empty.");
		this.path = path;
		this.tarPath = tarPath;
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		String path0 = path + tarPath;

		// Create replace remote home temporary directory.
		createReplaceRemoteDirectory(remoteHost, user, config.getTranform().getRemoteHomeTmpDir(), sshkey);

		// Transfer to remote temporary directory.
		transferToRemoteTmpDir(remoteHost, user, sshkey, path0);

		// Uncompress program.
		decompressRemoteProgram(remoteHost, user, sshkey, path0);

		// UnInstall older remote executable program.
		unInstallOlderRemoteProgram(remoteHost, user, path0, project.getParentAppHome(), sshkey);

		// Install newer executable program.
		installNewerRemoteProgram(remoteHost, user, path0, project.getParentAppHome(), sshkey);
	}

}