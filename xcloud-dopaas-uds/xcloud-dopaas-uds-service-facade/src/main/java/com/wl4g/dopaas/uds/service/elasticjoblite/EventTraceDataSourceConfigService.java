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

import java.util.Optional;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.uds.service.elasticjoblite.domain.EventTraceDataSourceConfig;
import com.wl4g.dopaas.uds.service.elasticjoblite.domain.EventTraceDataSourceConfigs;

/**
 * Event trace data source configuration service.
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/eventTraceDataSourceConfig-service")
public interface EventTraceDataSourceConfigService {

	/**
	 * Load all event trace data source configurations.
	 *
	 * @return all event trace data source configuration
	 */
	@RequestMapping(path = "loadAll", method = GET)
	EventTraceDataSourceConfigs loadAll();

	/**
	 * Load event trace data source configuration.
	 * 
	 * @param name
	 *            name of event trace data source configuration
	 * @return event trace data source configuration
	 */
	@RequestMapping(path = "load", method = GET)
	EventTraceDataSourceConfig load(@RequestParam(name = "name") String name);

	/**
	 * Find event trace data source configuration.
	 *
	 * @param name
	 *            name of event trace data source configuration
	 * @param configs
	 *            event trace data source configurations
	 * @return event trace data source configuration
	 */
	@RequestMapping(path = "find", method = GET)
	EventTraceDataSourceConfig find(@RequestParam(name = "name") String name, @RequestBody EventTraceDataSourceConfigs configs);

	/**
	 * Load activated event trace data source configuration.
	 * 
	 * @return activated event trace data source configuration
	 */
	@RequestMapping(path = "loadActivated", method = POST)
	Optional<EventTraceDataSourceConfig> loadActivated();

	/**
	 * Add event trace data source configuration.
	 * 
	 * @param config
	 *            event trace data source configuration
	 * @return success to add or not
	 */
	@RequestMapping(path = "add", method = POST)
	boolean add(@RequestBody EventTraceDataSourceConfig config);

	/**
	 * Delete event trace data source configuration.
	 *
	 * @param name
	 *            name of event trace data source configuration
	 */
	@RequestMapping(path = "delete", method = POST)
	void delete(@RequestParam(name = "name") String name);
}
