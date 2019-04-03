package com.wl4g.devops.common.bean.srm;

import java.util.List;

public class Storage {

	private String key;
	private List<String> value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}
}
