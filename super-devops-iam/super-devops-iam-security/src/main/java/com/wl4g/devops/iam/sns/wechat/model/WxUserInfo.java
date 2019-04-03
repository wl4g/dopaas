/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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