package com.wl4g.devops.scm.client.config;

import com.wl4g.devops.scm.client.configure.watch.DeferredRefreshWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2019-04-01 17:51:00
 */
@Component
public class AfterStart implements ApplicationRunner {

    //@Autowired(required = false)
    @Autowired
    private DeferredRefreshWatcher deferredRefreshWatcher;

    @Override
    public void run(ApplicationArguments var1) throws Exception {
        try {
            deferredRefreshWatcher.watch();
        }catch (Exception e){
            throw e;
        }
    }
}


