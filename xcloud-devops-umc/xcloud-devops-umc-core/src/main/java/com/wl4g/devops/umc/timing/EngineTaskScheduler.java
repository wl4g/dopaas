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
package com.wl4g.devops.umc.timing;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.components.core.bean.umc.CustomEngine;
import com.wl4g.devops.dao.umc.CustomEngineDao;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.notNull;

/**
 * Pipeline timing scheduler manager
 *
 * @author vjay
 * @date 2019-07-19 09:50:00
 */
public class EngineTaskScheduler implements ApplicationRunner {

	final protected SmartLogger log = SmartLoggerFactory.getLogger(getClass());

	final public static int STOP = 0;
	final public static int WAIT = 1;
	final public static int RUNNING = 2;

	private static ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	protected BeanFactory beanFactory;

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private CustomEngineDao customEngineDao;


	@Override
	public void run(ApplicationArguments args) {
		refreshAll();
	}

	/**
	 * Refresh timing pipeline job all.
	 */
	private void refreshAll() {
		List<CustomEngine> customEngines = customEngineDao.list(null);//select all
		for (CustomEngine customEngine : customEngines) {
			refreshTimingPipeline(customEngine);
		}
	}

	/**
	 * Refresh pipeline job.
	 * 
	 * @param key
	 * @param expression
	 * @param trigger
	 */
	public void refreshTimingPipeline(CustomEngine customEngine) {
		if (log.isInfoEnabled()) {
			log.info("Refresh timing pipeline for customEngineId:'{}', expression: '{}'", customEngine.getId(), customEngine.getCron());
		}
		customEngine = customEngineDao.selectByPrimaryKey(customEngine.getId());
		stopTimingPipeline(customEngine);
		notNull(customEngine, "customEngine is null");
		// Startup to pipeline.
		startupTimingPipeline(customEngine);
	}

	/**
	 * Startup pipeline job.
	 * 
	 * @param trigger
	 * @param project
	 * @param task
	 * @param taskInstances
	 */
	private void startupTimingPipeline(CustomEngine customEngine) {
		log.info("Startup timing pipeline for customEngineId: {}, expression: '{}'", customEngine.getId(), customEngine.getCron());
		stopTimingPipeline(customEngine);
		if (Objects.isNull(customEngine.getStatus()) ||  customEngine.getStatus() == 0) {
			return;
		}
		CronTrigger cronTrigger = checkCron(customEngine.getCron());
		if(Objects.isNull(cronTrigger)){
			log.error("cron is error, cron={}",customEngine.getCron());
			return;
		}
		TimingEngineProvider provider = beanFactory.getBean(TimingEngineProvider.class, new Object[] {customEngine});
		ScheduledFuture<?> future = taskScheduler.schedule(provider, new CronTrigger(customEngine.getCron()));
		EngineTaskScheduler.map.put(getTimingPipelineKey(customEngine.getId()), future);
	}

	/**
	 * Stopping pipeline job.
	 * 
	 * @param trigger
	 */
	public void stopTimingPipeline(CustomEngine customEngine) {
		log.info("Stopping timing  for customEngineId: {}, expression: '{}'", customEngine.getId(), customEngine.getCron());
		String key = getTimingPipelineKey(customEngine.getId());
		ScheduledFuture<?> future = EngineTaskScheduler.map.get(key);
		if (nonNull(future)) {
			boolean cancel = future.cancel(true);
			if (cancel) {
				map.remove(key);
			}else{
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
	private String getTimingPipelineKey(Integer id) {
		return id + "";
	}


	private CronTrigger checkCron(String cron){
		try {
			return new CronTrigger(cron);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

}