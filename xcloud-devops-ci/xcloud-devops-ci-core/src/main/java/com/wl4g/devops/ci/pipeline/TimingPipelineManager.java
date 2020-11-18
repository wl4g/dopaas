/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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

import com.wl4g.devops.ci.config.CiProperties;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.data.PipelineDao;
import com.wl4g.devops.ci.data.ProjectDao;
import com.wl4g.devops.ci.data.TriggerDao;
import com.wl4g.devops.ci.pipeline.provider.TimingPipelineProvider;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.Trigger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.wl4g.components.core.constants.CiDevOpsConstants.TASK_TYPE_TIMMING;
import static java.util.Objects.nonNull;

/**
 * Pipeline timing scheduler manager
 *
 * @author vjay
 * @date 2019-07-19 09:50:00
 */
public class TimingPipelineManager implements ApplicationRunner {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	private static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	protected CiProperties config;
	@Autowired
	protected BeanFactory beanFactory;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;
	@Autowired
	protected PipelineManager pipeline;

	@Autowired
	protected TriggerDao triggerDao;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected PipelineDao pipelineDao;

	@Override
	public void run(ApplicationArguments args) {
		refreshAll();
	}

	/**
	 * Refresh timing pipeline job all.
	 */
	private void refreshAll() {
		List<Trigger> triggers = triggerDao.selectByType(TASK_TYPE_TIMMING);
		for (Trigger trigger : triggers) {
			refreshPipeline(trigger.getId().toString(), trigger.getCron(), trigger);
		}
	}

	/**
	 * Refresh pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 */
	public void refreshPipeline(String key, String expression, Trigger trigger) {
		log.info("Refresh timing pipeline for key:'{}', expression: '{}', triggerId: {}", key, expression, trigger.getId());

		// Check stopped?
		stopPipeline(trigger);

		Pipeline pipeline = pipelineDao.selectByPrimaryKey(trigger.getTaskId());

		// Startup to pipeline.
		startPipeline(trigger, pipeline);
	}

	/**
	 * Startup pipeline job.
	 * 
	 * @param trigger
	 * @param project
	 * @param task
	 * @param taskInstances
	 */
	private void startPipeline(Trigger trigger, Pipeline pipeline) {
		stopPipeline(trigger);

		if (trigger.getEnable() != 1) {
			return;
		}

		TimingPipelineProvider provider = beanFactory.getBean(TimingPipelineProvider.class, new Object[] { trigger, pipeline });

		ScheduledFuture<?> future = scheduler.schedule(provider, new CronTrigger(trigger.getCron()));

		// TODO distributed cluster??
		TimingPipelineManager.map.put(getTimingPipelineKey(trigger), future);
	}

	/**
	 * Stopping pipeline job.
	 * 
	 * @param trigger
	 */
	public void stopPipeline(Trigger trigger) {
		if (log.isInfoEnabled()) {
			log.info("Stopping timing pipeline for triggerId: {}, taskId: {}, expression: '{}'", trigger.getId(),
					trigger.getTaskId(), trigger.getCron());
		}

		String key = getTimingPipelineKey(trigger);
		ScheduledFuture<?> future = TimingPipelineManager.map.get(key);

		if (nonNull(future)) {
			boolean cancel = future.cancel(true);
			if (cancel) {
				map.remove(key);
			} else {
				throw new IllegalStateException(String.format("Failed to stopped timing pipeline of '%s'", key));
			}
		}
	}

	/**
	 * Get timing pipeline key.
	 * 
	 * @param trigger
	 * @return
	 */
	private String getTimingPipelineKey(Trigger trigger) {
		return trigger.getId() + "";
	}

}