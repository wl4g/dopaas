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

package com.wl4g.dopaas.cmdb.shardingspherev4.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.dopaas.cmdb.shardingspherev4.servcie.ShardingPropertiesService;

/**
 * RESTful API of sharding properties.
 */
@RestController
@RequestMapping("/api/props")
public class ShardingPropertiesController {

	@Autowired
	private ShardingPropertiesService shardingPropertiesService;

	/**
	 * Load sharding properties.
	 *
	 * @return response result
	 */
	@RequestMapping(value = "get", method = RequestMethod.GET)
	public RespBase<String> loadShardingProperties() {
		return RespBase.<String> create().withData(shardingPropertiesService.loadShardingProperties());
	}

	/**
	 * Update sharding properties.
	 *
	 * @param configMap
	 *            config map
	 * @return response result
	 */
	@RequestMapping(value = "put", method = RequestMethod.PUT)
	public RespBase<?> updateShardingProperties(@RequestBody final Map<String, String> configMap) {
		shardingPropertiesService.updateShardingProperties(configMap.get("props"));
		return RespBase.create();
	}
}
