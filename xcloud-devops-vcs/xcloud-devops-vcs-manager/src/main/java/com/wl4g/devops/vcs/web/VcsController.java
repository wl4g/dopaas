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
package com.wl4g.devops.vcs.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.bean.vcs.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import com.wl4g.devops.vcs.service.VcsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * @author vjay
 * @date 2019-11-12 11:03:00
 */
@RestController
@RequestMapping("/vcs")
public class VcsController extends BaseController {

	@Autowired
	private VcsService vcsService;

	@RequestMapping("/list")
	@RequiresPermissions(value = { "vcs" }, logical = AND)
	public RespBase<PageModel<Vcs>> list(PageModel<Vcs> pm, String name, String providerKind, Integer authType) {
		RespBase<PageModel<Vcs>> resp = RespBase.create();
		resp.setData(vcsService.list(pm, name, providerKind, authType));
		return resp;
	}

	@RequestMapping("/save")
	@RequiresPermissions(value = { "vcs" }, logical = AND)
	public RespBase<?> save(@RequestBody Vcs vcs) {
		RespBase<Object> resp = RespBase.create();
		vcsService.save(vcs);
		return resp;
	}

	@RequestMapping("/del")
	@RequiresPermissions(value = { "vcs" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		vcsService.del(id);
		return resp;
	}

	@RequestMapping("/detail")
	@RequiresPermissions(value = { "vcs" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		Vcs vcs = vcsService.detail(id);
		resp.setData(vcs);
		return resp;
	}

	@RequestMapping("/all")
	public RespBase<?> all() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(vcsService.all());
		return resp;
	}

	@RequestMapping(value = "/vcsProjects")
	public RespBase<?> searchVcsProjects(Long vcsId, String projectName) throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<CompositeBasicVcsProjectModel> remoteProjects = vcsService.getProjectsToCompositeBasic(vcsId, projectName);
		resp.setData(remoteProjects);
		return resp;
	}

	@RequestMapping("/getGroups")
	public RespBase<?> getGroups(Long id, String groupName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(vcsService.getGroups(id, groupName));
		return resp;
	}

	@RequestMapping(value = "/getProjects")
	public RespBase<PageModel<VcsProjectModel>> getProjects(PageModel<VcsProjectModel> pm, Long vcsId, Long groupId,
			String projectName) throws Exception {
		RespBase<PageModel<VcsProjectModel>> resp = RespBase.create();
		List<VcsProjectModel> projects = vcsService.getProjects(pm, vcsId, groupId, projectName);
		pm.setRecords(projects);
		resp.setData(pm);
		return resp;
	}

	@RequestMapping(value = "/getProjectById")
	public RespBase<?> getProjectById(Long vcsId, Long projectId) {
		RespBase<Object> resp = RespBase.create();
		VcsProjectModel project = vcsService.getProjectById(vcsId, projectId);
		resp.setData(project);
		return resp;
	}

	@RequestMapping(value = "/getBranchs")
	public RespBase<?> getBranchs(Long vcsId, Long projectId) throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<VcsBranchModel> branchs = vcsService.getBranchs(vcsId, projectId);
		resp.setData(branchs);
		return resp;
	}

	@RequestMapping(value = "/getTags")
	public RespBase<?> tags(Long vcsId, Long projectId) throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<VcsTagModel> tags = vcsService.getTags(vcsId, projectId);
		resp.setData(tags);
		return resp;
	}

	@RequestMapping(value = "/createBranch")
	@RequiresPermissions(value = { "vcs" }, logical = AND)
	public RespBase<?> createBranch(Long vcsId, Long projectId, String branch, String ref) throws Exception {
		RespBase<Object> resp = RespBase.create();
		VcsBranchModel vcsBranchModel = vcsService.createBranch(vcsId, projectId, branch, ref);
		resp.setData(vcsBranchModel);
		return resp;
	}

	@RequestMapping(value = "/createTag")
	@RequiresPermissions(value = { "vcs" }, logical = AND)
	public RespBase<?> createTag(Long vcsId, Long projectId, String tag, String ref, String message, String releaseDescription)
			throws Exception {
		RespBase<Object> resp = RespBase.create();
		VcsTagModel vcsServiceTag = vcsService.createTag(vcsId, projectId, tag, ref, message, releaseDescription);
		resp.setData(vcsServiceTag);
		return resp;
	}

}