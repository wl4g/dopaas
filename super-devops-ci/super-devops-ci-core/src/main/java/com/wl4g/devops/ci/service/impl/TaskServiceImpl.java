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
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.tool.common.lang.DateUtils2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

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
	private ProjectService projectService;

	@Override
	@Transactional
	public Task save(Task task) {
		Assert.notEmpty(task.getInstance(), "instance can not be null");
		Assert.notNull(task, "task can not be null");
		Integer appClusterId = task.getAppClusterId();
		Assert.notNull(appClusterId, "appClusterId can not be null");
		Project project = projectDao.getByAppClusterId(appClusterId);
		Assert.notNull(project, "Not found project , Please check you project config");
		task.setProjectId(project.getId());

		if (null != task.getId() && task.getId() > 0) {
			task.preUpdate();
			task = update(task, task.getInstance(), task.getTaskBuildCommands());
		} else {
			task = insert(task, task.getInstance(), task.getTaskBuildCommands());
		}
		return task;
	}

	@Override
	public PageModel list(PageModel pm, Integer id, String taskName, String groupName, String branchName, String providerKind,
			String startDate, String endDate, String envType) {
		String endDateStr = null;
		if (StringUtils.isNotBlank(endDate)) {
			endDateStr = DateUtils2.formatDate(DateUtils2.addDays(DateUtils2.parseDate(endDate), 1));
		}
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(taskDao.list(id, taskName, groupName, branchName, providerKind, startDate, endDateStr, envType));
		return pm;
	}

	private Task insert(Task task, Integer[] instanceIds, List<TaskBuildCommand> taskBuildCommands) {
		task.preInsert();
		task.setDelFlag(DEL_FLAG_NORMAL);
		taskDao.insertSelective(task);
		int taskId = task.getId();
		List<TaskInstance> taskInstances = new ArrayList<>();
		for (Integer instanceId : instanceIds) {
			TaskInstance taskInstance = new TaskInstance();
			taskInstance.preInsert();
			taskInstance.setTaskId(taskId);
			taskInstance.setInstanceId(instanceId);
			taskDetailDao.insertSelective(taskInstance);
			taskInstances.add(taskInstance);
		}
		for (TaskBuildCommand taskBuildCommand : taskBuildCommands) {
			taskBuildCommand.setTaskId(taskId);
			taskBuildCommand.preInsert();
			taskBuildCommandDao.insertSelective(taskBuildCommand);
		}
		task.setTaskInstances(taskInstances);
		return task;
	}

	private Task update(Task task, Integer[] instanceIds, List<TaskBuildCommand> taskBuildCommands) {
		task.preUpdate();
		task.preUpdate();
		taskDao.updateByPrimaryKeySelective(task);
		List<TaskInstance> taskInstances = new ArrayList<>();
		taskDetailDao.deleteByTaskId(task.getId());
		for (Integer instanceId : instanceIds) {
			TaskInstance taskInstance = new TaskInstance();
			taskInstance.preInsert();
			taskInstance.setTaskId(task.getId());
			taskInstance.setInstanceId(instanceId);
			taskDetailDao.insertSelective(taskInstance);
			taskInstances.add(taskInstance);
		}
		taskBuildCommandDao.deleteByTaskId(task.getId());
		for (TaskBuildCommand taskBuildCommand : taskBuildCommands) {
			taskBuildCommand.setTaskId(task.getId());
			taskBuildCommand.preInsert();
			taskBuildCommandDao.insertSelective(taskBuildCommand);
		}
		task.setTaskInstances(taskInstances);
		return task;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> detail(Integer id) {
		Assert.notNull(id, "id can not be null");
		// Task.
		Map data = new HashMap();
		Task task = getTaskDetailById(id);
		data.put("task", task);
		// Instances.
		Integer[] instances = new Integer[task.getTaskInstances().size()];
		for (int i = 0; i < task.getTaskInstances().size(); i++) {
			instances[i] = task.getTaskInstances().get(i).getInstanceId();
		}
		data.put("instances", instances);
		// Commands.
		List<TaskBuildCommand> taskBuildCommands = taskBuildCommandDao.selectByTaskId(id);
		for (TaskBuildCommand taskBuildCommand : taskBuildCommands) {
			Project project = projectDao.selectByPrimaryKey(taskBuildCommand.getProjectId());
			if (project == null) {
				continue;
			}
			taskBuildCommand.setProjectName(project.getProjectName());
		}
		data.put("taskBuildCommands", taskBuildCommands);
		return data;
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
		List<TaskInstance> taskInstances = taskDetailDao.selectByTaskId(taskId);
		task.setTaskInstances(taskInstances);
		return task;
	}

	@Override
	public List<TaskBuildCommand> getDependency(Integer appClusterId, Integer taskId, Integer tagOrBranch) {
		Project project = projectDao.getByAppClusterId(appClusterId);
		if (project == null) {
			return Collections.emptyList();
		}

		List<TaskBuildCommand> oldTaskBuildCommands = taskBuildCommandDao.selectByTaskId(taskId);

		LinkedHashSet<Dependency> dependencys = dependencyService.getHierarchyDependencys(project.getId(), null);
		List<TaskBuildCommand> taskBuildCommands = new ArrayList<>();
		int i = 1;
		for (Dependency dependency : dependencys) {

			TaskBuildCommand taskBuildCommand = getTaskBuildCommands(oldTaskBuildCommands, dependency.getDependentId());
			if(isNull(taskBuildCommand)){
				taskBuildCommand = new TaskBuildCommand();
			}

			Project project1 = projectDao.selectByPrimaryKey(dependency.getDependentId());
			if (project1 == null) {
				continue;
			}

			taskBuildCommand.setProjectId(dependency.getDependentId());
			taskBuildCommand.setProjectName(project1.getProjectName());
			taskBuildCommand.setSort(i);
			i++;

			List<String> branchs = projectService.getBranchsByProjectId(taskBuildCommand.getProjectId(), tagOrBranch);
			taskBuildCommand.setBranchs(branchs);

			taskBuildCommands.add(taskBuildCommand);
		}
		return taskBuildCommands;
	}

	private TaskBuildCommand getTaskBuildCommands(List<TaskBuildCommand> taskBuildCommands,Integer projectId){
		if(isEmpty(taskBuildCommands) || isNull(projectId)){
			return null;
		}
		for(TaskBuildCommand taskBuildCommand : taskBuildCommands){
			if(taskBuildCommand.getProjectId().equals(projectId)){
				return taskBuildCommand;
			}
		}
		return null;
	}


	@Override
	public List<Task> getListByAppClusterId(Integer appClusterId) {
		List<Task> tasks = taskDao.selectByAppClusterId(appClusterId);
		return tasks;
	}

	@Override
	public List<Task> getForSelect() {
		return taskDao.list(null, null, null, null, null, null, null, null);
	}

}