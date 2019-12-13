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
package com.wl4g.devops.iam.common.subject;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.shiro.util.Assert.hasText;
import static org.apache.shiro.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

/**
 * Simple IAM principal account information.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2018-04-31
 * @since
 */
public class SimplePrincipalInfo implements IamPrincipalInfo {
	private static final long serialVersionUID = 1L;

	/** Authenticate principal ID. */
	@NotBlank
	private String principalId = EMPTY;

	/** Authenticate principal name. */
	@NotBlank
	private String principal = EMPTY;

	/** Authenticate principal DB stored credenticals. */
	private String storedCredentials = EMPTY;

	/** Authenticate principal role codes. */
	private String roles = EMPTY;

	/** Authenticate principal permission. */
	private String permissions = EMPTY;

	/** Authenticate principal attributes. */
	private Map<String, String> attributes = new HashMap<>();

	public SimplePrincipalInfo() {
		super();
	}

	public SimplePrincipalInfo(@NotBlank IamPrincipalInfo info) {
		this(info.getPrincipalId(), info.getPrincipal(), info.getStoredCredentials(), info.getRoles(), info.getPermissions(),
				info.getAttributes());
	}

	public SimplePrincipalInfo(@NotBlank String principalId, @NotBlank String principal) {
		this(principalId, principal, null, null, null);
	}

	public SimplePrincipalInfo(@NotBlank String principalId, String principal, String storedCredentials, String roles,
			String permissions) {
		this(principalId, principal, storedCredentials, roles, permissions, null);
	}

	public SimplePrincipalInfo(@NotBlank String principalId, String principal, String storedCredentials, String roles,
			String permissions, Map<String, String> attributes) {
		setPrincipalId(principalId);
		setPrincipal(principal);
		setStoredCredentials(storedCredentials);
		setRoles(roles);
		setPermissions(permissions);
		setAttributes(attributes);
	}

	@Override
	public final String getPrincipalId() {
		return principalId;
	}

	public final SimplePrincipalInfo setPrincipalId(String principalId) {
		hasText(principalId, "Authenticate principalId must not be empty.");
		this.principalId = principalId;
		return this;
	}

	@Override
	public final String getPrincipal() {
		return principal;
	}

	public final SimplePrincipalInfo setPrincipal(String principal) {
		hasText(principal, "Authenticate principal name can't empty");
		this.principal = principal;
		return this;
	}

	@Override
	public final String getStoredCredentials() {
		return storedCredentials;
	}

	public final SimplePrincipalInfo setStoredCredentials(String storedCredentials) {
		hasText(storedCredentials, "Authenticate storedCredentials can't empty");
		this.storedCredentials = storedCredentials;
		return this;
	}

	@Override
	public final String getRoles() {
		return roles;
	}

	public final SimplePrincipalInfo setRoles(String roles) {
		// hasText(roles, "Authenticate roles can't empty");
		this.roles = roles;
		return this;
	}

	@Override
	public final String getPermissions() {
		return permissions;
	}

	public final SimplePrincipalInfo setPermissions(String permissions) {
		// hasText(permissions, "Authenticate permissions can't empty");
		this.permissions = permissions;
		return this;
	}

	/**
	 * Principal account attributes.
	 * 
	 * @return
	 */
	@Override
	public final Map<String, String> getAttributes() {
		notNull(attributes, "Principal attributes can't null");
		return attributes;
	}

	public final SimplePrincipalInfo setAttributes(Map<String, String> attributes) {
		if (!isEmpty(attributes)) {
			this.attributes = attributes;
		}
		return this;
	}

	@Override
	public String toString() {
		return "SimplePrincipalInfo [principalId=" + principalId + ", principal=" + principal + ", storedCredentials="
				+ storedCredentials + ", roles=" + roles + ", permissions=" + permissions + ", attributes=" + attributes + "]";
	}

	/**
	 * Validation.
	 */
	@Override
	public final void validate() throws IllegalArgumentException {
		hasText(getPrincipalId(), "Authenticate principalId can't empty");
		hasText(getPrincipal(), "Authenticate principal name can't empty");
		// hasText(getRoles(), "Authenticate roles can't empty");
		// hasText(getPermissions(), "Authenticate permissions can't empty");
	}

}