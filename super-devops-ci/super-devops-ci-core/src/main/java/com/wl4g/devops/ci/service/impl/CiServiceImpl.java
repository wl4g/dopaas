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

import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.provider.BasedDeployProvider;
import com.wl4g.devops.ci.provider.DockerBuildDeployProvider;
import com.wl4g.devops.ci.provider.MvnAssembleTarDeployProvider;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.scm.AppGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-16 14:50:00
 */
@Service
public class CiServiceImpl implements CiService {

    @Autowired
    private DeployProperties config;

    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private TriggerDao triggerDao;

    @Autowired
    private TriggerDetailDao triggerDetailDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TaskHistoryService taskHistoryService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskDetailDao taskDetailDao;

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
    public Trigger getTriggerByAppGroupIdAndBranch(Integer appGroupId, String branchName) {
        Trigger trigger = triggerDao.getTriggerByAppGroupIdAndBranch(appGroupId,branchName);
        if (null == trigger) {
            return null;
        }
        return trigger;
    }


    @Override
    public void createTask(Integer appGroupId, String branchName, List<String> instanceIds, int type,int tarType) {
        Assert.notNull(appGroupId, "groupId is null");
        AppGroup appGroup = appGroupDao.getAppGroup(appGroupId);
        createTask(appGroup, branchName, instanceIds, type,tarType);
    }

    @Override
    public void createTask(String appGroupName, String branchName, List<String> instanceIds, int type,int tarType) {
        AppGroup appGroup = appGroupDao.getAppGroupByName(appGroupName);
        createTask(appGroup, branchName, instanceIds, type,tarType);
    }

    private void createTask(AppGroup appGroup, String branchName, List<String> instanceIds, int type,int tarType) {
        Assert.notNull(appGroup, "not found this app");
        Project project = projectDao.getByAppGroupId(appGroup.getId());
        Assert.notEmpty(instanceIds, "instanceIds find empty list,Please check the instanceId");
        List<AppInstance> instances = new ArrayList<>();
        for (String instanceId : instanceIds) {
            AppInstance instance = appGroupDao.getAppInstance(instanceId);
            instances.add(instance);
        }
        TaskHistory taskHistory = taskHistoryService.createTaskHistory(project, instances, type,
                CiDevOpsConstants.TASK_STATUS_CREATE, branchName, null, null, null, tarType);
        BasedDeployProvider provider = getDeployProvider(taskHistory);
        //execute
        execute(taskHistory.getId(), provider);
    }

    public void hook(String projectName, String branchName, String url) {
        // just for test
        // projectName = "safecloud-devops-datachecker";
        Project project = projectDao.getByProjectName(projectName);
        if (null == project) {
            return;
        }
        // Assert.notNull(project,"project not found, please config first");
        // AppGroup appGroup =
        // appGroupDao.getAppGroup(project.getAppGroupId().toString());
        // String alias = appGroup.getName();
        Trigger trigger = getTriggerByAppGroupIdAndBranch(project.getAppGroupId(), branchName);
        if (null == trigger) {
            return;
        }
        // Assert.notNull(trigger,"trigger not found, please config first");

        List<AppInstance> instances = new ArrayList<>();
        Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
        Assert.notNull(task,"task not found");
        List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(task.getId());

        for (TaskDetail taskDetail : taskDetails) {
            AppInstance instance = appGroupDao.getAppInstance(taskDetail.getInstanceId().toString());
            instances.add(instance);
        }
        Assert.notEmpty(instances, "instances not found, please config first");

        // get sha
        String sha = null;

        // Print to client
        //ShellContextHolder.printfQuietly("taskHistory begin");
        TaskHistory taskHistory = taskHistoryService.createTaskHistory(project, instances, CiDevOpsConstants.TASK_TYPE_TRIGGER,
                CiDevOpsConstants.TASK_STATUS_CREATE, branchName, sha, null, null, task.getTarType());
        BasedDeployProvider provider = getDeployProvider(taskHistory);
        //execute
        execute(taskHistory.getId(), provider);
    }

