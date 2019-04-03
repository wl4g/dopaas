package com.wl4g.devops.common.bean.scm;

/**
 * 列表展示实体
 * 
 * @author sut
 * @Description: TODO
 * @date 2018年9月20日
 */
public class ReleaseDetailList extends ReleaseDetail {
	private String groupId; // 组id
	private String releInstanceId; // 节点id
	private String envId;// 环境id=
	private Integer instanceCount; // 节点（实例）数

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
