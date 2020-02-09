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

import com.wl4g.devops.ci.vcs.GenericBasedGitVcsOperator;
import com.wl4g.devops.ci.vcs.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.tool.common.annotation.Reserved;

import java.util.List;

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
	public VcsProvider kind() {
		return VcsProvider.GITEE;
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

}