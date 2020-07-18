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

public class PipeStepDeploy extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer pipeId;

	private Integer deployType = 1;

	private String deployDockerfileContent;

	private Integer deployConfigType;

	private String deployConfigContent;

	public Integer getPipeId() {
		return pipeId;
	}

	public void setPipeId(Integer pipeId) {
		this.pipeId = pipeId;
	}

	public Integer getDeployType() {
		return deployType;
	}

	public void setDeployType(Integer deployType) {
		this.deployType = deployType;
	}

	public String getDeployDockerfileContent() {
		return deployDockerfileContent;
	}

	public void setDeployDockerfileContent(String deployDockerfileContent) {
		this.deployDockerfileContent = deployDockerfileContent == null ? null : deployDockerfileContent.trim();
	}

	public Integer getDeployConfigType() {
		return deployConfigType;
	}

	public void setDeployConfigType(Integer deployConfigType) {
		this.deployConfigType = deployConfigType;
	}

	public String getDeployConfigContent() {
		return deployConfigContent;
	}

	public void setDeployConfigContent(String deployConfigContent) {
		this.deployConfigContent = deployConfigContent == null ? null : deployConfigContent.trim();
	}
}