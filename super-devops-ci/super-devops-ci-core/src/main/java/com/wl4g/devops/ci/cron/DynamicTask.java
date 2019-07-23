package com.wl4g.devops.ci.cron;


import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
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

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * start All ,for after start app
     */
    public void startAll() {
        List<Trigger> triggers = triggerDao.selectByType(CiDevOpsConstants.TASK_TYPE_TIMMING);
        for (Trigger trigger : triggers) {
            Project project = projectDao.selectByPrimaryKey(trigger.getProjectId());
            restartCron(trigger.getId().toString(), trigger.getCron(), trigger, project);
        }
    }

    /**
     * start Cron
     */
    public void startCron(String key, String expression, Trigger trigger, Project project) {
        log.info("startCron ");
        if (isExist(key)) {
            stopCron(key);
        }
        if (trigger.getEnable() != 1) {
            return;
        }
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new CronRunnable(trigger, project, config, ciService, triggerService), new CronTrigger(expression));
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
    public void restartCron(String key, String expression, Trigger trigger, Project project) {
        log.info("restartCron");
        stopCron(key);
        startCron(key, expression, trigger, project);
    }

    private boolean isExist(String key) {
        return map.containsKey(key);
    }


    @Override
    public void run(ApplicationArguments applicationArguments) {
        startAll();
    }
}