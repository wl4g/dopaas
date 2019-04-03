package com.wl4g.devops.common.bean.scm;

import java.util.List;

public class Environment extends BaseBean {

	private String name;

	private String groupId;

	private List<AppInstance> appInstance;

	public List<AppInstance> getAppInstance() {
		return appInstance;
	}

	public void setAppInstance(List<AppInstance> appInstance) {
		this.appInstance = appInstance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
