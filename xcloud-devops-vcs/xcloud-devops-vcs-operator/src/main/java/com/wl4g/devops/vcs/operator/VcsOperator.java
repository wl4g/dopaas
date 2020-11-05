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
package com.wl4g.devops.vcs.operator;

import com.google.common.annotations.Beta;
import com.wl4g.components.core.bean.ci.Vcs;
import com.wl4g.components.core.bean.vcs.CompositeBasicVcsProjectModel;
import com.wl4g.components.core.framework.operator.Operator;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsGroupModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.util.Assert.notNull;

/**
 * VCS APIs operator.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
@Beta
public interface VcsOperator extends Operator<VcsOperator.VcsProviderKind> {

	// --- APIs operator. ---

	/**
	 * Gets VCS remote branch names.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectId
	 * @return
	 */
	<T extends VcsBranchModel> List<T> getRemoteBranchs(Vcs credentials, CompositeBasicVcsProjectModel vcsProject)
			throws Exception;

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
	<T extends VcsBranchModel> T createRemoteBranch(Vcs credentials, Long projectId, String branch, String ref);

	/**
	 * Gets VCS remote tag names.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectId
	 * @return
	 */
	<T extends VcsTagModel> List<T> getRemoteTags(Vcs credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception;

	/**
	 *
	 * @param credentials
	 * @param projectId
	 * @param branch
	 * @param ref
	 * @param <T>
	 * @return
	 */
	<T extends VcsTagModel> T createRemoteTag(Vcs credentials, Long projectId, String tag, String ref, String message,
			String releaseDescription);

	/**
	 * Gets remote project ID by project name.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectName
	 * @return
	 */
	Long getRemoteProjectId(Vcs credentials, String projectName) throws Exception;

	/**
	 * Search find remote projects by name.(unlimited)
	 * 
	 * @param credentials
	 * @param projectName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	default <T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Long groupId, String projectName,
			PageModel pm) throws Exception {
		return searchRemoteProjects(credentials, groupId, projectName, Long.MAX_VALUE, pm);
	}

	/**
	 * Search find remote projects by name.
	 * 
	 * @param credentials
	 * @param projectName
	 *            The item name to be searched can be empty. If it is empty, it
	 *            means unconditional.
	 * @param limit
	 *            Page limit maximum
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	<T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Long groupId, String projectName, long limit,
			PageModel pm) throws Exception;

	/**
	 * Search find remote projects by Id.
	 *
	 * @param credentials
	 * @param projectId
	 * @return
	 */
	<T extends VcsProjectModel> T searchRemoteProjectsById(Vcs credentials, Long projectId);

	/**
	 * Search find remote groups by name.(unlimited)
	 *
	 * @param credentials
	 * @param projectName
	 * @return
	 */
	default <T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName) {
		return searchRemoteGroups(credentials, groupName, Long.MAX_VALUE);
	}

	/**
	 * Search find remote groups by name.
	 *
	 * @param credentials
	 * @param projectName
	 *            The item name to be searched can be empty. If it is empty, it
	 *            means unconditional.
	 * @param limit
	 *            Page limit maximum
	 * @return
	 */
	<T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName, long limit);

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
	<T> T clone(Vcs credentials, String remoteUrl, String projecDir, String branchName) throws IOException;

	/**
	 * Checkout and pull of VCS repository.
	 *
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 */
	<T> T checkoutAndPull(Vcs credentials, String projecDir, String branchName, VcsAction action);

	/**
	 * Delete (local) branch.
	 *
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 * @param force
	 * @return
	 */
	List<String> delLocalBranch(String projecDir, String branchName, boolean force);

	/**
	 * Check VCS project have local repository exist.
	 *
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 */
	boolean hasLocalRepository(String projecDir);

	/**
	 * Gets (local) latest committed ID.
	 *
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 * @throws Exception
	 */
	String getLatestCommitted(String projecDir) throws Exception;

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
	<T> T rollback(Vcs credentials, String projecDir, String sign);

	/**
	 * VCS type definitions.
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

	public static enum VcsAction {

		/** Branch . */
		BRANCH("1"),

		/** Tag. */
		TAG("2");

		final private String value;

		private VcsAction(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link VcsAction}
		 *
		 * @param action
		 * @return
		 */
		final public static VcsAction safeOf(String action) {
			if (isNull(action)) {
				return null;
			}
			for (VcsAction t : values()) {
				if (StringUtils.equals(action, t.getValue())) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Converter string to {@link VcsAction}
		 *
		 * @param action
		 * @return
		 */
		final public static VcsAction of(String action) {
			VcsAction type = safeOf(action);
			notNull(type, String.format("Unsupported VCS provider for %s", action));
			return type;
		}

	}

}