    private void execute(Integer taskId, BasedDeployProvider provider) {

        // update task--running
        taskHistoryService.updateStatus(taskId, CiDevOpsConstants.TASK_STATUS_RUNNING);

        //optimize : use multithreading
        new Thread(new Runnable() {
            public void run() {
                try {
                    // exec
                    provider.execute();
                    if (provider.getSuccess()) {
                        // update task--success
                        taskHistoryService.updateStatusAndResultAndSha(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getResult().toString(), provider.getShaGit(), provider.getShaLocal());
                        //taskService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getResult().toString());
                    } else {
                        // update task--success
                        taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, provider.getResult().toString());
                    }
                } catch (Exception e) {
                    // update task--fail
                    taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private BasedDeployProvider getDeployProvider(Project project, int tarType, String path, String branch, String alias,
                                                  List<AppInstance> instances, TaskHistory taskHistory, TaskHistory refTaskHistory, List<TaskHistoryDetail> taskHistoryDetails) {
        switch (tarType) {
            case CiDevOpsConstants.TAR_TYPE_TAR:
                return new MvnAssembleTarDeployProvider(project, path, branch, alias, instances, taskHistory, refTaskHistory, taskHistoryDetails);
            case CiDevOpsConstants.TAR_TYPE_JAR:
                // return new JarSubject(path, url, branch,
                // alias,tarPath,instances,taskHistoryDetails);
            case CiDevOpsConstants.TAR_TYPE_DOCKER:
                return new DockerBuildDeployProvider(project, path, branch, alias, instances, taskHistory, refTaskHistory, taskHistoryDetails);
            default:
                throw new RuntimeException("unsuppost type:" + tarType);
        }
    }

    public BasedDeployProvider getDeployProvider(TaskHistory taskHistory) {
        Assert.notNull(taskHistory, "taskHistory can not be null");
        Project project = projectDao.selectByPrimaryKey(taskHistory.getProjectId());
        Assert.notNull(project, "project can not be null");
        AppGroup appGroup = appGroupDao.getAppGroup(project.getAppGroupId());
        Assert.notNull(appGroup, "appGroup can not be null");
        project.setGroupName(appGroup.getName());

        List<TaskHistoryDetail> taskHistoryDetails = taskHistoryService.getDetailByTaskId(taskHistory.getId());
        Assert.notNull(taskHistoryDetails, "taskHistoryDetails can not be null");

        TaskHistory refTaskHistory = null;
        if (taskHistory.getRefId() != null) {
            refTaskHistory = taskHistoryService.getById(taskHistory.getRefId());
        }

        List<AppInstance> instances = new ArrayList<>();
        for (TaskHistoryDetail taskHistoryDetail : taskHistoryDetails) {
            AppInstance instance = appGroupDao.getAppInstance(taskHistoryDetail.getInstanceId().toString());
            instances.add(instance);
        }
        return getDeployProvider(project, taskHistory.getTarType(), config.getGitBasePath() + "/" + project.getProjectName(),
                taskHistory.getBranchName(), appGroup.getName(), instances, taskHistory, refTaskHistory, taskHistoryDetails);
    }


    public void rollback(Integer taskId) {

        Assert.notNull(taskId, "taskId is null");
        TaskHistory taskHistoryOld = taskHistoryService.getById(taskId);
        Assert.notNull(taskHistoryOld, "not found this app");
        List<TaskHistoryDetail> taskHistoryDetails = taskHistoryService.getDetailByTaskId(taskId);
        Assert.notEmpty(taskHistoryDetails, "taskHistoryDetails find empty list");
        Project project = projectDao.selectByPrimaryKey(taskHistoryOld.getProjectId());
        Assert.notNull(project, "not found this project");
        List<AppInstance> instances = new ArrayList<>();
        for (TaskHistoryDetail taskHistoryDetail : taskHistoryDetails) {
            AppInstance instance = appGroupDao.getAppInstance(taskHistoryDetail.getInstanceId().toString());
            instances.add(instance);
        }
        TaskHistory taskHistory = taskHistoryService.createTaskHistory(project, instances, CiDevOpsConstants.TASK_TYPE_ROLLBACK,
                CiDevOpsConstants.TASK_STATUS_CREATE, taskHistoryOld.getBranchName(), null, taskId, null, CiDevOpsConstants.TAR_TYPE_TAR);
        BasedDeployProvider provider = getDeployProvider(taskHistory);
        //execute
        rollbackExecute(taskHistory.getId(), provider);

    }

    private void rollbackExecute(Integer taskId, BasedDeployProvider provider) {

        // update task--running
        taskHistoryService.updateStatus(taskId, CiDevOpsConstants.TASK_STATUS_RUNNING);

        //optimize : use multithreading
        new Thread(new Runnable() {
            public void run() {
                try {
                    // exec
                    provider.rollback();
                    if (provider.getSuccess()) {
                        // update task--success
                        taskHistoryService.updateStatusAndResultAndSha(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getResult().toString(), provider.getShaGit(), provider.getShaLocal());
                        //taskService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getResult().toString());
                    } else {
                        // update task--success
                        taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, provider.getResult().toString());
                    }
                } catch (Exception e) {
                    // update task--fail
                    taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

}