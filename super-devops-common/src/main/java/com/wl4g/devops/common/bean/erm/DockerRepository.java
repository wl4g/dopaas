/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class DockerRepository extends BaseBean {

	private static final long serialVersionUID = -7546448616357790576L;

	private String name;

	private String registryAddress;

	private String authConfig;

	// ===expand

	private AuthConfigModel authConfigModel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getRegistryAddress() {
		return registryAddress;
	}

	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress == null ? null : registryAddress.trim();
	}

	public String getAuthConfig() {
		if (StringUtils.isBlank(authConfig) && Objects.nonNull(authConfigModel)) {
			authConfig = JacksonUtils.toJSONString(authConfigModel);
		}
		return authConfig;
	}

	public void setAuthConfig(String authConfig) {
		this.authConfig = authConfig == null ? null : authConfig.trim();
	}

	public AuthConfigModel getAuthConfigModel() {
		if (Objects.isNull(authConfigModel) && StringUtils.isNotBlank(authConfig)) {
			authConfigModel = JacksonUtils.parseJSON(authConfig, AuthConfigModel.class);
		}
		return authConfigModel;
	}

	public void setAuthConfigModel(AuthConfigModel authConfigModel) {
		this.authConfigModel = authConfigModel;
	}

	public static class AuthConfigModel {

		private String username;

		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

}