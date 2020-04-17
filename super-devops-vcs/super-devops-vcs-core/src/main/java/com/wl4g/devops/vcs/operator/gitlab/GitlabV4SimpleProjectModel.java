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


import com.wl4g.devops.vcs.operator.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Gitlab API-v4 for projects simple model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
public class GitlabV4SimpleProjectModel implements VcsProjectModel, Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	private int id;
	private String description;
	private String name;
	private String name_with_namespace;
	private String path;
	private String path_with_namespace;
	private Date created_at;
	private String default_branch;
	private List<String> tag_list;
	private String ssh_url_to_repo;
	private String http_url_to_repo;
	private String web_url;
	private String readme_url;
	private String avatar_url;
	private int star_count;
	private int forks_count;
	private Date last_activity_at;
	private Namespace namespace;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName_with_namespace(String name_with_namespace) {
		this.name_with_namespace = name_with_namespace;
	}

	public String getName_with_namespace() {
		return name_with_namespace;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath_with_namespace(String path_with_namespace) {
		this.path_with_namespace = path_with_namespace;
	}

	public String getPath_with_namespace() {
		return path_with_namespace;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setDefault_branch(String default_branch) {
		this.default_branch = default_branch;
	}

	public String getDefault_branch() {
		return default_branch;
	}

	public void setTag_list(List<String> tag_list) {
		this.tag_list = tag_list;
	}

	public List<String> getTag_list() {
		return tag_list;
	}

	public void setSsh_url_to_repo(String ssh_url_to_repo) {
		this.ssh_url_to_repo = ssh_url_to_repo;
	}

	public String getSsh_url_to_repo() {
		return ssh_url_to_repo;
	}

	public void setHttp_url_to_repo(String http_url_to_repo) {
		this.http_url_to_repo = http_url_to_repo;
	}

	public String getHttp_url_to_repo() {
		return http_url_to_repo;
	}

	public void setWeb_url(String web_url) {
		this.web_url = web_url;
	}

	public String getWeb_url() {
		return web_url;
	}

	public void setReadme_url(String readme_url) {
		this.readme_url = readme_url;
	}

	public String getReadme_url() {
		return readme_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setStar_count(int star_count) {
		this.star_count = star_count;
	}

	public int getStar_count() {
		return star_count;
	}

	public void setForks_count(int forks_count) {
		this.forks_count = forks_count;
	}

	public int getForks_count() {
		return forks_count;
	}

	public void setLast_activity_at(Date last_activity_at) {
		this.last_activity_at = last_activity_at;
	}

	public Date getLast_activity_at() {
		return last_activity_at;
	}

	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	@Override
	public CompositeBasicVcsProjectModel toCompositeVcsProject() {
		return new CompositeBasicVcsProjectModel(getId(), getName(), getHttp_url_to_repo(), getSsh_url_to_repo());
	}

	/**
	 * Gitlab API-v4 for namespace.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	public static class Namespace {

		private int id;
		private String name;
		private String path;
		private String kind;
		private String full_path;
		private int parent_id;

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getKind() {
			return kind;
		}

		public void setFull_path(String full_path) {
			this.full_path = full_path;
		}

		public String getFull_path() {
			return full_path;
		}

		public void setParent_id(int parent_id) {
			this.parent_id = parent_id;
		}

		public int getParent_id() {
			return parent_id;
		}

	}

}