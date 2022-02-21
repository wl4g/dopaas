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
package com.wl4g.dopaas.urm.operator.gitlab;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.wl4g.dopaas.common.bean.urm.model.CompositeBasicVcsProjectModel;
import com.wl4g.dopaas.urm.operator.model.VcsProjectModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Gitlab API-v4 for projects simple model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
@Getter
@Setter
@ToString
public class GitlabV4SimpleProjectModel implements VcsProjectModel, Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	private Long id;
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

	@Override
	public CompositeBasicVcsProjectModel toCompositeVcsProject() {
		return new CompositeBasicVcsProjectModel(getId(), getName(), getHttp_url_to_repo(), getSsh_url_to_repo(),
				getPath_with_namespace());
	}

	/**
	 * Gitlab API-v4 for namespace.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	@Getter
	@Setter
	@ToString
	public static class Namespace {
		private int id;
		private String name;
		private String path;
		private String kind;
		private String full_path;
		private int parent_id;
	}

}