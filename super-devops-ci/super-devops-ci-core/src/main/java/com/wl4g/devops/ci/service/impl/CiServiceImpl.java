/*
 * Copyright 2015 the original author or authors.
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

import com.wl4g.devops.ci.config.DevConfig;
import com.wl4g.devops.ci.provider.BasedDeployProvider;
import com.wl4g.devops.ci.provider.MvnAssembleTarDeployProvider;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.dao.ci.TriggerDetailDao;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.shell.utils.ShellConsoleHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-05-16 14:50:00
 */
@Service
public class CiServiceImpl implements CiService {

	@Autowired
	private DevConfig devConfig;

	@Autowired
	private AppGroupDao appGroupDao;

	@Autowired
	private TriggerDao triggerDao;

	@Autowired
	private TriggerDetailDao triggerDetailDao;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private TaskService taskService;

	@Autowired
	private DependencyService dependencyService;

	@Override
	public List<AppGroup> grouplist() {
		return appGroupDao.grouplist();
	}

	@Override
	public List<Environment> environmentlist(String groupId) {
		return appGroupDao.environmentlist(groupId);
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}

	@Override
	public Trigger getTriggerByProjectAndBranch(Integer projectId, String branchName) {
		Map<String, Object> map = new HashMap<>();
		map.put("projectId", projectId);
		map.put("branchName", branchName);
		Trigger trigger = triggerDao.getTriggerByProjectAndBranch(map);
		if (null == trigger) {
			return null;
		}
		List<TriggerDetail> triggerDetails = triggerDetailDao.getDetailByTriggerId(trigger.getId());
		if (null == triggerDetails) {
			return null;
		}
		trigger.setTriggerDetails(triggerDetails);
		return trigger;
	}

	@Override
	public void createTask(String appGroupName, String branchName, List<String> instanceIds) {
		AppGroup appGroup = appGroupDao.getAppGroupByName(appGroupName);
		Assert.notNull(appGroup, "not found this app");
		Project project = projectDao.getByAppGroupId(appGroup.getId());
		Assert.notNull(appGroup, "not found this app");
		Assert.notEmpty(instanceIds, "instanceIds can not be null");
		List<AppInstance> instances = new ArrayList<>();
		for (String instanceId : instanceIds) {
			AppInstance instance = appGroupDao.getAppInstance(instanceId);
			instances.add(instance);
		}
		Task task = taskService.createTask(project, instances, CiDevOpsConstants.TASK_TYPE_TRIGGER,
				CiDevOpsConstants.TASK_STATUS_CREATE, branchName, null, null, null, CiDevOpsConstants.TAR_TYPE_TAR);
		BasedDeployProvider provider = getDeployProvider(task);

		try {
			//// update task--running
			taskService.updateTaskStatus(task.getId(), CiDevOpsConstants.TASK_STATUS_RUNNING);
			// TODO exec
			provider.exec();
			// update task--success
			taskService.updateTaskStatus(task.getId(), CiDevOpsConstants.TASK_STATUS_SUCCESS);
		} catch (Exception e) {
			// update task--fail
			taskService.updateTaskStatus(task.getId(), CiDevOpsConstants.TASK_STATUS_FAIL);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void hook(String projectName, String branchName, String url) {
		// TODO just for test
		// projectName = "safecloud-devops-datachecker";
		Project project = projectDao.getByProjectName(projectName);
		if (null == project) {
			return;
		}
		// Assert.notNull(project,"project not found, please config first");
		// AppGroup appGroup =
		// appGroupDao.getAppGroup(project.getAppGroupId().toString());
		// String alias = appGroup.getName();
		Trigger trigger = getTriggerByProjectAndBranch(project.getId(), branchName);
		if (null == trigger) {
			return;
		}
		// Assert.notNull(trigger,"trigger not found, please config first");

		List<AppInstance> instances = new ArrayList<>();
		for (TriggerDetail triggerDetail : trigger.getTriggerDetails()) {
			AppInstance instance = appGroupDao.getAppInstance(triggerDetail.getInstanceId().toString());
			instances.add(instance);
		}
		Assert.notEmpty(instances, "instances not found, please config first");

		// TODO get sha
		String sha = null;

		// Output stream message
		ShellConsoleHolder.printfQuietly("task begin");
		Task task = taskService.createTask(project, instances, CiDevOpsConstants.TASK_TYPE_TRIGGER,
				CiDevOpsConstants.TASK_STATUS_CREATE, branchName, sha, null, null, trigger.getTarType());
		BasedDeployProvider provider = getDeployProvider(task);

		try {
			//// update task--running
			taskService.updateTaskStatus(task.getId(), CiDevOpsConstants.TASK_STATUS_RUNNING);
			// TODO exec
			provider.exec();
			// update task--success
			taskService.updateTaskStatus(task.getId(), CiDevOpsConstants.TASK_STATUS_SUCCESS);
		} catch (Exception e) {
			// update task--fail
			taskService.updateTaskStatus(task.getId(), CiDevOpsConstants.TASK_STATUS_FAIL);
			e.printStackTrace();
		}

	}

	private BasedDeployProvider getDeployProvider(int projectId, int tarType, String path, String url, String branch,
			String alias, String tarPath, List<AppInstance> instances, List<TaskDetail> taskDetails) {
		switch (tarType) {
		case CiDevOpsConstants.TAR_TYPE_TAR:
			return new MvnAssembleTarDeployProvider(dependencyService, devConfig, projectId, path, url, branch, alias, tarPath,
					instances, taskDetails);
		case CiDevOpsConstants.TAR_TYPE_JAR:
			// return new JarSubject(path, url, branch,
			// alias,tarPath,instances,taskDetails);
		case CiDevOpsConstants.TAR_TYPE_OTHER:
			// return new OtherSubject();
		default:
			throw new RuntimeException("unsuppost type:" + tarType);
		}
	}

	public BasedDeployProvider getDeployProvider(Task task) {
		Assert.notNull(task, "task can not be null");
		Project project = projectDao.selectByPrimaryKey(task.getProjectId());
		Assert.notNull(project, "project can not be null");
		AppGroup appGroup = appGroupDao.getAppGroup(project.getAppGroupId().toString());
		Assert.notNull(appGroup, "appGroup can not be null");

		List<TaskDetail> taskDetails = taskService.getDetailByTaskId(task.getId());
		Assert.notNull(taskDetails, "taskDetails can not be null");
		List<AppInstance> instances = new ArrayList<>();
		for (TaskDetail taskDetail : taskDetails) {
			AppInstance instance = appGroupDao.getAppInstance(taskDetail.getInstanceId().toString());
			instances.add(instance);
		}
		return getDeployProvider(task.getProjectId(), task.getTarType(),
				devConfig.getGitBasePath() + "/" + project.getProjectName(), project.getGitUrl(), task.getBranchName(),
				appGroup.getName(), project.getTarPath(), instances, taskDetails);
	}

}