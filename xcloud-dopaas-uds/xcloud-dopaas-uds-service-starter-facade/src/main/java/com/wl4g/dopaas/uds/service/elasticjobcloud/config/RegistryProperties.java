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

package com.wl4g.dopaas.uds.service.elasticjobcloud.config;

import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import lombok.Setter;

@Setter
@Component
@ConfigurationProperties(prefix = "zk")
public final class RegistryProperties {

	private String servers;
	private String namespace;
	private String digest;

	public ZookeeperConfiguration getZookeeperConfiguration() {
		ZookeeperConfiguration result = new ZookeeperConfiguration(servers, namespace);
		if (!Strings.isNullOrEmpty(digest)) {
			result.setDigest(digest);
		}
		return result;
	}

}
