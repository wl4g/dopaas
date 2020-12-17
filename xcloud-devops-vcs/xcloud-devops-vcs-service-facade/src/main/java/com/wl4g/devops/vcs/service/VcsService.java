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
package com.wl4g.devops.vcs.service;

import com.wl4g.component.core.bean.model.PageModel;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.bean.vcs.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsGroupModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;

import java.util.List;

/**
 * {@link VcsService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2019-11-12 11:05:00
 * @sine v1.0.0
 * @see
 */
public interface VcsService {

	PageModel<Vcs> list(PageModel<Vcs> pm, String name, String provider, Integer authType);

	void save(Vcs vcs);

	void del(Long id);

	Vcs detail(Long id);

	List<Vcs> all();

	List<CompositeBasicVcsProjectModel> getProjectsToCompositeBasic(Long vcsId, String projectName) throws Exception;

	List<VcsGroupModel> getGroups(Long id, String groupName);

	List<VcsProjectModel> getProjects(PageModel<?> pm, Long vcsId, Long groupId, String projectName) throws Exception;

	VcsProjectModel getProjectById(Long vcsId, Long projectId);

	List<VcsBranchModel> getBranchs(Long vcsId, Long projectId) throws Exception;

	List<VcsTagModel> getTags(Long vcsId, Long projectId) throws Exception;

	VcsBranchModel createBranch(Long vcsId, Long projectId, String branch, String ref) throws Exception;

	VcsTagModel createTag(Long vcsId, Long projectId, String tag, String ref, String message, String releaseDescription)
			throws Exception;

}