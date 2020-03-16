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

import com.wl4g.devops.ci.pipeline.RktNativePipelineProvider;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.erm.AppInstance;

import java.util.List;

/**
 * CoreOS(Red hat) RKT native deployments deployer.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-16
 * @since
 */
public class RktNativePipeDeployer extends GenericHostPipeDeployer<RktNativePipelineProvider> {

	public RktNativePipeDeployer(RktNativePipelineProvider provider, AppInstance instance,
			List<TaskHistoryInstance> taskHistoryInstances) {
		super(provider, instance, taskHistoryInstances);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		throw new UnsupportedOperationException();
	}

}