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
package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.dao.scm.AppClusterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;

/**
 * Task CRUD service
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2019年10月17日
 * @since
 */
@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskDao taskDao;
	@Autowired
	private TaskDetailDao taskDetailDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private DependencyService dependencyService;
	@Autowired
	private TaskBuildCommandDao taskBuildCommandDao;
	@Autowired
	private AppClusterDao appClusterDao;

	@Override
	@Transactional
	public Task save(Task task, Integer[] instanceIds, List<TaskBuildCommand> taskBuildCommands) {
		// check task repeat
		Assert.state(!isRepeat(task, instanceIds), "trigger deploy this instance is Repeat,please check");
		Assert.notEmpty(instanceIds, "instance can not be null");
		Assert.notNull(task, "task can not be null");
		Project project = projectDao.getByAppClusterId(task.getAppClusterId());
		Assert.notNull(project, "Not found project , Please check you project config");
		// TODO filter command

		task.setProjectId(project.getId());
		if (null != task.getId() && task.getId() > 0) {
			task.preUpdate();
			task = update(task, instanceIds, taskBuildCommands);
		} else {
			task = insert(task, instanceIds, taskBuildCommands);
		}
		return task;
	}

	private Task insert(Task task, Integer[] instanceIds, List<TaskBuildCommand> taskBuildCommands) {
		task.preInsert();
		task.setDelFlag(DEL_FLAG_NORMAL);
		taskDao.insertSelective(task);
		int taskId = task.getId();
		List<TaskDetail> taskDetails = new ArrayList<>();
		for (Integer instanceId : instanceIds) {
			TaskDetail taskDetail = new TaskDetail();
			taskDetail.setTaskId(taskId);
			taskDetail.setInstanceId(instanceId);
			taskDetailDao.insertSelective(taskDetail);
			taskDetails.add(taskDetail);
		}
		for (TaskBuildCommand taskBuildCommand : taskBuildCommands) {
			taskBuildCommandDao.insertSelective(taskBuildCommand);
		}
		task.setTaskDetails(taskDetails);
		return task;
	}

	private Task update(Task task, Integer[] instanceIds, List<TaskBuildCommand> taskBuildCommands) {
		task.preUpdate();
		task.preUpdate();
		taskDao.updateByPrimaryKeySelective(task);
		List<TaskDetail> taskDetails = new ArrayList<>();
		taskDetailDao.deleteByTaskId(task.getId());
		for (Integer instanceId : instanceIds) {
			TaskDetail taskDetail = new TaskDetail();
			taskDetail.setTaskId(task.getId());
			taskDetail.setInstanceId(instanceId);
			taskDetailDao.insertSelective(taskDetail);
			taskDetails.add(taskDetail);
		}
		taskBuildCommandDao.deleteByTaskId(task.getId());
		for (TaskBuildCommand taskBuildCommand : taskBuildCommands) {
			taskBuildCommandDao.insertSelective(taskBuildCommand);
		}
		task.setTaskDetails(taskDetails);
		return task;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> detail(Integer id) {
		Assert.notNull(id, "id can not be null");
		// Task.
		Map data = new HashMap();
		Task task = getTaskDetailById(id);
		data.put("task", task);
		// Environment.
		for (TaskDetail taskDetail : task.getTaskDetails()) {
			Integer instanceId = taskDetail.getInstanceId();
			AppInstance instance = appClusterDao.getAppInstance(instanceId.toString());
			if (instance != null && instance.getEnvId() != null) {
				data.put("envId", instance.getEnvId());
				break;
			}
		}
		// Instances.
		Integer[] instances = new Integer[task.getTaskDetails().size()];
		for (int i = 0; i < task.getTaskDetails().size(); i++) {
			instances[i] = task.getTaskDetails().get(i).getInstanceId();
		}
		data.put("instances", instances);
		// Commands.
		data.put("taskBuildCommands", taskBuildCommandDao.selectByTaskId(id));
		return data;
	}

	/**
	 * check task repeat
	 *
	 * @param task
	 * @param instanceIds
	 * @return
	 */
	private boolean isRepeat(Task task, Integer[] instanceIds) {
		List<TaskDetail> taskDetails = taskDetailDao.getUsedInstance(task.getAppClusterId(), task.getId());
		for (TaskDetail taskDetail : taskDetails) {
			if (Arrays.asList(instanceIds).contains(taskDetail.getInstanceId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional
	public int delete(Integer taskId) {
		taskDetailDao.deleteByTaskId(taskId);
		return taskDao.deleteByPrimaryKey(taskId);
	}

	@Override
	public Task getTaskDetailById(Integer taskId) {
		Assert.notNull(taskId, "taskId is null");
		Task task = taskDao.selectByPrimaryKey(taskId);
		Assert.notNull(task, "not found task");
		List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(taskId);
		task.setTaskDetails(taskDetails);
		return task;
	}

	public List<TaskBuildCommand> getDependency(Integer clustomId) {
		Project project = projectDao.getByAppClusterId(clustomId);
		LinkedHashSet<Dependency> dependencys = dependencyService.getHierarchyDependencys(project.getId(), null);
		List<TaskBuildCommand> taskBuildCommands = new ArrayList<>();
		int i = 1;
		for (Dependency dependency : dependencys) {
			TaskBuildCommand taskBuildCommand = new TaskBuildCommand();
			taskBuildCommand.setProjectId(dependency.getDependentId());
			taskBuildCommand.setSort(i);
			i++;
			taskBuildCommands.add(taskBuildCommand);
		}
		return taskBuildCommands;
	}

}