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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static java.util.Objects.isNull;
import static org.springframework.util.Assert.notNull;

/**
 * CICD project with VCS credentials information.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public class Vcs extends BaseBean {
	private static final long serialVersionUID = 381411777614066880L;

	@NotBlank
	private String name;

	@NotNull
	private String providerKind;

	@NotNull
	private Integer authType;

	@NotBlank
	private String baseUri;

	private String sshKeyPub;

	private String sshKey;

	private String accessToken;

	private String username;

	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProviderKind() {
		return providerKind;
	}

	public void setProviderKind(String providerKind) {
		this.providerKind = providerKind;
	}

	public Integer getAuthType() {
		return authType;
	}

	public void setAuthType(Integer authType) {
		this.authType = authType;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri == null ? null : baseUri.trim();
	}

	public String getSshKeyPub() {
		return sshKeyPub;
	}

	public void setSshKeyPub(String sshKeyPub) {
		this.sshKeyPub = sshKeyPub == null ? null : sshKeyPub.trim();
	}

	public String getSshKey() {
		return sshKey;
	}

	public void setSshKey(String sshKey) {
		this.sshKey = sshKey == null ? null : sshKey.trim();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken == null ? null : accessToken.trim();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username == null ? null : username.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	@Override
	public String toString() {
		return "Vcs [name=" + name + ", provider=" + providerKind + ", authType=" + authType + ", baseUri=" + baseUri
				+ ", sshKeyPub=" + sshKeyPub + ", sshKey=" + sshKey + ", accessToken=" + accessToken + ", username=" + username
				+ ", password=" + password + "]";
	}

	/**
	 * VCS authentication type definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	public static enum VcsAuthType {

		/**
		 * VCS auth type for username-password.
		 */
		AUTH_PASSWD(1),

		/**
		 * VCS auth type for ssh-key.
		 */
		AUTH_SSH(2);

		final private int value;

		private VcsAuthType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link VcsAuthType}
		 * 
		 * @param vcsAuthType
		 * @return
		 */
		final public static VcsAuthType safeOf(Integer vcsAuthType) {
			if (isNull(vcsAuthType)) {
				return null;
			}
			for (VcsAuthType t : values()) {
				if (vcsAuthType.intValue() == t.getValue()) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Converter string to {@link VcsAuthType}
		 * 
		 * @param vcsAuthType
		 * @return
		 */
		final public static VcsAuthType of(Integer vcsAuthType) {
			VcsAuthType type = safeOf(vcsAuthType);
			notNull(type, String.format("Unsupported VCS auth type for %s", vcsAuthType));
			return type;
		}

	}

}