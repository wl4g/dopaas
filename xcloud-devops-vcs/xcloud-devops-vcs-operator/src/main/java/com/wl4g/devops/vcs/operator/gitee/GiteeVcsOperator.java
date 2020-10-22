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
package com.wl4g.devops.vcs.operator.gitee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.components.common.annotation.Reserved;
import com.wl4g.components.core.bean.ci.Vcs;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.operator.GenericBasedGitVcsOperator;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * VCS operator for GITEE.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
@Reserved
public class GiteeVcsOperator extends GenericBasedGitVcsOperator {

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.GITEE;
	}

	@Override
	protected HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials) {
		HttpHeaders headers = new HttpHeaders();
		//headers.add("PRIVATE-TOKEN", credentials.getAccessToken());
		headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}

	@Override
	public List<VcsBranchModel> getRemoteBranchs(Vcs credentials, Long projectId) {
		super.getRemoteBranchs(credentials, projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<VcsTagModel> getRemoteTags(Vcs credentials, Long projectId) {
		super.getRemoteTags(credentials, projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public Long getRemoteProjectId(Vcs credentials, String projectName) throws Exception {
		super.getRemoteProjectId(credentials, projectName);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<GiteeV5SimpleProjectModel> searchRemoteProjects(Vcs credentials, Long groupId, String projectName, long limit,
																 PageModel pm) throws Exception {
		super.searchRemoteProjects(credentials, groupId, projectName, limit, pm);

		// Parameters correcting.
		if (isBlank(projectName)) {
			projectName = EMPTY;
		}
		if (nonNull(pm) && nonNull(pm.getPageSize())) {
			limit = pm.getPageSize();
		} else {
			limit = 100;
		}
		int pageNum = 1;
		if (nonNull(pm) && nonNull(pm.getPageNum())) {
			pageNum = pm.getPageNum();
		}
		String url = String.format((credentials.getBaseUri() + "/api/v5/user/repos?access_token=%s&q=%s&per_page=%s&page=%s"),
				credentials.getAccessToken(),projectName, limit, pageNum);

		HttpHeaders headers = new HttpHeaders();
		List<GiteeV5SimpleProjectModel> projects = doRemoteExchangeSSL(credentials, url, headers,
				new TypeReference<List<GiteeV5SimpleProjectModel>>() {
				});
		/*if (nonNull(pm)) {
			pm.setTotal(Long.valueOf(headers.getFirst("X-Total")));
		}*/
		return safeList(projects);

	}

}