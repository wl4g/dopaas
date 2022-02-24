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
package com.wl4g.dopaas.uci.pipeline.provider;

import com.wl4g.infra.support.cli.command.DestroableCommand;
import com.wl4g.infra.support.cli.command.LocalDestroableCommand;
import com.wl4g.dopaas.uci.core.context.PipelineContext;
import com.wl4g.dopaas.uci.pipeline.deploy.SpringExecutableJarPipeDeployer;
import com.wl4g.dopaas.common.bean.uci.PipelineHistory;
import com.wl4g.dopaas.common.bean.uci.Project;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;

import java.io.File;

import static java.lang.String.format;

/**
 * Pipeline provider for deployment Spring-boot executable jar project.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public class SpringExecutableJarPipelineProvider extends BasedMavenPipelineProvider {

	public SpringExecutableJarPipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	protected void postBuiltModulesDependencies() throws Exception {

		Project project = getContext().getProject();
		String prgramInstallFileName = config.getPrgramInstallFileName(getContext().getAppCluster().getName());
		PipelineHistory pipelineHistory = getContext().getPipelineHistory();
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		File tmpCmdFile = config.getJobTmpCommandFile(pipelineHistory.getId(), project.getId());
		File jobLogFile = config.getJobLog(pipelineHistory.getId());
		String assetsDir = getContext().getPipeline().getAssetsDir();

		String tarCommand = format("cd %s/\nmkdir %s\ncp *.jar %s/\ntar -cvf %s/%s.tar %s", projectDir + assetsDir,
				prgramInstallFileName, prgramInstallFileName, projectDir + assetsDir, prgramInstallFileName,
				prgramInstallFileName);
		log.info(writeBuildLog("view native built, packing the assets file command: %s", tarCommand));

		// Execution command.
		// TODO timeoutMs?
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(pipelineHistory.getId()), tarCommand, tmpCmdFile,
				300000L).setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);

		super.postBuiltModulesDependencies();
	}

	@Override
	protected Runnable newPipeDeployer(AppInstance instance) {
		Object[] args = { this, instance, getContext().getPipelineHistoryInstances() };
		return beanFactory.getBean(SpringExecutableJarPipeDeployer.class, args);
	}

}