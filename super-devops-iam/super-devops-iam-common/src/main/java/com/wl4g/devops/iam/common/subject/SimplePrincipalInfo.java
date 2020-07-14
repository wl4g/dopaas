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

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasText;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.validation.constraints.NotBlank;

/**
 * Simple IAM principal account information.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2018-04-31
 * @since
 */
public class SimplePrincipalInfo implements IamPrincipalInfo {

	private static final long serialVersionUID = -2148910955172545592L;

	/** Authenticate principal ID. */
	@NotBlank
	private String principalId;

	/** Authenticate principal name. */
	@NotBlank
	private String principal;

	/** Authenticate principal DB stored credenticals. */
	private String storedCredentials;

	/** Authenticate principal role codes. */
	private String roles;

	/** Authenticate principal organization. */
	private PrincipalOrganization organization;

	/** Authenticate principal permission. */
	private String permissions;

	/** Authenticate principal attributes. */
	private Attributes attributes;

	public SimplePrincipalInfo() {
		super();
	}

	public SimplePrincipalInfo(@NotBlank IamPrincipalInfo info) {
		this(info.getPrincipalId(), info.getPrincipal(), info.getStoredCredentials(), info.getRoles(), info.getPermissions(),
				info.getOrganization(), info.attributes());
	}

	public SimplePrincipalInfo(@NotBlank String principalId, String principal, String storedCredentials, String roles,
			String permissions, PrincipalOrganization organization) {
		this(principalId, principal, storedCredentials, roles, permissions, organization, null);
	}

	public SimplePrincipalInfo(@NotBlank String principalId, String principal, String storedCredentials, String roles,
			String permissions, PrincipalOrganization organization, Attributes attributes) {
		setPrincipalId(principalId);
		setPrincipal(principal);
		setStoredCredentials(storedCredentials);
		setRoles(roles);
		setPermissions(permissions);
		setOrganization(organization);
		setAttributes(attributes);
	}

	@Override
	public final String getPrincipalId() {
		return principalId;
	}

	@Override
	public String principalId() {
		return isBlank(principalId) ? EMPTY : principalId;
	}

	public final void setPrincipalId(String principalId) {
		hasTextOf(principalId, "principalId");
		this.principalId = principalId;
	}

	public final SimplePrincipalInfo withPrincipalId(String principalId) {
		setPrincipalId(principalId);
		return this;
	}

	@Override
	public final String getPrincipal() {
		return principal;
	}

	@Override
	public String principal() {
		return isBlank(principal) ? EMPTY : principal;
	}

	public final void setPrincipal(String principal) {
		hasTextOf(principal, "principalName");
		this.principal = principal;
	}

	public final SimplePrincipalInfo withPrincipal(String principal) {
		setPrincipal(principal);
		return this;
	}

	@Override
	public final String getStoredCredentials() {
		return storedCredentials;
	}

	@Override
	public String storedCredentials() {
		return isBlank(storedCredentials) ? EMPTY : storedCredentials;
	}

	public final void setStoredCredentials(String storedCredentials) {
		// hasText(storedCredentials, "Authenticate storedCredentials can't
		// empty");
		this.storedCredentials = storedCredentials;
	}

	public final SimplePrincipalInfo withStoredCredentials(String storedCredentials) {
		setStoredCredentials(storedCredentials);
		return this;
	}

	@Override
	public final String getRoles() {
		return roles;
	}

	@Override
	public String roles() {
		return isBlank(roles) ? EMPTY : roles;
	}

	public final void setRoles(String roles) {
		// hasText(roles, "Authenticate roles can't empty");
		this.roles = roles;
	}

	public final SimplePrincipalInfo withRoles(String roles) {
		setRoles(roles);
		return this;
	}

	@Override
	public final PrincipalOrganization getOrganization() {
		return organization;
	}

	@Override
	public PrincipalOrganization organization() {
		return isNull(organization) ? (organization = new PrincipalOrganization()) : organization;
	}

	public void setOrganization(PrincipalOrganization organization) {
		// notNullOf(organization, "organization");
		this.organization = organization;
	}

	public SimplePrincipalInfo withOrganization(PrincipalOrganization organization) {
		setOrganization(organization);
		return this;
	}

	@Override
	public final String getPermissions() {
		return permissions;
	}

	@Override
	public String permissions() {
		return isBlank(permissions) ? EMPTY : permissions;
	}

	public final void setPermissions(String permissions) {
		// hasText(permissions, "Authenticate permissions can't empty");
		this.permissions = permissions;
	}

	public final SimplePrincipalInfo withPermissions(String permissions) {
		setPermissions(permissions);
		return this;
	}

	@Override
	public final Attributes getAttributes() {
		// notNull(attributes, "Principal attributes can't null");
		return attributes;
	}

	@Override
	public final Attributes attributes() {
		return isNull(attributes) ? (attributes = new Attributes()) : attributes;
	}

	/**
	 * Sets principal account attributes.
	 * 
	 * @param attributes
	 * @return
	 */
	public final void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * Sets with principal account attributes.
	 * 
	 * @param attributes
	 * @return
	 */
	public final SimplePrincipalInfo withAttributes(Attributes attributes) {
		setAttributes(attributes);
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
	public final IamPrincipalInfo validate() throws IllegalArgumentException {
		hasText(getPrincipalId(), "Authenticate principalId can't empty");
		hasText(getPrincipal(), "Authenticate principal name can't empty");
		// hasText(getRoles(), "Authenticate roles can't empty");
		// notNull(getOrganization(), "Authenticate organization can't empty");
		// hasText(getPermissions(), "Authenticate permissions can't empty");
		return this;
	}

}