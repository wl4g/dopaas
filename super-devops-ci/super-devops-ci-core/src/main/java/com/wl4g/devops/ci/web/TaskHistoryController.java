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

import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.tool.common.io.FileIOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

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
	private TaskHistoryService taskHistoryService;

	@Autowired
	private PipelineManager pipe;

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
	@RequiresPermissions(value = { "ci", "ci:taskhis" }, logical = AND)
	public RespBase<?> list(PageModel pm, String groupName, String projectName, String branchName, String startDate,
			String endDate, String envType) {
		log.info("into TaskHistoryController.list prarms::" + "groupName = {} , projectName = {} , branchName = {} , pm = {} ",
				groupName, projectName, branchName, pm);
		RespBase<Object> resp = RespBase.create();
		PageModel list = taskHistoryService.list(pm, groupName, projectName, branchName, startDate, endDate, envType);
		resp.setData(list);
		return resp;
	}

	/**
	 * Detail by id
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "ci", "ci:taskhis" }, logical = AND)
	public RespBase<?> detail(Integer taskId) {
		log.info("into TaskHistoryController.detail prarms::" + "taskId = {} ", taskId);
		RespBase<Object> resp = RespBase.create();
		TaskHistory taskHistory = taskHistoryService.getById(taskId);
		List<TaskHistoryInstance> taskHistoryInstances = taskHistoryService.getDetailByTaskId(taskId);
		resp.forMap().put("group", taskHistory.getGroupName());
		resp.forMap().put("annex", taskHistory.getAnnex());
		resp.forMap().put("branch", taskHistory.getBranchName());
		resp.forMap().put("result", taskHistory.getResult());
		resp.forMap().put("taskInstances", taskHistoryInstances);
		return resp;
	}

	/**
	 * Rollback
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/rollback")
	@RequiresPermissions(value = { "ci", "ci:taskhis" }, logical = AND)
	public RespBase<?> rollback(Integer taskId) {
		log.info("into TaskHistoryController.rollback prarms::" + "taskId = {} ", taskId);
		RespBase<Object> resp = RespBase.create();
		// TODO remark???
		pipe.rollbackPipeline(new RollbackParameter(taskId, "rollback"));
		return resp;
	}

	@RequestMapping(value = "/readLog")
	@RequiresPermissions(value = { "ci", "ci:taskhis" }, logical = AND)
	public RespBase<?> readLog(Integer taskHisId, Long startPos, Integer size) {
		RespBase<Object> resp = RespBase.create();
		FileIOUtils.ReadResult readResult = pipe.logfile(taskHisId, startPos, size);
		resp.forMap().put("data", readResult);
		return resp;
	}

	@RequestMapping(value = "/readDetailLog")
	@RequiresPermissions(value = { "ci", "ci:taskhis" }, logical = AND)
	public RespBase<?> readDetailLog(Integer taskHisId, Integer instanceId, Long startPos, Integer size) {
		RespBase<Object> resp = RespBase.create();
		FileIOUtils.ReadResult readResult = pipe.logDetailFile(taskHisId, instanceId, startPos, size);
		resp.forMap().put("data", readResult);
		return resp;
	}

	@RequestMapping(value = "/stopTask")
	@RequiresPermissions(value = { "ci", "ci:taskhis" }, logical = AND)
	public RespBase<?> create(Integer taskHisId) {
		RespBase<Object> resp = RespBase.create();
		taskHistoryService.stopByTaskHisId(taskHisId);
		return resp;
	}

}