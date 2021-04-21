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

package com.wl4g.dopaas.cmdb.shardingspherev4.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.constant.ForwardServiceType;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.domain.ForwardServiceConfig;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.domain.ForwardServiceConfigs;
import com.wl4g.dopaas.cmdb.shardingspherev4.repository.ForwardServiceConfigsRepository;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.forward.ShardingScalingForwardService;

/**
 * Sharding scaling forward controller.
 */
@RestController
@RequestMapping("/api/shardingscaling")
public class ShardingScalingForwardController {

	@Autowired
	private ShardingScalingForwardService shardingScalingForwardService;

	@Autowired
	private ForwardServiceConfigsRepository forwardServiceConfigsRepository;

	/**
	 * Get sharding scaling service config.
	 *
	 * @return sharding scaling service config
	 */
	@RequestMapping(value = "get", method = RequestMethod.GET)
	public RespBase<ForwardServiceConfig> getService() {
		RespBase<ForwardServiceConfig> resp = RespBase.create();
		Optional<ForwardServiceConfig> result = forwardServiceConfigsRepository.load()
				.getForwardServiceConfig(ForwardServiceType.SHARDING_SCALING.getName());
		return result.isPresent() ? resp.withData(result.get()) : resp.withMessage("No configured sharding scaling services");
	}

	/**
	 * Configure sharding scaling service.
	 *
	 * @param shardingScalingServiceConfig
	 *            sharding scaling service config
	 * @return response result
	 */
	@RequestMapping(value = "post", method = RequestMethod.POST)
	public RespBase<?> configService(@RequestBody final ForwardServiceConfig shardingScalingServiceConfig) {
		RespBase<?> resp = RespBase.create();
		ForwardServiceConfigs currentForwardServiceConfig = forwardServiceConfigsRepository.load();
		currentForwardServiceConfig.putForwardServiceConfig(ForwardServiceType.SHARDING_SCALING.getName(),
				shardingScalingServiceConfig);
		forwardServiceConfigsRepository.save(currentForwardServiceConfig);
		return resp;
	}

	/**
	 * Delete sharding scaling service.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "del", method = RequestMethod.DELETE)
	public RespBase<?> deleteService() {
		ForwardServiceConfigs currentForwardServiceConfig = forwardServiceConfigsRepository.load();
		currentForwardServiceConfig.removeForwardServiceConfig(ForwardServiceType.SHARDING_SCALING.getName());
		forwardServiceConfigsRepository.save(currentForwardServiceConfig);
		return RespBase.create();
	}

	/**
	 * List all sharding scaling jobs.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "/job/list", method = RequestMethod.GET)
	public RespBase<?> listAllShardingScalingJobs() {
		return RespBase.create().withData(shardingScalingForwardService.listAllShardingScalingJobs());
	}

	/**
	 * Start sharding scaling job progress.
	 *
	 * @param requestBody
	 *            request body of start sharding scaling job
	 * @return response result
	 */
	@RequestMapping(value = "/job/start", method = RequestMethod.POST)
	public RespBase<?> startShardingScalingJob(@RequestBody final String requestBody) {
		return RespBase.create().withData(shardingScalingForwardService.startShardingScalingJobs(requestBody));
	}

	/**
	 * Get sharding scaling job progress.
	 *
	 * @param jobId
	 *            job id
	 * @return response result
	 */
	@RequestMapping(value = "/job/progress", method = RequestMethod.GET)
	public RespBase<?> getShardingScalingJobProgress(final int jobId) {
		return RespBase.create().withData(shardingScalingForwardService.getShardingScalingJobProgress(jobId));
	}

	/**
	 * Stop sharding scaling job progress.
	 *
	 * @param requestBody
	 *            request body of stop sharding scaling job
	 * @return response result
	 */
	@RequestMapping(value = "/job/stop", method = RequestMethod.POST)
	public RespBase<?> stopShardingScalingJob(@RequestBody final String requestBody) {
		return RespBase.create().withData(shardingScalingForwardService.stopShardingScalingJob(requestBody));
	}
}
