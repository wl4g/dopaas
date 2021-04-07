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

package com.wl4g.dopaas.cmdb.shardingsphere.servcie.impl;

import org.apache.shardingsphere.governance.core.registry.RegistryCenterNode;
import org.apache.shardingsphere.governance.repository.api.RegistryRepository;
import com.wl4g.dopaas.cmdb.shardingsphere.common.domain.CenterConfig;
import com.wl4g.dopaas.cmdb.shardingsphere.common.exception.ShardingSphereUIException;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.CenterConfigService;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.RegistryCenterService;
import com.wl4g.dopaas.cmdb.shardingsphere.util.CenterRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Registry center service.
 */
@Service
public class RegistryCenterServiceImpl implements RegistryCenterService {
    
    @Autowired
    private CenterConfigService centerConfigService;
    
    @Override
    public RegistryRepository getActivatedRegistryCenter() {
        Optional<CenterConfig> optional = centerConfigService.loadActivated();
        if (optional.isPresent()) {
            return CenterRepositoryFactory.createRegistryRepository(optional.get());
        }
        throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "No activated registry center!");
    }
    
    @Override
    public RegistryCenterNode getActivatedStateNode() {
        Optional<CenterConfig> optional = centerConfigService.loadActivated();
        if (optional.isPresent()) {
            return new RegistryCenterNode();
        }
        throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "No activated registry center!");
    }
}
