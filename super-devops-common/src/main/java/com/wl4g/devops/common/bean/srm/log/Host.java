package com.wl4g.devops.common.bean.srm.log;

public class Host {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Host{" + "name='" + name + '\'' + '}';
	}
}
