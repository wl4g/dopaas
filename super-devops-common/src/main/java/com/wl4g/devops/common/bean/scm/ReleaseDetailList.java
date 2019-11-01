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

/**
 * 列表展示实体
 * 
 * @author sut
 * @date 2018年9月20日
 */
public class ReleaseDetailList extends ReleaseDetail {
	private static final long serialVersionUID = 4996112344663821862L;

	private String clusterId; // 组id
	private String releInstanceId; // 节点id
	private String envId;// 环境id=
	private Integer instanceCount; // 节点（实例）数

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public String getReleInstanceId() {
		return releInstanceId;
	}

	public void setReleInstanceId(String releInstanceId) {
		this.releInstanceId = releInstanceId;
	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public Integer getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(Integer instanceCount) {
		this.instanceCount = instanceCount;
	}
}