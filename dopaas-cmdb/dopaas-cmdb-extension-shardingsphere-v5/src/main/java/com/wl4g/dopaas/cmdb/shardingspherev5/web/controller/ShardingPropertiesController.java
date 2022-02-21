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

package com.wl4g.dopaas.cmdb.shardingspherev5.web.controller;

import com.wl4g.dopaas.cmdb.shardingspherev5.servcie.ShardingPropertiesService;
import com.wl4g.dopaas.cmdb.shardingspherev5.web.response.ResponseResult;
import com.wl4g.dopaas.cmdb.shardingspherev5.web.response.ResponseResultUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * RESTFul API of sharding properties.
 */
@RestController
@RequestMapping("/api/props")
public class ShardingPropertiesController {
    
    @Resource
    private ShardingPropertiesService shardingPropertiesService;
    
    /**
     * Load sharding properties.
     *
     * @return response result
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseResult<String> loadShardingProperties() {
        return ResponseResultUtil.build(shardingPropertiesService.loadShardingProperties());
    }
    
    /**
     * Update sharding properties.
     *
     * @param configMap config map
     * @return response result
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseResult updateShardingProperties(@RequestBody final Map<String, String> configMap) {
        shardingPropertiesService.updateShardingProperties(configMap.get("props"));
        return ResponseResultUtil.success();
    }
}
