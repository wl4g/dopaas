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

import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;

public class PipeHistoryPcm extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer enable;

	private Integer pipeId;

	private Integer pcmId;

	private Integer xProjectId;

	private String xTracker;

	private String xStatus;

	private String xSubject;

	private String xDescription;

	private String xPriority;

	private String xAssignTo;

	private Date xStartDate;

	private Long xExpectedTime;

	private String xCustomFields;

	private String xParentIssueId;

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getPipeId() {
		return pipeId;
	}

	public void setPipeId(Integer pipeId) {
		this.pipeId = pipeId;
	}

	public Integer getPcmId() {
		return pcmId;
	}

	public void setPcmId(Integer pcmId) {
		this.pcmId = pcmId;
	}

	public Integer getxProjectId() {
		return xProjectId;
	}

	public void setxProjectId(Integer xProjectId) {
		this.xProjectId = xProjectId;
	}

	public String getxTracker() {
		return xTracker;
	}

	public void setxTracker(String xTracker) {
		this.xTracker = xTracker == null ? null : xTracker.trim();
	}

	public String getxStatus() {
		return xStatus;
	}

	public void setxStatus(String xStatus) {
		this.xStatus = xStatus == null ? null : xStatus.trim();
	}

	public String getxSubject() {
		return xSubject;
	}

	public void setxSubject(String xSubject) {
		this.xSubject = xSubject == null ? null : xSubject.trim();
	}

	public String getxDescription() {
		return xDescription;
	}

	public void setxDescription(String xDescription) {
		this.xDescription = xDescription == null ? null : xDescription.trim();
	}

	public String getxPriority() {
		return xPriority;
	}

	public void setxPriority(String xPriority) {
		this.xPriority = xPriority == null ? null : xPriority.trim();
	}

	public String getxAssignTo() {
		return xAssignTo;
	}

	public void setxAssignTo(String xAssignTo) {
		this.xAssignTo = xAssignTo == null ? null : xAssignTo.trim();
	}

	public Date getxStartDate() {
		return xStartDate;
	}

	public void setxStartDate(Date xStartDate) {
		this.xStartDate = xStartDate;
	}

	public Long getxExpectedTime() {
		return xExpectedTime;
	}

	public void setxExpectedTime(Long xExpectedTime) {
		this.xExpectedTime = xExpectedTime;
	}

	public String getxCustomFields() {
		return xCustomFields;
	}

	public void setxCustomFields(String xCustomFields) {
		this.xCustomFields = xCustomFields == null ? null : xCustomFields.trim();
	}

	public String getxParentIssueId() {
		return xParentIssueId;
	}

	public void setxParentIssueId(String xParentIssueId) {
		this.xParentIssueId = xParentIssueId;
	}
}