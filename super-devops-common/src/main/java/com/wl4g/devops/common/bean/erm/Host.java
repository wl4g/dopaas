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

import java.util.List;

public class Host extends BaseBean {

	private static final long serialVersionUID = -7546448616357790576L;

	private String name;

	private String hostname;

	private Integer idcId;

	private Integer status;

	private List<Integer> sshIds;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname == null ? null : hostname.trim();
	}

	public Integer getIdcId() {
		return idcId;
	}

	public void setIdcId(Integer idcId) {
		this.idcId = idcId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Integer> getSshIds() {
		return sshIds;
	}

	public void setSshIds(List<Integer> sshIds) {
		this.sshIds = sshIds;
	}
}