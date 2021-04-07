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

import com.wl4g.dopaas.cmdb.shardingsphere.common.domain.CenterConfig;
import com.wl4g.dopaas.cmdb.shardingsphere.common.domain.CenterConfigs;
import com.wl4g.dopaas.cmdb.shardingsphere.common.dto.CenterConfigDTO;
import com.wl4g.dopaas.cmdb.shardingsphere.common.exception.ShardingSphereUIException;
import com.wl4g.dopaas.cmdb.shardingsphere.repository.CenterConfigsRepository;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.CenterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Center config service.
 */
@Service
public class CenterConfigServiceImpl implements CenterConfigService {
    
    @Autowired
    private CenterConfigsRepository centerConfigsRepository;
    
    @Override
    public CenterConfig load(final String name) {
        return find(name, loadAll());
    }
    
    @Override
    public Optional<CenterConfig> loadActivated() {
        return Optional.ofNullable(findActivatedCenterConfiguration(loadAll()));
    }
    
    @Override
    public void add(final CenterConfig config) {
        CenterConfigs configs = loadAll();
        CenterConfig existedConfig = find(config.getName(), configs);
        if (null != existedConfig) {
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, String.format("Center %s already existed!", config.getName()));
        }
        configs.getCenterConfigs().add(config);
        centerConfigsRepository.save(configs);
    }
    
    @Override
    public void delete(final String name) {
        CenterConfigs configs = loadAll();
        CenterConfig toBeRemovedConfig = find(name, configs);
        if (null != toBeRemovedConfig) {
            configs.getCenterConfigs().remove(toBeRemovedConfig);
            centerConfigsRepository.save(configs);
        }
    }
    
    @Override
    public void setActivated(final String name) {
        CenterConfigs configs = loadAll();
        CenterConfig config = find(name, configs);
        if (null == config) {
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "Center not existed!");
        }
        CenterConfig activatedConfig = findActivatedCenterConfiguration(configs);
        if (!config.equals(activatedConfig)) {
            if (null != activatedConfig) {
                activatedConfig.setActivated(false);
            }
            config.setActivated(true);
            centerConfigsRepository.save(configs);
        }
    }
    
    @Override
    public CenterConfigs loadAll() {
        return centerConfigsRepository.load();
    }
    
    @Override
    public void update(CenterConfigDTO config) {
        CenterConfigs configs = loadAll();
        if (!config.getPrimaryName().equals(config.getName())) {
            CenterConfig existedConfig = find(config.getName(), configs);
            if (null != existedConfig) {
                throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, String.format("Center %s already existed!", config.getName()));
            }
        }
        CenterConfig toBeUpdatedConfig = find(config.getPrimaryName(), configs);
        if (null != toBeUpdatedConfig) {
            toBeUpdatedConfig.setName(config.getName());
            toBeUpdatedConfig.setInstanceType(config.getInstanceType());
            toBeUpdatedConfig.setServerLists(config.getServerLists());
            toBeUpdatedConfig.setGovernanceName(config.getGovernanceName());
            toBeUpdatedConfig.setDigest(config.getDigest());
            toBeUpdatedConfig.setAdditionalConfigCenterServerList(config.getAdditionalConfigCenterServerList());
            toBeUpdatedConfig.setAdditionalConfigCenterType(config.getAdditionalConfigCenterType());
            toBeUpdatedConfig.setAdditionalDigest(config.getAdditionalDigest());
            centerConfigsRepository.save(configs);
        }
    }
    
    private CenterConfig findActivatedCenterConfiguration(final CenterConfigs centerConfigs) {
        return null == centerConfigs ? null : centerConfigs.getCenterConfigs().stream()
                .filter(each->each.isActivated())
                .findAny()
                .orElse(null);
    }
    
    private CenterConfig find(final String name, final CenterConfigs configs) {
        return configs.getCenterConfigs().stream()
                .filter(each->name.equals(each.getName()))
                .findAny()
                .orElse(null);
    }
}
