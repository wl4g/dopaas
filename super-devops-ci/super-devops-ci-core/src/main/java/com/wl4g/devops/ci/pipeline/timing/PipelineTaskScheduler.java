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
import static org.springframework.util.Assert.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Pipeline timing scheduler manager
 *
 * @author vjay
 * @date 2019-07-19 09:50:00
 */
public class PipelineTaskScheduler implements ApplicationRunner {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	private static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected PipelineManager pipeline;
	@Autowired
	protected BeanFactory beanFactory;
	@Autowired
	private ThreadPoolTaskScheduler scheduler;

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
		refreshTimingPipelineAll();
	}

	/**
	 * Refresh timing pipeline job all.
	 */
	private void refreshTimingPipelineAll() {
		List<Trigger> triggers = triggerDao.selectByType(TASK_TYPE_TIMMING);
		for (Trigger trigger : triggers) {
			refreshTimingPipeline(trigger.getId().toString(), trigger.getCron(), trigger);
		}
	}

	/**
	 * Refresh pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 */
	public void refreshTimingPipeline(String key, String expression, Trigger trigger) {
		if (log.isInfoEnabled()) {
			log.info("Refresh timing pipeline for key:'{}', expression: '{}', trigger: {}", key, expression, trigger);
		}
		// Check stopped?
		if (!stopTimingPipeline(key)) {
			throw new IllegalStateException(String.format("Failed to stopped timing pipeline of '%s'", key));
		}

		Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
		notNull(task, String.format("Timing pipeline not found for taskId:{}", trigger.getTaskId()));
		List<TaskInstance> instances = taskDetailDao.selectByTaskId(trigger.getTaskId());
		notEmpty(instances, String.format("Timing pipeline instances is empty for taskId:{}", trigger.getTaskId()));
		Project project = projectDao.selectByPrimaryKey(task.getProjectId());
		notNull(project, String.format("Timing pipeline project:(%s) not found", task.getProjectId()));

		// Startup to pipeline.
		startupTimingPipeline(key, expression, trigger, project, task, instances);
	}

	/**
	 * Startup pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 * @param project
	 * @param task
	 * @param taskInstances
	 */
	private void startupTimingPipeline(String key, String expression, Trigger trigger, Project project, Task task,
			List<TaskInstance> taskInstances) {
		if (log.isInfoEnabled()) {
			log.info(
					"Startup timing pipeline: triggerId = {} , expression = {} , trigger = {} , project = {} , task = {} , taskInstances = {} ",
					key, expression, trigger, project, task, taskInstances);
		}

		if (map.containsKey(key)) {
			stopTimingPipeline(key);
		}
		if (trigger.getEnable() != 1) {
			return;
		}

		TimingPipelineProvider provider = beanFactory.getBean(TimingPipelineProvider.class,
				new Object[] { trigger, project, task, taskInstances });
		ScheduledFuture<?> future = scheduler.schedule(provider, new CronTrigger(expression));
		// TODO distributed cluster??
		PipelineTaskScheduler.map.put(key, future);
	}

	/**
	 * Stop pipeline job.
	 * 
	 * @param key
	 * @return
	 */
	public boolean stopTimingPipeline(String key) {
		if (log.isInfoEnabled()) {
			log.info("into DynamicTask.stopCron prarms::" + "triggerId = {} ", key);
		}
		ScheduledFuture<?> future = PipelineTaskScheduler.map.get(key);
		if (future != null) {
			return future.cancel(true);
		}
		return false;
	}

}