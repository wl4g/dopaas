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
package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.List;

public class CustomDataSource extends BaseBean {
	private static final long serialVersionUID = 381411777614066880L;

	private String name;

	private String provider;

	private Integer status;

	private List<CustomDataSourceProperties> customDataSourceProperties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider == null ? null : provider.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<CustomDataSourceProperties> getCustomDataSourceProperties() {
		return customDataSourceProperties;
	}

	public void setCustomDataSourceProperties(List<CustomDataSourceProperties> customDataSourceProperties) {
		this.customDataSourceProperties = customDataSourceProperties;
	}
}