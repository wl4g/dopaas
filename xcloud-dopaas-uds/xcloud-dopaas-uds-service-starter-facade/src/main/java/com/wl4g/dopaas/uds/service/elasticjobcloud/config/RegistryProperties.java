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

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.Setter;

/***
 * {@link RegistryProperties}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-03-23
 * @sine v1.0
 * @see
 */
@Setter
@Getter
public final class RegistryProperties {
	private String servers = "127.0.0.1:2181";
	private String namespace = "elasticjob-cloud";
	private String digest;

	public ZookeeperConfiguration getZookeeperConfiguration() {
		ZookeeperConfiguration result = new ZookeeperConfiguration(servers, namespace);
		if (!Strings.isNullOrEmpty(digest)) {
			result.setDigest(digest);
		}
		return result;
	}

}
