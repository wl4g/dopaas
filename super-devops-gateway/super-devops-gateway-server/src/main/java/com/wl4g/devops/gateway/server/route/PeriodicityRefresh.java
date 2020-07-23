package com.wl4g.devops.gateway.server.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * {@link PeriodicityRefresh}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-21
 * @since
 */
@Component
@EnableScheduling
public class PeriodicityRefresh {

	@Autowired
	private ApplicationContext applicationContext;

	// @Scheduled(cron="0/5 * * * * ? ")
	// @Scheduled(fixedRate = 5000, initialDelay = 10000)
	public void reportCurrentTime() {
		applicationContext.getBean(IRouteCacheRefresh.class).flushRoutesPermanentToMemery();



		
	}

}
