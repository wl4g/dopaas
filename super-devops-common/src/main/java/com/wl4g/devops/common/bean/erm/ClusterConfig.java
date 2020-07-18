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

/**
 * Application cluster of environment configuration bean.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月7日
 * @since
 */
public class ClusterConfig extends BaseBean {
	private static final long serialVersionUID = -7546448616357790576L;

	private Integer clusterId;
	private String name;
	private String displayName;
	private Integer type;
	private String envType;
	private String viewExtranetBaseUri;
	private String extranetBaseUri;
	private String intranetBaseUri;

	public ClusterConfig() {
	}

	public ClusterConfig(String name, String viewExtranetBaseUri) {
		this.name = name;
		this.viewExtranetBaseUri = viewExtranetBaseUri;
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName == null ? null : displayName.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType == null ? null : envType.trim();
	}

	public String getViewExtranetBaseUri() {
		return viewExtranetBaseUri;
	}

	public void setViewExtranetBaseUri(String viewExtranetBaseUri) {
		this.viewExtranetBaseUri = viewExtranetBaseUri == null ? null : viewExtranetBaseUri.trim();
	}

	public String getExtranetBaseUri() {
		return extranetBaseUri;
	}

	public void setExtranetBaseUri(String extranetBaseUri) {
		this.extranetBaseUri = extranetBaseUri == null ? null : extranetBaseUri.trim();
	}

	public String getIntranetBaseUri() {
		return intranetBaseUri;
	}

	public void setIntranetBaseUri(String intranetBaseUri) {
		this.intranetBaseUri = intranetBaseUri == null ? null : intranetBaseUri.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}