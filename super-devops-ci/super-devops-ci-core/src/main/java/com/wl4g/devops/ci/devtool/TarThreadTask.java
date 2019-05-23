package com.wl4g.devops.ci.devtool;

import com.wl4g.devops.ci.service.TaskService;
import com.wl4g.devops.ci.subject.TarSubject;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

public class TarThreadTask extends Thread {
    private Logger logger = LoggerFactory.getLogger(TarThreadTask.class);

    private TarSubject tarSubject;
    private String path;
    private AppInstance instance;
    private String tarPath;
    private TaskService taskService;
    private Integer taskDetailId;
    private String alias;

    public TarThreadTask(TarSubject tarSubject, String path, AppInstance instance, String tarPath, List<TaskDetail> taskDetails, String alias) {
        taskService = ApplicationContextProvider.getBean(TaskService.class);
        this.tarSubject = tarSubject;
        this.path = path;
        this.instance = instance;
        this.tarPath = tarPath;
        this.alias = alias;
        Assert.notNull(taskDetails, "taskDetails can not be null");
        for (TaskDetail taskDetail : taskDetails) {
            if (taskDetail.getInstanceId().intValue() == instance.getId().intValue()) {
                taskDetailId = taskDetail.getId();
            }
        }

    }


    @Override
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info("scp thread is starting!");
        }
        Assert.notNull(taskDetailId, "taskDetailId can not be null");
        try {
            //update status
            taskService.updateTaskDetailStatus(taskDetailId, CiDevOpsConstants.TASK_STATUS_RUNNING);
            //scp to tmp,rename,move to webapps
            tarSubject.scpAndTar(path + tarPath, instance.getHost(), instance.getServerAccount(), instance.getBasePath() + "/" + alias + "-package", instance.getSshRsa());
            //change link
            tarSubject.reLink(instance.getHost(), instance.getBasePath() + "/" + alias + "-package", instance.getServerAccount(), path + tarPath, instance.getSshRsa());
            //restart
            tarSubject.restart(instance.getHost(), instance.getServerAccount(), instance.getSshRsa());
            //update status
            taskService.updateTaskDetailStatus(taskDetailId, CiDevOpsConstants.TASK_STATUS_SUCCESS);
        } catch (Exception e) {
            logger.error("scp thread error");
            taskService.updateTaskDetailStatus(taskDetailId, CiDevOpsConstants.TASK_STATUS_FAIL);
            //e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (logger.isInfoEnabled()) {
            logger.info("scp thread is finish!");
        }


    }

}