package com.wl4g.devops.common.bean.scm;

import java.util.ArrayList;
import java.util.List;

public class ConfigSourceBean {

	private ConfigReleaseMeta releaseMeta = new ConfigReleaseMeta();
	private List<VersionContentBean> contents = new ArrayList<>();

	public ConfigReleaseMeta getReleaseMeta() {
		return releaseMeta;
	}

	public void setReleaseMeta(ConfigReleaseMeta releaseMeta) {
		if (releaseMeta != null) {
			this.releaseMeta = releaseMeta;
		}
	}

	public List<VersionContentBean> getContents() {
		return contents;
	}

	public void setContents(List<VersionContentBean> versions) {
		if (versions != null) {
			this.contents = versions;
		}
	}

}
