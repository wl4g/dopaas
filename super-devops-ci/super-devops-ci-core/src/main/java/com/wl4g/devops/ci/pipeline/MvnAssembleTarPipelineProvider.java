/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import com.wl4g.devops.ci.pipeline.handler.MvnAssembleTarPipelineHandler;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.FileCodec;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * MAVEN assemble tar provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class MvnAssembleTarPipelineProvider extends AbstractMavenPipelineProvider {

	public MvnAssembleTarPipelineProvider(PipelineInfo info) {
		super(info);
	}

	/**
	 * Execution build and deploy
	 */
	@Override
	public void execute() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(pipelineInfo.getProject().getId());
		// maven install , include dependency
		build(pipelineInfo.getTaskHistory(), taskResult, false);
		if (!taskResult.isSuccess()) {
			return;
		}
		// get git sha
		setShaGit(GitUtils.getLatestCommitted(getPipelineInfo().getPath()));
		deploy();
	}

	/**
	 * Roll-back
	 */
	@Override
	public void rollback() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getPipelineInfo().getProject().getId());
		// Old file
		String oldFilePath = config.getBackup().getBaseDir() + "/" + getPipelineInfo().getAlias() + "/"
				+ getPipelineInfo().getBranch() + "/" + subPackname(getPipelineInfo().getProject().getTarPath()) + "#"
				+ getPipelineInfo().getTaskHistory().getRefId();
		File oldFile = new File(oldFilePath);
		if (oldFile.exists()) {// Check bakup file isExist , if not -- check out
			// from git
			getBackupLocal(oldFilePath, getPipelineInfo().getPath() + getPipelineInfo().getProject().getTarPath());
			setShaGit(getPipelineInfo().getRefTaskHistory().getShaGit());
		} else {
			// getDependencyService().rollback(getTaskHistory(),
			// getRefTaskHistory(), dependency, getBranch(), taskResult, false);
			build(getPipelineInfo().getTaskHistory(), taskResult, true);
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
		backupLocal(getPipelineInfo().getPath() + getPipelineInfo().getProject().getTarPath(),
				getPipelineInfo().getTaskHistory().getId().toString(), getPipelineInfo().getAlias(),
				getPipelineInfo().getBranch());
		// scp to server
		List<Future<?>> futures = new ArrayList<>();
		for (AppInstance instance : getPipelineInfo().getInstances()) {
			// create deploy task
			Runnable task = new MvnAssembleTarPipelineHandler(this, getPipelineInfo().getProject(), getPipelineInfo().getPath(),
					instance, getPipelineInfo().getProject().getTarPath(), getPipelineInfo().getTaskHistoryDetails());
			Future<?> submit = pipelineTaskRunner.getWorker().submit(task);
			futures.add(submit);
		}

		if (CollectionUtils.isEmpty(futures)) {
			return;
		}
		while (true) {
			boolean isAllDone = true;
			for (Future<?> future : futures) {
				if (!future.isDone()) {
					isAllDone = false;
					break;
				}
			}
			if (isAllDone) {
				break;
			}
			Thread.sleep(500);
		}

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

}