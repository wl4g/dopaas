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
package com.wl4g.devops.urm.service;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.devops.common.bean.uci.Vcs;
import com.wl4g.devops.common.bean.urm.CompositeBasicVcsProjectModel;
import com.wl4g.devops.urm.operator.model.VcsBranchModel;
import com.wl4g.devops.urm.operator.model.VcsGroupModel;
import com.wl4g.devops.urm.operator.model.VcsProjectModel;
import com.wl4g.devops.urm.operator.model.VcsTagModel;

/**
 * {@link RepoService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2019-11-12 11:05:00
 * @sine v1.0.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.urm-facade:repoService}")
@RequestMapping("/vcs")
public interface RepoService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Vcs> list(@RequestBody PageHolder<Vcs> pm, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "provider", required = false) String provider,
			@RequestParam(name = "authType", required = false) Integer authType);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Vcs vcs);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/detail", method = POST)
	Vcs detail(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/all", method = POST)
	List<Vcs> all();

	@RequestMapping(value = "/getProjectsToCompositeBasic", method = POST)
	List<CompositeBasicVcsProjectModel> getProjectsToCompositeBasic(@RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "projectName", required = false) String projectName) throws Exception;

	@RequestMapping(value = "/getGroups", method = POST)
	List<VcsGroupModel> getGroups(@RequestParam(name = "id", required = false) Long id,
			@RequestParam(name = "groupName", required = false) String groupName);

	@RequestMapping(value = "/getProjects", method = POST)
	List<VcsProjectModel> getProjects(@RequestBody PageHolder<?> pm, @RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "groupId", required = false) Long groupId,
			@RequestParam(name = "projectName", required = false) String projectName) throws Exception;

	@RequestMapping(value = "/getProjectById", method = POST)
	VcsProjectModel getProjectById(@RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "projectId", required = false) Long projectId);

	@RequestMapping(value = "/getBranchs", method = POST)
	List<VcsBranchModel> getBranchs(@RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "projectId", required = false) Long projectId) throws Exception;

	@RequestMapping(value = "/getTags", method = POST)
	List<VcsTagModel> getTags(@RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "projectId", required = false) Long projectId) throws Exception;

	@RequestMapping(value = "/createBranch", method = POST)
	VcsBranchModel createBranch(@RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "projectId", required = false) Long projectId,
			@RequestParam(name = "branch", required = false) String branch,
			@RequestParam(name = "ref", required = false) String ref) throws Exception;

	@RequestMapping(value = "/createTag", method = POST)
	VcsTagModel createTag(@RequestParam(name = "vcsId", required = false) Long vcsId,
			@RequestParam(name = "projectId", required = false) Long projectId,
			@RequestParam(name = "tag", required = false) String tag, @RequestParam(name = "ref", required = false) String ref,
			@RequestParam(name = "message", required = false) String message,
			@RequestParam(name = "releaseDescription", required = false) String releaseDescription) throws Exception;

}