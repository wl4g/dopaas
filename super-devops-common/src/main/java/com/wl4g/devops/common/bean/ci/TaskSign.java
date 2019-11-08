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
package com.wl4g.devops.common.bean.ci;

import java.io.Serializable;

import com.wl4g.devops.common.bean.BaseBean;

public class TaskSign extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private Integer taskId;

	private Integer dependencyId;

	private String shaGit;

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getDependenvyId() {
		return dependencyId;
	}

	public void setDependenvyId(Integer dependenvyId) {
		this.dependencyId = dependenvyId;
	}

	public String getShaGit() {
		return shaGit;
	}

	public void setShaGit(String shaGit) {
		this.shaGit = shaGit == null ? null : shaGit.trim();
	}
}