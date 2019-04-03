package com.wl4g.devops.common.bean.scm;

import java.util.List;

public class VersionOfDetail extends ConfigVersion {
	private int envId; // 环境
	private List<String> nodeIdList;// 节点集合
	private List<VersionContentBean> configGurations;

	public int getEnvId() {
		return envId;
	}

	public void setEnvId(int envId) {
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
