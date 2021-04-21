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

package com.wl4g.dopaas.cmdb.shardingspherev5.util;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.governance.repository.api.ConfigurationRepository;
import org.apache.shardingsphere.governance.repository.api.GovernanceRepository;
import org.apache.shardingsphere.governance.repository.api.RegistryRepository;
import org.apache.shardingsphere.governance.repository.api.config.GovernanceCenterConfiguration;
import org.apache.shardingsphere.governance.repository.etcd.EtcdRepository;
import org.apache.shardingsphere.governance.repository.zookeeper.CuratorZookeeperRepository;
import com.wl4g.dopaas.cmdb.shardingspherev5.common.constant.InstanceType;
import com.wl4g.dopaas.cmdb.shardingspherev5.common.domain.CenterConfig;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Center factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CenterRepositoryFactory {
    
    private static final ConcurrentHashMap<String, RegistryRepository> REGISTRY_REPOSITORY_MAP = new ConcurrentHashMap<>();
    
    private static final ConcurrentHashMap<String, ConfigurationRepository> CONFIG_REPOSITORY_MAP = new ConcurrentHashMap<>();
    
    /**
     * Create registry repository.
     *
     * @param config center config
     * @return registry repository
     */
    public static RegistryRepository createRegistryRepository(final CenterConfig config) {
        RegistryRepository result = REGISTRY_REPOSITORY_MAP.get(config.getName());
        if (null != result) {
            return result;
        }
        result = (RegistryRepository) createGovernanceRepository(config.getInstanceType());
        result.init(config.getGovernanceName(), convert(config));
        REGISTRY_REPOSITORY_MAP.put(config.getName(), result);
        return result;
    }
    
    /**
     * Create configuration repository.
     * 
     * @param config center config
     * @return configuration repository
     */
    public static ConfigurationRepository createConfigurationRepository(final CenterConfig config) {
        ConfigurationRepository result = CONFIG_REPOSITORY_MAP.get(config.getName());
        if (null != result) {
            return result;
        }
        if (!Strings.isNullOrEmpty(config.getAdditionalConfigCenterServerList())
                && !Strings.isNullOrEmpty(config.getAdditionalConfigCenterType())) {
            result = (ConfigurationRepository) createGovernanceRepository(config.getAdditionalConfigCenterType());
        } else {
            RegistryRepository registryRepository = (RegistryRepository) createGovernanceRepository(config.getInstanceType());
            if (registryRepository instanceof  ConfigurationRepository) {
                result = (ConfigurationRepository) registryRepository;
            } else {
                throw new IllegalArgumentException("Registry repository is not suitable for config center and no additional config center configuration provided.");
            }
        }
        result.init(config.getGovernanceName(), convert(config));
        CONFIG_REPOSITORY_MAP.put(config.getName(), result);
        return result;
    }
    
    private static GovernanceCenterConfiguration convert(final CenterConfig config) {
        GovernanceCenterConfiguration result = new GovernanceCenterConfiguration(config.getInstanceType(), config.getServerLists(), new Properties());
        result.getProps().put("digest", config.getDigest());
        return result;
    }
    
    private static GovernanceRepository createGovernanceRepository(final String instanceType) {
        RegistryRepository result;
        InstanceType type = InstanceType.nameOf(instanceType);
        switch (type) {
            case ZOOKEEPER:
                result = new CuratorZookeeperRepository();
                break;
            case ETCD:
                EtcdRepository etcdCenterRepository = new EtcdRepository();
                etcdCenterRepository.setProps(new Properties());
                result = etcdCenterRepository;
                break;
            default:
                throw new UnsupportedOperationException(instanceType);
        }
        return result;
    }
}
