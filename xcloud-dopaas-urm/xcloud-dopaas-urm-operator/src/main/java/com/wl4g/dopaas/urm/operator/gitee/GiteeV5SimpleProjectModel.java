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
package com.wl4g.dopaas.urm.operator.gitee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.dopaas.common.bean.urm.model.CompositeBasicVcsProjectModel;
import com.wl4g.dopaas.urm.operator.model.VcsProjectModel;

import java.io.Serializable;

/**
 * Gitlab API-v4 for projects simple model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
public class GiteeV5SimpleProjectModel implements VcsProjectModel, Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	private Long id;
	@JsonProperty("full_name")
	private String fullName;
	@JsonProperty("human_name")
	private String humanName;
	private String url;
	private String path;
	private String name;
	private String description;
	private boolean internal;
	private boolean fork;
	@JsonProperty("html_url")
	private String htmlUrl;
	@JsonProperty("ssh_url")
	private String sshUrl;

	@Override
	public CompositeBasicVcsProjectModel toCompositeVcsProject() {
		return new CompositeBasicVcsProjectModel(getId(), getName(), getHtmlUrl(), getSshUrl(),
				getFullName());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getHumanName() {
		return humanName;
	}

	public void setHumanName(String humanName) {
		this.humanName = humanName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public boolean isFork() {
		return fork;
	}

	public void setFork(boolean fork) {
		this.fork = fork;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}

	public String getSshUrl() {
		return sshUrl;
	}

	public void setSshUrl(String sshUrl) {
		this.sshUrl = sshUrl;
	}
}