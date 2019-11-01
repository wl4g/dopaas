package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

public class GroupRole extends BaseBean {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer groupId;

	private Integer roleId;

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

}