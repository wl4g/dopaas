package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.common.utils.task.CronUtils;
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

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * Timing Tasks
 *
 * @author vjay
 * @date 2019-07-22 16:40:00
 */
@Component
@EnableScheduling
public class GlobalTimeoutHandlerCleanFinalizer implements ApplicationRunner {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CiCdProperties config;

	@Autowired
	private TaskHistoryDao taskHistoryDao;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	private ScheduledFuture<?> future;

	/**
	 * Scan timeout task , modify their status
	 *
	 * @param applicationArguments
	 */
	@Override
	public void run(ApplicationArguments applicationArguments) {
		// Initializing timeout checker.
		resetTimeoutCheckerExpression("00/30 * * * * ?");
	}

	/**
	 * Reseting timeout task checker, for update task status to Timeout
	 *
	 * @param expression
	 */
	public void resetTimeoutCheckerExpression(String expression) {
		if (log.isInfoEnabled()) {
			log.info("Reseting timeout scnaner expression: {}", expression);
		}
		if (!CronUtils.isValidExpression(expression)) {
			log.info("modify expression fail , expression is not valid , expression={}", expression);
			return;
		}
		if (Objects.nonNull(future) && !future.isDone()) {
			this.future.cancel(true);
		}

		// Resume timeout scanner.
		this.future = taskScheduler.schedule(() -> {
			if (config.getJob().getCleanTimeout() != null && config.getJob().getCleanTimeout() > 0) {
				taskHistoryDao.updateStatus(config.getJob().getCleanTimeout());
			}
		}, new CronTrigger(expression));

		if (log.isInfoEnabled()) {
			log.info("Reseted timeout scanner expression: {}", expression);
		}
	}

}
