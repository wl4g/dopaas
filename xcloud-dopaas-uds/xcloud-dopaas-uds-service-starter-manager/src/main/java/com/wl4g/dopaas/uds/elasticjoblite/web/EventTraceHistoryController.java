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

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.common.bean.uds.elasticjoblite.JobExecutionEvent;
import com.wl4g.dopaas.common.bean.uds.elasticjoblite.JobStatusTraceEvent;
import com.wl4g.dopaas.uds.service.elasticjoblite.EventTraceHistoryService;
import com.wl4g.dopaas.uds.service.elasticjoblite.model.FindJobExecutionEventsRequest;
import com.wl4g.dopaas.uds.service.elasticjoblite.model.FindJobStatusTraceEventsRequest;

/**
 * Event trace history RESTful API.
 */
@RestController
@RequestMapping("/api/event-trace")
public class EventTraceHistoryController extends BaseController {

	private @Autowired EventTraceHistoryService eventTraceHistoryService;

	/**
	 * Find job execution events.
	 *
	 * @param requestParams
	 *            query criteria
	 * @return job execution event trace result
	 */
	@PostMapping(value = "/execution")
	public RespBase<PageHolder<JobExecutionEvent>> findJobExecutionEvents(
			@RequestBody final FindJobExecutionEventsRequest requestParams) {
		RespBase<PageHolder<JobExecutionEvent>> resp = RespBase.create();

		Page<JobExecutionEvent> events = eventTraceHistoryService.findJobExecutionEvents(requestParams);
		PageHolder<JobExecutionEvent> pageHolder = new PageHolder<>();
		pageHolder.setTotal(events.getTotalElements());
		pageHolder.setRecords(events.getContent());

		return resp.withData(pageHolder);
	}

	/**
	 * Find all job names with specific prefix.
	 *
	 * @param jobNamePrefix
	 *            job name prefix
	 * @return matched job names
	 */
	@GetMapping(value = { "/execution/jobNames", "/execution/jobNames/{jobNamePrefix:.+}" })
	public RespBase<List<String>> findJobNamesByPrefix(@PathVariable(required = false) final String jobNamePrefix) {
		return RespBase.<List<String>> create()
				.withData(eventTraceHistoryService.findJobNamesInExecutionLog(Optional.ofNullable(jobNamePrefix).orElse("")));
	}

	/**
	 * Find all ip addresses with specific prefix.
	 * 
	 * @param ipPrefix
	 *            ip prefix
	 * @return matched ip addresses
	 */
	@GetMapping(value = { "/execution/ip", "/execution/ip/{ipPrefix:.+}" })
	public RespBase<List<String>> findIpByPrefix(@PathVariable(required = false) final String ipPrefix) {
		return RespBase.<List<String>> create()
				.withData(eventTraceHistoryService.findIpInExecutionLog(Optional.ofNullable(ipPrefix).orElse("")));
	}

	/**
	 * Find job status trace events.
	 *
	 * @param requestParams
	 *            query criteria
	 * @return job status trace result
	 */
	@PostMapping(value = "/status")
	public RespBase<PageHolder<JobStatusTraceEvent>> findJobStatusTraceEvents(
			@RequestBody final FindJobStatusTraceEventsRequest requestParams) {
		RespBase<PageHolder<JobStatusTraceEvent>> resp = RespBase.create();

		Page<JobStatusTraceEvent> events = eventTraceHistoryService.findJobStatusTraceEvents(requestParams);
		PageHolder<JobStatusTraceEvent> pageHolder = new PageHolder<>();
		pageHolder.setTotal(events.getTotalElements());
		pageHolder.setRecords(events.getContent());

		return resp.withData(pageHolder);
	}

	/**
	 * Find all job names with specific prefix in status trace log.
	 *
	 * @param jobNamePrefix
	 *            job name prefix
	 * @return matched job names
	 */
	@GetMapping(value = { "/status/jobNames", "/status/jobNames/{jobNamePrefix:.+}" })
	public RespBase<List<String>> findJobNamesByPrefixInStatusTraceLog(
			@PathVariable(required = false) final String jobNamePrefix) {
		return RespBase.<List<String>> create()
				.withData(eventTraceHistoryService.findJobNamesInStatusTraceLog(Optional.ofNullable(jobNamePrefix).orElse("")));
	}

	// @ModelAttribute
	// private void initDataSource() {
	// eventTraceDataSourceConfigService.loadActivated()
	// .ifPresent(LiteSessionEventTraceDataSourceFactory::setDataSourceConfiguration);
	// }

}
