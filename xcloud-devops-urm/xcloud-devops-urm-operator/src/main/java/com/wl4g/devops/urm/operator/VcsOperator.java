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
package com.wl4g.devops.urm.operator;

import com.google.common.annotations.Beta;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.core.framework.operator.Operator;
import com.wl4g.devops.common.bean.uci.Vcs;
import com.wl4g.devops.common.bean.urm.CompositeBasicVcsProjectModel;
import com.wl4g.devops.urm.operator.model.VcsBranchModel;
import com.wl4g.devops.urm.operator.model.VcsGroupModel;
import com.wl4g.devops.urm.operator.model.VcsProjectModel;
import com.wl4g.devops.urm.operator.model.VcsTagModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.urm.operator.VcsOperator.VcsProviderKind;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * VCS APIs operator.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
@Beta
public interface VcsOperator extends Operator<VcsProviderKind> {

	/**
	 * Gets this {@link SmartLogger} instance.
	 * 
	 * @return
	 */
	SmartLogger getLog();

	//
	// --- APIs operators. ---
	//

	/**
	 * Gets VCS remote branch names.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectId
	 * @return
	 */
	default <T extends VcsBranchModel> List<T> getRemoteBranchs(Vcs credentials, CompositeBasicVcsProjectModel vcsProject)
			throws Exception {
		notNull(credentials, "Get remote branchs credentials can't is null.");
		notNull(vcsProject, "Get remote branchs vcsProject can't is null");
		return null;
	}

	/**
	 * Create Branch
	 * 
	 * @param credentials
	 * @param projectId
	 * @param branch
	 * @param ref
	 * @param <T>
	 * @return
	 */
	default <T extends VcsBranchModel> T createRemoteBranch(Vcs credentials, Long projectId, String branch, String ref) {
		return null;
	}

	/**
	 * Gets VCS remote tag names.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectId
	 * @return
	 */
	default <T extends VcsTagModel> List<T> getRemoteTags(Vcs credentials, CompositeBasicVcsProjectModel vcsProject)
			throws Exception {
		notNull(credentials, "Get remote tags credentials can't is null.");
		notNull(credentials, "Get remote tags vcsProject can't is null.");
		return null;
	}

	/**
	 * Create tag to remote VCS server.
	 *
	 * @param credentials
	 * @param projectId
	 * @param branch
	 * @param ref
	 * @param <T>
	 * @return
	 */
	default <T extends VcsTagModel> T createRemoteTag(Vcs credentials, Long projectId, String tag, String ref, String message,
			String releaseDescription) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets remote project ID by project name.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectName
	 * @return
	 */
	default Long getRemoteProjectId(Vcs credentials, String projectName) throws Exception {
		notNull(credentials, "Get remote projectId credentials can't is null.");
		hasText(projectName, "Get remote projectId can't is empty");
		getLog().info("Search remote projectIds by projectName: {}", projectName);
		return null;
	}

