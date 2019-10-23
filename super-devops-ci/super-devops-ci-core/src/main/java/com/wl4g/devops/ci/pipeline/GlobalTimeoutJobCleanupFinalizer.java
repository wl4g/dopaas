/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.common.utils.task.CronUtils;
import com.wl4g.devops.dao.ci.TaskHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import static java.util.Objects.nonNull;
import java.util.concurrent.ScheduledFuture;

/**
 * Global timeout job handler finalizer.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-15
 * @since
 */
public class GlobalTimeoutJobCleanupFinalizer implements ApplicationRunner {

	final public static String DEFAULT_CLEANER_EXPRESS = "00/30 * * * * ?";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CiCdProperties config;
	@Autowired
	private TaskHistoryDao taskHistoryDao;
	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	private ScheduledFuture<?> future;

	/**
	 * Scan timeout task , modify their status
	 *
	 * @param applicationArguments
	 */
	@Override
	public void run(ApplicationArguments applicationArguments) {
		// Initializing timeout checker.
		resetTimeoutCheckerExpression(DEFAULT_CLEANER_EXPRESS);
	}

	/**
	 * Reseting timeout task checker, for update task status to Timeout
	 *
	 * @param expression
	 */
	public void resetTimeoutCheckerExpression(String expression) {
		if (log.isInfoEnabled()) {
			log.info("Reseting globalTimeout finalizer for expression: {}", expression);
		}
		if (!CronUtils.isValidExpression(expression)) {
			log.warn("Failed to reset globalTimeout finalizer, because invalid expression: {}", expression);
			return;
		}
		if (nonNull(future) && !future.isDone()) {
			future.cancel(true);
		}

		// Resume timeout scanner.
		future = scheduler.schedule(() -> {
			if (config.getJob().getJobTimeoutMs() > 0) {
				taskHistoryDao.updateStatus(config.getJob().getJobTimeoutMs());
			}
		}, new CronTrigger(expression));

		if (log.isInfoEnabled()) {
			log.info("Reseted globalTimeout finalizer for expression: {}", expression);
		}
	}

}