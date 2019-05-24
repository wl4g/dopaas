package com.wl4g.devops.ci.console.args;

import com.wl4g.devops.shell.annotation.ShellOption;

import java.io.Serializable;
import java.util.List;

/**
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-21 15:53:00
 */
public class BuildArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "a", lopt = "app", help = "Application service name to be deployed")
	private String appGroupName;

	@ShellOption(opt = "b", lopt = "branch", help = "Branch name of application source code to deploy")
	private String branchName;

	@ShellOption(opt = "I", lopt = "instances", help = "List of destination instances (hosts) to deploy")
	private List<String> instances;

	public String getAppGroupName() {
		return appGroupName;
	}

	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public List<String> getInstances() {
		return instances;
	}

	public void setInstances(List<String> instances) {
		this.instances = instances;
	}
}
