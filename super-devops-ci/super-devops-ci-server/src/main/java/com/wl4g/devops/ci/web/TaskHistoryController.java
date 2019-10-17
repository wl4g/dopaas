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
import com.wl4g.devops.ci.core.Pipeline;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Task History controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/taskHis")
public class TaskHistoryController extends BaseController {

	@Autowired
	private Pipeline pipelineCoreProcessor;

	@Autowired
	private TaskHistoryService taskHistoryService;

	/**
	 * List
	 * 
	 * @param groupName
	 * @param projectName
	 * @param branchName
	 * @param customPage
	 * @return
	 */
	@RequestMapping(value = "/list")
	public RespBase<?> list(String groupName, String projectName, String branchName, CustomPage customPage) {
		log.info(
				"into TaskHistoryController.list prarms::"
						+ "groupName = {} , projectName = {} , branchName = {} , customPage = {} ",
				groupName, projectName, branchName, customPage);
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
		Page<TaskHistory> page = PageHelper.startPage(pageNum, pageSize, true);
		List<TaskHistory> list = taskHistoryService.list(groupName, projectName, branchName);
		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	/**
	 * Detail by id
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer taskId) {
		log.info("into TaskHistoryController.detail prarms::" + "taskId = {} ", taskId);
		RespBase<Object> resp = RespBase.create();
		TaskHistory taskHistory = taskHistoryService.getById(taskId);
		List<TaskHistoryDetail> taskHistoryDetails = taskHistoryService.getDetailByTaskId(taskId);
		resp.getData().put("group", taskHistory.getGroupName());
		resp.getData().put("branch", taskHistory.getBranchName());
		resp.getData().put("result", taskHistory.getResult());
		resp.getData().put("taskDetails", taskHistoryDetails);
		return resp;
	}

	/**
	 * Rollback
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/rollback")
	public RespBase<?> rollback(Integer taskId) {
		log.info("into TaskHistoryController.rollback prarms::" + "taskId = {} ", taskId);
		RespBase<Object> resp = RespBase.create();
		pipelineCoreProcessor.createRollbackTask(taskId);
		return resp;
	}


	@RequestMapping(value = "/readLog")
	public RespBase<?> readLog(Integer taskHisId,Integer index,Integer size) {
		RespBase<Object> resp = RespBase.create();
		List<String> strings = pipelineCoreProcessor.readLog(taskHisId, index, size);
		resp.getData().put("data",strings);
		return resp;
	}

}