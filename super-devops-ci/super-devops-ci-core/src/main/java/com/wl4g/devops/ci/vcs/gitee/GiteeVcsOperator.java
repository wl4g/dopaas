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
package com.wl4g.devops.ci.vcs.gitee;

import java.io.IOException;
import java.util.List;

import com.wl4g.devops.ci.vcs.AbstractVcsOperator;

/**
 * VCS operator for GITEE.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
public class GiteeVcsOperator extends AbstractVcsOperator {

	@Override
	public String vcsType() {
		return VcsType.GITEE;
	}

	@Override
	public List<String> getRemoteBranchNames(int projectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getRemoteTags(int projectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer findRemoteProjectId(String projectName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T clone(Object credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkoutAndPull(Object credentials, String projecDir, String branchName) {
		throw new UnsupportedOperationException();

	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean checkGitPath(String projecDir) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T rollback(Object credentials, String projecDir, String sign) {
		throw new UnsupportedOperationException();
	}

}
