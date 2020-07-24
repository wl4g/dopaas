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
package com.wl4g.devops.common.utils.bean;

import com.wl4g.devops.common.bean.iam.SocialAuthorizeInfo;
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.validation.constraints.NotBlank;

/**
 * Simple IAM principal account information.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2018-04-31
 * @since
 */
public class MyUserPrincipal implements Serializable {
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

	public MyUserPrincipal() {
		super();
	}

	public MyUserPrincipal(@NotBlank MyUserPrincipal info) {
		this(info.getPrincipalId(), info.getPrincipal(), info.getStoredCredentials(), info.getRoles(), info.getPermissions(),
				info.getOrganization(), info.attributes());
	}

	public MyUserPrincipal(@NotBlank String principalId, String principal, String storedCredentials, String roles,
			String permissions, PrincipalOrganization organization) {
		this(principalId, principal, storedCredentials, roles, permissions, organization, null);
	}

	public MyUserPrincipal(@NotBlank String principalId, String principal, String storedCredentials, String roles,
			String permissions, PrincipalOrganization organization, Attributes attributes) {
		setPrincipalId(principalId);
		setPrincipal(principal);
		setStoredCredentials(storedCredentials);
		setRoles(roles);
		setPermissions(permissions);
		setOrganization(organization);
		setAttributes(attributes);
	}

	public final String getPrincipalId() {
		return principalId;
	}

	public String principalId() {
		return isBlank(principalId) ? EMPTY : principalId;
	}

	public final void setPrincipalId(String principalId) {
		hasTextOf(principalId, "principalId");
		this.principalId = principalId;
	}

	public final MyUserPrincipal withPrincipalId(String principalId) {
		setPrincipalId(principalId);
		return this;
	}

	public final String getPrincipal() {
		return principal;
	}

	public String principal() {
		return isBlank(principal) ? EMPTY : principal;
	}

	public final void setPrincipal(String principal) {
		hasTextOf(principal, "principalName");
		this.principal = principal;
	}

	public final MyUserPrincipal withPrincipal(String principal) {
		setPrincipal(principal);
		return this;
	}

	public final String getStoredCredentials() {
		return storedCredentials;
	}

	public String storedCredentials() {
		return isBlank(storedCredentials) ? EMPTY : storedCredentials;
	}

	public final void setStoredCredentials(String storedCredentials) {
		// hasText(storedCredentials, "Authenticate storedCredentials can't
		// empty");
		this.storedCredentials = storedCredentials;
	}

	public final MyUserPrincipal withStoredCredentials(String storedCredentials) {
		setStoredCredentials(storedCredentials);
		return this;
	}

	public final String getRoles() {
		return roles;
	}

	public String roles() {
		return isBlank(roles) ? EMPTY : roles;
	}

	public final void setRoles(String roles) {
		// hasText(roles, "Authenticate roles can't empty");
		this.roles = roles;
	}

	public final MyUserPrincipal withRoles(String roles) {
		setRoles(roles);
		return this;
	}

	public final PrincipalOrganization getOrganization() {
		return organization;
	}

	public PrincipalOrganization organization() {
		return isNull(organization) ? (organization = new PrincipalOrganization()) : organization;
	}

	public void setOrganization(PrincipalOrganization organization) {
		// notNullOf(organization, "organization");
		this.organization = organization;
	}

	public MyUserPrincipal withOrganization(PrincipalOrganization organization) {
		setOrganization(organization);
		return this;
	}

	public final String getPermissions() {
		return permissions;
	}

	public String permissions() {
		return isBlank(permissions) ? EMPTY : permissions;
	}

	public final void setPermissions(String permissions) {
		// hasText(permissions, "Authenticate permissions can't empty");
		this.permissions = permissions;
	}

	public final MyUserPrincipal withPermissions(String permissions) {
		setPermissions(permissions);
		return this;
	}

