package com.wl4g.devops.gataway.server.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2020-07-20 10:35:00
 */
@Component
@EnableScheduling
public class PeriodicityRefresh {

	@Autowired
	private ApplicationContext applicationContext;

	// @Scheduled(cron="0/5 * * * * ? ")
	@Scheduled(fixedRate = 5000, initialDelay = 10000)
	public void reportCurrentTime() {
		applicationContext.getBean(IRouteCacheRefresh.class).flushRoutesPermanentToMemery();
	}

}
