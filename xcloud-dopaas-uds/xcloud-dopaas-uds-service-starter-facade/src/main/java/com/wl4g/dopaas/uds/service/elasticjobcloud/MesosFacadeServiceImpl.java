/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.dopaas.uds.service.elasticjobcloud;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.infra.context.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Mesos facade service.
 */
@Service
public class MesosFacadeServiceImpl implements MesosFacadeService {

	private @Autowired CloudJobConfigService cloudJobConfigService;
	private @Autowired ReadyService readyService;
	private @Autowired RunningService runningService;
	private @Autowired FailoverService failoverService;
	private @Autowired DisableAppService disableAppService;
	private @Autowired DisableJobService disableJobService;

	/**
	 * Add transient job to ready queue.
	 *
	 * @param jobName
	 *            job name
	 */
	public void addTransient(final String jobName) {
		readyService.addTransient(jobName);
	}

	/**
	 * Load cloud job config.
	 *
	 * @param jobName
	 *            job name
	 * @return cloud job config
	 */
	@Override
	public Optional<CloudJobConfigurationPOJO> load(final String jobName) {
		return cloudJobConfigService.load(jobName);
	}

	/**
	 * Get all ready tasks.
	 *
	 * @return ready tasks
	 */
	@Override
	public Map<String, Integer> getAllReadyTasks() {
		return readyService.getAllReadyTasks();
	}

	/**
	 * Get all running tasks.
	 *
	 * @return running tasks
	 */
	@Override
	public Map<String, Set<TaskContext>> getAllRunningTasks() {
		return runningService.getAllRunningTasks();
	}

	/**
	 * Get all failover tasks.
	 *
	 * @return failover tasks
	 */
	@Override
	public Map<String, Collection<FailoverTaskInfo>> getAllFailoverTasks() {
		return failoverService.getAllFailoverTasks();
	}

	/**
	 * Determine whether the job is disable or not.
	 *
	 * @param jobName
	 *            job name
	 * @return true is disabled, otherwise not
	 */
	@Override
	public boolean isJobDisabled(final String jobName) {
		Optional<CloudJobConfigurationPOJO> jobConfiguration = cloudJobConfigService.load(jobName);
		return !jobConfiguration.isPresent() || disableAppService.isDisabled(jobConfiguration.get().getAppName())
				|| disableJobService.isDisabled(jobName);
	}

	/**
	 * Enable job.
	 *
	 * @param jobName
	 *            job name
	 */
	@Override
	public void enableJob(final String jobName) {
		disableJobService.remove(jobName);
	}

	/**
	 * Disable job.
	 *
	 * @param jobName
	 *            job name
	 */
	@Override
	public void disableJob(final String jobName) {
		disableJobService.add(jobName);
	}
}
