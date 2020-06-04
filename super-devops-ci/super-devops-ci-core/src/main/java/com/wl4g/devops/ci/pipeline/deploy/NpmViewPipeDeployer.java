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

import com.wl4g.devops.ci.pipeline.NpmViewPipelineProvider;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.common.bean.erm.AppInstance;

import java.util.List;

/**
 * NPM view deployments pipeline handler tasks.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class NpmViewPipeDeployer extends GenericHostPipeDeployer<NpmViewPipelineProvider> {

	public NpmViewPipeDeployer(NpmViewPipelineProvider provider, AppInstance instance,
							   List<PipelineHistoryInstance> pipelineHistoryInstances) {
		super(provider, instance, pipelineHistoryInstances);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		super.doRemoteDeploying(remoteHost, user, sshkey);
	}

}