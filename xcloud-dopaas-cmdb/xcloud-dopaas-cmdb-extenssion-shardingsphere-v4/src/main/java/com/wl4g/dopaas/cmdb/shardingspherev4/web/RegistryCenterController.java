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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.bean.CenterConfig;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.constant.OrchestrationType;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.model.CenterConfigModel;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.CenterConfigService;
import com.wl4g.dopaas.cmdb.shardingspherev4.util.CenterRepositoryFactory;

/**
 * RESTful API of registry center configuration.
 */
@RestController
@RequestMapping("/api/reg-center")
public class RegistryCenterController {

	@Autowired
	private CenterConfigService centerConfigService;

	/**
	 * Load all registry center configs.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "get", method = RequestMethod.GET)
	public RespBase<List<CenterConfig>> loadConfigs() {
		return RespBase.<List<CenterConfig>> create()
				.withData(centerConfigService.loadAll(OrchestrationType.REGISTRY_CENTER.getValue()).getCenterConfigs());
	}

	/**
	 * Add registry center config.
	 *
	 * @param config
	 *            registry center config
	 * @return response result
	 */
	@RequestMapping(value = "post", method = RequestMethod.POST)
	public RespBase<?> add(@RequestBody final CenterConfig config) {
		centerConfigService.add(config);
		return RespBase.create();
	}

	/**
	 * Delete registry center config.
	 *
	 * @param config
	 *            registry center config
	 * @return response result
	 */
	@RequestMapping(value = "del", method = RequestMethod.DELETE)
	public RespBase<?> delete(@RequestBody final CenterConfig config) {
		centerConfigService.delete(config.getName(), OrchestrationType.REGISTRY_CENTER.getValue());
		return RespBase.create();
	}

	/**
	 * Connect registry center.
	 * 
	 * @param config
	 *            registry center config
	 * @return response result
	 */
	@RequestMapping(value = "/connect", method = RequestMethod.POST)
	public RespBase<Boolean> connect(@RequestBody final CenterConfig config) {
		CenterRepositoryFactory
				.createRegistryCenter(centerConfigService.load(config.getName(), OrchestrationType.REGISTRY_CENTER.getValue()));
		centerConfigService.setActivated(config.getName(), OrchestrationType.REGISTRY_CENTER.getValue());
		return RespBase.<Boolean> create().withData(Boolean.TRUE);
	}

	/**
	 * Get activated registry center config.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "/activated", method = RequestMethod.GET)
	public RespBase<CenterConfig> activated() {
		return RespBase.<CenterConfig> create()
				.withData(centerConfigService.loadActivated(OrchestrationType.REGISTRY_CENTER.getValue()).orElse(null));
	}

	/**
	 * Update registry center.
	 *
	 * @param config
	 *            registry center config
	 * @return response result
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public RespBase<?> update(@RequestBody final CenterConfigModel config) {
		centerConfigService.update(config);
		return RespBase.create();
	}
}
