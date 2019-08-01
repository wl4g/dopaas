package com.wl4g.devops.ci.cron;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.dao.ci.TaskHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

/**
 * Timing Tasks
 * @author vjay
 * @date 2019-07-22 16:40:00
 */
@Component
@EnableScheduling
public class TimingTasks implements ApplicationRunner {

    final protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CiCdProperties config;

    @Autowired
    private TaskHistoryDao taskHistoryDao;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private static ScheduledFuture<?> future;


    /**
     * Scan timeout task , modify their status
     * @param applicationArguments
     */
    @Override
    public void run(ApplicationArguments applicationArguments) {
        //Scan time out task
        future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                Integer taskTimeout = config.getTaskTimeout();
                if (taskTimeout == null || taskTimeout == 0) {
                    return;
                }
                taskHistoryDao.updateStatus(taskTimeout);
            }
        }, new CronTrigger("00/30 * * * * ?"));
    }


    /**
     * modify expression , update time out task status
     */
    public void modifyExpression(String expression){
        log.info("modify expression , expression={}",expression);
        if(!CronUtils.isValidExpression(expression)){
            log.info("modify expression fail , expression is not valid , expression={}",expression);
            return;
        }
        if(null != future){
            future.cancel(true);
        }
        //Scan time out task
        future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                Integer taskTimeout = config.getTaskTimeout();
                if (taskTimeout == null || taskTimeout == 0) {
                    return;
                }
                taskHistoryDao.updateStatus(taskTimeout);
            }
        }, new CronTrigger(expression));
        log.info("modify expression success , expression={}",expression);
    }
}
