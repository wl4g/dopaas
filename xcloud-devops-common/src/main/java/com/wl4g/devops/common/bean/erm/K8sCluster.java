/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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

import java.util.List;

import com.wl4g.component.core.bean.BaseBean;

public class K8sCluster extends BaseBean {

	private static final long serialVersionUID = -7546448616357790576L;

	private List<Long> hostIds;

	private String name;

	private String masterAddr;

	private String secondaryMasterAddr;

	public List<Long> getHostIds() {
		return hostIds;
	}

	public void setHostIds(List<Long> hostIds) {
		this.hostIds = hostIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getMasterAddr() {
		return masterAddr;
	}

	public void setMasterAddr(String masterAddr) {
		this.masterAddr = masterAddr == null ? null : masterAddr.trim();
	}

	public String getSecondaryMasterAddr() {
		return secondaryMasterAddr;
	}

	public void setSecondaryMasterAddr(String secondaryMasterAddr) {
		this.secondaryMasterAddr = secondaryMasterAddr == null ? null : secondaryMasterAddr.trim();
	}

}