package com.wl4g.devops.common.bean.scm;

/**
 * 对应表：cf_release_history
 * 
 * @author zzh
 * @Description: TODO
 * @date 2018年9月26日
 */
public class ReleaseHistory extends BaseBean {

	private int versionid; // 版本号ID
	// private String namespaceid; // 命名空间ID
	private String status; // 发布状态（1:成功/2:失败）
	private Integer type; // （1:成功/2:失败）
	// 关系层级枚举

	public enum type {

		RELEASE(1), ROLLBACK(2);

		private Integer value;

		public Integer getValue() {
			return value;
		}

		type(Integer value) {
			this.value = value;
		}

	}

	public int getVersionid() {
		return versionid;
	}

	public void setVersionid(int versionid) {
		this.versionid = versionid;
	}

	// public String getNamespaceid() {
	// return namespaceid;
	// }
	//
	// public void setNamespaceid(String namespaceid) {
	// this.namespaceid = namespaceid;
	// }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
