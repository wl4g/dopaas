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

import java.util.List;

import com.wl4g.devops.ci.pipeline.Python3StandardPipelineProvider;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

/**
 * Golang stdandard transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月25日
 * @since
 */
public class GolangStandardPipeDeployer extends GenericHostPipeDeployer<Python3StandardPipelineProvider> {

	public GolangStandardPipeDeployer(Python3StandardPipelineProvider provider, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		super(provider, instance, taskHistoryDetails);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		super.doRemoteDeploying(remoteHost, user, sshkey);
	}

}