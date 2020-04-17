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

import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskBuildCommand;
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

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * Task controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

	@Autowired
	private PipelineManager pipeliner;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FlowManager flowManager;

	/**
	 * Page List
	 * 
	 * @param customPage
	 * @param id
	 * @param taskName
	 * @param groupName
	 * @param branchName
	 * @param providerKind
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> list(PageModel pm, Integer id, String taskName, String groupName, String branchName, String providerKind,
			String startDate, String endDate, String envType) {
		RespBase<Object> resp = RespBase.create();
		PageModel list = taskService.list(pm, id, taskName, groupName, branchName, providerKind, startDate, endDate, envType);
		resp.setData(list);
		return resp;
	}

	/**
	 * Save
	 * 
	 * @param task
	 * @param instance
	 * @return
	 */
	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> save(@RequestBody Task task) {
		log.info("into TaskController.save prarms::" + "task = {} ", task);
		Assert.notNull(task, "task can not be null");
		Assert.notEmpty(task.getInstance(), "instances can not be empty");
		checkTask(task);
		RespBase<Object> resp = RespBase.create();
		taskService.save(task);
		return resp;
	}

	/**
	 * Detail by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> detail(Integer id) {
		log.info("into TaskController.detail prarms::" + "id = {} ", id);
		Assert.notNull(id, "id can not be null");
		RespBase<Object> resp = RespBase.create();
		resp.setData(taskService.detail(id));
		return resp;
	}

	/**
	 * Delete by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> del(Integer id) {
		Assert.notNull(id, "id can not be null");
		RespBase<Object> resp = RespBase.create();
		taskService.delete(id);
		return resp;
	}

	/**
	 * Check the form
	 * 
	 * @param task
	 */
	private void checkTask(Task task) {
		Assert.hasText(task.getTaskName(), "taskName is null");
		Assert.notNull(task.getAppClusterId(), "appClusterId is null");
		Assert.notNull(task.getProviderKind(), "packType is null");
		Assert.hasText(task.getBranchName(), "branchName is null");
	}

	/**
	 * Get List By appClusterId
	 * 
	 * @param appClusterId
	 */
	@RequestMapping(value = "/getListByAppClusterId")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> getListByAppClusterId(Integer appClusterId) {
		Assert.notNull(appClusterId, "appClusterId can not be null");
		RespBase<Object> resp = RespBase.create();
		List<Task> tasks = taskService.getListByAppClusterId(appClusterId);
		resp.setData(tasks);
		return resp;
	}

	/**
	 * create Task History and run Task
	 * 
	 * @param taskId
	 */
	@RequestMapping(value = "/create")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> create(Integer taskId, String trackId, Integer trackType, String remark, String annex) {
		RespBase<Object> resp = RespBase.create();
		PipelineModel pipelineModel = flowManager.buildPipeline(taskId);
		pipeliner.runPipeline(new NewParameter(taskId, remark, trackId, trackType, annex),pipelineModel);
		return resp;
	}

	@RequestMapping(value = "/getDependencys")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> getDependencys(Integer appClusterId, Integer taskId, Integer tagOrBranch) {
		Assert.notNull(appClusterId, "appClusterId is null");
		RespBase<Object> resp = RespBase.create();
		List<TaskBuildCommand> taskBuildCommands = taskService.getDependency(appClusterId, taskId, tagOrBranch);
		resp.forMap().put("list", taskBuildCommands);
		return resp;
	}

	@RequestMapping(value = "/getForSelect")
	@RequiresPermissions(value = { "ci", "ci:task" }, logical = AND)
	public RespBase<?> getForSelect() {
		RespBase<Object> resp = RespBase.create();
		List<Task> list = taskService.getForSelect();
		resp.setData(list);
		return resp;

	}

}