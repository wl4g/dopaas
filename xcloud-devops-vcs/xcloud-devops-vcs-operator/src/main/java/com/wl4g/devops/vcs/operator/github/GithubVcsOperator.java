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
package com.wl4g.devops.vcs.operator.github;

import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.components.tools.common.annotation.Reserved;
import com.wl4g.devops.vcs.operator.GenericBasedGitVcsOperator;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * VCS operator for GITHUB.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
@Reserved
public class GithubVcsOperator extends GenericBasedGitVcsOperator {

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.GITHUB;
	}

	@Override
	protected HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("PRIVATE-TOKEN", credentials.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}

	@Override
	public List<VcsBranchModel> getRemoteBranchs(Vcs credentials, int projectId) {
		super.getRemoteBranchs(credentials, projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<VcsTagModel> getRemoteTags(Vcs credentials, int projectId) {
		super.getRemoteTags(credentials, projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getRemoteProjectId(Vcs credentials, String projectName) {
		super.getRemoteProjectId(credentials, projectName);
		throw new UnsupportedOperationException();
	}

}