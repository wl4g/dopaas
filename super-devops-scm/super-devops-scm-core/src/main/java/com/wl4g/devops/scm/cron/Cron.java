package com.wl4g.devops.scm.cron;

import com.wl4g.devops.scm.context.ConfigContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


@EnableScheduling
@Component
public class Cron {

    final private static Logger log = LoggerFactory.getLogger(Cron.class);

    @Autowired
    private ConfigContextHandler configContextHandler;


    //@Scheduled(cron = "0/50 * * * * ?")
    public void refreshMeta() {
        configContextHandler.refreshMeta(false);
    }


}