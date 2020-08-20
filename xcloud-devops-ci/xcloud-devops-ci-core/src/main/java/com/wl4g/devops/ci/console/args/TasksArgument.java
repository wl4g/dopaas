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
package com.wl4g.devops.ci.console.args;

import java.io.Serializable;

import com.wl4g.shell.common.annotation.ShellOption;

/**
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-21 15:53:00
 */
public class TasksArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "p", lopt = "pagenum", help = "page num", required = false, defaultValue = "1")
	private Integer pageNum = 1;
	@ShellOption(opt = "s", lopt = "pagesize", help = "page size", required = false, defaultValue = "10")
	private Integer pageSize = 10;

	@ShellOption(opt = "i", lopt = "taskId", help = "page size", required = false)
	private Integer id;
	@ShellOption(opt = "t", lopt = "taskName", help = "task name", required = false)
	private String taskName;
	@ShellOption(opt = "g", lopt = "groupName", help = "task cluster name", required = false)
	private String groupName;
	@ShellOption(opt = "b", lopt = "branchName", help = "task project branch name", required = false)
	private String branchName;
	@ShellOption(opt = "P", lopt = "tarType", help = "task pipeline provider type", required = false)
	private String tarType;
	@ShellOption(opt = "S", lopt = "startDate", help = "task start date(yyyy-MM-dd HH:mm:ss)", required = false)
	private String startDate;
	@ShellOption(opt = "E", lopt = "endDate", help = "task end date(yyyy-MM-dd HH:mm:ss)", required = false)
	private String endDate;

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getTarType() {
		return tarType;
	}

	public void setTarType(String tarType) {
		this.tarType = tarType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}