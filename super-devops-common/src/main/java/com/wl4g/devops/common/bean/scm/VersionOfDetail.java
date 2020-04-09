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
package com.wl4g.devops.common.bean.scm;

import java.util.List;

public class VersionOfDetail extends ConfigVersion {
	private static final long serialVersionUID = 987629446638218612L;

	private String envId; // 环境
	private List<String> nodeIdList;// 节点集合
	private List<VersionContentBean> configGurations;

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public List<String> getNodeIdList() {
		return nodeIdList;
	}

	public void setNodeIdList(List<String> nodeIdList) {
		this.nodeIdList = nodeIdList;
	}

	public List<VersionContentBean> getConfigGurations() {
		return configGurations;
	}

	public void setConfigGurations(List<VersionContentBean> configGurations) {
		this.configGurations = configGurations;
	}
}