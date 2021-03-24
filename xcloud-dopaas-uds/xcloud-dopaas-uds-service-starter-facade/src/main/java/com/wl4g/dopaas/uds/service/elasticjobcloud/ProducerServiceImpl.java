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

import java.util.Optional;

import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.infra.exception.JobConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.dopaas.uds.service.elasticjobcloud.exception.AppConfigurationException;
import com.wl4g.dopaas.uds.service.elasticjobcloud.model.CloudAppConfigurationPOJO;

/**
 * Producer manager.
 */
@Service
public class ProducerServiceImpl implements ProducerService {

	private @Autowired CloudAppConfigService appConfigService;
	private @Autowired CloudJobConfigService configService;
	private @Autowired DisableJobService disableJobService;

	/**
	 * Register the job.
	 * 
	 * @param cloudJobConfig
	 *            cloud job configuration
	 */
	@Override
	public void register(final CloudJobConfigurationPOJO cloudJobConfig) {
		if (disableJobService.isDisabled(cloudJobConfig.getJobName())) {
			throw new JobConfigurationException("Job '%s' has been disable.", cloudJobConfig.getJobName());
		}
		Optional<CloudAppConfigurationPOJO> appConfigFromZk = appConfigService.load(cloudJobConfig.getAppName());
		if (!appConfigFromZk.isPresent()) {
			throw new AppConfigurationException("Register app '%s' firstly.", cloudJobConfig.getAppName());
		}
		Optional<CloudJobConfigurationPOJO> jobConfigFromZk = configService.load(cloudJobConfig.getJobName());
		if (jobConfigFromZk.isPresent()) {
			throw new JobConfigurationException("Job '%s' already existed.", cloudJobConfig.getJobName());
		}
		configService.add(cloudJobConfig);
	}

	/**
	 * Update the job.
	 *
	 * @param cloudJobConfig
	 *            cloud job configuration
	 */
	@Override
	public void update(final CloudJobConfigurationPOJO cloudJobConfig) {
		Optional<CloudJobConfigurationPOJO> jobConfigFromZk = configService.load(cloudJobConfig.getJobName());
		if (!jobConfigFromZk.isPresent()) {
			throw new JobConfigurationException("Cannot found job '%s', please register first.", cloudJobConfig.getJobName());
		}
		configService.update(cloudJobConfig);
	}

	/**
	 * Deregister the job.
	 * 
	 * @param jobName
	 *            job name
	 */
	@Override
	public void deregister(final String jobName) {
		Optional<CloudJobConfigurationPOJO> jobConfig = configService.load(jobName);
		if (jobConfig.isPresent()) {
			disableJobService.remove(jobName);
			configService.remove(jobName);
		}
	}
}
