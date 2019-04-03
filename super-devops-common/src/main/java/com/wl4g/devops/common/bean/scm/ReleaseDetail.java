package com.wl4g.devops.common.bean.scm;

/**
 * 对应表：cf_release_Detail
 * 
 * @date 2018年9月26日
 */
public class ReleaseDetail extends BaseBean {

	private int releaseId; // 历史轨迹ID
	private int instanceId; // 命名空间ID
	private String description;
	private String result;
	private Integer status; // 发布状态（1:成功/2:失败）

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
