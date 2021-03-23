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

import java.util.Optional;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.uds.service.elasticjobcloud.domain.RegistryCenterConfiguration;
import com.wl4g.dopaas.uds.service.elasticjobcloud.domain.RegistryCenterConfigurations;

/**
 * Registry center configuration service.
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/registryCenterConfig-service")
public interface RegistryCenterConfigService {

	/**
	 * Load all registry center configurations.
	 *
	 * @return all registry center configurations
	 */
	@RequestMapping(path = "loadAll", method = GET)
	RegistryCenterConfigurations loadAll();

	/**
	 * Load registry center configuration.
	 *
	 * @param name
	 *            name of registry center configuration
	 * @return registry center configuration
	 */
	@RequestMapping(path = "load", method = GET)
	RegistryCenterConfiguration load(@RequestParam("name") String name);

	/**
	 * Find registry center configuration.
	 * 
	 * @param name
	 *            name of registry center configuration
	 * @param configs
	 *            registry center configurations
	 * @return registry center configuration
	 */

	@RequestMapping(path = "find", method = GET)
	RegistryCenterConfiguration find(@RequestParam("name") String name, @RequestBody RegistryCenterConfigurations configs);

	/**
	 * Load activated registry center configuration.
	 *
	 * @return activated registry center configuration
	 */
	@RequestMapping(path = "loadActivated", method = GET)
	Optional<RegistryCenterConfiguration> loadActivated();

	/**
	 * Add registry center configuration.
	 *
	 * @param config
	 *            registry center configuration
	 * @return success to add or not
	 */
	@RequestMapping(path = "add", method = POST)
	boolean add(@RequestBody RegistryCenterConfiguration config);

	/**
	 * Delete registry center configuration.
	 *
	 * @param name
	 *            name of registry center configuration
	 */
	@RequestMapping(path = "delete", method = POST)
	void delete(@RequestParam("name") String name);
}
