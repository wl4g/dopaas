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

import java.io.Serializable;

public class Trigger extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String name;

	private Integer appClusterId;

	private Integer taskId;

	private Integer type;

	private String cron;

	private String sha;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Integer getAppClusterId() {
		return appClusterId;
	}

	public void setAppClusterId(Integer appClusterId) {
		this.appClusterId = appClusterId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron == null ? null : cron.trim();
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha == null ? null : sha.trim();
	}

	@Override
	public String toString() {
		return "Trigger{" + "name='" + name + '\'' + ", appClusterId=" + appClusterId + ", taskId=" + taskId + ", type=" + type
				+ ", cron='" + cron + '\'' + ", sha='" + sha + '\'' + '}';
	}
}