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

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.model.InstanceModel;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.model.SlaveDataSourceModel;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.OrchestrationService;

/**
 * RESTful API of orchestration operation.
 */
@RestController
@RequestMapping("/api/orchestration")
public class ShardingOrchestrationController {

	@Autowired
	private OrchestrationService orchestrationService;

	/**
	 * Load all instances.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "/instance/get", method = RequestMethod.GET)
	public RespBase<Collection<InstanceModel>> loadAllInstances() {
		return RespBase.<Collection<InstanceModel>> create().withData(orchestrationService.getALLInstance());
	}

	/**
	 * update instance status.
	 *
	 * @param instanceDTO
	 *            instance DTO
	 * @return response result
	 */
	@RequestMapping(value = "/instance/put", method = RequestMethod.PUT)
	public RespBase<?> updateInstanceStatus(@RequestBody final InstanceModel instanceDTO) {
		orchestrationService.updateInstanceStatus(instanceDTO.getInstanceId(), instanceDTO.isEnabled());
		return RespBase.create();
	}

	/**
	 * Load all slave data sources.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "/datasource/get", method = RequestMethod.GET)
	public RespBase<Collection<SlaveDataSourceModel>> loadAllSlaveDataSources() {
		return RespBase.<Collection<SlaveDataSourceModel>> create().withData(orchestrationService.getAllSlaveDataSource());
	}

	/**
	 * Update slave data source status.
	 *
	 * @param slaveDataSourceDTO
	 *            slave data source DTO
	 * @return response result
	 */
	@RequestMapping(value = "/datasource/put", method = RequestMethod.PUT)
	public RespBase<?> updateSlaveDataSourceStatus(@RequestBody final SlaveDataSourceModel slaveDataSourceDTO) {
		orchestrationService.updateSlaveDataSourceStatus(slaveDataSourceDTO.getSchema(),
				slaveDataSourceDTO.getSlaveDataSourceName(), slaveDataSourceDTO.isEnabled());
		return RespBase.create();
	}

}
