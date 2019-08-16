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
package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

/**
 * 对应：app_instance表
 * 
 * @author sut
 * @Description: TODO
 * @date 2018年9月25日
 */
public class AppInstance extends BaseBean {

	private Long appClusterId; // 应用分组ID
	private Integer hostId;
	private String endpoint; // 服务监听端口
	private String envId; // 环境id
	private String versionId; // 版本id

	// 20190517add
	// private String basePath;// 项目部署路径
	private String sshUser;// 登录账号
	private String sshKey;


	//other
	private String host;


	public Long getAppClusterId() {
		return appClusterId;
	}

	public void setAppClusterId(Long appClusterId) {
		this.appClusterId = appClusterId;
	}


	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}


	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getSshKey() {
		return sshKey;
	}

	public void setSshKey(String sshKey) {
		this.sshKey = sshKey;
	}

	public String getSshUser() {
		return sshUser;
	}

	public void setSshUser(String sshUser) {
		this.sshUser = sshUser;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return "AppInstance{" +
				"appClusterId=" + appClusterId +
				", endpoint=" + endpoint +
				", envId='" + envId + '\'' +
				", versionId='" + versionId + '\'' +
				", sshUser='" + sshUser + '\'' +
				", sshKey='" + sshKey + '\'' +
				'}';
	}
}