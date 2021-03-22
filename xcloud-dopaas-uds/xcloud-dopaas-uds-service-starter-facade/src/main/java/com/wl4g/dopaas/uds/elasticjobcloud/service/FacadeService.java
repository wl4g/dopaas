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

package com.wl4g.dopaas.uds.elasticjobcloud.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.infra.context.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.dopaas.uds.elasticjobcloud.service.job.CloudJobConfigurationService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.disable.app.DisableAppService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.disable.job.DisableJobService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.failover.FailoverService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.failover.FailoverTaskInfo;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.ready.ReadyService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.running.RunningService;

/**
 * Mesos facade service.
 */
@Service
public final class FacadeService {

	@Autowired
	private CloudJobConfigurationService jobConfigService;

	@Autowired
	private ReadyService readyService;

	@Autowired
	private RunningService runningService;

	@Autowired
	private FailoverService failoverService;

	@Autowired
	private DisableAppService disableAppService;

	@Autowired
	private DisableJobService disableJobService;

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
	public Optional<CloudJobConfigurationPOJO> load(final String jobName) {
		return jobConfigService.load(jobName);
	}

	/**
	 * Get all ready tasks.
	 *
	 * @return ready tasks
	 */
	public Map<String, Integer> getAllReadyTasks() {
		return readyService.getAllReadyTasks();
	}

	/**
	 * Get all running tasks.
	 *
	 * @return running tasks
	 */
	public Map<String, Set<TaskContext>> getAllRunningTasks() {
		return runningService.getAllRunningTasks();
	}

	/**
	 * Get all failover tasks.
	 *
	 * @return failover tasks
	 */
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
	public boolean isJobDisabled(final String jobName) {
		Optional<CloudJobConfigurationPOJO> jobConfiguration = jobConfigService.load(jobName);
		return !jobConfiguration.isPresent() || disableAppService.isDisabled(jobConfiguration.get().getAppName())
				|| disableJobService.isDisabled(jobName);
	}

	/**
	 * Enable job.
	 *
	 * @param jobName
	 *            job name
	 */
	public void enableJob(final String jobName) {
		disableJobService.remove(jobName);
	}

	/**
	 * Disable job.
	 *
	 * @param jobName
	 *            job name
	 */
	public void disableJob(final String jobName) {
		disableJobService.add(jobName);
	}
}
