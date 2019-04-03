package com.wl4g.devops.common.bean.scm;

/**
 * 列表展示实体
 * 
 * @date 2018年9月20日
 */
public class ReleaseHistoryList extends ReleaseHistory {

	private Integer instanceCount; // 节点（实例）数

	public Integer getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(Integer instanceCount) {
		this.instanceCount = instanceCount;
	}
}
