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

import static com.wl4g.devops.ci.utils.PipelineUtils.ensureDirectory;
import static org.springframework.util.StringUtils.getFilename;

/**
 * Based physical disk backups pipeline provider.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月12日
 * @since
 */
public abstract class BasedPhysicalBackupPipelineProvider extends GenericDependenciesPipelineProvider {

	public BasedPhysicalBackupPipelineProvider(PipelineContext context) {
		super(context);
	}

	/**
	 * Roll-back backup assets files.
	 * 
	 * @throws Exception
	 */
	protected void rollbackBackupAssets() throws Exception {
		Integer taskHisRefId = getContext().getRefTaskHistory().getId();
		String backupPath = config.getJobBackup(taskHisRefId).getAbsolutePath()
				+ getFilename(getContext().getProject().getAssetsPath());

		String target = getContext().getProjectSourceDir() + getContext().getProject().getAssetsPath();
		String command = "cp -Rf " + backupPath + " " + target;
		processManager.exec(command, config.getJobLog(taskHisRefId), 300000);
	}

	/**
	 * Handling assets backup. The default implements is to copy the asset files
	 * to the local shared disk. </br>
	 * For example, the docker based deployment should be backed up to the
	 * docker server image repository.
	 * 
	 * @throws Exception
	 */
	protected void handleBackupAssets() throws Exception {
		Integer taskHisId = getContext().getTaskHistory().getId();
		String targetPath = getContext().getProjectSourceDir() + "/" + getContext().getProject().getAssetsPath();
		String backupPath = config.getJobBackup(taskHisId).getAbsolutePath() + "/"
				+ getFilename(getContext().getProject().getAssetsPath());

		// Ensure backup directory.
		ensureDirectory(config.getJobBackup(taskHisId).getAbsolutePath());

		String command = "cp -Rf " + targetPath + " " + backupPath;
		processManager.exec(command, config.getJobLog(taskHisId), 300000);
	}

}