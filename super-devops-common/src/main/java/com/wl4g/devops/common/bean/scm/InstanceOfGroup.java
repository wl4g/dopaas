package com.wl4g.devops.common.bean.scm;

import java.util.List;

public class InstanceOfGroup extends AppGroup {

	private List<AppInstance> appInstance;

	private List<Environment> environment;

	private String envId;

	public List<AppInstance> getAppInstance() {
		return appInstance;
	}

	public void setAppInstance(List<AppInstance> appInstance) {
		this.appInstance = appInstance;
	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public List<Environment> getEnvironment() {
		return environment;
	}

	public void setEnvironment(List<Environment> environment) {
		this.environment = environment;
	}
}
