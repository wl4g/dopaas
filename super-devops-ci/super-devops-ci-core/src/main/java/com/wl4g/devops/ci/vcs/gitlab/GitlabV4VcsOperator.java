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
import com.wl4g.devops.ci.vcs.gitlab.model.GitlabV4ProjectSimpleModel;
import com.wl4g.devops.ci.vcs.model.VcsProjectDto;
import com.wl4g.devops.common.bean.ci.Vcs;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
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
	public VcsProvider vcsProvider() {
		return VcsProvider.GITLAB;
	}

	@Override
	public List<String> getRemoteBranchNames(Vcs credentials, int projectId) {
		super.getRemoteBranchNames(credentials, projectId);

		String url = credentials.getBaseUri() + "/api/v4/projects/" + projectId + "/repository/branches";
		// Extract branch names.
		List<Map<String, Object>> branchs = doGitExchange(credentials, url, new TypeReference<List<Map<String, Object>>>() {
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
		List<Map<String, Object>> tags = doGitExchange(credentials, url, new TypeReference<List<Map<String, Object>>>() {
		});
		List<String> tagNames = safeList(tags).stream().map(m -> m.getOrDefault("name", EMPTY).toString())
				.filter(s -> !isEmpty(s)).collect(toList());

		if (log.isInfoEnabled()) {
			log.info("Extract remote tag names: {}", tagNames);
		}
		return tagNames;
	}

	@Override
	public Integer findRemoteProjectId(Vcs credentials, String projectName) {
		super.findRemoteProjectId(credentials, projectName);

		String url = credentials.getBaseUri() + "/api/v4/projects?simple=true&search=" + projectName;
		// Extract project IDs.
		List<GitlabV4ProjectSimpleModel> projects = doGitExchange(credentials, url,
				new TypeReference<List<GitlabV4ProjectSimpleModel>>() {
				});

		Integer id = null;
		for (GitlabV4ProjectSimpleModel p : projects) {
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

	@Override
	public List<VcsProjectDto> findRemoteProjects(Vcs credentials, String projectName) {
		//super.findRemoteProjectId(credentials, projectName);
		String search = "";
		if(StringUtils.isNotBlank(projectName)){
			search = "&search="+projectName;
		}
		//TODO per_page = 50 , get from vcs
		String url = credentials.getBaseUri() + "/api/v4/projects?simple=true&per_page=50" + search;
		// Extract project IDs.
		List<GitlabV4ProjectSimpleModel> projects = doGitExchange(credentials, url,
				new TypeReference<List<GitlabV4ProjectSimpleModel>>() {
				});
		List<VcsProjectDto> vcsProjectDtos = new ArrayList<>();
		for(GitlabV4ProjectSimpleModel gitlabV4ProjectSimpleModel : projects){
			VcsProjectDto vcsProjectDto = new VcsProjectDto();
			vcsProjectDto.setId(gitlabV4ProjectSimpleModel.getId());
			vcsProjectDto.setName(gitlabV4ProjectSimpleModel.getName());
			vcsProjectDto.setHttpUrl(gitlabV4ProjectSimpleModel.getHttp_url_to_repo());
			vcsProjectDto.setSshUrl(gitlabV4ProjectSimpleModel.getSsh_url_to_repo());
			vcsProjectDtos.add(vcsProjectDto);
		}
		return vcsProjectDtos;
	}

}