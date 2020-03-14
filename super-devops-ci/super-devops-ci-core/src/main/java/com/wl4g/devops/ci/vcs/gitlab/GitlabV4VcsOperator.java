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
package com.wl4g.devops.ci.vcs.gitlab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.ci.vcs.GenericBasedGitVcsOperator;
import com.wl4g.devops.common.bean.ci.Vcs;

import java.util.List;
import java.util.Map;

import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static java.util.stream.Collectors.toList;
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
	public List<String> getRemoteBranchNames(Vcs credentials, int projectId) {
		super.getRemoteBranchNames(credentials, projectId);

		String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/branches";
		// Extract branch names.
		List<Map<String, Object>> branchs = doRemoteExchange(credentials, url, new TypeReference<List<Map<String, Object>>>() {
		});
		List<String> branchNames = safeList(branchs).stream().map(m -> m.getOrDefault("name", EMPTY).toString())
				.filter(s -> !isEmpty(s)).collect(toList());

		if (log.isInfoEnabled()) {
			log.info("Extract remote branch names: {}", branchNames);
		}
		return branchNames;
	}

	@Override
	public List<String> getRemoteTags(Vcs credentials, int projectId) {
		super.getRemoteTags(credentials, projectId);

		String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/tags";
		// Extract tag names.
		List<Map<String, Object>> tags = doRemoteExchange(credentials, url, new TypeReference<List<Map<String, Object>>>() {
		});
		List<String> tagNames = safeList(tags).stream().map(m -> m.getOrDefault("name", EMPTY).toString())
				.filter(s -> !isEmpty(s)).collect(toList());

		if (log.isInfoEnabled()) {
			log.info("Extract remote tag names: {}", tagNames);
		}
		return tagNames;
	}

	@Override
	public Integer getRemoteProjectId(Vcs credentials, String projectName) {
		super.getRemoteProjectId(credentials, projectName);

		// Search projects for GITLAB.
		List<GitlabV4SimpleProjectModel> projects = searchRemoteProjects(credentials, projectName);
		Integer id = null;
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
	public List<GitlabV4SimpleProjectModel> searchRemoteProjects(Vcs credentials, String projectName, int limit) {
		super.searchRemoteProjects(credentials, projectName, limit);

		// Parameters correcting.
		if (isBlank(projectName)) {
			projectName = EMPTY;
			limit = config.getVcs().getGitlab().getSearchProjectsDefaultPageLimit();
		}

		// Search of remote URL.
		String url = String.format((credentials.getBaseUri() + "/api/v4/projects?simple=true&search=%s&per_page=%s"), projectName,
				limit);
		// Search projects.
		List<GitlabV4SimpleProjectModel> projects = doRemoteExchange(credentials, url,
				new TypeReference<List<GitlabV4SimpleProjectModel>>() {
				});
		return safeList(projects);
	}

}