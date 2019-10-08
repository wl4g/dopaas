package com.wl4g.devops.ci.core.provider;

import com.wl4g.devops.ci.anno.DeployType;
import com.wl4g.devops.ci.bean.BaseDeployBean;
import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.constant.DeployTypeEnum;
import com.wl4g.devops.ci.core.BaseDeploy;
import com.wl4g.devops.ci.core.DeployInterface;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.FileCodec;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.scm.AppClusterDao;
import com.wl4g.devops.shell.utils.ShellContextHolder;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;

/**
 * @author vjay
 * @date 2019-09-30 10:09:00
 */
@DeployType(DeployTypeEnum.MvnAssembleTar)
@Component
public class MvnAssembleTarDeploy extends BaseDeploy implements DeployInterface {

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

    @Autowired
    private SimpleRedisLockManager lockManager;

    @Autowired
    private TaskHistoryDao taskHistoryDao;

    @Autowired
    private TaskHistoryDetailDao taskHistoryDetailDao;

    @Autowired
    private TaskHistoryService taskHistoryService;

    @Autowired
    private AppClusterDao appClusterDao;


    @Override
    public void getSource(BaseDeployBean bean) {
        //do nothing , get Source is doing in build Step
    }

    @Override
    public void build(BaseDeployBean bean) throws Exception {
        TaskHistory taskHistory = bean.getTaskHistory();
        TaskResult taskResult = bean.getTaskResult();
        build(taskHistory,taskResult);
    }

    @Override
    public void preCommand(BaseDeployBean bean) throws Exception {
        TaskHistory taskHistory = bean.getTaskHistory();
        SSHTool.exec(taskHistory.getPreCommand(),bean.getTaskResult());
    }

    @Override
    public List<Thread> deploy(BaseDeployBean bean) {
        TaskHistory taskHistory = bean.getTaskHistory();
        String path = config.getGitBasePath() + "/" + taskHistory.getProjectName();
        List<Thread> threads = new ArrayList<>();
        List<TaskHistoryDetail> taskHistoryDetails = taskHistoryDetailDao.getDetailByTaskId(bean.getTaskHistory().getId());
        for(TaskHistoryDetail taskHistoryDetail : taskHistoryDetails){
            StringBuilder result = new StringBuilder();

            Thread thread = new Thread(){
                public void run() {
                    //======== deploy start =========
                    try {
                        // Update status
                        taskHistoryService.updateDetailStatusAndResult(taskHistoryDetail.getId(), TASK_STATUS_RUNNING, null);

                        AppInstance instance = appClusterDao.getAppInstance(taskHistoryDetail.getInstanceId().toString());

                        // Scp to tmp,rename,move to webapps
                        String s = scpAndTar(path + bean.getProject().getTarPath(), instance.getHostname(), instance.getSshUser(),
                                bean.getProject().getParentAppHome(), instance.getSshKey());
                        result.append(s).append("\n");

                        // post command (restart command)
                        String s2 = exceCommand(instance.getHostname(), instance.getSshUser(),
                                taskHistory.getPostCommand(), instance.getSshKey());
                        result.append(s2).append("\n");

                        // Update status
                        taskHistoryService.updateDetailStatusAndResult(taskHistoryDetail.getId(), TASK_STATUS_SUCCESS, result.toString());

                    } catch (Exception e) {
                        log.error("Deploy job failed", e);
                        taskHistoryService.updateDetailStatusAndResult(taskHistoryDetail.getId(), TASK_STATUS_FAIL,
                                result.toString() + "\n" + e.toString());
                        // throw new RuntimeException(e);
                    }

                    if (log.isInfoEnabled()) {
                        log.info("Deploy task is finished!");
                    }
                    //======== deploy end =========
                }
            };
            threads.add(thread);
        }

        return null;
    }


    @Override
    public void bakcup(BaseDeployBean bean) throws Exception {
        TaskHistory taskHistory = bean.getTaskHistory();
        String path = config.getGitBasePath() + "/" + taskHistory.getProjectName();
        String sha = GitUtils.getLatestCommitted(path);
        String md5 = FileCodec.getFileMD5(new File(path + bean.getProject().getTarPath()));
        TaskHistory taskHistoryUpdate = new TaskHistory();
        taskHistoryUpdate.setId(taskHistory.getId());
        taskHistoryUpdate.setShaGit(sha);
        taskHistoryUpdate.setShaLocal(md5);
        taskHistoryDao.updateByPrimaryKeySelective(taskHistoryUpdate);
        backupLocal(path+ bean.getProject().getTarPath(),taskHistory.getId().toString());

    }

