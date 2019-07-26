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
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskSign;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskSignDao;
import com.wl4g.devops.shell.utils.ShellContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS_LOCK;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS__UNLOCK;

/**
 * Dependency service implements
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-22 11:39:00
 */
@Service
public class DependencyServiceImpl implements DependencyService {

    @Autowired
    private DeployProperties config;

    @Autowired
    private DependencyDao dependencyDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskSignDao taskSignDao;

    @Override
    public void build(TaskHistory taskHistory, Dependency dependency, String branch, Boolean success, StringBuffer result, boolean isDependency) throws Exception {
        Integer projectId = dependency.getProjectId();

        List<Dependency> dependencies = dependencyDao.getParentsByProjectId(projectId);
        if (dependencies != null && dependencies.size() > 0) {
            for (Dependency dep : dependencies) {
                String br = dep.getBranch();
                Dependency dependency1 = new Dependency(dep.getDependentId());
                dependency1.setId(dep.getId());
                build(taskHistory, dependency1, StringUtils.isBlank(br) ? branch : br, success, result, true);
            }
        }

        // Is Continue ? if fail then return
        if (!success) {
            return;
        }
        // build
        Project project = projectDao.selectByPrimaryKey(projectId);
        Assert.notNull(project, "project not exist");
        try {
            if (project.getLockStatus() == TASK_LOCK_STATUS_LOCK) {
                throw new RuntimeException("project is lock , please check the project lock status");
            }
            projectService.updateLockStatus(projectId, TASK_LOCK_STATUS_LOCK);

            String path = config.getGitBasePath() + "/" + project.getProjectName();
            if (GitUtils.checkGitPahtExist(path)) {
                GitUtils.checkout(config.getCredentials(), path, branch);
                result.append("project checkout success:").append(project.getProjectName()).append("\n");
            } else {
                GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, branch);
                result.append("project clone success:").append(project.getProjectName()).append("\n");
            }

            //save
            if (isDependency) {
                TaskSign taskSign = new TaskSign();
                taskSign.setTaskId(taskHistory.getId());
                taskSign.setDependenvyId(dependency.getId());
                taskSign.setShaGit(GitUtils.getOldestCommitSha(path));
                taskSignDao.insertSelective(taskSign);
            }

            // Install
            String installResult = mvnInstall(path);
            result.append(installResult);
        } finally {
            //finish then unlock the project
            projectService.updateLockStatus(projectId, TASK_LOCK_STATUS__UNLOCK);
        }
    }

    @Override
    public void rollback(TaskHistory taskHistory, TaskHistory refTaskHistory, Dependency dependency, String branch, Boolean success, StringBuffer result, boolean isDependency) throws Exception {
        Integer projectId = dependency.getProjectId();
        List<Dependency> dependencies = dependencyDao.getParentsByProjectId(projectId);
        if (dependencies != null && dependencies.size() > 0) {
            for (Dependency dep : dependencies) {
                String br = dep.getBranch();
                Dependency dependency1 = new Dependency(dep.getDependentId());
                dependency1.setId(dep.getId());
                rollback(taskHistory, refTaskHistory, dependency1, StringUtils.isBlank(br) ? branch : br, success, result, true);
            }
        }

        // Is Continue ? if fail then return
        if (!success) {
            return;
        }
        // build
        Project project = projectDao.selectByPrimaryKey(projectId);
        Assert.notNull(project, "project not exist");
        try {
            if (project.getLockStatus() == TASK_LOCK_STATUS_LOCK) {
                throw new RuntimeException("project is lock , please check the project lock status");
            }
            projectService.updateLockStatus(projectId, TASK_LOCK_STATUS_LOCK);

            String path = config.getGitBasePath() + "/" + project.getProjectName();

            String sha;
            if (isDependency) {
                TaskSign taskSign = taskSignDao.selectByDependencyIdAndTaskId(dependency.getId(), taskHistory.getRefId());
                Assert.notNull(taskSign, "not found taskSign");
                sha = taskSign.getShaGit();
            } else {
                sha = refTaskHistory.getShaGit();
            }

            if (GitUtils.checkGitPahtExist(path)) {
                GitUtils.roolback(config.getCredentials(), path, sha);
                result.append("project rollback success:").append(project.getProjectName()).append("\n");
            } else {
                GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, branch);
                result.append("project clone success:").append(project.getProjectName()).append("\n");
                GitUtils.roolback(config.getCredentials(), path, sha);
                result.append("project rollback success:").append(project.getProjectName()).append("\n");
            }

            //save
            if (isDependency) {
                TaskSign taskSign = new TaskSign();
                taskSign.setTaskId(taskHistory.getId());
                taskSign.setDependenvyId(dependency.getId());
                taskSign.setShaGit(GitUtils.getOldestCommitSha(path));
                taskSignDao.insertSelective(taskSign);
            }

            // Install
            String installResult = mvnInstall(path);
            result.append(installResult);
        } finally {
            //finish then unlock the project
            projectService.updateLockStatus(projectId, TASK_LOCK_STATUS__UNLOCK);
        }
    }


    /**
     * Building (maven)
     */
    private String mvnInstall(String path) throws Exception {
        // Execution mvn
        String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
        return SSHTool.exec(command, inlog -> !ShellContextHolder.isInterruptIfNecessary());
    }

}