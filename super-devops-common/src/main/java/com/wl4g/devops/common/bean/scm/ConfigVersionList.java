package com.wl4g.devops.common.bean.scm;

/**
 * 列表展示实体
 * 
 * @date 2018年9月20日
 */
public class ConfigVersionList extends ConfigVersion {
	private String instanceId; // 节点id
	private String envId;// 环境id=
	private String historyId;// 轨迹id=
	private Integer instanceCount; //
	private Integer type;

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getHistoryId() {
		return historyId;
	}

	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}
}
