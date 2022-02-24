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

import com.wl4g.dopaas.common.bean.urm.SourceRepo;
import com.wl4g.dopaas.common.bean.urm.model.CompositeBasicVcsProjectModel;
import com.wl4g.dopaas.urm.operator.GenericBasedGitVcsOperator;
import com.wl4g.dopaas.urm.operator.model.VcsBranchModel;
import com.wl4g.dopaas.urm.operator.model.VcsTagModel;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpMethod.*;

/**
 * VCS operator for GITEE.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
public class GiteeVcsOperator extends GenericBasedGitVcsOperator {

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.GITEE;
	}

	@Override
	protected HttpEntity<String> createRequestEntity(SourceRepo credentials) {
		HttpHeaders headers = new HttpHeaders();
		// headers.add("PRIVATE-TOKEN", credentials.getAccessToken());
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}

	@Override
	public List<VcsBranchModel> getRemoteBranchs(SourceRepo credentials, CompositeBasicVcsProjectModel vcsProject)
			throws Exception {
		super.getRemoteBranchs(credentials, vcsProject);

		String url = String.format((credentials.getBaseUri() + "/api/v5/repos/%s/branches?access_token=%s"),
				vcsProject.getPathWithNamespace(), credentials.getAccessToken());

		HttpHeaders headers = new HttpHeaders();
		// Search projects.
		List<VcsBranchModel> branchs = doRemoteRequest(GET, credentials, url, headers,
				new ParameterizedTypeReference<List<VcsBranchModel>>() {
				}).getBody();
		return branchs;
	}

	@Override
	public List<VcsTagModel> getRemoteTags(SourceRepo credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception {
		super.getRemoteTags(credentials, vcsProject);

		String url = String.format((credentials.getBaseUri() + "/api/v5/repos/%s/tags?access_token=%s"),
				vcsProject.getPathWithNamespace(), credentials.getAccessToken());
		// Search projects.
		return doRemoteRequest(GET, credentials, url, new HttpHeaders(), new ParameterizedTypeReference<List<VcsTagModel>>() {
		}).getBody();
	}

	@Override
	public Long getRemoteProjectId(SourceRepo credentials, String projectName) throws Exception {
		super.getRemoteProjectId(credentials, projectName);
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<GiteeV5SimpleProjectModel> searchRemoteProjects(SourceRepo credentials, Long groupId, String projectName,
			SearchMeta meta) throws Exception {
		super.searchRemoteProjects(credentials, groupId, projectName, meta);

		// Parameters correcting.
		projectName = isBlank(projectName) ? EMPTY : projectName;

		String url = String.format((credentials.getBaseUri() + "/api/v5/user/repos?access_token=%s&q=%s&per_page=%s&page=%s"),
				credentials.getAccessToken(), projectName, meta.getLimit(), meta.getPageNo());

		List<GiteeV5SimpleProjectModel> projects = doRemoteRequest(GET, credentials, url, null,
				new ParameterizedTypeReference<List<GiteeV5SimpleProjectModel>>() {
				}).getBody();
		/*
		 * if (nonNull(pm)) {
		 * pm.setTotal(Long.valueOf(headers.getFirst("X-Total"))); }
		 */
		return safeList(projects);

	}

}