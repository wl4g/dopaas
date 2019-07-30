package com.wl4g.devops.ci.cron;


import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Dynamic Timing Task
 * @author vjay
 * @date 2019-07-19 09:50:00
 */
@RestController
@Component
public class DynamicTask implements ApplicationRunner {
    final protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap();

    @Autowired
    private TriggerDao triggerDao;

    @Autowired
    private DeployProperties config;

    @Autowired
    private CiService ciService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TriggerService triggerService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskDetailDao taskDetailDao;


    /**
     * start All ,for after start app
     */
    public void startAll() {
        List<Trigger> triggers = triggerDao.selectByType(CiDevOpsConstants.TASK_TYPE_TIMMING);
        for (Trigger trigger : triggers) {
            restartCron(trigger.getId().toString(), trigger.getCron(), trigger);
        }
    }

    /**
     * start Cron
     */
    public void startCron(String key, String expression, Trigger trigger, Project project,Task task,List<TaskDetail> taskDetails) {
        log.info("startCron ");
        if (map.containsKey(key)) {
            stopCron(key);
        }
        if (trigger.getEnable() != 1) {
            return;
        }
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new CronRunnable(trigger, project, config, ciService, triggerService,task,taskDetails), new CronTrigger(expression));
        DynamicTask.map.put(key, future);
    }

    /**
     * stopCron
     */
    public void stopCron(String key) {
        log.info("stopCron");
        ScheduledFuture<?> future = DynamicTask.map.get(key);
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * restartCron
     */
    public void restartCron(String key, String expression, Trigger trigger) {
        log.info("restartCron");
        stopCron(key);

        Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
        List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(trigger.getTaskId());
        Assert.notNull(task,"task not found");
        Assert.notEmpty(taskDetails,"taskDetails is empty");
        Project project = projectDao.selectByPrimaryKey(task.getProjectId());
        Assert.notNull(project,"project not found");

        startCron(key, expression, trigger, project,task,taskDetails);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        //start all after app start
        startAll();
    }
}