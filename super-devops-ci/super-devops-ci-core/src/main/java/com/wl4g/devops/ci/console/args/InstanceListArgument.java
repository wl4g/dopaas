package com.wl4g.devops.ci.console.args;

import com.wl4g.devops.shell.annotation.ShellOption;

import java.io.Serializable;

/**
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-21 16:18:00
 */
public class InstanceListArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "a", lopt = "app", help = "Application service name to be deployed", required = false)
	private String appGroupName;

	@ShellOption(opt = "e", lopt = "env", help = "The environment of the application to be deployed", required = false)
	private String envName;

	public String getAppGroupName() {
		return appGroupName;
	}

	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}
}
