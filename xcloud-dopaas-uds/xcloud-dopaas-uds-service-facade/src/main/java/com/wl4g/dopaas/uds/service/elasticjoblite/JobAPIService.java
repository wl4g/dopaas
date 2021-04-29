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

package com.wl4g.dopaas.uds.service.elasticjoblite;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Collection;

import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ServerBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ShardingInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;

@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/jobAPI-service")
public interface JobAPIService {

	// --- Job configuration. ---

	/**
	 * Gets Job configuration .
	 *
	 * @return job configuration
	 */
	@RequestMapping(path = "getJobConfiguration", method = GET)
	JobConfigurationPOJO getJobConfiguration(@RequestParam(name = "jobName") String jobName);

	/**
	 * Update job configuration.
	 *
	 * @param jobConfig
	 *            job configuration
	 */
	@RequestMapping(path = "updateJobConfiguration", method = POST)
	void updateJobConfiguration(@RequestBody JobConfigurationPOJO jobConfig);

	/**
	 * Remove job configuration.
	 *
	 * @param jobName
	 *            job name
	 */
	@RequestMapping(path = "removeJobConfiguration", method = POST)
	void removeJobConfiguration(@RequestParam(name = "jobName") String jobName);

	// --- Job Statistics. ---

	/**
	 * Get jobs total count.
	 *
	 * @return jobs total count.
	 */
	@RequestMapping(path = "getJobsTotalCount", method = GET)
	int getJobsTotalCount();

	/**
	 * Get all jobs brief info.
	 *
	 * @return all jobs brief info.
	 */
	@RequestMapping(path = "getAllJobsBriefInfo", method = GET)
	Collection<JobBriefInfo> getAllJobsBriefInfo();

	/**
	 * Get job brief info.
	 *
	 * @param jobName
	 *            job name
	 * @return job brief info
	 */
	@RequestMapping(path = "getJobBriefInfo", method = GET)
	JobBriefInfo getJobBriefInfo(@RequestParam(name = "jobName") String jobName);

	/**
	 * Get jobs brief info.
	 *
	 * @param ip
	 *            server IP address
	 * @return jobs brief info
	 */
	@RequestMapping(path = "getJobsBriefInfo", method = GET)
	Collection<JobBriefInfo> getJobsBriefInfo(@RequestParam(name = "ip") String ip);

	// --- Job operators. ---

	/**
	 * Trigger job to run at once.
	 *
	 * <p>
	 * Job will not start until it does not conflict with the last running job,
	 * and this tag will be automatically cleaned up after it starts.
	 * </p>
	 *
	 * @param jobName
	 *            job name
	 */
	@RequestMapping(path = "triggerJob", method = POST)
	void triggerJob(@RequestParam(name = "jobName") String jobName);

	/**
	 * Disable job.
	 * 
	 * <p>
	 * Will cause resharding.
	 * </p>
	 *
	 * @param jobName
	 *            job name
	 * @param serverIp
	 *            server IP address
	 */
	@RequestMapping(path = "disableJob", method = POST)
	void disableJob(@RequestParam(name = "jobName") String jobName, @RequestParam(name = "serverIp") String serverIp);

	/**
	 * Enable job.
	 * 
	 * @param jobName
	 *            job name
	 * @param serverIp
	 *            server IP address
	 */
	@RequestMapping(path = "enableJob", method = POST)
	void enableJob(@RequestParam(name = "jobName") String jobName, @RequestParam(name = "serverIp") String serverIp);

	/**
	 * Shutdown Job.
	 *
	 * @param jobName
	 *            job name
	 * @param serverIp
	 *            server IP address
	 */
	@RequestMapping(path = "shutdownJob", method = POST)
	void shutdownJob(@RequestParam(name = "jobName") String jobName, @RequestParam(name = "serverIp") String serverIp);

	/**
	 * Remove job.
	 * 
	 * @param jobName
	 *            job name
	 * @param serverIp
	 *            server IP address
	 */
	@RequestMapping(path = "removeJob", method = POST)
	void removeJob(@RequestParam(name = "jobName") String jobName, @RequestParam(name = "serverIp") String serverIp);

	// --- Sharding operators. ---

	/**
	 * Disable job sharding item.
	 * 
	 * @param jobName
	 *            job name
	 * @param item
	 *            sharding item
	 */
	@RequestMapping(path = "disableSharding", method = POST)
	void disableSharding(@RequestParam(name = "jobName") String jobName, @RequestParam(name = "item") String item);

	/**
	 * Enable job sharding item.
	 *
	 * @param jobName
	 *            job name
	 * @param item
	 *            sharding item
	 */
	@RequestMapping(path = "enableSharding", method = POST)
	void enableSharding(@RequestParam(name = "jobName") String jobName, @RequestParam(name = "item") String item);

	// --- Server statistics. ---

	/**
	 * Get servers total count.
	 *
	 * @return servers total count
	 */
	@RequestMapping(path = "getServersTotalCount", method = GET)
	int getServersTotalCount();

	/**
	 * Get all servers brief info.
	 *
	 * @return all servers brief info
	 */
	@RequestMapping(path = "getAllServersBriefInfo", method = GET)
	Collection<ServerBriefInfo> getAllServersBriefInfo();

	// --- Sharding statistics. ---

	/**
	 * Get sharding info.
	 *
	 * @param jobName
	 *            job name
	 * @return sharding info of job
	 */
	@RequestMapping(path = "getShardingInfo", method = GET)
	Collection<ShardingInfo> getShardingInfo(@RequestParam(name = "jobName") String jobName);

}