    @Override
    public void rollback(BaseDeployBean bean) throws Exception {
        String path = config.getGitBasePath() + "/" + bean.getTaskHistory().getProjectName();
        Dependency dependency = new Dependency();
        dependency.setProjectId(bean.getProject().getId());
        // Old file
        String oldFilePath = config.getBackupPath() + "/" + subPackname(bean.getProject().getTarPath()) + "#"
                + bean.getTaskHistory().getRefId();
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {// Check bakup file isExist , if not -- check out
            // from git
            getBackupLocal(oldFilePath, path + bean.getProject().getTarPath());
            TaskHistory refTaskHistory = taskHistoryService.getById(bean.getTaskHistory().getRefId());
            TaskHistory taskHistoryUpdate = new TaskHistory();
            taskHistoryUpdate.setId(bean.getTaskHistory().getId());
            taskHistoryUpdate.setShaGit(refTaskHistory.getShaGit());
            taskHistoryDao.updateByPrimaryKeySelective(taskHistoryUpdate);

        } else {
            //TODO
            //getDependencyService().rollback(getTaskHistory(), getRefTaskHistory(), dependency, getBranch(), taskResult, false);
            //setShaGit(GitUtils.getLatestCommitted(getPath()));
        }

        List<Thread> threads = deploy(bean);
        for(Thread thread: threads){
            thread.start();
            thread.join();
        }
    }




    /**
     * get dependency
     * @param projectId
     * @param set
     * @return
     */
    private LinkedHashSet<Dependency> getDependencys(Integer projectId, LinkedHashSet<Dependency> set){
        if(null == set){
            set = new LinkedHashSet<>();
        }
        List<Dependency> dependencies = dependencyDao.getParentsByProjectId(projectId);
        if (dependencies != null && dependencies.size() > 0) {
            for (Dependency dep : dependencies) {
                set.add(dep);
                getDependencys(dep.getDependentId(),set);
            }
        }
        return set;
    }

    /**
     * build
     * @param taskHistory
     * @param taskResult
     * @throws Exception
     */
    protected void build(TaskHistory taskHistory, TaskResult taskResult) throws Exception{

        LinkedHashSet<Dependency> dependencys = getDependencys(taskHistory.getProjectId(), null);
        Dependency[] dependencys2 = (Dependency[]) dependencys.toArray();

        for (int i = dependencys2.length - 1; i >= 0; i--) {
            Dependency dependency1 = dependencys2[i];
            build(taskHistory,dependency1.getProjectId(),dependency1.getDependentId(),dependency1.getBranch(),taskResult,true);
            // Is Continue ? if fail then return
            if (!taskResult.isSuccess()) {
                return;
            }
        }

        build(taskHistory,taskHistory.getProjectId(),null,taskHistory.getBranchName(),taskResult,false);

    }


    private void build(TaskHistory taskHistory, Integer projectId,Integer dependencyId, String branch, TaskResult taskResult, boolean isDependency)throws Exception{
        // ===== redis lock =====
        Lock lock = lockManager.getLock(CI_LOCK+projectId, LOCK_TIME, TimeUnit.MINUTES);
        if(lock.tryLock()){// needn't wait
            //Do
            try {
                build2(taskHistory,projectId,dependencyId,branch,taskResult,isDependency);
            }finally {
                lock.unlock();
            }
        }else{
            //not yet
            try {
                if (lock.tryLock(LOCK_TIME, TimeUnit.MINUTES)) {//Wait
                    log.info("One Task is running , Waiting");
                    build2(taskHistory,projectId,dependencyId,branch,taskResult,isDependency);
                } else {
                    log.error("One Task is running ,Waiting timeout");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                lock.unlock();
            }

        }
    }

    private void build2(TaskHistory taskHistory, Integer projectId,Integer dependencyId, String branch, TaskResult taskResult, boolean isDependency) throws Exception{
        log.info("build start projectId={}", projectId);
        Project project = projectDao.selectByPrimaryKey(projectId);
        Assert.notNull(project, "project not exist");


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
                taskSign.setDependenvyId(dependencyId);
                taskSign.setShaGit(GitUtils.getLatestCommitted(path));
                taskSignDao.insertSelective(taskSign);
            }

            // run install command
            String installResult = mvnInstall(path, taskResult);

            // ===== build end =====
            taskResult.getStringBuffer().append(installResult);

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
