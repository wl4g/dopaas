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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.infra.context.TaskContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;

/**
 * Mesos facade service.
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/mesosFacade-service")
public interface MesosFacadeService {

	/**
	 * Add transient job to ready queue.
	 *
	 * @param jobName
	 *            job name
	 */
	@RequestMapping(path = "addTransient", method = POST)
	void addTransient(@RequestParam("jobName") String jobName);

	/**
	 * Load cloud job config.
	 *
	 * @param jobName
	 *            job name
	 * @return cloud job config
	 */
	@RequestMapping(path = "load", method = GET)
	Optional<CloudJobConfigurationPOJO> load(@RequestParam("jobName") String jobName);

	/**
	 * Get all ready tasks.
	 *
	 * @return ready tasks
	 */
	@RequestMapping(path = "getAllReadyTasks", method = GET)
	Map<String, Integer> getAllReadyTasks();

	/**
	 * Get all running tasks.
	 *
	 * @return running tasks
	 */
	@RequestMapping(path = "getAllRunningTasks", method = GET)
	Map<String, Set<TaskContext>> getAllRunningTasks();

	/**
	 * Get all failover tasks.
	 *
	 * @return failover tasks
	 */
	@RequestMapping(path = "getAllFailoverTasks", method = GET)
	Map<String, Collection<FailoverTaskInfo>> getAllFailoverTasks();

	/**
	 * Determine whether the job is disable or not.
	 *
	 * @param jobName
	 *            job name
	 * @return true is disabled, otherwise not
	 */
	@RequestMapping(path = "isJobDisabled", method = GET)
	boolean isJobDisabled(@RequestParam("jobName") String jobName);

	/**
	 * Enable job.
	 *
	 * @param jobName
	 *            job name
	 */
	@RequestMapping(path = "enableJob", method = POST)
	void enableJob(@RequestParam("jobName") String jobName);

	/**
	 * Disable job.
	 *
	 * @param jobName
	 *            job name
	 */
	@RequestMapping(path = "disableJob", method = POST)
	void disableJob(@RequestParam("jobName") String jobName);

}
