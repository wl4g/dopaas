/*
 * Copyright 2015 the original author or authors.
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
 * 对应：cf_app_instance表
 * 
 * @author sut
 * @Description: TODO
 * @date 2018年9月25日
 */
public class AppInstance extends BaseBean {

	private Long groupId; // 应用分组ID
	private String host; // 实例节点Host（如：web-node1）
	private String ip; // 主机IP地址
	private int port; // 服务监听端口
	private String envId; // 环境id
	private String opsIds = "1"; // 运维者userIds（逗号分隔）
	private String versionId; // 版本id

	//20190517add
	private String webappsPath;//项目部署路径
	private String serverAccount;//登录账号


	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public String getOpsIds() {
		return opsIds;
	}

	public void setOpsIds(String opsIds) {
		this.opsIds = opsIds;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getWebappsPath() {
		return webappsPath;
	}

	public void setWebappsPath(String webappsPath) {
		this.webappsPath = webappsPath;
	}

	public String getServerAccount() {
		return serverAccount;
	}

	public void setServerAccount(String serverAccount) {
		this.serverAccount = serverAccount;
	}
}