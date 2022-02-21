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
import java.util.Optional;

import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;

/**
 * Cloud job configuration service.
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/cloudJobConfig-service")
public interface CloudJobConfigService {

	/**
	 * Add cloud job configuration.
	 * 
	 * @param cloudJobConfig
	 *            cloud job configuration
	 */
	@RequestMapping(path = "add", method = POST)
	void add(@RequestBody CloudJobConfigurationPOJO cloudJobConfig);

	/**
	 * Update cloud job configuration.
	 *
	 * @param cloudJobConfig
	 *            cloud job configuration
	 */
	@RequestMapping(path = "update", method = POST)
	void update(@RequestBody CloudJobConfigurationPOJO cloudJobConfig);

	/**
	 * Load all registered cloud job configurations.
	 *
	 * @return collection of the registered cloud job configuration
	 */
	@RequestMapping(path = "loadAll", method = GET)
	Collection<CloudJobConfigurationPOJO> loadAll();

	/**
	 * Load cloud job configuration by job name.
	 *
	 * @param jobName
	 *            job name
	 * @return cloud job configuration
	 */
	@RequestMapping(path = "load", method = GET)
	Optional<CloudJobConfigurationPOJO> load(@RequestParam("jobName") String jobName);

	/**
	 * Remove cloud job configuration.
	 *
	 * @param jobName
	 *            job name
	 */
	@RequestMapping(path = "remove", method = POST)
	void remove(@RequestParam("jobName") String jobName);

}
