/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;
import java.util.List;

public class Trigger extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private Integer projectId;

	private String branchName;

	private String command;

	private Integer tarType;

	private List<TriggerDetail> triggerDetails;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Integer getTarType() {
		return tarType;
	}

	public void setTarType(Integer tarType) {
		this.tarType = tarType;
	}

	public List<TriggerDetail> getTriggerDetails() {
		return triggerDetails;
	}

	public void setTriggerDetails(List<TriggerDetail> triggerDetails) {
		this.triggerDetails = triggerDetails;
	}
}