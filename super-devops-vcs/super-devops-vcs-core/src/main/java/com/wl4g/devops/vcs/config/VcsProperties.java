package com.wl4g.devops.vcs.config;

/**
 * @author vjay
 * @date 2020-04-21 10:32:00
 */
public class VcsProperties {

	private String branchFormat;

	private String tagFormat;

	public String getBranchFormat() {
		return branchFormat;
	}

	public void setBranchFormat(String branchFormat) {
		this.branchFormat = branchFormat;
	}

	public String getTagFormat() {
		return tagFormat;
	}

	public void setTagFormat(String tagFormat) {
		this.tagFormat = tagFormat;
	}
}
