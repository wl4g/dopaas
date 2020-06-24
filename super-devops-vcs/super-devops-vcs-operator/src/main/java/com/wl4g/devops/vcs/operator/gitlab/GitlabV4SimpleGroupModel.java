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
package com.wl4g.devops.vcs.operator.gitlab;


import com.wl4g.devops.vcs.operator.model.VcsGroupModel;

import java.io.Serializable;
import java.util.List;

/**
 * Gitlab API-v4 for projects simple model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
public class GitlabV4SimpleGroupModel implements VcsGroupModel, Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	private Integer id;
	private String web_url;
	private String name;
	private String path;
	private String description;
	private String visibility;
	private Boolean lfs_enabled;
	private String avatar_url;
	private Boolean request_access_enabled;
	private String full_name;
	private String full_path;
	private Integer parent_id;


	//other
	List<GitlabV4SimpleGroupModel> children;

	public GitlabV4SimpleGroupModel() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWeb_url() {
		return web_url;
	}

	public void setWeb_url(String web_url) {
		this.web_url = web_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Boolean getLfs_enabled() {
		return lfs_enabled;
	}

	public void setLfs_enabled(Boolean lfs_enabled) {
		this.lfs_enabled = lfs_enabled;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public Boolean getRequest_access_enabled() {
		return request_access_enabled;
	}

	public void setRequest_access_enabled(Boolean request_access_enabled) {
		this.request_access_enabled = request_access_enabled;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getFull_path() {
		return full_path;
	}

	public void setFull_path(String full_path) {
		this.full_path = full_path;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public List<GitlabV4SimpleGroupModel> getChildren() {
		return children;
	}

	public void setChildren(List<GitlabV4SimpleGroupModel> children) {
		this.children = children;
	}
}