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

import java.sql.Driver;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.uds.service.elasticjoblite.EventTraceDataSourceConfigService;
import com.wl4g.dopaas.uds.service.elasticjoblite.domain.EventTraceDataSourceConfig;

/**
 * Event trace data source RESTful API.
 */
@RestController
@RequestMapping("/api/data-source")
public class EventTraceDataSourceController extends BaseController {
	public static final String DATA_SOURCE_CONFIG_KEY = "data_source_config_key";

	private @Autowired EventTraceDataSourceConfigService eventTraceDataSourceConfigService;

	/**
	 * Get all available driver classes.
	 *
	 * @return Driver classes
	 */
	@GetMapping("/drivers")
	public RespBase<Collection<String>> availableDrivers() {
		RespBase<Collection<String>> resp = RespBase.create();
		Collection<String> result = new HashSet<>();
		ServiceLoader.load(Driver.class).forEach(each -> result.add(each.getClass().getName()));
		return resp.withData(result);
	}

	/**
	 * Judge whether event trace data source is activated.
	 *
	 * @param request
	 *            HTTP request
	 * @return event trace data source is activated or not
	 */
	@GetMapping("/activated")
	public RespBase<Boolean> activated(final HttpServletRequest request) {
		RespBase<Boolean> resp = RespBase.create();
		boolean result = eventTraceDataSourceConfigService.loadActivated().isPresent();
		return resp.withData(result);
	}

	/**
	 * Load event trace data source configuration.
	 *
	 * @param request
	 *            HTTP request
	 * @return event trace data source configurations
	 */
	@GetMapping("/load")
	public RespBase<Collection<EventTraceDataSourceConfig>> load(final HttpServletRequest request) {
		RespBase<Collection<EventTraceDataSourceConfig>> resp = RespBase.create();
		eventTraceDataSourceConfigService.loadActivated().ifPresent(
				eventTraceDataSourceConfig -> setDataSourceNameToSession(eventTraceDataSourceConfig, request.getSession()));
		return resp.withData(eventTraceDataSourceConfigService.loadAll().getEventTraceDataSourceConfiguration());
	}

	/**
	 * Add event trace data source configuration.
	 *
	 * @param config
	 *            event trace data source configuration
	 * @return success to added or not
	 */
	@PostMapping("/add")
	public RespBase<Boolean> add(@RequestBody final EventTraceDataSourceConfig config) {
		RespBase<Boolean> resp = RespBase.create();
		return resp.withData(eventTraceDataSourceConfigService.add(config));
	}

	/**
	 * Delete event trace data source configuration.
	 *
	 * @param config
	 *            event trace data source configuration
	 */
	@DeleteMapping
	public RespBase<?> delete(@RequestBody final EventTraceDataSourceConfig config) {
		eventTraceDataSourceConfigService.delete(config.getName());
		return RespBase.create();
	}

	/**
	 * Test event trace data source connection.
	 *
	 * @param config
	 *            event trace data source configuration
	 * @param request
	 *            HTTP request
	 * @return success or not
	 */
	@PostMapping(value = "/connectTest")
	public RespBase<Boolean> connectTest(@RequestBody final EventTraceDataSourceConfig config, final HttpServletRequest request) {
		RespBase<Boolean> resp = RespBase.create();
		setDataSourceNameToSession(config, request.getSession());
		return resp.withData(true);
	}

	/**
	 * Connect event trace data source.
	 *
	 * @param config
	 *            event trace data source
	 * @param request
	 *            HTTP request
	 * @return success or not
	 */
	@PostMapping(value = "/connect")
	public RespBase<Boolean> connect(@RequestBody final EventTraceDataSourceConfig config, final HttpServletRequest request) {
		RespBase<Boolean> resp = RespBase.create();
		setDataSourceNameToSession(
				eventTraceDataSourceConfigService.find(config.getName(), eventTraceDataSourceConfigService.loadAll()),
				request.getSession());
		eventTraceDataSourceConfigService.load(config.getName());
		return resp.withData(true);
	}

	private void setDataSourceNameToSession(final EventTraceDataSourceConfig dataSourceConfig, final HttpSession session) {
		// session.setAttribute(DATA_SOURCE_CONFIG_KEY, dataSourceConfig);
		// EventTraceDataSourceFactory.createEventTraceDataSource(dataSourceConfig.getDriver(),
		// dataSourceConfig.getUrl(),
		// dataSourceConfig.getUsername(), dataSourceConfig.getPassword());
		// LiteSessionEventTraceDataSourceFactory
		// .setDataSourceConfiguration((EventTraceDataSourceConfig)
		// session.getAttribute(DATA_SOURCE_CONFIG_KEY));
	}

}
