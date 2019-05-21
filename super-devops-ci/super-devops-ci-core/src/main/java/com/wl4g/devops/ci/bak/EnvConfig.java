package com.wl4g.devops.ci.bak;

/**
 * @author vjay
 * @date 2019-05-06 19:03:00
 */
public class EnvConfig {

	private String name;
	private String  targetHost;
	private String targetPath;
	private ChildrenSubjectConfig[] child;

	//TODO just build this subject
	private String[] includeChild;

	public EnvConfig(String name, String targetHost, String targetPath, ChildrenSubjectConfig[] child) {
		this.name = name;
		this.targetHost = targetHost;
		this.targetPath = targetPath;
		this.child = child;
		this.includeChild = includeChild;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public ChildrenSubjectConfig[] getChild() {
		return child;
	}

	public void setChild(ChildrenSubjectConfig[] child) {
		this.child = child;
	}

	public String[] getIncludeChild() {
		return includeChild;
	}

	public void setIncludeChild(String[] includeChild) {
		this.includeChild = includeChild;
	}
}
