/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.common.bean.scm;

/**
 * 对应表：cf_app_group
 * 
 * @date 2018年9月19日
 */
public class AppGroup extends BaseBean {

	private Long deptId; // 应用所属部门ID
	private String name; // 应用名称 Disabled
	private Integer enable; // 启用状态（0:禁止/1:启用）
	private String evnname; // 环境名

	/**
	 * IAM fast-CAS load balancing base/index(redirect) URI.
	 */
	private String loadBalanceBaseUri;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getEvnname() {
		return evnname;
	}

	public void setEvnname(String evnname) {
		this.evnname = evnname;
	}

	public String getLoadBalanceBaseUri() {
		return loadBalanceBaseUri;
	}

	public void setLoadBalanceBaseUri(String loadBalanceBaseUri) {
		this.loadBalanceBaseUri = loadBalanceBaseUri;
	}

}