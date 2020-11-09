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

import com.wl4g.components.core.bean.ci.PipelineHistoryInstance;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.devops.ci.pipeline.provider.Python3PipelineProvider;

import java.util.List;

/**
 * Python3 stdandard transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月25日
 * @since
 */
public class Python3PipeDeployer extends GenericHostPipeDeployer<Python3PipelineProvider> {

	public Python3PipeDeployer(Python3PipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		super(provider, instance, pipelineHistoryInstances);
	}

}