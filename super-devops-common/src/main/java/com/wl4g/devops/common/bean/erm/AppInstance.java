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
package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

/**
 * 对应：app_instance表
 * 
 * @author sut
 * @date 2018年9月25日
 */
public class AppInstance extends BaseBean {
	private static final long serialVersionUID = 4324569366421220002L;

	private Integer clusterId;

	private Integer versionId;

	private Integer hostId;

	private String envType;

	private Integer enable;

	private String endpoint;

	private String remark;

	private String sshUser;

	private String sshKey;

	private String sshKeyPub;

	//
	// --- Temporary fields. ---
	//

	private String hostname;

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType;
	}

	@Override
	public Integer getEnable() {
		return enable;
	}

	@Override
	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSshUser() {
		return sshUser;
	}

	public void setSshUser(String sshUser) {
		this.sshUser = sshUser;
	}

	public String getSshKey() {
		return sshKey;
	}

	public void setSshKey(String sshKey) {
		this.sshKey = sshKey;
	}

	public String getSshKeyPub() {
		return sshKeyPub;
	}

	public void setSshKeyPub(String sshKeyPub) {
		this.sshKeyPub = sshKeyPub;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String toString() {
		return "AppInstance{" +
				"clusterId=" + clusterId +
				", versionId=" + versionId +
				", hostId=" + hostId +
				", envType='" + envType + '\'' +
				", enable=" + enable +
				", endpoint='" + endpoint + '\'' +
				", remark='" + remark + '\'' +
				", sshUser='" + sshUser + '\'' +
				", sshKey='" + sshKey + '\'' +
				", sshKeyPub='" + sshKeyPub + '\'' +
				", hostname='" + hostname + '\'' +
				'}';
	}
}