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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ServerBriefInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.uds.service.elasticjoblite.JobAPIService;

/**
 * Server operation RESTful API.
 */
@RestController
@RequestMapping("/api/servers")
public final class ServerOperationController extends BaseController {

	private JobAPIService jobAPIService;

	@Autowired
	public ServerOperationController(final JobAPIService jobAPIService) {
		this.jobAPIService = jobAPIService;
	}

	/**
	 * Get servers total count.
	 * 
	 * @return servers total count
	 */
	@GetMapping("/count")
	public int getServersTotalCount() {
		return jobAPIService.getServersTotalCount();
	}

	/**
	 * Get all servers brief info.
	 * 
	 * @return all servers brief info
	 */
	@GetMapping("/getAllServersBriefInfo")
	public RespBase<Collection<ServerBriefInfo>> getAllServersBriefInfo() {
		Collection<ServerBriefInfo> data = Objects.nonNull(SessionRegistryCenterFactory.getRegistryCenterConfiguration())
				? jobAPIService.getAllServersBriefInfo()
				: Collections.emptyList();
		return RespBase.<Collection<ServerBriefInfo>> create().withData(data);
	}

	/**
	 * Disable server.
	 *
	 * @param serverIp
	 *            server IP address
	 */
	@PostMapping("/{serverIp}/disable")
	public RespBase<Boolean> disableServer(@PathVariable("serverIp") final String serverIp) {
		jobAPIService.disableJob(null, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Enable server.
	 *
	 * @param serverIp
	 *            server IP address
	 */
	@PostMapping("/{serverIp}/enable")
	public RespBase<Boolean> enableServer(@PathVariable("serverIp") final String serverIp) {
		jobAPIService.enableJob(null, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Shutdown server.
	 *
	 * @param serverIp
	 *            server IP address
	 */
	@PostMapping("/{serverIp}/shutdown")
	public RespBase<Boolean> shutdownServer(@PathVariable("serverIp") final String serverIp) {
		jobAPIService.shutdownJob(null, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Remove server.
	 *
	 * @param serverIp
	 *            server IP address
	 */
	@DeleteMapping("/{serverIp:.+}")
	public RespBase<Boolean> removeServer(@PathVariable("serverIp") final String serverIp) {
		jobAPIService.removeJob(null, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Get jobs.
	 *
	 * @param serverIp
	 *            server IP address
	 * @return Job brief info
	 */
	@GetMapping(value = "/{serverIp}/jobs")
	public RespBase<Collection<JobBriefInfo>> getJobs(@PathVariable("serverIp") final String serverIp) {
		Collection<JobBriefInfo> data = jobAPIService.getJobsBriefInfo(serverIp);
		return RespBase.<Collection<JobBriefInfo>> create().withData(data);
	}

	/**
	 * Disable server job.
	 * 
	 * @param serverIp
	 *            server IP address
	 * @param jobName
	 *            job name
	 */
	@PostMapping(value = "/{serverIp}/jobs/{jobName}/disable")
	public RespBase<Boolean> disableServerJob(@PathVariable("serverIp") final String serverIp,
			@PathVariable("jobName") final String jobName) {
		jobAPIService.disableJob(jobName, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Enable server job.
	 *
	 * @param serverIp
	 *            server IP address
	 * @param jobName
	 *            job name
	 */
	@PostMapping("/{serverIp}/jobs/{jobName}/enable")
	public RespBase<Boolean> enableServerJob(@PathVariable("serverIp") final String serverIp,
			@PathVariable("jobName") final String jobName) {
		jobAPIService.enableJob(jobName, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Shutdown server job.
	 *
	 * @param serverIp
	 *            server IP address
	 * @param jobName
	 *            job name
	 */
	@PostMapping("/{serverIp}/jobs/{jobName}/shutdown")
	public RespBase<Boolean> shutdownServerJob(@PathVariable("serverIp") final String serverIp,
			@PathVariable("jobName") final String jobName) {
		jobAPIService.shutdownJob(jobName, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Remove server job.
	 *
	 * @param serverIp
	 *            server IP address
	 * @param jobName
	 *            job name
	 */
	@DeleteMapping("/{serverIp}/jobs/{jobName:.+}")
	public RespBase<Boolean> removeServerJob(@PathVariable("serverIp") final String serverIp,
			@PathVariable("jobName") final String jobName) {
		jobAPIService.removeJob(jobName, serverIp);
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}
}
