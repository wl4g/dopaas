package com.wl4g.devops.iam.sns.wechat.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class WxUserInfo extends WxBasedUserInfo {
	private static final long serialVersionUID = 843944424065492261L;

	@JsonProperty("privilege")
	private List<String> privilege = new ArrayList<>();

	public List<String> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List<String> privilege) {
		this.privilege = privilege;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxUserInfo build(String message) {
		return JacksonUtils.parseJSON(message, WxUserInfo.class);
	}

}