	public final Attributes getAttributes() {
		// notNull(attributes, "Principal attributes can't null");
		return attributes;
	}

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
	public final MyUserPrincipal withAttributes(Attributes attributes) {
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
	public final MyUserPrincipal validate() throws IllegalArgumentException {
		hasText(getPrincipalId(), "Authenticate principalId can't empty");
		hasText(getPrincipal(), "Authenticate principal name can't empty");
		// hasText(getRoles(), "Authenticate roles can't empty");
		// notNull(getOrganization(), "Authenticate organization can't empty");
		// hasText(getPermissions(), "Authenticate permissions can't empty");
		return this;
	}

	// --- Authenticating parameter's. ---

	/**
	 * {@link MyUserPrincipal} attributes wrapper.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年7月7日 v1.0.0
	 * @see
	 */
	public static class Attributes extends LinkedHashMap<String, String> {
		final private static long serialVersionUID = 3252340509258189795L;

		/**
		 * Gets session locale language
		 * 
		 * @return
		 */
		public String getSessionLang() {
			return get("KEY_LANG_NAME");
		}

		/**
		 * Sets session locale language
		 * 
		 * @return
		 */
		public Attributes setSessionLang(String lang) {
			if (!isBlank(lang)) {
				put("KEY_LANG_NAME", lang);
			}
			return this;
		}

		/**
		 * Gets authenticating client remote host
		 * 
		 * @return
		 */
		public String getClientHost() {
			return get("KEY_AUTHC_HOST_NAME");
		}

		/**
		 * Sets authenticating client remote host
		 * 
		 * @return
		 */
		public Attributes setClientHost(String clientHost) {
			if (!isBlank(clientHost)) {
				put("KEY_AUTHC_HOST_NAME", clientHost);
			}
			return this;
		}

		/**
		 * Gets authentication parent session id.
		 * 
		 * @return
		 */
		public String getParentSessionId() {
			return get("KEY_PARENT_SESSIONID_NAME");
		}

		/**
		 * Sets authentication parent session id.
		 * 
		 * @return
		 */
		public Attributes setParentSessionId(String parentSessionId) {
			if (!isBlank(parentSessionId)) {
				put("KEY_PARENT_SESSIONID_NAME", parentSessionId);
			}
			return this;
		}

		/**
		 * Gets authentication data cipher.
		 * 
		 * @return
		 */
		public String getDataCipher() {
			return get("KEY_DATA_CIPHER_NAME");
		}

		/**
		 * Sets authentication data cipher.
		 * 
		 * @return
		 */
		public Attributes setDataCipher(String dataCipher) {
			if (!isBlank(dataCipher)) {
				put("KEY_DATA_CIPHER_NAME", dataCipher);
			}
			return this;
		}

		/**
		 * Gets authentication access_token signature.
		 * 
		 * @return
		 */
		public String getAccessTokenSign() {
			return get("KEY_ACCESSTOKEN_SIGN_NAME");
		}

		/**
		 * Sets authentication access_token signature.
		 * 
		 * @return
		 */
		public Attributes setAccessTokenSign(String accessTokenSign) {
			if (!isBlank(accessTokenSign)) {
				put("KEY_ACCESSTOKEN_SIGN_NAME", accessTokenSign);
			}
			return this;
		}

		/**
		 * Gets authentication rememberMe.
		 * 
		 * @return
		 */
		public boolean getRememberMe() {
			return parseBoolean(valueOf(getOrDefault("KEY_REMEMBERME_NAME", FALSE.toString())));
		}

		/**
		 * Sets authentication rememberMe.
		 * 
		 * @return
		 */
		public Attributes setRememberMe(String rememberMe) {
			if (!isBlank(rememberMe)) {
				put("KEY_REMEMBERME_NAME", rememberMe);
			}
			return this;
		}

		/**
		 * Gets sns {@link SocialAuthorizeInfo}
		 * 
		 * @return
		 */
		public SocialAuthorizeInfo getSocialAuthorizeInfo() {
			String snsAuthzInfoJson = get("KEY_SNS_AUTHORIZED_INFO");
			return parseJSON(snsAuthzInfoJson, SocialAuthorizeInfo.class);
		}

		/**
		 * Sets sns {@link SocialAuthorizeInfo}
		 * 
		 * @return
		 */
		public Attributes setSocialAuthorizeInfo(SocialAuthorizeInfo info) {
			if (!isNull(info)) {
				put("KEY_SNS_AUTHORIZED_INFO", toJSONString(info));
			}
			return this;
		}

		/**
		 * NoOp {@link Attributes}
		 */
		final public static Attributes NOOP = new Attributes() {
			private static final long serialVersionUID = 6965759508221042024L;

			@Override
			public String getSessionLang() {
				return null;
			}

			@Override
			public Attributes setSessionLang(String lang) {
				return null;
			}

			@Override
			public String getClientHost() {
				return null;
			}

			@Override
			public Attributes setClientHost(String clientHost) {
				return null;
			}

			@Override
			public String getParentSessionId() {
				return null;
			}

			@Override
			public Attributes setParentSessionId(String parentSessionId) {
				return null;
			}

			@Override
			public String getDataCipher() {
				return null;
			}

			@Override
			public Attributes setDataCipher(String dataCipher) {
				return null;
			}

			@Override
			public String getAccessTokenSign() {
				return null;
			}

			@Override
			public Attributes setAccessTokenSign(String accessTokenSign) {
				return null;
			}

			@Override
			public boolean getRememberMe() {
				return false;
			}

			@Override
			public Attributes setRememberMe(String rememberMe) {
				return null;
			}

			@Override
			public SocialAuthorizeInfo getSocialAuthorizeInfo() {
				return null;
			}

			@Override
			public Attributes setSocialAuthorizeInfo(SocialAuthorizeInfo info) {
				return null;
			}

			@Override
			public boolean containsValue(Object value) {
				return false;
			}

			@Override
			public String get(Object key) {
				return null;
			}

			@Override
			public String getOrDefault(Object key, String defaultValue) {
				return null;
			}

			@Override
			public void clear() {
			}

			@Override
			protected boolean removeEldestEntry(Entry<String, String> eldest) {
				return false;
			}

			@Override
			public Set<String> keySet() {
				return null;
			}

			@Override
			public Collection<String> values() {
				return null;
			}

			@Override
			public Set<Entry<String, String>> entrySet() {
				return null;
			}

			@Override
			public void forEach(BiConsumer<? super String, ? super String> action) {
			}

			@Override
			public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean containsKey(Object key) {
				return false;
			}

			@Override
			public String put(String key, String value) {
				return null;
			}

			@Override
			public void putAll(Map<? extends String, ? extends String> m) {
			}

			@Override
			public String remove(Object key) {
				return null;
			}

			@Override
			public String putIfAbsent(String key, String value) {
				return null;
			}

			@Override
			public boolean remove(Object key, Object value) {
				return false;
			}

			@Override
			public boolean replace(String key, String oldValue, String newValue) {
				return false;
			}

			@Override
			public String replace(String key, String value) {
				return null;
			}

			@Override
			public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
				return null;
			}

			@Override
			public String computeIfPresent(String key,
					BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
				return null;
			}

			@Override
			public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
				return null;
			}

			@Override
			public String merge(String key, String value,
					BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
				return null;
			}

			@Override
			public Object clone() {
				return this;
			}

		};

	}

	/**
	 * Parameters for obtaining account information
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月8日
	 * @since
	 */
	public static interface Parameter extends Serializable {

	}

	/**
	 * SNS parameter definition
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月8日
	 * @since
	 */
	public static interface SnsParameter extends Parameter {

		/**
		 * If the provider is not empty, it means social network login. Provider
		 * is optional: qq/wechat/sina/google/github/twitter/facebook/dingtalk,
		 * etc.
		 * 
		 * @return
		 */
		String getProvider();

		/**
		 * Social networking services openId
		 * 
		 * @return
		 */
		String getOpenId();

		/**
		 * Social networking services unionId(optional)
		 * 
		 * @return
		 */
		String getUnionId();

	}

	/**
	 * Abstract base parameters definition
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月8日
	 * @since
	 */
	public static abstract class BaseParameter implements Parameter {
		private static final long serialVersionUID = -898874009263858359L;

		final private String principal;

		public BaseParameter(String principal) {
			hasText(principal, "'principal' must not be empty");
			this.principal = principal;
		}

		public String getPrincipal() {
			return principal;
		}

	}

	/**
	 * UsernamePassword sign-in parameter definition
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月8日
	 * @since
	 */
	public static class SimpleParameter extends BaseParameter {
		private static final long serialVersionUID = -7501007252263127579L;

		public SimpleParameter(String principal) {
			super(principal);
		}

	}

	/**
	 * SMS dynamic password sign-in parameter definition
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月8日
	 * @since
	 */
	public static class SmsParameter extends BaseParameter {
		private static final long serialVersionUID = -7501007252263557579L;

		public SmsParameter(String principal) {
			super(principal);
		}

	}

	/**
	 * SNS authorizing parameter definition
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月8日
	 * @since
	 */
	public static class SnsAuthorizingParameter implements SnsParameter {
		private static final long serialVersionUID = -898874019263858359L;

		/**
		 * If the provider is not empty, it means social network login. Provider
		 * is optional: qq/wechat/sina/google/github/twitter/facebook/dingtalk,
		 * etc.
		 */
		final private String provider;

		/**
		 * Social networking services openId
		 */
		final private String openId;

		/**
		 * Social networking services unionId
		 */
		final private String unionId;

		/**
		 * SnsAuthorizingParameter construction
		 * 
		 * @param provider
		 * @param openId
		 * @param unionId
		 *            May be empty, Unionid is possible only when WeChat or
		 *            Facebook public platforms
		 */
		public SnsAuthorizingParameter(String provider, String openId, String unionId) {
			notNull(provider, "'provider' must not be null");
			notNull(openId, "'openId' must not be null");
			this.provider = provider;
			this.openId = openId;
			this.unionId = unionId;
		}

		@Override
		public String getProvider() {
			return provider;
		}

		@Override
		public String getOpenId() {
			return openId;
		}

		@Override
		public String getUnionId() {
			return unionId;
		}

	}

	/**
	 * Principal organization tree info.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年5月18日 v1.0.0
	 * @see
	 */
	public static class PrincipalOrganization implements Serializable {
		private static final long serialVersionUID = -8256334665161483288L;

		/**
		 * Principal organization identification. Organization structure, unique
		 * ID, Uneditable.
		 */
		private List<OrganizationInfo> organizations = new ArrayList<>(4);

		public PrincipalOrganization() {
			super();
		}

		public PrincipalOrganization(List<OrganizationInfo> organizations) {
			setOrganizations(organizations);
		}

		public List<OrganizationInfo> getOrganizations() {
			return organizations;
		}

		public List<OrganizationInfo> organizations() {
			return organizations;
		}

		public void setOrganizations(List<OrganizationInfo> organizations) {
			// if (!isEmpty(organizations)) {
			// this.organizations.addAll(organizations);
			// }
			this.organizations = organizations;
		}

		public PrincipalOrganization withOrganizations(List<OrganizationInfo> organizations) {
			setOrganizations(organizations);
			return this;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " => " + toJSONString(this);
		}

		/**
		 * NoOp {@link PrincipalOrganization}
		 */
		final public static PrincipalOrganization NOOP = new PrincipalOrganization() {

			private static final long serialVersionUID = 6965751108221042024L;

			@Override
			public List<OrganizationInfo> getOrganizations() {
				return null;
			}

			@Override
			public List<OrganizationInfo> organizations() {
				return null;
			}

			@Override
			public void setOrganizations(List<OrganizationInfo> organizations) {
			}

			@Override
			public PrincipalOrganization withOrganizations(List<OrganizationInfo> organizations) {
				return null;
			}

		};

	}

	/**
	 * Organization info.
	 *
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年5月18日 v1.0.0
	 * @see
	 */
	public static class OrganizationInfo implements Serializable {
		private static final long serialVersionUID = -8256333665161483288L;

		/**
		 * Organization Unique identification
		 */
		private String organizationCode;

		/**
		 * Parent organization code
		 */
		private String parent;

		/**
		 * Type
		 */
		private Integer type;

		/**
		 * Name
		 */
		private String name;

		private Integer areaId;

		public OrganizationInfo() {
			super();
		}

		public OrganizationInfo(String organizationCode, String parent, Integer type, String name, Integer areaId) {
			this.organizationCode = organizationCode;
			this.parent = parent;
			this.type = type;
			this.name = name;
			this.areaId = areaId;
		}

		public String getOrganizationCode() {
			return organizationCode;
		}

		public String organizationCode() {
			return isBlank(organizationCode) ? EMPTY : organizationCode;
		}

		public void setOrganizationCode(String organizationCode) {
			// hasTextOf(organizationCode, "organizationCode");
			this.organizationCode = organizationCode;
		}

		public OrganizationInfo withOrganizationCode(String organizationCode) {
			// hasTextOf(organizationCode, "organizationCode");
			this.organizationCode = organizationCode;
			return this;
		}

		public String getParent() {
			return parent;
		}

		public String parent() {
			return isBlank(parent) ? EMPTY : parent;
		}

		public void setParent(String parent) {
			// hasTextOf(parent, "parent");
			this.parent = parent;
		}

		public OrganizationInfo withParent(String parent) {
			setParent(parent);
			return this;
		}

		public Integer getType() {
			return type;
		}

		public Integer type() {
			return isNull(type) ? -1 : type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public OrganizationInfo withType(Integer type) {
			setType(type);
			return this;
		}

		public String getName() {
			return name;
		}

		public String name() {
			return isBlank(name) ? EMPTY : name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public OrganizationInfo withName(String name) {
			setName(name);
			return this;
		}

		public Integer getAreaId() {
			return areaId;
		}

		public Integer areaId() {
			return isNull(areaId) ? -1 : areaId;
		}

		public void setAreaId(Integer areaId) {
			this.areaId = areaId;
		}

		public OrganizationInfo withAreaId(Integer areaId) {
			this.areaId = areaId;
			return this;
		}

		/**
		 * NoOp {@link OrganizationInfo}
		 */
		final public static OrganizationInfo NOOP = new OrganizationInfo() {
			private static final long serialVersionUID = 6965751111221042024L;

		};

	}

}