	/**
	 * Search find remote projects by name.(unlimited)
	 * 
	 * @param credentials
	 * @param projectName
	 * @return
	 */
	default <T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Long groupId, String projectName)
			throws Exception {
		return searchRemoteProjects(credentials, groupId, projectName, SearchMeta.MAX_LIMIT);
	}

	/**
	 * Search find remote projects by name.
	 * 
	 * @param credentials
	 * @param projectName
	 *            The item name to be searched can be empty. If it is empty, it
	 *            means unconditional.
	 * @param meta
	 * @return
	 */
	default <T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Long groupId, String projectName,
			SearchMeta meta) throws Exception {
		notNull(credentials, "Search remote projects credentials can't is null.");
		/*
		 * The item name to be searched can be empty. If it is empty, it means
		 * unconditional.
		 */
		// hasText(projectName, "Search remote projects name can't is empty");
		isTrue(meta.getPageNo() > 0, "Search remote pageNo > 0");
		isTrue(meta.getLimit() > 0, "Search remote limit > 0");
		return null;
	}

	/**
	 * Search find remote projects by Id.
	 *
	 * @param credentials
	 * @param vcsProjectId
	 * @return
	 */
	default <T extends VcsProjectModel> T searchRemoteProjectsById(Vcs credentials, Long vcsProjectId) {
		notNullOf(vcsProjectId, "vcsProjectId");
		return null;
	}

	/**
	 * Search find remote groups by name.(unlimited)
	 *
	 * @param credentials
	 * @param projectName
	 * @return
	 */
	default <T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName) {
		return searchRemoteGroups(credentials, groupName, SearchMeta.MAX_LIMIT);
	}

	/**
	 * Search find remote teams(groups) by name.
	 *
	 * @param credentials
	 * @param projectName
	 *            The item name to be searched can be empty. If it is empty, it
	 *            means unconditional.
	 * @param meta
	 * @return
	 */
	default <T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName, SearchMeta meta) {
		return null;
	}

	//
	// --- GIT operators. ---
	//

	/**
	 * Clone from remote VCS server.
	 *
	 * @param <T>
	 * @param credentials
	 *            VCS authentication credentials
	 * @param remoteUrl
	 *            remote VCS server URI
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 * @throws IOException
	 */
	default <T> T clone(Vcs credentials, String remoteUrl, String projecDir) throws IOException {
		return clone(credentials, remoteUrl, projecDir, null);
	}

	/**
	 * Clone from remote VCS server.
	 *
	 * @param <T>
	 * @param credentials
	 *            VCS authentication credentials
	 * @param remoteUrl
	 *            remote VCS server URI
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 * @return
	 * @throws IOException
	 */
	default <T> T clone(Vcs credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		notNull(credentials, "Clone credentials is requires.");
		hasText(remoteUrl, "Clone remoteUrl is requires");
		hasText(projecDir, "Clone projecDir is requires");
		hasText(branchName, "Clone branchName is requires");
		getLog().info("Cloning VCS repository for remoteUrl: {}, projecDir: {}, branchName:{}", remoteUrl, projecDir, branchName);
		return null;
	}

	/**
	 * Checkout and pull of VCS repository.
	 *
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param refName
	 *            VCS source name (branch/tag)
	 */
	default <T> T checkoutAndPull(Vcs credentials, String projecDir, String refName, RefType action) {
		notNull(credentials, "Checkout & pull credentials is requires.");
		hasText(projecDir, "Checkout & pull projecDir is requires.");
		hasText(refName, "Checkout & pull branchName is requires.");
		getLog().info("Checkout & pull for projecDir: {}, branchName: {}", projecDir, refName);
		return null;
	}

	/**
	 * Delete (local) branch.
	 *
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 * @param force
	 * @return
	 */
	default List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		hasText(projecDir, "Deletion local branch projecDir is requires.");
		hasText(branchName, "Deletion local branch  branchName is requires.");
		getLog().info("Deletion local branch for projecDir: {}, branchName: {}, force: {}", projecDir, branchName, force);
		return null;
	}

	/**
	 * Check VCS project have local repository exist.
	 *
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 */
	default boolean hasLocalRepository(String projecDir) {
		hasText(projecDir, "Check VCS repository projecDir can't is empty");
		getLog().info("Check VCS repository for projecDir: {}", projecDir);
		return false;
	}

	/**
	 * Gets (local) latest committed ID.
	 *
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 * @throws Exception
	 */
	default String getLatestCommitted(String projecDir) throws Exception {
		hasText(projecDir, "Get committed projecDir can't is empty");
		getLog().info("Get latest committed for projecDir: {}", projecDir);
		return null;
	}

	/**
	 * Roll-back VCS project local repository(fetch and checkout).
	 *
	 * @param <T>
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param sign
	 *            committed ID.
	 * @return
	 */
	default <T> T rollback(Vcs credentials, String projecDir, String sign) {
		notNull(credentials, "Rollback credentials is requires.");
		hasText(projecDir, "Rollback projecDir can't is empty");
		hasText(sign, "Rollback sign can't is empty");
		getLog().info("Rollback for projecDir: {}, sign: {}", projecDir, sign);
		return null;
	}

	/**
	 * VCS provider definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static enum VcsProviderKind {

		/** Vcs for GITLAB. */
		GITLAB(1),

		/** Vcs for github. */
		GITHUB(2),

		/** Vcs for gitee. */
		GITEE(3),

		/** Vcs for alicode. */
		ALICODE(4),

		/** Vcs for bitbucket. */
		BITBUCKET(5),

		/** Vcs for coding. */
		CODING(6);

		final private int value;

		private VcsProviderKind(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link VcsProviderKind}
		 * 
		 * @param vcsProvider
		 * @return
		 */
		final public static VcsProviderKind safeOf(Integer vcsProvider) {
			if (isNull(vcsProvider)) {
				return null;
			}
			for (VcsProviderKind t : values()) {
				if (vcsProvider.intValue() == t.getValue()) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Converter string to {@link VcsProviderKind}
		 * 
		 * @param vcsProvider
		 * @return
		 */
		final public static VcsProviderKind of(Integer vcsProvider) {
			VcsProviderKind type = safeOf(vcsProvider);
			notNull(type, String.format("Unsupported VCS provider for %s", vcsProvider));
			return type;
		}

	}

	/**
	 * VCS resource ref definitions.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-11-09
	 * @sine v1.0.0
	 * @see
	 */
	public static enum RefType {

		/** Branch . */
		BRANCH("1"),

		/** Tag. */
		TAG("2");

		final private String value;

		private RefType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link RefType}
		 *
		 * @param action
		 * @return
		 */
		final public static RefType safeOf(String action) {
			if (isNull(action)) {
				return null;
			}
			for (RefType t : values()) {
				if (StringUtils.equals(action, t.getValue())) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Converter string to {@link RefType}
		 *
		 * @param action
		 * @return
		 */
		final public static RefType of(String action) {
			RefType type = safeOf(action);
			notNull(type, String.format("Unsupported VCS provider for %s", action));
			return type;
		}

	}

	/**
	 * Search page result metadata wrapper.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-11-09
	 * @sine v1.0.0
	 * @see
	 */
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static final class SearchMeta {
		private Integer pageNo = 1;
		private Integer limit = 20;
		private Integer total;

		public SearchMeta(Integer pageNo, Integer limit) {
			this.pageNo = pageNo;
			this.limit = limit;
		}

		public static final SearchMeta MAX_LIMIT = new SearchMeta(1, Integer.MAX_VALUE, 0);

	}

}