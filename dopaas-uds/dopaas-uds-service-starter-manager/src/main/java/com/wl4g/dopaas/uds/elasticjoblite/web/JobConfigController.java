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

package com.wl4g.dopaas.uds.elasticjoblite.web;

import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.uds.service.elasticjoblite.JobAPIService;

/**
 * Job configuration RESTful API.
 */
@RestController
@RequestMapping("/api/jobs/config")
public class JobConfigController extends BaseController {

	private @Autowired JobAPIService jobAPIService;

	/**
	 * Get job configuration.
	 *
	 * @param jobName
	 *            job name
	 * @return job configuration
	 */
	@GetMapping(value = "/{jobName:.+}")
	public RespBase<JobConfigurationPOJO> getJobConfig(@PathVariable("jobName") final String jobName) {
		JobConfigurationPOJO data = jobAPIService.getJobConfiguration(jobName);
		data.setJobExtraConfigurations(null);
		return RespBase.<JobConfigurationPOJO> create().withData(data);
	}

	/**
	 * Update job configuration.
	 *
	 * @param jobConfiguration
	 *            job configuration
	 */
	@PutMapping
	public RespBase<Boolean> updateJobConfig(@RequestBody final JobConfigurationPOJO jobConfiguration) {
		jobConfiguration.setJobExtraConfigurations(
				jobAPIService.getJobConfiguration(jobConfiguration.getJobName()).getJobExtraConfigurations());
		jobAPIService.updateJobConfiguration(jobConfiguration);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Remove job configuration.
	 *
	 * @param jobName
	 *            job name
	 */
	@DeleteMapping("/{jobName:.+}")
	public RespBase<Boolean> removeJob(@PathVariable("jobName") final String jobName) {
		jobAPIService.removeJobConfiguration(jobName);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

}
