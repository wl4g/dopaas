package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

public class GroupMenu extends BaseBean {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer groupId;

	private Integer menuId;

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

}