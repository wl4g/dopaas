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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.shardingsphere.governance.repository.api.ConfigurationRepository;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import com.wl4g.dopaas.cmdb.shardingspherev5.servcie.ConfigCenterService;
import com.wl4g.dopaas.cmdb.shardingspherev5.servcie.ShardingSchemaService;
import com.wl4g.dopaas.cmdb.shardingspherev5.util.ConfigurationYamlConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implementation of sharding schema service.
 */
@Service
public class ShardingSchemaServiceImpl implements ShardingSchemaService {

    @Resource
    private ConfigCenterService configCenterService;

    @Override
    public Collection<String> getAllSchemaNames() {
        return configCenterService.getActivatedConfigCenter().getChildrenKeys(configCenterService.getActivateConfigurationNode().getSchemasPath());
    }
    
    @Override
    public String getRuleConfiguration(final String schemaName) {
        return configCenterService.getActivatedConfigCenter().get(configCenterService.getActivateConfigurationNode().getRulePath(schemaName));
    }
    
    @Override
    public String getDataSourceConfiguration(final String schemaName) {
        return configCenterService.getActivatedConfigCenter().get(configCenterService.getActivateConfigurationNode().getDataSourcePath(schemaName));
    }
    
    @Override
    public void updateRuleConfiguration(final String schemaName, final String configData) {
        checkRuleConfiguration(configData);
        persistRuleConfiguration(schemaName, configData);
    }
    
    @Override
    public void updateDataSourceConfiguration(final String schemaName, final String configData) {
        checkDataSourceConfiguration(configData);
        persistDataSourceConfiguration(schemaName, configData);
    }
    
    @Override
    public void addSchemaConfiguration(final String schemaName, final String ruleConfiguration, final String dataSourceConfiguration) {
        checkSchemaName(schemaName, getAllSchemaNames());
        checkRuleConfiguration(ruleConfiguration);
        checkDataSourceConfiguration(dataSourceConfiguration);
        persistRuleConfiguration(schemaName, ruleConfiguration);
        persistDataSourceConfiguration(schemaName, dataSourceConfiguration);
        persistSchemaName(schemaName);
    }
    
    @Override
    public void deleteSchemaConfiguration(final String schemaName) {
        ConfigurationRepository configCenterRepository = configCenterService.getActivatedConfigCenter();
        String schemaNamePath = configCenterService.getActivateConfigurationNode().getSchemaNamePath(schemaName);
        configCenterRepository.delete(schemaNamePath);
        String schemaNames = configCenterRepository.get(configCenterService.getActivateConfigurationNode().getSchemasPath());
        List<String> schemaNameList = new ArrayList<>(Splitter.on(",").splitToList(schemaNames));
        schemaNameList.remove(schemaName);
        configCenterRepository.persist(configCenterService.getActivateConfigurationNode().getSchemasPath(), Joiner.on(",").join(schemaNameList));
    }

    @Override
    public String getMetadataConfiguration(final String schemaName) {
        return configCenterService.getActivatedConfigCenter().get(
                configCenterService.getActivateConfigurationNode().getTablePath(schemaName));
    }

    private void checkRuleConfiguration(final String configData) {
        try {
            ConfigurationYamlConverter.loadRuleConfigurations(configData);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("rule configuration is invalid.", ex);
        }
    }
    
    private void persistRuleConfiguration(final String schemaName, final String ruleConfiguration) {
        configCenterService.getActivatedConfigCenter().persist(configCenterService.getActivateConfigurationNode().getRulePath(schemaName), ruleConfiguration);
    }
    
    private void checkDataSourceConfiguration(final String configData) {
        try {
            Map<String, DataSourceConfiguration> dataSourceConfigs = ConfigurationYamlConverter.loadDataSourceConfigurations(configData);
            Preconditions.checkState(!dataSourceConfigs.isEmpty(), "data source configuration is invalid.");
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("data source configuration is invalid.");
        }
    }
    
    private void persistDataSourceConfiguration(final String schemaName, final String dataSourceConfiguration) {
        configCenterService.getActivatedConfigCenter().persist(configCenterService.getActivateConfigurationNode().getDataSourcePath(schemaName), dataSourceConfiguration);
    }
    
    private void checkSchemaName(final String schemaName, final Collection<String> existedSchemaNames) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(schemaName), "schema name is invalid.");
        Preconditions.checkArgument(!existedSchemaNames.contains(schemaName), "schema name already exists.");
    }
    
    private void persistSchemaName(final String schemaName) {
        ConfigurationRepository configCenterRepository = configCenterService.getActivatedConfigCenter();
        String schemaPath = configCenterService.getActivateConfigurationNode().getSchemasPath();
        String schemaNames = configCenterRepository.get(schemaPath);
        List<String> schemaNameList = Strings.isNullOrEmpty(schemaNames) ? new ArrayList<>() : new ArrayList<>(Splitter.on(",").splitToList(schemaNames));
        if (!schemaNameList.contains(schemaName)) {
            schemaNameList.add(schemaName);
            configCenterRepository.persist(schemaPath, Joiner.on(",").join(schemaNameList));
        }
    }
}
