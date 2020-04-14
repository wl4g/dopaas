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
package com.wl4g.devops.iam.common.session;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notEmptyOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

/**
 * IAM authentication grant ticket information.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月12日
 * @since
 */
public class GrantCredentialsInfo implements Serializable {
	private static final long serialVersionUID = -3499216861786196071L;

	/**
	 * Grant credentials applications info.
	 */
	private Map<String, GrantApp> applications = new HashMap<>();

	public GrantCredentialsInfo() {
	}

	public GrantCredentialsInfo(Map<String, GrantApp> applications) {
		setApplications(applications);
	}

	/**
	 * Gets grant credentials applications info.
	 * 
	 * @return
	 */
	public Map<String, GrantApp> getApplications() {
		return applications;
	}

	/**
	 * Sets grant credentials applications info.
	 * 
	 * @param applications
	 * @return
	 */
	public GrantCredentialsInfo setApplications(Map<String, GrantApp> applications) {
		notEmptyOf(applications, "applications");
		this.applications.putAll(applications);
		return this;
	}

	/**
	 * Adds grant credentials {@link GrantApp}.
	 * 
	 * @param grantAppname
	 * @param grant
	 * @return
	 */
	public GrantCredentialsInfo addApplications(String grantAppname, GrantApp grant) {
		hasTextOf(grantAppname, "grantAppname");
		notNullOf(grant, "grantAppInfo");
		this.applications.put(grantAppname, grant);
		return this;
	}

	/**
	 * Check whether there are authorized certification ticket by grantAppname.
	 * 
	 * @param grantAppname
	 * @return
	 */
	public boolean hasApplication(String grantAppname) {
		return getApplications().containsKey(grantAppname);
	}

	/**
	 * Check whether there are authorized certification ticket.
	 * 
	 * @return
	 */
	public boolean hasApplications() {
		return !isEmpty(getApplications());
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	/**
	 * Grant ticket of application info
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年4月14日
	 * @since
	 */
	public static class GrantApp implements Serializable {

		private static final long serialVersionUID = 5275955249812704743L;

		/**
		 * Grant credentials temporary ticket.
		 */
		@NotBlank
		private String grantTicket;

		/**
		 * Grant generated child accessTokenSign key of application.
		 */
		// @NotBlank
		private String accessTokenSignKey;

		/**
		 * Grant generated dataCipher of application.
		 */
		// @NotBlank
		private String dataCipher;

		public GrantApp() {
			super();
		}

		public GrantApp(@NotBlank String grantTicket) {
			setGrantTicket(grantTicket);
		}

		public String getGrantTicket() {
			return grantTicket;
		}

		public GrantApp setGrantTicket(String grantTicket) {
			hasTextOf(grantTicket, "grantTicket");
			this.grantTicket = grantTicket;
			return this;
		}

		public String getAccessTokenSignKey() {
			return accessTokenSignKey;
		}

		public GrantApp setAccessTokenSignKey(String accessTokenSignKey) {
			// hasTextOf(accessTokenSignKey, "accessTokenSignKey");
			this.accessTokenSignKey = accessTokenSignKey;
			return this;
		}

		public String getDataCipher() {
			return dataCipher;
		}

		public GrantApp setDataCipher(String dataCipher) {
			// hasTextOf(dataCipher, "dataCipher");
			this.dataCipher = dataCipher;
			return this;
		}

		@Override
		public String toString() {
			return toJSONString(this);
		}

	}

}