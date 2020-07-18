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
package com.wl4g.devops.iam.web.model;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.Serializable;

/**
 * {@link IamPrincipalPermitsResult}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月30日
 * @since
 */
public class IamPrincipalPermitsResult implements Serializable {
	private static final long serialVersionUID = 8802525988725357453L;

	/** Authenticate principal role codes. */
	private String roles = EMPTY;

	/** Authenticate principal permission. */
	private String permissions = EMPTY;

	public IamPrincipalPermitsResult() {
	}

	public IamPrincipalPermitsResult(String roles, String permissions) {
		setRoles(roles);
		setPermissions(permissions);
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}