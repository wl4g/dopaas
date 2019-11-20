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
package com.wl4g.devops.ci.pipeline.timing;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskInstance;
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
public class TimingPipelineManager implements ApplicationRunner {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	private static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected PipelineManager pipeline;
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
		List<TaskInstance> taskInstances = taskDetailDao.selectByTaskId(trigger.getTaskId());
		Assert.notNull(task, "task not found");
		Assert.notEmpty(taskInstances, "taskInstances is empty");
		Project project = projectDao.selectByPrimaryKey(task.getProjectId());
		Assert.notNull(project, "project not found");

		startPipeline(key, expression, trigger, project, task, taskInstances);
	}

	/**
	 * Starting pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 * @param project
	 * @param task
	 * @param taskInstances
	 */
	public void startPipeline(String key, String expression, Trigger trigger, Project project, Task task,
			List<TaskInstance> taskInstances) {
		log.info(
				"into DynamicTask.startCron prarms::"
						+ "triggerId = {} , expression = {} , trigger = {} , project = {} , task = {} , taskInstances = {} ",
				key, expression, trigger, project, task, taskInstances);
		if (map.containsKey(key)) {
			stopPipeline(key);
		}
		if (trigger.getEnable() != 1) {
			return;
		}

		TimingPipelineProvider handler = beanFactory.getBean(TimingPipelineProvider.class,
				new Object[] { trigger, project, task, taskInstances});
		ScheduledFuture<?> future = scheduler.schedule(handler, new CronTrigger(expression));
		// TODO distributed cluster??
		TimingPipelineManager.map.put(key, future);
	}

	/**
	 * Stopping pipeline job.
	 * 
	 * @param key
	 */
	public void stopPipeline(String key) {
		log.info("into DynamicTask.stopCron prarms::" + "triggerId = {} ", key);
		ScheduledFuture<?> future = TimingPipelineManager.map.get(key);
		if (future != null) {
			future.cancel(true);
		}
	}

}