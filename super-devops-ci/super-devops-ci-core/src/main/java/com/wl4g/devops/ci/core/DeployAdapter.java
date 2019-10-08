package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.anno.DeployType;
import com.wl4g.devops.ci.bean.BaseDeployBean;
import com.wl4g.devops.ci.constant.DeployTypeEnum;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vjay
 * @date 2019-09-23 10:29:00
 */
@Component
public class DeployAdapter {

    @Autowired
    private TaskHistoryDao taskHistoryDao;

    @Autowired
    private ProjectDao projectDao;

    private final Map<DeployTypeEnum, DeployInterface> deployInterfaceMap;


    /**
     * deploy
     */
    public void deploy(DeployTypeEnum deployTypeEnum,int taskHisId) throws Exception {
        TaskHistory taskHistory = taskHistoryDao.selectByPrimaryKey(taskHisId);
        Assert.notNull(taskHistory,"not found Task History");
        Project project = projectDao.selectByPrimaryKey(taskHistory.getProjectId());
        Assert.notNull(project,"not found Project");
        DeployInterface deployInterface = getDeploy(deployTypeEnum);
        BaseDeployBean bean = new BaseDeployBean();
        bean.setTaskHistory(taskHistory);
        bean.setProject(project);
        deployInterface.getSource(bean);
        deployInterface.build(bean);
        if (!bean.getTaskResult().isSuccess()) {
            return;
        }
        deployInterface.bakcup(bean);
        deployInterface.preCommand(bean);

        List<Thread> threads = deployInterface.deploy(bean);
        for(Thread thread: threads){
            thread.start();
            thread.join();
        }
    }

    /**
     * rollback
     */
    public void rollback(DeployTypeEnum deployTypeEnum,int taskHisId) throws Exception {
        TaskHistory taskHistory = taskHistoryDao.selectByPrimaryKey(taskHisId);
        Assert.notNull(taskHistory,"not found Task History");
        Project project = projectDao.selectByPrimaryKey(taskHistory.getProjectId());
        Assert.notNull(project,"not found Project");
        DeployInterface deployInterface = getDeploy(deployTypeEnum);
        BaseDeployBean bean = new BaseDeployBean();
        bean.setTaskHistory(taskHistory);
        bean.setProject(project);
        deployInterface.rollback(bean);

    }

    public DeployInterface getDeploy(DeployTypeEnum deployTypeEnum){
        return deployInterfaceMap.get(deployTypeEnum);
    }

    @Autowired
    public DeployAdapter(List<DeployInterface> deploys) {
        this.deployInterfaceMap = Collections.unmodifiableMap(buildDeployStrategies(deploys));
    }

    private Map<DeployTypeEnum, DeployInterface> buildDeployStrategies(List<DeployInterface> deploys) {
        return deploys.stream()
                .filter(deploy -> deploy.getClass().isAnnotationPresent(DeployType.class))
                .collect(Collectors.toMap(o -> o.getClass().getAnnotation(DeployType.class).value(), o -> o));
    }


}
