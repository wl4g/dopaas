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

package com.wl4g.dopaas.cmdb.shardingspherev5.servcie.impl;

import org.apache.shardingsphere.infra.config.properties.ConfigurationProperties;
import com.wl4g.dopaas.cmdb.shardingspherev5.servcie.ConfigCenterService;
import com.wl4g.dopaas.cmdb.shardingspherev5.servcie.ShardingPropertiesService;
import com.wl4g.dopaas.cmdb.shardingspherev5.util.ConfigurationYamlConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * Implementation of sharding properties service.
 */
@Service
public class ShardingPropertiesServiceImpl implements ShardingPropertiesService {
    
    @Resource
    private ConfigCenterService configCenterService;
    
    @Override
    public String loadShardingProperties() {
        return configCenterService.getActivatedConfigCenter().get(configCenterService.getActivateConfigurationNode().getPropsPath());
    }
    
    @Override
    public void updateShardingProperties(final String configData) {
        checkShardingProperties(configData);
        configCenterService.getActivatedConfigCenter().persist(configCenterService.getActivateConfigurationNode().getPropsPath(), configData);
    }
    
    private void checkShardingProperties(final String configData) {
        try {
            Properties props = ConfigurationYamlConverter.loadProperties(configData);
            new ConfigurationProperties(props);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("Sharding properties is invalid.");
        }
    }
}
