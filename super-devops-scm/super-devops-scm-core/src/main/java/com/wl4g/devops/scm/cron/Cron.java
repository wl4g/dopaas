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

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigContextHandler configContextHandler;

	// TODO
	// @Scheduled(cron = "0/50 * * * * ?")
	public void refreshMeta() {
		configContextHandler.refreshMeta(false);
	}

}