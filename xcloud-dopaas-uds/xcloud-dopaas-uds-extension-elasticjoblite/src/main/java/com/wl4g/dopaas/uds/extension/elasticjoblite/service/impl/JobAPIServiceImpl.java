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

package com.wl4g.dopaas.uds.extension.elasticjoblite.service.impl;

import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobAPIFactory;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobConfigurationAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobStatisticsAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.ServerStatisticsAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.ShardingOperateAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.ShardingStatisticsAPI;
import com.wl4g.dopaas.uds.extension.elasticjoblite.domain.LiteRegistryCenterConfig;
import com.wl4g.dopaas.uds.extension.elasticjoblite.service.JobAPIService;
import com.wl4g.dopaas.uds.extension.elasticjoblite.util.LiteSessionRegistryCenterFactory;
import org.springframework.stereotype.Service;

/**
 * Job API service implementation.
 */
@Service
public final class JobAPIServiceImpl implements JobAPIService {
    
    @Override
    public JobConfigurationAPI getJobConfigurationAPI() {
        LiteRegistryCenterConfig regCenterConfig = LiteSessionRegistryCenterFactory.getRegistryCenterConfiguration();
        return JobAPIFactory.createJobConfigurationAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public JobOperateAPI getJobOperatorAPI() {
        LiteRegistryCenterConfig regCenterConfig = LiteSessionRegistryCenterFactory.getRegistryCenterConfiguration();
        return JobAPIFactory.createJobOperateAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public ShardingOperateAPI getShardingOperateAPI() {
        LiteRegistryCenterConfig regCenterConfig = LiteSessionRegistryCenterFactory.getRegistryCenterConfiguration();
        return JobAPIFactory.createShardingOperateAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public JobStatisticsAPI getJobStatisticsAPI() {
        LiteRegistryCenterConfig regCenterConfig = LiteSessionRegistryCenterFactory.getRegistryCenterConfiguration();
        return JobAPIFactory.createJobStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public ServerStatisticsAPI getServerStatisticsAPI() {
        LiteRegistryCenterConfig regCenterConfig = LiteSessionRegistryCenterFactory.getRegistryCenterConfiguration();
        return JobAPIFactory.createServerStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public ShardingStatisticsAPI getShardingStatisticsAPI() {
        LiteRegistryCenterConfig regCenterConfig = LiteSessionRegistryCenterFactory.getRegistryCenterConfiguration();
        return JobAPIFactory.createShardingStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
}
