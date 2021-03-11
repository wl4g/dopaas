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
package com.wl4g.devops.vcs.operator.gitlab;

import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.bean.vcs.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.GenericBasedGitVcsOperator;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpMethod.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.component.common.lang.TypeConverts.parseIntOrDefault;
import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * VCS operator for GITLAB V4.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public class GitlabV4VcsOperator extends GenericBasedGitVcsOperator {

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.GITLAB;
	}

	@Override
	protected HttpEntity<String> createRequestEntity(Vcs credentials) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("PRIVATE-TOKEN", credentials.getAccessToken());
		return new HttpEntity<>(null, headers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GitlabV4BranchModel> getRemoteBranchs(Vcs credentials, CompositeBasicVcsProjectModel vcsProject)
			throws Exception {
		super.getRemoteBranchs(credentials, vcsProject);

		String url = credentials.getBaseUri() + "/api/v4/projects/" + vcsProject.getId() + "/repository/branches";
		// Extract branch names.
		ResponseEntity<List<GitlabV4BranchModel>> branchs = doRemoteRequest(GET, credentials, url, null,
				new ParameterizedTypeReference<List<GitlabV4BranchModel>>() {
				});

		log.info("Extract remote branch names: {}", branchs);
		return branchs.getBody();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GitlabV4TagModel> getRemoteTags(Vcs credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception {
		super.getRemoteTags(credentials, vcsProject);

		String url = credentials.getBaseUri() + "/api/v4/projects/" + vcsProject.getId() + "/repository/tags";
		// Extract tag names.
		ResponseEntity<List<GitlabV4TagModel>> tags = doRemoteRequest(GET, credentials, url, null,
				new ParameterizedTypeReference<List<GitlabV4TagModel>>() {
				});

		log.info("Extract remote tag names: {}", tags.getBody());
		return tags.getBody();
	}

	@SuppressWarnings("unchecked")
	@Override
	public GitlabV4BranchModel createRemoteBranch(Vcs credentials, Long projectId, String branch, String ref) {
		super.createRemoteBranch(credentials, projectId, branch, ref);
		String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/branches?branch=%s&ref=%s";

		return doRemoteRequest(POST, credentials, format(url, branch, ref), null,
				new ParameterizedTypeReference<GitlabV4BranchModel>() {
				}).getBody();
	}

	@SuppressWarnings("unchecked")
	@Override
	public GitlabV4TagModel createRemoteTag(Vcs credentials, Long projectId, String tag, String ref, String message,
			String releaseDescription) {
		super.createRemoteTag(credentials, projectId, tag, ref, message, releaseDescription);

		String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId
				+ "/repository/tags?tag_name=%s&ref=%s&message=%s&release_description=%s";

		return doRemoteRequest(POST, credentials, format(url, tag, ref, message, releaseDescription), null,
				new ParameterizedTypeReference<GitlabV4TagModel>() {
				}).getBody();
	}

	@Override
	public Long getRemoteProjectId(Vcs credentials, String projectName) throws Exception {
		super.getRemoteProjectId(credentials, projectName);

		// Search projects for GITLAB.
		List<GitlabV4SimpleProjectModel> projects = searchRemoteProjects(credentials, null, projectName, null);
		Long id = null;
		for (GitlabV4SimpleProjectModel p : projects) {
			if (trimToEmpty(projectName).equals(p.getName())) {
				id = p.getId();
				break;
			}
		}

		if (log.isInfoEnabled()) {
			log.info("Extract remote project IDs: {}", id);
		}
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GitlabV4SimpleTeamModel> searchRemoteGroups(Vcs credentials, String groupName) {
		String url = format((credentials.getBaseUri() + "/api/v4/groups?search=%s&per_page=%s"), groupName);

		ResponseEntity<List<GitlabV4SimpleTeamModel>> gitTeams = doRemoteRequest(GET, credentials, url, null,
				new ParameterizedTypeReference<List<GitlabV4SimpleTeamModel>>() {
				});

		return transformGitTeamTree(safeList(gitTeams.getBody()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GitlabV4SimpleProjectModel> searchRemoteProjects(Vcs credentials, Long groupId, String projectName,
			SearchMeta meta) throws Exception {
		super.searchRemoteProjects(credentials, groupId, projectName, meta);

		// Parameters correcting.
		projectName = isBlank(projectName) ? EMPTY : projectName;

		// Build search URL.
		String url;
		if (nonNull(groupId)) {
			url = format((credentials.getBaseUri() + "/api/v4/groups/%d/projects?simple=true&search=%s&per_page=%s&page=%s"),
					groupId, projectName, meta.getLimit(), meta.getPageNo());
		} else {
			url = format((credentials.getBaseUri() + "/api/v4/projects?simple=true&search=%s&per_page=%s&page=%s"), projectName,
					meta.getLimit(), meta.getPageNo());
		}

		// Search projects.
		ResponseEntity<List<GitlabV4SimpleProjectModel>> projects = doRemoteRequest(GET, credentials, url, null,
				new ParameterizedTypeReference<List<GitlabV4SimpleProjectModel>>() {
				});

		meta.setTotal(parseIntOrDefault(projects.getHeaders().getFirst("X-Total")));

		return safeList(projects.getBody());
	}

	@SuppressWarnings("unchecked")
	@Override
	public GitlabV4ProjectModel searchRemoteProjectsById(Vcs credentials, Long vcsProjectId) {
		String url = format((credentials.getBaseUri() + "/api/v4/projects/%d"), vcsProjectId);
		return doRemoteRequest(GET, credentials, url, null, new ParameterizedTypeReference<GitlabV4ProjectModel>() {
		}).getBody();
	}

	private List<GitlabV4SimpleTeamModel> transformGitTeamTree(List<GitlabV4SimpleTeamModel> teams) {
		List<GitlabV4SimpleTeamModel> top = new ArrayList<>();
		for (GitlabV4SimpleTeamModel team : teams) {
			if (Objects.isNull(team.getParent_id())) {
				top.add(team);
			}
		}
		for (GitlabV4SimpleTeamModel t : top) {
			addChild(teams, t);
		}
		return top;
	}

	private void addChild(List<GitlabV4SimpleTeamModel> groups, GitlabV4SimpleTeamModel parent) {
		for (GitlabV4SimpleTeamModel group : groups) {
			if (parent.getId().equals(group.getParent_id())) {
				List<GitlabV4SimpleTeamModel> children = parent.getChildren();
				if (Objects.isNull(children)) {
					children = new ArrayList<>();
				}
				children.add(group);
				parent.setChildren(children);
				addChild(groups, group);
			}
		}
	}

}