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

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.uds.service.elasticjobcloud.model.CloudAppConfigurationPOJO;

/**
 * Cloud app configuration service.
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/cloudAppConfig-service")
public interface CloudAppConfigService {

	/**
	 * Add cloud app configuration.
	 *
	 * @param appConfig
	 *            cloud app configuration
	 */
	@RequestMapping(path = "add", method = POST)
	void add(@RequestBody CloudAppConfigurationPOJO appConfig);

	/**
	 * Update cloud app configuration.
	 *
	 * @param appConfig
	 *            cloud app configuration
	 */
	@RequestMapping(path = "update", method = POST)
	void update(@RequestBody CloudAppConfigurationPOJO appConfig);

	/**
	 * Load app configuration by app name.
	 *
	 * @param appName
	 *            application name
	 * @return cloud app configuration
	 */
	@RequestMapping(path = "load", method = GET)
	Optional<CloudAppConfigurationPOJO> load(@RequestParam("appName") String appName);

	/**
	 * Load all registered cloud app configurations.
	 *
	 * @return collection of the registered cloud app configuration
	 */
	@RequestMapping(path = "loadAll", method = GET)
	Collection<CloudAppConfigurationPOJO> loadAll();

	/**
	 * Remove cloud app configuration by app name.
	 *
	 * @param appName
	 *            to be removed application name
	 */
	@RequestMapping(path = "remove", method = POST)
	void remove(@RequestParam("appName") String appName);

}
