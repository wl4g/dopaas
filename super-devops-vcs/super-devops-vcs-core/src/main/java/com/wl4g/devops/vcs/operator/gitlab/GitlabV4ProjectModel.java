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

import com.wl4g.devops.tool.common.annotation.Reserved;

import java.util.List;

/**
 * Gitlab API-v4 for projects model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
@Reserved
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

	public void set_links(Links _links) {
		this._links = _links;
	}

	public Links get_links() {
		return _links;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean getArchived() {
		return archived;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setResolve_outdated_diff_discussions(boolean resolve_outdated_diff_discussions) {
		this.resolve_outdated_diff_discussions = resolve_outdated_diff_discussions;
	}

	public boolean getResolve_outdated_diff_discussions() {
		return resolve_outdated_diff_discussions;
	}

	public void setContainer_registry_enabled(boolean container_registry_enabled) {
		this.container_registry_enabled = container_registry_enabled;
	}

	public boolean getContainer_registry_enabled() {
		return container_registry_enabled;
	}

	public void setIssues_enabled(boolean issues_enabled) {
		this.issues_enabled = issues_enabled;
	}

	public boolean getIssues_enabled() {
		return issues_enabled;
	}

	public void setMerge_requests_enabled(boolean merge_requests_enabled) {
		this.merge_requests_enabled = merge_requests_enabled;
	}

	public boolean getMerge_requests_enabled() {
		return merge_requests_enabled;
	}

	public void setWiki_enabled(boolean wiki_enabled) {
		this.wiki_enabled = wiki_enabled;
	}

	public boolean getWiki_enabled() {
		return wiki_enabled;
	}

	public void setJobs_enabled(boolean jobs_enabled) {
		this.jobs_enabled = jobs_enabled;
	}

	public boolean getJobs_enabled() {
		return jobs_enabled;
	}

	public void setSnippets_enabled(boolean snippets_enabled) {
		this.snippets_enabled = snippets_enabled;
	}

	public boolean getSnippets_enabled() {
		return snippets_enabled;
	}

	public void setShared_runners_enabled(boolean shared_runners_enabled) {
		this.shared_runners_enabled = shared_runners_enabled;
	}

	public boolean getShared_runners_enabled() {
		return shared_runners_enabled;
	}

	public void setLfs_enabled(boolean lfs_enabled) {
		this.lfs_enabled = lfs_enabled;
	}

	public boolean getLfs_enabled() {
		return lfs_enabled;
	}

	public void setCreator_id(int creator_id) {
		this.creator_id = creator_id;
	}

	public int getCreator_id() {
		return creator_id;
	}

	public void setImport_status(String import_status) {
		this.import_status = import_status;
	}

	public String getImport_status() {
		return import_status;
	}

	public void setOpen_issues_count(int open_issues_count) {
		this.open_issues_count = open_issues_count;
	}

	public int getOpen_issues_count() {
		return open_issues_count;
	}

	public void setPublic_jobs(boolean public_jobs) {
		this.public_jobs = public_jobs;
	}

	public boolean getPublic_jobs() {
		return public_jobs;
	}

	public void setCi_config_path(String ci_config_path) {
		this.ci_config_path = ci_config_path;
	}

	public String getCi_config_path() {
		return ci_config_path;
	}

	public void setShared_with_groups(List<String> shared_with_groups) {
		this.shared_with_groups = shared_with_groups;
	}

	public List<String> getShared_with_groups() {
		return shared_with_groups;
	}

	public void setOnly_allow_merge_if_pipeline_succeeds(boolean only_allow_merge_if_pipeline_succeeds) {
		this.only_allow_merge_if_pipeline_succeeds = only_allow_merge_if_pipeline_succeeds;
	}

	public boolean getOnly_allow_merge_if_pipeline_succeeds() {
		return only_allow_merge_if_pipeline_succeeds;
	}

	public void setRequest_access_enabled(boolean request_access_enabled) {
		this.request_access_enabled = request_access_enabled;
	}

	public boolean getRequest_access_enabled() {
		return request_access_enabled;
	}

	public void setOnly_allow_merge_if_all_discussions_are_resolved(boolean only_allow_merge_if_all_discussions_are_resolved) {
		this.only_allow_merge_if_all_discussions_are_resolved = only_allow_merge_if_all_discussions_are_resolved;
	}

	public boolean getOnly_allow_merge_if_all_discussions_are_resolved() {
		return only_allow_merge_if_all_discussions_are_resolved;
	}

	public void setPrinting_merge_request_link_enabled(boolean printing_merge_request_link_enabled) {
		this.printing_merge_request_link_enabled = printing_merge_request_link_enabled;
	}

	public boolean getPrinting_merge_request_link_enabled() {
		return printing_merge_request_link_enabled;
	}

	public void setMerge_method(String merge_method) {
		this.merge_method = merge_method;
	}

	public String getMerge_method() {
		return merge_method;
	}

	public void setExternal_authorization_classification_label(String external_authorization_classification_label) {
		this.external_authorization_classification_label = external_authorization_classification_label;
	}

	public String getExternal_authorization_classification_label() {
		return external_authorization_classification_label;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	/**
	 * Gitlab API-v4 for group-access.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	public static class GroupAccess {

		private int access_level;
		private int notification_level;

		public void setAccess_level(int access_level) {
			this.access_level = access_level;
		}

		public int getAccess_level() {
			return access_level;
		}

		public void setNotification_level(int notification_level) {
			this.notification_level = notification_level;
		}

		public int getNotification_level() {
			return notification_level;
		}

	}

	/**
	 * Gitlab API-v4 for permissions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	public static class Permissions {

		private String project_access;
		private GroupAccess group_access;

		public void setProject_access(String project_access) {
			this.project_access = project_access;
		}

		public String getProject_access() {
			return project_access;
		}

		public void setGroup_access(GroupAccess group_access) {
			this.group_access = group_access;
		}

		public GroupAccess getGroup_access() {
			return group_access;
		}

	}

	/**
	 * Gitlab API-v4 for links.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月13日
	 * @since
	 */
	public static class Links {

		private String self;
		private String issues;
		private String merge_requests;
		private String repo_branches;
		private String labels;
		private String events;
		private String members;

		public void setSelf(String self) {
			this.self = self;
		}

		public String getSelf() {
			return self;
		}

		public void setIssues(String issues) {
			this.issues = issues;
		}

		public String getIssues() {
			return issues;
		}

		public void setMerge_requests(String merge_requests) {
			this.merge_requests = merge_requests;
		}

		public String getMerge_requests() {
			return merge_requests;
		}

		public void setRepo_branches(String repo_branches) {
			this.repo_branches = repo_branches;
		}

		public String getRepo_branches() {
			return repo_branches;
		}

		public void setLabels(String labels) {
			this.labels = labels;
		}

		public String getLabels() {
			return labels;
		}

		public void setEvents(String events) {
			this.events = events;
		}

		public String getEvents() {
			return events;
		}

		public void setMembers(String members) {
			this.members = members;
		}

		public String getMembers() {
			return members;
		}

	}

}