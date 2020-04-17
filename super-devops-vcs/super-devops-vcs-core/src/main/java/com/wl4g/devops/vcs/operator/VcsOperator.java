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
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.operator.model.VcsGroupModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;

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
	List<String> getRemoteBranchNames(Vcs credentials, int projectId);

	/**
	 * Gets VCS remote tag names.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectId
	 * @return
	 */
	List<String> getRemoteTags(Vcs credentials, int projectId);

	/**
	 * Gets remote project ID by project name.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projectName
	 * @return
	 */
	Integer getRemoteProjectId(Vcs credentials, String projectName);

	/**
	 * Search find remote projects by name.(unlimited)
	 * 
	 * @param credentials
	 * @param projectName
	 * @return
	 */
	default <T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Integer groupId, String projectName,PageModel pm) {
		return searchRemoteProjects(credentials, groupId, projectName, Integer.MAX_VALUE, pm);
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
	<T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Integer groupId, String projectName, int limit, PageModel pm);


	/**
	 * Search find remote projects by name.(unlimited)
	 *
	 * @param credentials
	 * @param projectName
	 * @return
	 */
	default <T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName) {
		return searchRemoteGroups(credentials, groupName, Integer.MAX_VALUE);
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
	<T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName, int limit);

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



}