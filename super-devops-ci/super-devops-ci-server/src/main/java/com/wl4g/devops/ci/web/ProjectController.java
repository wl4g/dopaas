/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.ci.vcs.git.GitlabV4VcsOperator;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.ci.ProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS__UNLOCK;

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

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private GitlabV4VcsOperator gitlabTemplate;

	/**
	 * list
	 * 
	 * @param groupName
	 * @param projectName
	 * @param customPage
	 * @return
	 */
	@RequestMapping(value = "/list")
	public RespBase<?> list(String groupName, String projectName, CustomPage customPage) {
		log.info("into ProjectController.list prarms::" + "groupName = {} , projectName = {} , customPage = {} ", groupName,
				projectName, customPage);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<Project> page = PageHelper.startPage(pageNum, pageSize, true);
		List<Project> list = projectService.list(groupName, projectName);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	/**
	 * save
	 * 
	 * @param project
	 * @return
	 */
	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody Project project) {
		log.info("into ProjectController.save prarms::" + "project = {} ", project);
		RespBase<Object> resp = RespBase.create();
		if (null != project.getId() && project.getId() > 0) {
			project.preUpdate();
			projectService.update(project);
		} else {
			project.preInsert();
			project.setDelFlag(DEL_FLAG_NORMAL);
			project.setEnable(ENABLED);
			project.setLockStatus(TASK_LOCK_STATUS__UNLOCK);
			projectService.insert(project);
		}
		return resp;
	}

	/**
	 * Get detail by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into ProjectController.detail prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(id, "id can not be null");
		Project project = projectService.selectByPrimaryKey(id);
		resp.getData().put("project", project);
		return resp;
	}

	/**
	 * delete by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del")
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
	@RequestMapping(value = "/all")
	public RespBase<?> all() {
		RespBase<Object> resp = RespBase.create();
		List<Project> list = projectService.list(null, null);
		resp.getData().put("list", list);
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
		projectService.updateLockStatus(id, TASK_LOCK_STATUS__UNLOCK);
		return resp;
	}

	/**
	 * Get a list of branches from GITLAB so that the front end can be displayed
	 * drop-down.
	 * 
	 * @param appClusterId
	 * @param tarOrBranch
	 * @return
	 */
	@RequestMapping(value = "/getBranchs")
	public RespBase<?> getBranchs(Integer appClusterId, Integer tarOrBranch) {
		log.debug("into ProjectController.getBranchs prarms::" + "appClusterId = {} , tarOrBranch = {} ", appClusterId,
				tarOrBranch);
		RespBase<Object> resp = RespBase.create();
		Assert.notNull(appClusterId, "id can not be null");

		Project project = projectDao.getByAppClusterId(appClusterId);
		Assert.notNull(project, "not found project ,please check you project config");
		String url = project.getGitUrl();

		// Find remote projectIds.
		String projectName = extProjectName(url);
		Integer gitlabProjectId = gitlabTemplate.findRemoteProjectId(projectName);
		Assert.notNull(gitlabProjectId, String.format("No found projectId of name: %s", projectName));

		if (tarOrBranch != null && tarOrBranch == 2) { // tag
			List<String> branchNames = gitlabTemplate.getRemoteTags(gitlabProjectId);
			resp.getData().put("branchNames", branchNames);
		}
		// Branch
		else {
			List<String> branchNames = gitlabTemplate.getRemoteBranchNames(gitlabProjectId);
			resp.getData().put("branchNames", branchNames);
		}
		return resp;
	}

	/**
	 * Tool for this class : get Git Project Name from Url
	 * 
	 * @param url
	 * @return
	 */
	private static String extProjectName(String url) {
		int index = url.lastIndexOf("/");
		url = url.substring(index + 1);
		index = url.lastIndexOf(".");
		url = url.substring(0, index);
		return url;
	}

}