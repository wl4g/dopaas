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

package com.wl4g.dopaas.cmdb.shardingspherev4.web.controller;

import com.wl4g.dopaas.cmdb.shardingspherev4.common.dto.ShardingSchemaDTO;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.ShardingSchemaService;
import com.wl4g.dopaas.cmdb.shardingspherev4.web.response.ResponseResult;
import com.wl4g.dopaas.cmdb.shardingspherev4.web.response.ResponseResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

/**
 * RESTful API of sharding schema configuration.
 */
@RestController
@RequestMapping("/api/schema")
public class ShardingSchemaController {
    
    @Autowired
    private ShardingSchemaService shardingSchemaService;
    
    /**
     * Load all schema names.
     *
     * @return response result
     */
    @RequestMapping(value = "get", method = RequestMethod.GET)
    public ResponseResult<Collection<String>> loadAllSchemaNames() {
        return ResponseResultUtil.build(shardingSchemaService.getAllSchemaNames());
    }
    
    /**
     * Add schema configuration.
     *
     * @param shardingSchema sharding schema DTO.
     * @return response result
     */
    @RequestMapping(value = "post", method = RequestMethod.POST)
    public ResponseResult addSchema(final @RequestBody ShardingSchemaDTO shardingSchema) {
        shardingSchemaService.addSchemaConfiguration(shardingSchema.getName(), shardingSchema.getRuleConfiguration(), shardingSchema.getDataSourceConfiguration());
        return ResponseResultUtil.success();
    }
    
    /**
     * Load rule configuration.
     *
     * @param schemaName schema name
     * @return response result
     */
    @RequestMapping(value = "/rule/get", method = RequestMethod.GET)
    public ResponseResult<String> loadRuleConfiguration(final String schemaName) {
        return ResponseResultUtil.build(shardingSchemaService.getRuleConfiguration(schemaName));
    }
    
    /**
     * Update rule configuration.
     *
     * @param schemaName schema name
     * @param configMap config map
     * @return response result
     */
    @RequestMapping(value = "/rule/put", method = RequestMethod.PUT)
    public ResponseResult updateRuleConfiguration(@RequestBody final Map<String, String> configMap) {
        shardingSchemaService.updateRuleConfiguration(configMap.get("schemaName"), configMap.get("ruleConfig"));
        return ResponseResultUtil.success();
    }
    
    /**
     * Load data source configuration.
     *
     * @param schemaName schema name
     * @return response result
     */
    @RequestMapping(value = "/datasource/get", method = RequestMethod.GET)
    public ResponseResult<String> loadDataSourceConfiguration(final String schemaName) {
        return ResponseResultUtil.build(shardingSchemaService.getDataSourceConfiguration(schemaName));
    }
    
    /**
     * Update data source configuration.
     *
     * @param schemaName schema name
     * @param configMap config map
     * @return response result
     */
    @RequestMapping(value = "/datasource/put", method = RequestMethod.PUT)
    public ResponseResult updateDataSourceConfiguration( @RequestBody final Map<String, String> configMap) {
        shardingSchemaService.updateDataSourceConfiguration(configMap.get("schemaName"), configMap.get("dataSourceConfig"));
        return ResponseResultUtil.success();
    }
    
}
