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

import com.wl4g.devops.ci.pipeline.job.MvnAssembleTarPipeTransferJob;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.FileCodec;

import static com.wl4g.devops.ci.utils.PipelineUtils.subPackname;

import java.io.File;

/**
 * MAVEN assemble tar provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class MvnAssembleTarPipelineProvider extends BasedMavenPipelineProvider {

	public MvnAssembleTarPipelineProvider(PipelineInfo info) {
		super(info);
	}

	@Override
	public void execute() throws Exception {
		// maven install , include dependency
		build(pipeInfo.getTaskHistory(), false);
		// get git sha
		setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));
		deploy();
	}

	/**
	 * Roll-back
	 */
	@Override
	public void rollback() throws Exception {
		// Old file
		File backupFile = getBackupFile();
		if (backupFile.exists()) {
			// from git
			rollbackBackupFile();
			setShaGit(getPipelineInfo().getRefTaskHistory().getShaGit());
		} else {
			// getDependencyService().rollback(getTaskHistory(),
			// getRefTaskHistory(), dependency, getBranch(), taskResult, false);
			build(getPipelineInfo().getTaskHistory(), true);
			setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));
		}
		deploy();
	}

	/**
	 * Deploy
	 */
	private void deploy() throws Exception {
		// get local sha
		setShaLocal(FileCodec.getFileMD5(new File(getPipelineInfo().getPath() + getPipelineInfo().getProject().getTarPath())));
		// backup in local
		backupLocal();

		// Startup pipeline jobs.
		doTransferForInstances();

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}

	}

	private File getBackupFile() {
		String oldFilePath = config.getWorkspace() + "/" + getPipelineInfo().getTaskHistory().getRefId() + "/"
				+ subPackname(getPipelineInfo().getProject().getTarPath());
		return new File(oldFilePath);
	}

	@Override
	protected Runnable newTransferJob(AppInstance instance) {
		return new MvnAssembleTarPipeTransferJob(config, this, getPipelineInfo().getProject(), getPipelineInfo().getPath(),
				instance, getPipelineInfo().getProject().getTarPath(), getPipelineInfo().getTaskHistoryDetails());
	}

}