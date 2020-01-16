package com.wl4g.devops.common.bean.doc;

import com.wl4g.devops.common.bean.BaseBean;

public class Label extends BaseBean {
	private static final long serialVersionUID = -7171357316844793042L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}