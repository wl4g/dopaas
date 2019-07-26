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

import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.dao.scm.AppGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:44:00
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
    private AppGroupDao appGroupDao;

    @Override
    public List<TaskHistory> list(String groupName, String projectName, String branchName) {
        return taskDao.list(groupName, projectName, branchName);
    }

    @Override
    public List<TaskHistoryDetail> getDetailByTaskId(Integer id) {
        return taskDetailDao.getDetailByTaskId(id);
    }

    @Override
    public TaskHistory getTaskById(Integer id) {
        TaskHistory taskHistory = taskDao.selectByPrimaryKey(id);
        Project project = projectDao.selectByPrimaryKey(taskHistory.getProjectId());
        if (null != project && null != project.getAppGroupId()) {
            AppGroup appGroup = appGroupDao.getAppGroup(project.getAppGroupId());
            if (null != appGroup) {
                taskHistory.setGroupName(appGroup.getName());
            }
        }
        return taskHistory;

    }

    @Override
    @Transactional
    public TaskHistory createTask(Project project, List<AppInstance> instances, int type, int status, String branchName, String sha,
                                  Integer refId, String command, Integer tarType) {
        Assert.notNull(project, "not found project,please check che project config");
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.preInsert();
        taskHistory.setType(type);
        taskHistory.setProjectId(project.getId());
        taskHistory.setStatus(status);
        taskHistory.setBranchName(branchName);
        taskHistory.setShaGit(sha);
        taskHistory.setRefId(refId);
        taskHistory.setCommand(command);
        taskHistory.setTarType(tarType);
        taskHistory.setEnable(CiDevOpsConstants.TASK_ENABLE_STATUS);
        taskDao.insertSelective(taskHistory);
        for (AppInstance instance : instances) {
            TaskHistoryDetail taskHistoryDetail = new TaskHistoryDetail();
            taskHistoryDetail.preInsert();
            taskHistoryDetail.setTaskId(taskHistory.getId());
            taskHistoryDetail.setInstanceId(instance.getId());
            taskHistoryDetail.setStatus(CiDevOpsConstants.TASK_STATUS_CREATE);
            taskDetailDao.insertSelective(taskHistoryDetail);
        }
        return taskHistory;
    }

    @Override
    public void updateTaskStatus(int taskId, int status) {
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.preUpdate();
        taskHistory.setId(taskId);
        taskHistory.setStatus(status);
        taskDao.updateByPrimaryKeySelective(taskHistory);
    }

    @Override
    public void updateTaskStatusAndResult(int taskId, int status, String result) {
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.preUpdate();
        taskHistory.setId(taskId);
        taskHistory.setStatus(status);
        taskHistory.setResult(result);
        taskDao.updateByPrimaryKeySelective(taskHistory);
    }

    @Override
    public void updateTaskStatusAndResultAndSha(int taskId, int status, String result, String sha, String md5) {
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.preUpdate();
        taskHistory.setId(taskId);
        taskHistory.setStatus(status);
        taskHistory.setResult(result);
        taskHistory.setShaGit(sha);
        taskHistory.setShaLocal(md5);
        taskDao.updateByPrimaryKeySelective(taskHistory);
    }

    @Override
    public void updateTaskDetailStatusAndResult(int taskDetailId, int status, String result) {
        TaskHistoryDetail taskHistoryDetail = new TaskHistoryDetail();
        taskHistoryDetail.preUpdate();
        taskHistoryDetail.setId(taskDetailId);
        taskHistoryDetail.setStatus(status);
        taskHistoryDetail.setResult(result);
        taskDetailDao.updateByPrimaryKeySelective(taskHistoryDetail);
    }


}