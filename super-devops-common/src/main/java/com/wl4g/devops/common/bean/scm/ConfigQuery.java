package com.wl4g.devops.common.bean.scm;

/***
 * 版本查询条件bean zzh
 */
public class ConfigQuery {
	private int groupId; // 组id
	private int instanceId; // 节点id
	private int envId;// 环境id=

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}
}
