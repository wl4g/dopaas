package com.wl4g.devops.ci.pipeline.handler;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.CiService;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Dynamic Timing Task
 * 
 * @author vjay
 * @date 2019-07-19 09:50:00
 */
@Component
public class TimingPipelineScheduler implements ApplicationRunner {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	private static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	private TriggerDao triggerDao;

	@Autowired
	private CiCdProperties config;

	@Autowired
	private CiService ciService;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private TriggerService triggerService;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private TaskDetailDao taskDetailDao;

	/**
	 * start All ,for after start app
	 */
	private void startAll() {
		List<Trigger> triggers = triggerDao.selectByType(CiDevOpsConstants.TASK_TYPE_TIMMING);
		for (Trigger trigger : triggers) {
			restartCron(trigger.getId().toString(), trigger.getCron(), trigger);
		}
	}

	/**
	 * start Cron
	 */
	private void startCron(String key, String expression, Trigger trigger, Project project, Task task,
			List<TaskDetail> taskDetails) {
		log.info(
				"into DynamicTask.startCron prarms::"
						+ "triggerId = {} , expression = {} , trigger = {} , project = {} , task = {} , taskDetails = {} ",
				key, expression, trigger, project, task, taskDetails);
		if (map.containsKey(key)) {
			stopCron(key);
		}
		if (trigger.getEnable() != 1) {
			return;
		}
		ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(
				new TimingPipelineHandler(trigger, project, config, ciService, triggerService, task, taskDetails),
				new CronTrigger(expression));
		TimingPipelineScheduler.map.put(key, future);
	}

	/**
	 * stopCron
	 */
	public void stopCron(String key) {
		log.info("into DynamicTask.stopCron prarms::" + "triggerId = {} ", key);
		ScheduledFuture<?> future = TimingPipelineScheduler.map.get(key);
		if (future != null) {
			future.cancel(true);
		}
	}

	/**
	 * restartCron
	 */
	public void restartCron(String key, String expression, Trigger trigger) {
		log.info("into DynamicTask.restartCron prarms::" + "key = {} , expression = {} , trigger = {} ", key, expression,
				trigger);
		stopCron(key);

		Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
		List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(trigger.getTaskId());
		Assert.notNull(task, "task not found");
		Assert.notEmpty(taskDetails, "taskDetails is empty");
		Project project = projectDao.selectByPrimaryKey(task.getProjectId());
		Assert.notNull(project, "project not found");

		startCron(key, expression, trigger, project, task, taskDetails);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) {
		// start all after app start
		startAll();
	}

}