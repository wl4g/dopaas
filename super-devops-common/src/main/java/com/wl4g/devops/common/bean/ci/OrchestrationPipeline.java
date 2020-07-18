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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class OrchestrationPipeline extends BaseBean {

	private static final long serialVersionUID = 6815608076300843748L;

	private Integer orchestrationId;

	private Integer pipelineId;

	private Integer priority;

	public Integer getOrchestrationId() {
		return orchestrationId;
	}

	public void setOrchestrationId(Integer orchestrationId) {
		this.orchestrationId = orchestrationId;
	}

	public Integer getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(Integer pipelineId) {
		this.pipelineId = pipelineId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}