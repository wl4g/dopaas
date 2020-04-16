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
package com.wl4g.devops.ci.vcs.coding;

import com.wl4g.devops.ci.vcs.AbstractVcsOperator;
import com.wl4g.devops.ci.vcs.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.tool.common.annotation.Reserved;

import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.List;

/**
 * VCS operator for Coding.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
@Reserved
public class CodingVcsOperator extends AbstractVcsOperator {

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.CODING;
	}

	@Override
	public List<String> getRemoteBranchNames(Vcs credentials, int projectId) {
		super.getRemoteBranchNames(credentials, projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getRemoteTags(Vcs credentials, int projectId) {
		super.getRemoteTags(credentials, projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getRemoteProjectId(Vcs credentials, String projectName) {
		super.getRemoteProjectId(credentials, projectName);
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CompositeBasicVcsProjectModel> searchRemoteProjects(Vcs credentials, String projectName, int limit) {
		super.searchRemoteProjects(credentials, projectName, limit);
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T clone(Vcs credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		super.clone(credentials, remoteUrl, projecDir, branchName);
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T checkoutAndPull(Vcs credentials, String projecDir, String branchName, VcsAction action) {
		super.checkoutAndPull(credentials, projecDir, branchName, action);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		super.delLocalBranch(projecDir, branchName, force);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasLocalRepository(String projecDir) {
		super.hasLocalRepository(projecDir);
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		super.getLatestCommitted(projecDir);
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T rollback(Vcs credentials, String projecDir, String sign) {
		super.rollback(credentials, projecDir, sign);
		throw new UnsupportedOperationException();
	}

	@Override
	protected HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials) {
		throw new UnsupportedOperationException();
	}

}