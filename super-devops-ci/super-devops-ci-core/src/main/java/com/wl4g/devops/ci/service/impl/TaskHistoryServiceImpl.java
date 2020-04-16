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
package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskBuildCommand;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskHistoryBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskHistoryDao;
import com.wl4g.devops.dao.ci.TaskHistoryDetailDao;
import com.wl4g.devops.dao.erm.AppClusterDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.destroy.DestroySignal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_STOPING;

/**
 * @author vjay
 * @date 2019-05-17 11:44:00
 */
@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {

	@Autowired
	private TaskHistoryDao taskHistoryDao;
	@Autowired
	private TaskHistoryDetailDao taskHistoryDetailDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private AppClusterDao appClusterDao;
	@Autowired
	private TaskHistoryBuildCommandDao taskHistoryBuildCommandDao;

	@Autowired
	protected DestroableProcessManager pm;

	@Override
	public PageModel list(PageModel pm, String groupName, String projectName, String branchName, String startDate, String endDate,
			String envType) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(taskHistoryDao.list(groupName, projectName, branchName, startDate, endDate, envType));
		return pm;
	}

	@Override
	public List<TaskHistoryInstance> getDetailByTaskId(Integer id) {
		return taskHistoryDetailDao.getDetailByTaskId(id);
	}

	@Override
	public TaskHistory getById(Integer id) {
		TaskHistory taskHistory = taskHistoryDao.selectByPrimaryKey(id);
		Project project = projectDao.selectByPrimaryKey(taskHistory.getProjectId());
		if (null != project && null != project.getAppClusterId()) {
			AppCluster appCluster = appClusterDao.selectByPrimaryKey(project.getAppClusterId());
			if (null != appCluster) {
				taskHistory.setGroupName(appCluster.getName());
			}
		}
		return taskHistory;

	}

	@Override
	@Transactional
	public TaskHistory createTaskHistory(Project project, List<AppInstance> instances, int type, int status, String branchName,
			String sha, Integer refId, String buildCommand, String preCommand, String postCommand, String tarType,String branchType,
			Integer contactGroupId, List<TaskBuildCommand> taskBuildCommands, String trackId, Integer trackType, String remark,
			String envType, String annex,String parentAppHome,String assetsPath) {
		Assert.notNull(project, "not found project,please check che project config");
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.preInsert();
		taskHistory.setType(type);
		taskHistory.setProjectId(project.getId());
		taskHistory.setStatus(status);
		taskHistory.setBranchName(branchName);
		taskHistory.setShaGit(sha);
		taskHistory.setRefId(refId);
		taskHistory.setBuildCommand(buildCommand);
		taskHistory.setPreCommand(preCommand);
		taskHistory.setPostCommand(postCommand);
		taskHistory.setProviderKind(tarType);
		taskHistory.setBranchType(branchType);
		taskHistory.setEnable(CiDevOpsConstants.TASK_ENABLE_STATUS);
		taskHistory.setContactGroupId(contactGroupId);
		// 1107 add
		taskHistory.setTrackId(trackId);
		taskHistory.setTrackType(trackType);
		taskHistory.setRemark(remark);
		taskHistory.setEnvType(envType);
		taskHistory.setAnnex(annex);
		// 20200311 add
		taskHistory.setParentAppHome(parentAppHome);
		taskHistory.setAssetsPath(assetsPath);

		taskHistoryDao.insertSelective(taskHistory);
		for (AppInstance instance : instances) {
			TaskHistoryInstance taskHistoryInstance = new TaskHistoryInstance();
			taskHistoryInstance.preInsert();
			taskHistoryInstance.setTaskId(taskHistory.getId());
			taskHistoryInstance.setInstanceId(instance.getId());
			taskHistoryInstance.setStatus(CiDevOpsConstants.TASK_STATUS_CREATE);
			taskHistoryDetailDao.insertSelective(taskHistoryInstance);
		}
		for (TaskBuildCommand taskBuildCommand : taskBuildCommands) {
			taskBuildCommand.setId(null);
			taskBuildCommand.preInsert();
			taskBuildCommand.setTaskId(taskHistory.getId());
			taskHistoryBuildCommandDao.insertSelective(taskBuildCommand);
		}
		return taskHistory;
	}

	@Override
	public void updateStatus(int taskId, int status) {
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.preUpdate();
		taskHistory.setId(taskId);
		taskHistory.setStatus(status);
		taskHistoryDao.updateByPrimaryKeySelective(taskHistory);
	}

	@Override
	public void updateStatusAndResult(int taskId, int status, String result) {
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.preUpdate();
		taskHistory.setId(taskId);
		taskHistory.setStatus(status);
		// modify -- read fomr file
		// taskHistory.setResult(result);
		taskHistoryDao.updateByPrimaryKeySelective(taskHistory);
	}

	@Override
	public void updateStatusAndResultAndSha(int taskId, int status, String result, String sha, String md5) {
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.preUpdate();
		taskHistory.setId(taskId);
		taskHistory.setStatus(status);
		// modify -- read fomr file
		// taskHistory.setResult(result);
		taskHistory.setShaGit(sha);
		taskHistory.setShaLocal(md5);
		taskHistoryDao.updateByPrimaryKeySelective(taskHistory);
	}

	@Override
	public void stopByTaskHisId(Integer taskHisId) {
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.preUpdate();
		taskHistory.setId(taskHisId);
		taskHistory.setStatus(TASK_STATUS_STOPING);
		taskHistoryDao.updateByPrimaryKeySelective(taskHistory);

		// TODO timeoutMs?
		pm.destroyForComplete(new DestroySignal(String.valueOf(taskHisId), 5000L));
	}

	@Override
	public void updateCostTime(int taskId, long costTime) {
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.preUpdate();
		taskHistory.setId(taskId);
		taskHistory.setCostTime(costTime);
		taskHistoryDao.updateByPrimaryKeySelective(taskHistory);
	}

	@Override
	public void updateDetailStatus(int taskDetailId, int status) {
		TaskHistoryInstance taskHistoryInstance = new TaskHistoryInstance();
		taskHistoryInstance.preUpdate();
		taskHistoryInstance.setId(taskDetailId);
		taskHistoryInstance.setStatus(status);
		taskHistoryDetailDao.updateByPrimaryKeySelective(taskHistoryInstance);
	}

}