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
package com.wl4g.devops.rest.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.*;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * CI/CD controller
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/task")
public class TaskController {

	@Autowired
	private CiService ciService;

	@Autowired
	private TaskService taskService;


	/*@RequestMapping(value = "/grouplist")
	public RespBase<?> grouplist() {
		RespBase<List<AppGroup>> resp = RespBase.create();
		resp.getData().put("appGroups", ciService.grouplist());
		return resp;
	}

	@RequestMapping(value = "/environmentlist")
	public RespBase<?> environmentlist(String groupId) {
		RespBase<List<Environment>> resp = RespBase.create();
		List<Environment> environments = ciService.environmentlist(groupId);
		resp.getData().put("environments", environments);
		return resp;
	}

	@RequestMapping(value = "/instancelist")
	public RespBase<?> instancelist(AppInstance appInstance) {
		RespBase<List<AppInstance>> resp = RespBase.create();
		List<AppInstance> appInstances = ciService.instancelist(appInstance);
		resp.getData().put("appInstances", appInstances);
		return resp;
	}*/

	@RequestMapping(value = "/list")
	public RespBase<?> list(String groupName,String projectName,String branchName ,CustomPage customPage) {
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
		Page<ConfigVersionList> page = PageHelper.startPage(pageNum, pageSize, true);
		List<Task> list = taskService.list(groupName,projectName,branchName);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/create")
	public RespBase<?> create(Integer appGroupId,String branchName,Integer[] instances){
		RespBase<Object> resp = RespBase.create();
		List<String> instanceStrs = new ArrayList<>();
		for(Integer instance : instances){
			instanceStrs.add(String.valueOf(instance));
		}
		ciService.createTask(appGroupId, branchName, instanceStrs,CiDevOpsConstants.TASK_TYPE_MANUAL);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer taskId){
		RespBase<Object> resp = RespBase.create();
		Task task = taskService.getTaskById(taskId);
		List<TaskDetail> taskDetails = taskService.getDetailByTaskId(taskId);
		resp.getData().put("group",task.getGroupName());
		resp.getData().put("branch",task.getBranchName());
		resp.getData().put("result",task.getResult());
		resp.getData().put("taskDetails",taskDetails);
		return resp;
	}

	@RequestMapping(value = "/rollback")
	public RespBase<?> rollback(Integer taskId){
		RespBase<Object> resp = RespBase.create();
		Task task = taskService.getTaskById(taskId);
		ciService.rollback(taskId);
		return resp;
	}




}