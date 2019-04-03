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
}
