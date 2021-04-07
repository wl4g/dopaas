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

package com.wl4g.dopaas.cmdb.shardingsphere.web.controller;

import com.wl4g.dopaas.cmdb.shardingsphere.common.dto.InstanceDTO;
import com.wl4g.dopaas.cmdb.shardingsphere.common.dto.ReplicaDataSourceDTO;
import com.wl4g.dopaas.cmdb.shardingsphere.servcie.GovernanceService;
import com.wl4g.dopaas.cmdb.shardingsphere.web.response.ResponseResult;
import com.wl4g.dopaas.cmdb.shardingsphere.web.response.ResponseResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * RESTful API of governance operation.
 */
@RestController
@RequestMapping("/api/governance")
public class GovernanceController {
    
    @Autowired
    private GovernanceService governanceService;
    
    /**
     * Load all instances.
     *
     * @return response result
     */
    @RequestMapping(value = "/instance", method = RequestMethod.GET)
    public ResponseResult<Collection<InstanceDTO>> loadAllInstances() {
        return ResponseResultUtil.build(governanceService.getALLInstance());
    }
    
    /**
     * update instance status.
     *
     * @param instanceDTO instance DTO
     * @return response result
     */
    @RequestMapping(value = "/instance", method = RequestMethod.PUT)
    public ResponseResult updateInstanceStatus(@RequestBody final InstanceDTO instanceDTO) {
        governanceService.updateInstanceStatus(instanceDTO.getInstanceId(), instanceDTO.isEnabled());
        return ResponseResultUtil.success();
    }
    
    /**
     * Load all replica data sources.
     *
     * @return response result
     */
    @RequestMapping(value = "/datasource", method = RequestMethod.GET)
    public ResponseResult<Collection<ReplicaDataSourceDTO>> loadAllReplicaDataSources() {
        return ResponseResultUtil.build(governanceService.getAllReplicaDataSource());
    }
    
    /**
     * Update replica data source status.
     *
     * @param replicaDataSourceDTO replica data source DTO
     * @return response result
     */
    @RequestMapping(value = "/datasource", method = RequestMethod.PUT)
    public ResponseResult updateReplicaDataSourceStatus(@RequestBody final ReplicaDataSourceDTO replicaDataSourceDTO) {
        governanceService.updateReplicaDataSourceStatus(replicaDataSourceDTO.getSchema(), replicaDataSourceDTO.getReplicaDataSourceName(), replicaDataSourceDTO.isEnabled());
        return ResponseResultUtil.success();
    }
    
}
