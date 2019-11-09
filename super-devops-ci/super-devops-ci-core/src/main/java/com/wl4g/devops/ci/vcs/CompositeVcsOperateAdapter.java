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

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.state;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.springframework.util.Assert;

import com.wl4g.devops.ci.vcs.gitlab.GitlabV4VcsOperator;
import com.wl4g.devops.common.utils.lang.OnceModifiableMap;

/**
 * Composite VCS operator adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月1日
 * @since
 */
public class CompositeVcsOperateAdapter implements VcsOperator {

	/**
	 * Vcs operator.
	 */
	final protected Map<String, VcsOperator> registry = new OnceModifiableMap<>(new HashMap<>());

	/**
	 * Real delegate VcsOperator.
	 */
	final private ThreadLocal<VcsOperator> delegate = new InheritableThreadLocal<>();

	public CompositeVcsOperateAdapter(List<VcsOperator> operators) {
		Assert.state(!isEmpty(operators), "Vcs operators has at least one.");
		// Duplicate checks.
		Set<String> vcsTypes = new HashSet<>();
		operators.forEach(o -> {
			hasText(o.vcsType(), String.format("VcsType must not be empty for VcsOperator %s", o));
			state(!vcsTypes.contains(o.vcsType()), String.format("Repeated definition VcsOperator with %s", o.vcsType()));
			vcsTypes.add(o.vcsType());
		});
		// Register.
		this.registry.putAll(operators.stream().collect(toMap(VcsOperator::vcsType, oper -> oper)));
	}

	/**
	 * Adapt the default VCs operator {@link GitlabV4VcsOperator}.
	 * 
	 * @return
	 */
	public VcsOperator forDefault() {
		return forAdapt(VcsType.GITLAB);
	}

	/**
	 * Making the adaptation actually execute {@link VcsOperator}.
	 * 
	 * @param type
	 * @return
	 */
	public VcsOperator forAdapt(@NotBlank String type) {
		VcsOperator operator = registry.get(type);
		Assert.notNull(operator, String.format("Unsupport VcsOperator for '%s'", type));
		delegate.set(operator);
		return operator;
	}

	@Override
	public List<String> getRemoteBranchNames(int projectId) {
		return getAdapted().getRemoteBranchNames(projectId);
	}

	@Override
	public List<String> getRemoteTags(int projectId) {
		return getAdapted().getRemoteTags(projectId);
	}

	@Override
	public Integer findRemoteProjectId(String projectName) {
		return getAdapted().findRemoteProjectId(projectName);
	}

	/**
	 * Get adapted {@link VcsOperator}.
	 * 
	 * @param type
	 * @return
	 */
	private VcsOperator getAdapted() {
		VcsOperator operator = delegate.get();
		Assert.state(operator != null,
				"Not adapted to specify actual VcsOperator, You must use adapted() to adapt before you can.");
		return operator;
	}

	@Override
	public <T> T clone(Object credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		return getAdapted().clone(credentials, remoteUrl, projecDir, branchName);
	}

	@Override
	public void checkoutAndPull(Object credentials, String projecDir, String branchName) {
		getAdapted().checkoutAndPull(credentials, projecDir, branchName);
	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		return getAdapted().delLocalBranch(projecDir, branchName, force);
	}

	@Override
	public boolean ensureLocalRepo(String projecDir) {
		return getAdapted().ensureLocalRepo(projecDir);
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		return getAdapted().getLatestCommitted(projecDir);
	}

	@Override
	public <T> T rollback(Object credentials, String projecDir, String sign) {
		return getAdapted().rollback(credentials, projecDir, sign);
	}

}
