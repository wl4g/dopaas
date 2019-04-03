package com.wl4g.devops.common.bean.srm.log;

public class Beat {
	private String hostname;
	private String version;
	private String name;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Beat{" + "hostname='" + hostname + '\'' + ", version='" + version + '\'' + ", name='" + name + '\'' + '}';
	}

}