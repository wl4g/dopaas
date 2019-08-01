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
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.dao.scm.AppGroupDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * CI/CD
 * @author vjay
 * @date 2019-05-16 14:50:00
 */
@Service
public class CiServiceImpl implements CiService {

    final protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeployProperties config;

    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private TriggerDao triggerDao;

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


    /**
     * Create Task History
     * @param taskId
     */
    @Override
    public void createTask(Integer taskId) {
        log.debug("into CiServiceImpl.createTask prarms::"+ "taskId = {} ", taskId );
        Assert.notNull(taskId,"taskId is null");
        Task task = taskDao.selectByPrimaryKey(taskId);
        Assert.notNull(task,"task is null");
        List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(taskId);
        List<String> instanceStrs = new ArrayList<>();
        for (TaskDetail taskDetail : taskDetails) {
            instanceStrs.add(String.valueOf(taskDetail.getInstanceId()));
        }
        Assert.notNull(task.getAppGroupId(), "groupId is null");
        AppGroup appGroup = appGroupDao.getAppGroup(task.getAppGroupId());

        Assert.notNull(appGroup, "not found this app");
        Project project = projectDao.getByAppGroupId(appGroup.getId());

        Assert.notEmpty(instanceStrs, "instanceIds find empty list,Please check the instanceId");
        List<AppInstance> instances = new ArrayList<>();
        for (String instanceId : instanceStrs) {
            AppInstance instance = appGroupDao.getAppInstance(instanceId);
            instances.add(instance);
        }
        TaskHistory taskHistory = taskHistoryService.createTaskHistory(project, instances, CiDevOpsConstants.TASK_TYPE_MANUAL,
                CiDevOpsConstants.TASK_STATUS_CREATE, task.getBranchName(), null, null, task.getPreCommand(),task.getPostCommand(), task.getTarType());
        BasedDeployProvider provider = buildDeployProvider(taskHistory);
        //execute
        execute(taskHistory.getId(), provider);
    }


    /**
     * Hook -- for gitlab hook
     * @param projectName
     * @param branchName
     * @param url
     */
    public void hook(String projectName, String branchName, String url) {
        log.info("into CiServiceImpl.hook prarms::"+ "projectName = {} , branchName = {} , url = {} ", projectName, branchName, url );
        Project project = projectDao.getByProjectName(projectName);
        if (null == project) {
            return;
        }
        Trigger trigger = getTriggerByAppGroupIdAndBranch(project.getAppGroupId(), branchName);
        if (null == trigger) {
            return;
        }
        Assert.notNull(trigger,"trigger not found, please config first");

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
                CiDevOpsConstants.TASK_STATUS_CREATE, branchName, sha, null, task.getPreCommand(),task.getPostCommand(), task.getTarType());
        BasedDeployProvider provider = buildDeployProvider(taskHistory);
        //execute
        execute(taskHistory.getId(), provider);
    }

    /**
     * Execute task
     * @param taskId
     * @param provider
     */
    private void execute(Integer taskId, BasedDeployProvider provider) {
        log.info("task start taskId={}",taskId);
        // update task--running
        taskHistoryService.updateStatus(taskId, CiDevOpsConstants.TASK_STATUS_RUNNING);

        //optimize : use multithreading
        new Thread(new Runnable() {
            public void run() {
                try {
                    // exec
                    provider.execute();
                    if (provider.getTaskResult().isSuccess()) {
                        // update task--success
                        log.info("task succcess taskId={}",taskId);
                        taskHistoryService.updateStatusAndResultAndSha(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getTaskResult().getStringBuffer().toString(), provider.getShaGit(), provider.getShaLocal());
                        //taskService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getResult().toString());
                    } else {
                        // update task--fail
                        log.info("task fail taskId={}",taskId);
                        taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, provider.getTaskResult().getStringBuffer().toString());
                    }
                } catch (Exception e) {
                    // update task--fail
                    log.info("task fail taskId={}",taskId);
                    taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, provider.getTaskResult().getStringBuffer().toString()+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Get Deploy Provider by type
     * @param project
     * @param tarType
     * @param path
     * @param branch
     * @param alias
     * @param instances
     * @param taskHistory
     * @param refTaskHistory
     * @param taskHistoryDetails
     * @return
     */
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

    /**
     * build Deploy Provider
     * @param taskHistory
     * @return
     */
    public BasedDeployProvider buildDeployProvider(TaskHistory taskHistory) {
        log.info("into CiServiceImpl.buildDeployProvider prarms::"+ "taskHistory = {} ", taskHistory );
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


    /**
     * Create Rollback Task by taskId
     * @param taskId
     */
    public void createRollbackTask(Integer taskId) {
        log.info("into CiServiceImpl.rollback prarms::"+ "taskId = {} ", taskId );
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
                CiDevOpsConstants.TASK_STATUS_CREATE, taskHistoryOld.getBranchName(), null, taskId, taskHistoryOld.getPreCommand(),taskHistoryOld.getPostCommand(), CiDevOpsConstants.TAR_TYPE_TAR);
        BasedDeployProvider provider = buildDeployProvider(taskHistory);
        //execute
        rollback(taskHistory.getId(), provider);

    }

    /**
     * Run rollback task
     * @param taskId
     * @param provider
     */
    private void rollback(Integer taskId, BasedDeployProvider provider) {
        // update task--running
        taskHistoryService.updateStatus(taskId, CiDevOpsConstants.TASK_STATUS_RUNNING);
        //optimize : use multithreading
        new Thread(new Runnable() {
            public void run() {
                try {
                    // exec
                    provider.rollback();
                    if (provider.getTaskResult().isSuccess()) {
                        // update task--success
                        taskHistoryService.updateStatusAndResultAndSha(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getTaskResult().getStringBuffer().toString(), provider.getShaGit(), provider.getShaLocal());
                        //taskService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_SUCCESS, provider.getResult().toString());
                    } else {
                        // update task--success
                        taskHistoryService.updateStatusAndResult(taskId, CiDevOpsConstants.TASK_STATUS_FAIL, provider.getTaskResult().getStringBuffer().toString());
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