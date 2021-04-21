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

package com.wl4g.dopaas.cmdb.shardingspherev4.servcie.impl;

import org.apache.shardingsphere.orchestration.center.ConfigCenterRepository;
import org.apache.shardingsphere.orchestration.core.configcenter.ConfigCenterNode;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.constant.OrchestrationType;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.domain.CenterConfig;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.exception.ShardingSphereUIException;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.CenterConfigService;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.ConfigCenterService;
import com.wl4g.dopaas.cmdb.shardingspherev4.util.CenterRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Config center service.
 */
@Service
public class ConfigCenterServiceImpl implements ConfigCenterService {

	@Autowired
	private CenterConfigService centerConfigService;

	@Override
	public ConfigCenterRepository getActivatedConfigCenter() {
		Optional<CenterConfig> optional = centerConfigService.loadActivated(OrchestrationType.CONFIG_CENTER.getValue());
		if (optional.isPresent()) {
			return CenterRepositoryFactory.createConfigCenter(optional.get());
		}
		throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "No activated config center!");
	}

	@Override
	public ConfigCenterNode getActivateConfigurationNode() {
		Optional<CenterConfig> optional = centerConfigService.loadActivated(OrchestrationType.CONFIG_CENTER.getValue());
		if (optional.isPresent()) {
			return new ConfigCenterNode(optional.get().getOrchestrationName());
		}
		throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "No activated config center!");
	}
}
