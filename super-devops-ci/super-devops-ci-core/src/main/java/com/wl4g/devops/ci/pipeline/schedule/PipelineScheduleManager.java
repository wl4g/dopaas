package com.wl4g.devops.ci.pipeline.schedule;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.Pipeline;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.dao.ci.TaskDetailDao;
import com.wl4g.devops.dao.ci.TriggerDao;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Timing pipeline schedule manager
 *
 * @author vjay
 * @date 2019-07-19 09:50:00
 */
public class PipelineScheduleManager implements ApplicationRunner {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	private static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected Pipeline pipeline;
	@Autowired
	protected BeanFactory beanFactory;

	@Autowired
	protected TriggerDao triggerDao;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected TriggerService triggerService;
	@Autowired
	protected TaskDao taskDao;
	@Autowired
	protected TaskDetailDao taskDetailDao;

	@Override
	public void run(ApplicationArguments args) {
		// start all after app start
		resumePipelineJobAll();
	}

	/**
	 * Refresh and resume pipeline job all.
	 */
	private void resumePipelineJobAll() {
		List<Trigger> triggers = triggerDao.selectByType(TASK_TYPE_TIMMING);
		for (Trigger trigger : triggers) {
			refreshPipeline(trigger.getId().toString(), trigger.getCron(), trigger);
		}
	}

	/**
	 * Refresh and resume pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 */
	public void refreshPipeline(String key, String expression, Trigger trigger) {
		log.info("into DynamicTask.restartCron prarms::" + "key = {} , expression = {} , trigger = {} ", key, expression,
				trigger);
		stopPipeline(key);

		Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
		List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(trigger.getTaskId());
		Assert.notNull(task, "task not found");
		Assert.notEmpty(taskDetails, "taskDetails is empty");
		Project project = projectDao.selectByPrimaryKey(task.getProjectId());
		Assert.notNull(project, "project not found");

		startPipeline(key, expression, trigger, project, task, taskDetails);
	}

	/**
	 * Starting pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 * @param project
	 * @param task
	 * @param taskDetails
	 */
	public void startPipeline(String key, String expression, Trigger trigger, Project project, Task task,
			List<TaskDetail> taskDetails) {
		log.info(
				"into DynamicTask.startCron prarms::"
						+ "triggerId = {} , expression = {} , trigger = {} , project = {} , task = {} , taskDetails = {} ",
				key, expression, trigger, project, task, taskDetails);
		if (map.containsKey(key)) {
			stopPipeline(key);
		}
		if (trigger.getEnable() != 1) {
			return;
		}

		TimingPipelineHandler handler = beanFactory.getBean(TimingPipelineHandler.class,
				new Object[] { trigger, project, task, taskDetails });
		ScheduledFuture<?> future = scheduler.schedule(handler, new CronTrigger(expression));
		// TODO distributed cluster??
		PipelineScheduleManager.map.put(key, future);
	}

	/**
	 * Stopping pipeline job.
	 * 
	 * @param key
	 */
	public void stopPipeline(String key) {
		log.info("into DynamicTask.stopCron prarms::" + "triggerId = {} ", key);
		ScheduledFuture<?> future = PipelineScheduleManager.map.get(key);
		if (future != null) {
			future.cancel(true);
		}
	}

}