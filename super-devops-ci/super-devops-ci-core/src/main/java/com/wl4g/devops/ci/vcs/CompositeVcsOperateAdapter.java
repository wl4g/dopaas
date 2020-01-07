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
package com.wl4g.devops.ci.vcs;

import com.wl4g.devops.ci.vcs.model.VcsProjectModel;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;

import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.Ref;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

import static com.wl4g.devops.ci.vcs.VcsOperator.VcsProvider;

/**
 * Composite VCS operator adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月1日
 * @since
 */
public class CompositeVcsOperateAdapter extends GenericOperatorAdapter<VcsProvider, VcsOperator> implements VcsOperator {

	public CompositeVcsOperateAdapter(List<VcsOperator> operators) {
		super(operators);
	}

	/**
	 * Making the adaptation actually execute {@link Vcs}.
	 * 
	 * @param vcs
	 * @return
	 */
	public VcsOperator forAdapt(@NotNull Vcs vcs) {
		return forAdapt(String.valueOf(vcs.getProviderKind()));
	}

	@Override
	public List<String> getRemoteBranchNames(Vcs credentials, int projectId) {
		return getAdapted().getRemoteBranchNames(credentials, projectId);
	}

	@Override
	public List<String> getRemoteTags(Vcs credentials, int projectId) {
		return getAdapted().getRemoteTags(credentials, projectId);
	}

	@Override
	public Integer getRemoteProjectId(Vcs credentials, String projectName) {
		return getAdapted().getRemoteProjectId(credentials, projectName);
	}

	@Override
	public <T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, String projectName, int limit) {
		return getAdapted().searchRemoteProjects(credentials, projectName, 0);
	}

	@Override
	public <T> T clone(Vcs credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		return getAdapted().clone(credentials, remoteUrl, projecDir, branchName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PullResult checkoutAndPull(Vcs credentials, String projecDir, String branchName) {
		return getAdapted().checkoutAndPull(credentials, projecDir, branchName);
	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		return getAdapted().delLocalBranch(projecDir, branchName, force);
	}

	@Override
	public boolean hasLocalRepository(String projecDir) {
		return getAdapted().hasLocalRepository(projecDir);
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		return getAdapted().getLatestCommitted(projecDir);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Ref rollback(Vcs credentials, String projecDir, String sign) {
		return getAdapted().rollback(credentials, projecDir, sign);
	}

}