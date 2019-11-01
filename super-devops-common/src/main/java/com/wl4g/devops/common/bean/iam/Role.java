package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

public class Role extends BaseBean {
	private static final long serialVersionUID = 381411777614066880L;

	private String name;;

	private String displayName;

	private Integer type;

	private Integer status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName == null ? null : displayName.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}