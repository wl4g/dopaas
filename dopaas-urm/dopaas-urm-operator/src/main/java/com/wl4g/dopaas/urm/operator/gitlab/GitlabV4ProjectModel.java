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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.component.common.annotation.Reserved;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Gitlab API-v4 for projects model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
@Reserved
@Getter
@Setter
@ToString
public class GitlabV4ProjectModel extends GitlabV4SimpleProjectModel {
	private static final long serialVersionUID = 614084533443846624L;

	private Links _links;
	private boolean archived;
	private String visibility;
	private boolean resolve_outdated_diff_discussions;
	private boolean container_registry_enabled;
	private boolean issues_enabled;
	private boolean merge_requests_enabled;
	private boolean wiki_enabled;
	private boolean jobs_enabled;
	private boolean snippets_enabled;
	private boolean shared_runners_enabled;
	private boolean lfs_enabled;
	private int creator_id;
	private String import_status;
	private int open_issues_count;
	private boolean public_jobs;
	private String ci_config_path;
	private List<String> shared_with_groups;
	private boolean only_allow_merge_if_pipeline_succeeds;
	private boolean request_access_enabled;
	private boolean only_allow_merge_if_all_discussions_are_resolved;
	private boolean printing_merge_request_link_enabled;
	private String merge_method;
	private String external_authorization_classification_label;
	private Permissions permissions;
	private Owner owner;

	/**
	 * Gitlab API-v4 for group-access.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	@Getter
	@Setter
	@ToString
	public static class GroupAccess {
		private int access_level;
		private int notification_level;
	}

	/**
	 * Gitlab API-v4 for permissions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	@Getter
	@Setter
	@ToString
	public static class Permissions {
		private GroupAccess project_access;
		private GroupAccess group_access;
	}

	/**
	 * Gitlab API-v4 for links.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	@Getter
	@Setter
	@ToString
	public static class Links {
		private String self;
		private String issues;
		private String merge_requests;
		private String repo_branches;
		private String labels;
		private String events;
		private String members;
	}

	@Getter
	@Setter
	@ToString
	public static class Owner {
		private int id;
		private String name;
		private String username;
		private String state;
		@JsonProperty("avatar_url")
		private String avatarUrl;
		@JsonProperty("web_url")
		private String webUrl;
	}

}