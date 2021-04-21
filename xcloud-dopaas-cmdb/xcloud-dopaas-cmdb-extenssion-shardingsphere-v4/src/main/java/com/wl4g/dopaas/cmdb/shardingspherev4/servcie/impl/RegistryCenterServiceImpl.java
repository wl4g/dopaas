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

import org.apache.shardingsphere.orchestration.center.RegistryCenterRepository;
import org.apache.shardingsphere.orchestration.core.configcenter.ConfigCenterNode;
import org.apache.shardingsphere.orchestration.core.registrycenter.RegistryCenterNode;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.constant.OrchestrationType;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.domain.CenterConfig;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.exception.ShardingSphereUIException;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.CenterConfigService;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.RegistryCenterService;
import com.wl4g.dopaas.cmdb.shardingspherev4.util.CenterRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Registry center service.
 */
@Service
public final class RegistryCenterServiceImpl implements RegistryCenterService {
    
    @Autowired
    private CenterConfigService centerConfigService;
    
    @Override
    public RegistryCenterRepository getActivatedRegistryCenter() {
        Optional<CenterConfig> optional = centerConfigService.loadActivated(OrchestrationType.REGISTRY_CENTER.getValue());
        if (optional.isPresent()) {
            return CenterRepositoryFactory.createRegistryCenter(optional.get());
        }
        throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "No activated registry center!");
    }
    
    @Override
    public RegistryCenterNode getActivatedStateNode() {
        Optional<CenterConfig> optional = centerConfigService.loadActivated(OrchestrationType.REGISTRY_CENTER.getValue());
        if (optional.isPresent()) {
            return new RegistryCenterNode(optional.get().getOrchestrationName());
        }
        throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "No activated registry center!");
    }
}
