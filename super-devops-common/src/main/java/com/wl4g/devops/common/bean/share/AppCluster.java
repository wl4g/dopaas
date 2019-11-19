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
package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.List;

/**
 * 对应表：app_cluster
 * 
 * @date 2018年9月19日
 */
public class AppCluster extends BaseBean {
	private static final long serialVersionUID = -3298424126317938674L;

	private Integer deptId;

	private String name;

	private Integer type;

	private Integer enable;

	private String remark;

	// --- Temporary. ---

	private int instanceCount;
	private List<AppInstance> instances;

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public Integer getEnable() {
		return enable;
	}

	@Override
	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}
}