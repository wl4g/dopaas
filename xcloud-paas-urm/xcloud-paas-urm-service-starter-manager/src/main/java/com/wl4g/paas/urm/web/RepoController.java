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
package com.wl4g.paas.urm.web;

import static org.apache.shiro.authz.annotation.Logical.AND;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.paas.common.bean.uci.Vcs;
import com.wl4g.paas.common.bean.urm.CompositeBasicVcsProjectModel;
import com.wl4g.paas.urm.operator.model.VcsBranchModel;
import com.wl4g.paas.urm.operator.model.VcsProjectModel;
import com.wl4g.paas.urm.operator.model.VcsTagModel;
import com.wl4g.paas.urm.service.RepoService;

/**
 * @author vjay
 * @date 2019-11-12 11:03:00
 */
@RestController
@RequestMapping("/vcs")
public class RepoController extends BaseController {

	@Autowired
	private RepoService repoService;

	@RequestMapping("/list")
	@RequiresPermissions(value = { "urm" }, logical = AND)
	public RespBase<PageHolder<Vcs>> list(PageHolder<Vcs> pm, String name, String providerKind, Integer authType) {
		RespBase<PageHolder<Vcs>> resp = RespBase.create();
		resp.setData(repoService.list(pm, name, providerKind, authType));
		return resp;
	}

	@RequestMapping("/save")
	@RequiresPermissions(value = { "urm" }, logical = AND)
	public RespBase<?> save(@RequestBody Vcs vcs) {
		RespBase<Object> resp = RespBase.create();
		repoService.save(vcs);
		return resp;
	}

	@RequestMapping("/del")
	@RequiresPermissions(value = { "urm" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		repoService.del(id);
		return resp;
	}

	@RequestMapping("/detail")
	@RequiresPermissions(value = { "urm" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		Vcs vcs = repoService.detail(id);
		resp.setData(vcs);
		return resp;
	}

	@RequestMapping("/all")
	public RespBase<?> all() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(repoService.all());
		return resp;
	}

	@RequestMapping(value = "/vcsProjects")
	public RespBase<?> searchVcsProjects(Long vcsId, String projectName) throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<CompositeBasicVcsProjectModel> remoteProjects = repoService.getProjectsToCompositeBasic(vcsId, projectName);
		resp.setData(remoteProjects);
		return resp;
	}

	@RequestMapping("/getGroups")
	public RespBase<?> getGroups(Long id, String groupName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(repoService.getGroups(id, groupName));
		return resp;
	}

	@RequestMapping(value = "/getProjects")
	public RespBase<PageHolder<VcsProjectModel>> getProjects(PageHolder<VcsProjectModel> pm, Long vcsId, Long groupId,
			String projectName) throws Exception {
		RespBase<PageHolder<VcsProjectModel>> resp = RespBase.create();
		List<VcsProjectModel> projects = repoService.getProjects(pm, vcsId, groupId, projectName);
		pm.setRecords(projects);
		resp.setData(pm);
		return resp;
	}

	@RequestMapping(value = "/getProjectById")
	public RespBase<?> getProjectById(Long vcsId, Long projectId) {
		RespBase<Object> resp = RespBase.create();
		VcsProjectModel project = repoService.getProjectById(vcsId, projectId);
		resp.setData(project);
		return resp;
	}

	@RequestMapping(value = "/getBranchs")
	public RespBase<?> getBranchs(Long vcsId, Long projectId) throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<VcsBranchModel> branchs = repoService.getBranchs(vcsId, projectId);
		resp.setData(branchs);
		return resp;
	}

	@RequestMapping(value = "/getTags")
	public RespBase<?> tags(Long vcsId, Long projectId) throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<VcsTagModel> tags = repoService.getTags(vcsId, projectId);
		resp.setData(tags);
		return resp;
	}

	@RequestMapping(value = "/createBranch")
	@RequiresPermissions(value = { "urm" }, logical = AND)
	public RespBase<?> createBranch(Long vcsId, Long projectId, String branch, String ref) throws Exception {
		RespBase<Object> resp = RespBase.create();
		VcsBranchModel vcsBranchModel = repoService.createBranch(vcsId, projectId, branch, ref);
		resp.setData(vcsBranchModel);
		return resp;
	}

	@RequestMapping(value = "/createTag")
	@RequiresPermissions(value = { "urm" }, logical = AND)
	public RespBase<?> createTag(Long vcsId, Long projectId, String tag, String ref, String message, String releaseDescription)
			throws Exception {
		RespBase<Object> resp = RespBase.create();
		VcsTagModel vcsServiceTag = repoService.createTag(vcsId, projectId, tag, ref, message, releaseDescription);
		resp.setData(vcsServiceTag);
		return resp;
	}

}