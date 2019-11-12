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

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpEntity;

import com.wl4g.devops.ci.vcs.AbstractVcsOperator;

/**
 * VCS operator for Coding.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
public class CodingVcsOperator extends AbstractVcsOperator {

	@Override
	public VcsProvider vcsProvider() {
		return VcsProvider.CODING;
	}

	@Override
	public List<String> getRemoteBranchNames(int projectId) {
		super.getRemoteBranchNames(projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getRemoteTags(int projectId) {
		super.getRemoteTags(projectId);
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer findRemoteProjectId(String projectName) {
		super.findRemoteProjectId(projectName);
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T clone(Object credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		super.clone(credentials, remoteUrl, projecDir, branchName);
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkoutAndPull(Object credentials, String projecDir, String branchName) {
		super.checkoutAndPull(credentials, projecDir, branchName);
		throw new UnsupportedOperationException();

	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		super.delLocalBranch(projecDir, branchName, force);
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean ensureRepo(String projecDir) {
		super.ensureRepo(projecDir);
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		super.getLatestCommitted(projecDir);
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T rollback(Object credentials, String projecDir, String sign) {
		super.rollback(credentials, projecDir, sign);
		throw new UnsupportedOperationException();
	}

	@Override
	protected HttpEntity<String> createVcsRequestHttpEntity() {
		throw new UnsupportedOperationException();
	}

}
