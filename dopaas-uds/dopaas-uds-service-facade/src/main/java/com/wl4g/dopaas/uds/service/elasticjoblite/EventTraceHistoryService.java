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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.infra.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.uds.elasticjoblite.JobExecutionEvent;
import com.wl4g.dopaas.common.bean.uds.elasticjoblite.JobStatusTraceEvent;
import com.wl4g.dopaas.uds.service.elasticjoblite.model.FindJobExecutionEventsRequest;
import com.wl4g.dopaas.uds.service.elasticjoblite.model.FindJobStatusTraceEventsRequest;

/**
 * Event trace history service.
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/eventTraceHistory-service")
public interface EventTraceHistoryService {

	/**
	 * Find job execution events.
	 *
	 * @param findJobExecutionEventsRequest
	 *            query params
	 * @return job execution events
	 */
	@RequestMapping(path = "findJobExecutionEvents", method = GET)
	Page<JobExecutionEvent> findJobExecutionEvents(@RequestBody FindJobExecutionEventsRequest findJobExecutionEventsRequest);

	/**
	 * Find job names with specific prefix.
	 *
	 * @param jobNamePrefix
	 *            job name prefix
	 * @return matched job names
	 */
	@RequestMapping(path = "findJobNamesInExecutionLog", method = GET)
	List<String> findJobNamesInExecutionLog(@RequestParam(name = "jobNamePrefix") String jobNamePrefix);

	/**
	 * Find ip addresses with specific prefix.
	 *
	 * @param ipPrefix
	 *            ip prefix
	 * @return matched ip addresses
	 */
	@RequestMapping(path = "findIpInExecutionLog", method = GET)
	List<String> findIpInExecutionLog(@RequestParam(name = "ipPrefix") String ipPrefix);

	/**
	 * Find job status trace events.
	 *
	 * @param findJobStatusTraceEventsRequest
	 *            query params
	 * @return job status trace events
	 */
	@RequestMapping(path = "findJobStatusTraceEvents", method = GET)
	Page<JobStatusTraceEvent> findJobStatusTraceEvents(
			@RequestBody FindJobStatusTraceEventsRequest findJobStatusTraceEventsRequest);

	/**
	 * Find job names with specific prefix in status trace log.
	 *
	 * @param jobNamePrefix
	 *            job name prefix
	 * @return matched job names
	 */
	@RequestMapping(path = "findJobNamesInStatusTraceLog", method = GET)
	List<String> findJobNamesInStatusTraceLog(@RequestParam(name = "jobNamePrefix") String jobNamePrefix);

}
