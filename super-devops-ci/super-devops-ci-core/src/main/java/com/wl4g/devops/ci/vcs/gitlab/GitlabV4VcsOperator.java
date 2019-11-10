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

import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * VCS operator for GITLAB V4.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public class GitlabV4VcsOperator extends GenericBasedGitVcsOperator {

	@Override
	public String vcsType() {
		return VcsType.GITLAB;
	}

	@Override
	public List<String> getRemoteBranchNames(int projectId) {
		super.getRemoteBranchNames(projectId);

		String url = config.getVcs().getGitlab().getBaseUrl() + "/api/v4/projects/" + projectId + "/repository/branches";
		// Extract branch names.
		List<Map<String, Object>> branchs = doGitExchange(url, new TypeReference<List<Map<String, Object>>>() {
		});
		List<String> branchNames = safeList(branchs).stream().map(m -> m.getOrDefault("name", EMPTY).toString())
				.filter(s -> !isEmpty(s)).collect(toList());

		if (log.isInfoEnabled()) {
			log.info("Extract remote branch names: {}", branchNames);
		}
		return branchNames;
	}

	@Override
	public List<String> getRemoteTags(int projectId) {
		super.getRemoteTags(projectId);

		String url = config.getVcs().getGitlab().getBaseUrl() + "/api/v4/projects/" + projectId + "/repository/tags";
		// Extract tag names.
		List<Map<String, Object>> tags = doGitExchange(url, new TypeReference<List<Map<String, Object>>>() {
		});
		List<String> tagNames = safeList(tags).stream().map(m -> m.getOrDefault("name", EMPTY).toString())
				.filter(s -> !isEmpty(s)).collect(toList());

		if (log.isInfoEnabled()) {
			log.info("Extract remote tag names: {}", tagNames);
		}
		return tagNames;
	}

	@Override
	public Integer findRemoteProjectId(String projectName) {
		super.findRemoteProjectId(projectName);

		String url = config.getVcs().getGitlab().getBaseUrl() + "/api/v4/projects?simple=true&search=" + projectName;
		// Extract project IDs.
		List<Map<String, Object>> projects = doGitExchange(url, new TypeReference<List<Map<String, Object>>>() {
		});

		Integer id = null;
		for (Map<String, Object> map : projects) {
			if (map.getOrDefault("name", "-1").toString().equals(projectName)) {
				id = Integer.parseInt(map.getOrDefault("id", "-1").toString());
				break;
			}
		}

		if (log.isInfoEnabled()) {
			log.info("Extract remote project IDs: {}", id);
		}
		return id;
	}

}