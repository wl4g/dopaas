package com.wl4g.devops.erm.dns.config;

import org.apache.commons.lang3.StringUtils;

public class DnsProperties {

	private String prefix;

	public String getPrefix() {
		if (StringUtils.isNoneBlank(prefix)) {
			return prefix;
		}
		return "";
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
