package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskSign;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskSignDao;
import com.wl4g.devops.shell.utils.ShellContextHolder;
import com.wl4g.devops.support.cache.JedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS_LOCK;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS__UNLOCK;

/**
 * @author vjay
 * @date 2019-09-24 09:21:00
 */
@Component
public class BaseDeploy {

    protected final Logger log = LoggerFactory.getLogger(BaseDeploy.class);

    @Autowired
    private JedisService jedisService;

    @Autowired
    private DependencyDao dependencyDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    ProjectService projectService;

    @Autowired
    private TaskSignDao taskSignDao;

    @Autowired
    private CiCdProperties config;

    protected void build(TaskHistory taskHistory, Dependency dependency, String branch, TaskResult taskResult, boolean isDependency) throws Exception{

        Integer projectId = dependency.getProjectId();
        List<Dependency> dependencies = dependencyDao.getParentsByProjectId(projectId);
        if (dependencies != null && dependencies.size() > 0) {
            for (Dependency dep : dependencies) {
                String br = dep.getBranch();
                Dependency dependency1 = new Dependency(dep.getDependentId());
                dependency1.setId(dep.getId());
                // 如果依赖配置中有配置分支，则用配置的分支，若没有，则默认用打包项目的分支
                build(taskHistory, dependency1, StringUtils.isBlank(br) ? branch : br, taskResult, true);
            }
        }

        // Is Continue ? if fail then return
        if (!taskResult.isSuccess()) {
            return;
        }
        // ===== build start =====
        log.info("build start projectId={}", projectId);
        Project project = projectDao.selectByPrimaryKey(projectId);
        Assert.notNull(project, "project not exist");
        try {
            if (project.getLockStatus() != null && project.getLockStatus() == TASK_LOCK_STATUS_LOCK) { // 校验项目锁定状态
                // ，锁定则无法继续
                throw new RuntimeException("project is lock , please check the project lock status");
            }
            projectService.updateLockStatus(projectId, TASK_LOCK_STATUS_LOCK);// 锁定项目，防止同一个项目同时build

            String path = config.getGitBasePath() + "/" + project.getProjectName();
            if (GitUtils.checkGitPath(path)) {// 若果目录存在则:chekcout 分支 并 pull
                GitUtils.checkout(config.getCredentials(), path, branch);
                taskResult.getStringBuffer().append("project checkout success:").append(project.getProjectName()).append("\n");
            } else { // 若目录不存在: 则clone 项目并 checkout 对应分支
                GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, branch);
                taskResult.getStringBuffer().append("project clone success:").append(project.getProjectName()).append("\n");
            }

            // save dependency git sha -- 保存依赖项目的sha，用于回滚时找回对应的 历史依赖项目
            if (isDependency) {
                TaskSign taskSign = new TaskSign();
                taskSign.setTaskId(taskHistory.getId());
                taskSign.setDependenvyId(dependency.getId());
                taskSign.setShaGit(GitUtils.getLatestCommitted(path));
                taskSignDao.insertSelective(taskSign);
            }

            // run install command
            String installResult = mvnInstall(path, taskResult);

            // ===== build end =====
            taskResult.getStringBuffer().append(installResult);
        } finally {
            // finish then unlock the project
            projectService.updateLockStatus(projectId, TASK_LOCK_STATUS__UNLOCK);
        }

    }



    private void test(){

    }

    /**
     * Building (maven)
     */
    private String mvnInstall(String path, TaskResult taskResult) throws Exception {
        // Execution mvn
        String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
        return SSHTool.exec(command, inlog -> !ShellContextHolder.isInterruptIfNecessary(), taskResult);
    }




}
