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
package com.wl4g.devops.ci.vcs.bitbucket;

import java.util.List;

import com.wl4g.devops.ci.vcs.AbstractVcsOperator;

/**
 * Bitbucket Vcs operator.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
public class BitbucketVcsOperator extends AbstractVcsOperator {

	@Override
	public String vcsType() {
		return VcsType.BITBUCKET;
	}

	@Override
	public List<String> getRemoteBranchNames(int projectId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRemoteTags(int projectId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer findRemoteProjectId(String projectName) {
		// TODO Auto-generated method stub
		return null;
	}

}
