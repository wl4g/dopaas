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
import com.wl4g.devops.ci.pipeline.deploy.MvnAssembleTarPipeDeployer;
import com.wl4g.devops.common.bean.share.AppInstance;

import static com.wl4g.devops.ci.utils.PipelineUtils.subPackname;
import static com.wl4g.devops.common.utils.codec.FingerprintCodec.getMd5Fingerprint;

import java.io.File;

/**
 * MAVEN assemble tar provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class MvnAssembleTarPipelineProvider extends BasedMavenPipelineProvider {

	public MvnAssembleTarPipelineProvider(PipelineContext info) {
		super(info);
	}

	@Override
	public void execute() throws Exception {
		// Maven building and include dependencies.
		mvnBuild(getContext().getTaskHistory(), false);

		// Setup Vcs source fingerprint.
		setupSourceFingerprint(vcsOperator.getLatestCommitted(getContext().getProjectSourceDir()));

		// MVN build.
		doMvnBuildInternal();
	}

	/**
	 * Roll-back
	 */
	@Override
	public void rollback() throws Exception {
		// Older file
		File backupFile = getBackupFile();
		if (backupFile.exists()) {
			// Direct using backup file.
			rollbackBackupFile();
			// Setup vcs source fingerprint.
			setupSourceFingerprint(getContext().getRefTaskHistory().getShaGit());
		} else {
			// New building and include dependencies.
			mvnBuild(getContext().getTaskHistory(), true);
			// Setup vcs source fingerprint.
			setupSourceFingerprint(vcsOperator.getLatestCommitted(getContext().getProjectSourceDir()));
		}

		// MVN build.
		doMvnBuildInternal();
	}

	/**
	 * Invoke internal MVN build.
	 */
	private void doMvnBuildInternal() throws Exception {
		// Setup assets file fingerprint.
		File file = new File(getContext().getProjectSourceDir() + getContext().getProject().getAssetsPath());
		setupAssetsFingerprint(getMd5Fingerprint(file));

		// backup in local
		backupLocal();

		// Do transfer to remote jobs.
		doExecuteTransferToRemoteInstances();

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}

	}

	private File getBackupFile() {
		String oldFilePath = config.getWorkspace() + "/" + getContext().getTaskHistory().getRefId() + "/"
				+ subPackname(getContext().getProject().getAssetsPath());
		return new File(oldFilePath);
	}

	@Override
	protected Runnable newDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getTaskHistoryDetails() };
		return beanFactory.getBean(MvnAssembleTarPipeDeployer.class, args);
	}

}