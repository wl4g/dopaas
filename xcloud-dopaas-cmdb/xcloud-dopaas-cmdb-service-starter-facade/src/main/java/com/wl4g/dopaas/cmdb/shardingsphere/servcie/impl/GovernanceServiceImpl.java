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

import com.google.common.base.Joiner;
import org.apache.shardingsphere.governance.core.registry.RegistryCenterNodeStatus;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.ReplicaQueryRuleConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.rule.ReplicaQueryDataSourceRuleConfiguration;
import com.wl4g.dopaas.cmdb.shardingsphere.common.dto.InstanceDTO;
import com.wl4g.dopaas.cmdb.shardingsphere.common.dto.ReplicaDataSourceDTO;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.GovernanceService;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.RegistryCenterService;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.ShardingSchemaService;
import com.wl4g.dopaas.cmdb.shardingsphere.util.ConfigurationYamlConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of governance operation service.
 */
@Service
public class GovernanceServiceImpl implements GovernanceService {
    
    @Resource
    private RegistryCenterService registryCenterService;
    
    @Resource
    private ShardingSchemaService shardingSchemaService;
    
    @Override
    public Collection<InstanceDTO> getALLInstance() {
        List<String> instanceIds = registryCenterService.getActivatedRegistryCenter().getChildrenKeys(getInstancesNodeFullRootPath());
        Collection<InstanceDTO> result = new ArrayList<>(instanceIds.size());
        for (String instanceId : instanceIds) {
            String value = registryCenterService.getActivatedRegistryCenter().get(registryCenterService.getActivatedStateNode().getProxyNodePath(instanceId));
            result.add(new InstanceDTO(instanceId, !RegistryCenterNodeStatus.DISABLED.toString().equalsIgnoreCase(value)));
        }
        return result;
    }
    
    @Override
    public void updateInstanceStatus(final String instanceId, final boolean enabled) {
        String value = enabled ? "" : RegistryCenterNodeStatus.DISABLED.toString();
        registryCenterService.getActivatedRegistryCenter().persist(registryCenterService.getActivatedStateNode().getProxyNodePath(instanceId), value);
    }
    
    @Override
    public Collection<ReplicaDataSourceDTO> getAllReplicaDataSource() {
        Collection<ReplicaDataSourceDTO> result = new ArrayList<>();
        for (String schemaName : shardingSchemaService.getAllSchemaNames()) {
            String configData = shardingSchemaService.getRuleConfiguration(schemaName);
            if (configData.contains("!SHARDING")) {
                handleShardingRuleConfiguration(result, configData, schemaName);
            } else if (configData.contains("!REPLICA_QUERY")) {
                handleMasterSlaveRuleConfiguration(result, configData, schemaName);
            }
        }
        return result;
    }
    
    @Override
    public void updateReplicaDataSourceStatus(final String schemaNames, final String replicaDataSourceName, final boolean enabled) {
        String value = enabled ? "" : RegistryCenterNodeStatus.DISABLED.toString();
        registryCenterService.getActivatedRegistryCenter().persist(registryCenterService.getActivatedStateNode().getDataSourcePath(schemaNames, replicaDataSourceName), value);
    }
    
    private String getInstancesNodeFullRootPath() {
        String result = registryCenterService.getActivatedStateNode().getProxyNodePath("");
        return result.substring(0, result.length() - 1);
    }
    
    private void handleShardingRuleConfiguration(final Collection<ReplicaDataSourceDTO> replicaDataSourceDTOS, final String configData, final String schemaName) {
        Collection<RuleConfiguration> configurations = ConfigurationYamlConverter.loadRuleConfigurations(configData);
        Collection<ReplicaQueryRuleConfiguration> replicaQueryRuleConfigurations = configurations.stream().filter(
            config -> config instanceof ReplicaQueryRuleConfiguration).map(config -> (ReplicaQueryRuleConfiguration) config).collect(Collectors.toList());
        for (ReplicaQueryRuleConfiguration replicaQueryRuleConfiguration : replicaQueryRuleConfigurations) {
            addSlaveDataSource(replicaDataSourceDTOS, replicaQueryRuleConfiguration, schemaName);
        }
    }
    
    private void handleMasterSlaveRuleConfiguration(final Collection<ReplicaDataSourceDTO> replicaDataSourceDTOS, final String configData, final String schemaName) {
        ReplicaQueryRuleConfiguration replicaQueryRuleConfiguration = ConfigurationYamlConverter.loadPrimaryReplicaRuleConfiguration(configData);
        addSlaveDataSource(replicaDataSourceDTOS, replicaQueryRuleConfiguration, schemaName);
    }
    
    private void addSlaveDataSource(final Collection<ReplicaDataSourceDTO> replicaDataSourceDTOS, final ReplicaQueryRuleConfiguration replicaQueryRuleConfiguration, final String schemaName) {
        Collection<String> disabledSchemaDataSourceNames = getDisabledSchemaDataSourceNames();
        for (ReplicaQueryDataSourceRuleConfiguration each : replicaQueryRuleConfiguration.getDataSources()) {
            replicaDataSourceDTOS.addAll(getReplicaDataSourceDTOS(schemaName, disabledSchemaDataSourceNames, each));
        }
    }
    
    private Collection<ReplicaDataSourceDTO> getReplicaDataSourceDTOS(final String schemaName, final Collection<String> disabledSchemaDataSourceNames, final ReplicaQueryDataSourceRuleConfiguration group) {
        Collection<ReplicaDataSourceDTO> result = new LinkedList<>();
        for (String each : group.getReplicaDataSourceNames()) {
            result.add(new ReplicaDataSourceDTO(schemaName, group.getPrimaryDataSourceName(), each, !disabledSchemaDataSourceNames.contains(schemaName + "." + each)));
        }
        return result;
    }
    
    private Collection<String> getDisabledSchemaDataSourceNames() {
        List<String> result = new ArrayList<>();
        List<String> schemaNames = registryCenterService.getActivatedRegistryCenter().getChildrenKeys(registryCenterService.getActivatedStateNode().getDataNodesPath());
        for (String schemaName : schemaNames) {
            List<String> dataSourceNames = registryCenterService.getActivatedRegistryCenter().getChildrenKeys(registryCenterService.getActivatedStateNode().getSchemaPath(schemaName));
            for (String dataSourceName : dataSourceNames) {
                String value = registryCenterService.getActivatedRegistryCenter().get(registryCenterService.getActivatedStateNode().getDataSourcePath(schemaName, dataSourceName));
                if (RegistryCenterNodeStatus.DISABLED.toString().equalsIgnoreCase(value)) {
                    result.add(Joiner.on(".").join(schemaName, dataSourceName));
                }
            }
        }
        return result;
    }
}
