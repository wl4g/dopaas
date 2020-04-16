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
package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.ci.vcs.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS_UNLOCK;
import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * CICD projects controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

	@Autowired
	private ProjectService projectService;

	/**
	 * list
	 * 
	 * @param groupName
	 * @param projectName
	 * @param customPage
	 * @return
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> list(String groupName, String projectName, PageModel pm) {
		if (log.isInfoEnabled()) {
			log.info("Query projects for groupName: {}, projectName: {}, {} ", groupName, projectName, pm);
		}
		RespBase<Object> resp = RespBase.create();
		projectService.list(pm, groupName, projectName);
		resp.setData(pm);
		return resp;
	}

	/**
	 * save
	 * 
	 * @param project
	 * @return
	 */
	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> save(@RequestBody Project project) {
		log.info("into ProjectController.save prarms::" + "project = {} ", project);
		RespBase<Object> resp = RespBase.create();
		projectService.save(project);
		return resp;
	}

	/**
	 * Get detail by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> detail(Integer id) {
		log.info("into ProjectController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(id, "id can not be null");
		Project project = projectService.selectByPrimaryKey(id);
		resp.forMap().put("project", project);
		return resp;
	}

	/**
	 * delete by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> del(Integer id) {
		log.info("into ProjectController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(id, "id can not be null");
		projectService.deleteById(id);
		return resp;
	}

	/**
	 * 用于下拉框
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getBySelect")
	public RespBase<?> getBySelect(Integer isBoot) {
		RespBase<Object> resp = RespBase.create();
		List<Project> list = projectService.getBySelect(isBoot);
		resp.forMap().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/getByAppClusterId")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> getByAppClusterId(Integer appClusterId) {
		log.info("ProjectController.detail prarms::" + "id = {} ", appClusterId);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(appClusterId, "appClusterId can not be null");
		Project project = projectService.getByAppClusterId(appClusterId);
		resp.setData(project);
		return resp;
	}

	/**
	 * The build task in execution locks the project, automatically unlocks in
	 * normal situations, and unlocks in exceptional situations with this
	 * interface.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/unlock")
	public RespBase<?> unlock(Integer id) {
		log.info("into ProjectController.unlock prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(id, "id can not be null");
		projectService.updateLockStatus(id, TASK_LOCK_STATUS_UNLOCK);
		return resp;
	}

	@RequestMapping(value = "/vcsProjects")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> searchVcsProjects(Integer vcsId, String projectName) {
		RespBase<Object> resp = RespBase.create();
		List<CompositeBasicVcsProjectModel> remoteProjects = projectService.vcsProjects(vcsId, projectName);
		resp.setData(remoteProjects);
		return resp;
	}

	/**
	 * Get a list of branches from GITLAB so that the front end can be displayed
	 * drop-down.
	 * 
	 * @param appClusterId
	 * @param tagOrBranch
	 * @return
	 */
	@RequestMapping(value = "/getBranchs")
	@RequiresPermissions(value = { "ci", "ci:project" }, logical = AND)
	public RespBase<?> getBranchs(Integer appClusterId, Integer tagOrBranch) {
		RespBase<Object> resp = RespBase.create();
		List<String> branchs = projectService.getBranchs(appClusterId, tagOrBranch);
		resp.forMap().put("branchNames", branchs);
		return resp;
	}

}