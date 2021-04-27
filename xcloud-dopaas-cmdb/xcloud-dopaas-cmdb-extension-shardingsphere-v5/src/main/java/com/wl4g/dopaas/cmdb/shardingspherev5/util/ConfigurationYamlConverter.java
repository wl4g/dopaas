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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.governance.core.yaml.config.YamlDataSourceConfiguration;
import org.apache.shardingsphere.governance.core.yaml.swapper.DataSourceConfigurationYamlSwapper;
import org.apache.shardingsphere.infra.auth.Authentication;
import org.apache.shardingsphere.infra.auth.yaml.config.YamlAuthenticationConfiguration;
import org.apache.shardingsphere.infra.auth.yaml.swapper.AuthenticationYamlSwapper;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.infra.yaml.config.YamlRootRuleConfigurations;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.apache.shardingsphere.infra.yaml.swapper.YamlRuleConfigurationSwapperEngine;
import org.apache.shardingsphere.replicaquery.api.config.ReplicaQueryRuleConfiguration;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * YAML converter for configuration.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationYamlConverter {
    
    /**
     * Load data source configurations.
     *
     * @param data data
     * @return data source configurations
     */
    @SuppressWarnings("unchecked")
    public static Map<String, DataSourceConfiguration> loadDataSourceConfigurations(final String data) {
        Map<String, YamlDataSourceConfiguration> result = (Map) YamlEngine.unmarshal(data);
        Preconditions.checkState(null != result && !result.isEmpty(), "No available data sources to load for governance.");
        return Maps.transformValues(result, new DataSourceConfigurationYamlSwapper()::swapToObject);
    }
    
    /**
     * Load rule configurations.
     *
     * @param data data
     * @return rule configurations
     */
    public static Collection<RuleConfiguration> loadRuleConfigurations(final String data) {
        return new YamlRuleConfigurationSwapperEngine().swapToRuleConfigurations(YamlEngine.unmarshal(data, YamlRootRuleConfigurations.class).getRules());
    }
    
    /**
     * Load replica query rule configuration.
     *
     * @param data data
     * @return replica query rule configuration
     */
    public static ReplicaQueryRuleConfiguration loadPrimaryReplicaRuleConfiguration(final String data) {
        Collection<RuleConfiguration> ruleConfigurations = loadRuleConfigurations(data);
        Optional<ReplicaQueryRuleConfiguration> result = ruleConfigurations.stream().filter(
            each -> each instanceof ReplicaQueryRuleConfiguration).map(ruleConfiguration -> (ReplicaQueryRuleConfiguration) ruleConfiguration).findFirst();
        Preconditions.checkState(result.isPresent());
        return result.get();
    }
    
    /**
     * Load authentication.
     *
     * @param data data
     * @return authentication
     */
    public static Authentication loadAuthentication(final String data) {
        return new AuthenticationYamlSwapper().swapToObject(YamlEngine.unmarshal(data, YamlAuthenticationConfiguration.class));
    }
    
    /**
     * Load properties configuration.
     *
     * @param data data
     * @return properties
     */
    public static Properties loadProperties(final String data) {
        return YamlEngine.unmarshalProperties(data);
    }
}
