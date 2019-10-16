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

import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.CommandUtils;

/**
 * Django standard deployments provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public class VuePipelineProvider extends BasedViewPipelineProvider {

	public VuePipelineProvider(PipelineInfo deployProviderBean) {
		super(deployProviderBean);
	}

	@Override
	public void execute() throws Exception {
		//build
		build();



	}

	@Override
	public void rollback() throws Exception {
		throw new UnsupportedOperationException();
	}


	public void build() throws Exception {
		//npm install
		String path = getPipelineInfo().getPath();
		String logPath = config.getJob().getLogBaseDir(getPipelineInfo().getTaskHistory().getId()) + "/build.log";
		String installCommand = "npm install "+path+" | tee -a " + logPath;
		CommandUtils.exec(installCommand ,null,getTaskResult());

		//npm run build
		String buildCommand = "npm run build | tee -a " + logPath;
		CommandUtils.exec(installCommand ,null,getTaskResult(),path);

	}



}