package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AlarmContactGroup extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}