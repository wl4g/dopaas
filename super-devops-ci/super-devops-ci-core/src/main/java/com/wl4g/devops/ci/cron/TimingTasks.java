package com.wl4g.devops.ci.cron;

import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.dao.ci.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2019-07-22 16:40:00
 */
@Component
@EnableScheduling
public class TimingTasks {

    @Autowired
    private DeployProperties config;

    @Autowired
    private TaskDao taskDao;

    /**
     * Scan time out task
     */
    //@Scheduled(cron = "00/30 * * * * ?")
    public void delCache() {
        Integer taskTimeout = config.getTaskTimeout();
        if(taskTimeout==null || taskTimeout==0){
            return;
        }
        taskDao.updateStatus(taskTimeout);
    }


}
