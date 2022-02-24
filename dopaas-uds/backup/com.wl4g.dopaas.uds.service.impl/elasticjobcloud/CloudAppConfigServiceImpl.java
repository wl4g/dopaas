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

package com.wl4g.dopaas.uds.service.elasticjobcloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.shardingsphere.elasticjob.infra.yaml.YamlEngine;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.wl4g.infra.core.framework.proxy.SmartProxyFor;
import com.wl4g.dopaas.uds.service.elasticjobcloud.model.CloudAppConfigurationPOJO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Cloud app configuration service.
 */
@Service
@SmartProxyFor(CloudAppConfigService.class)
public final class CloudAppConfigServiceImpl implements CloudAppConfigService {

	private @Autowired CoordinatorRegistryCenter regCenter;

	/**
	 * Add cloud app configuration.
	 *
	 * @param appConfig
	 *            cloud app configuration
	 */
	public void add(final CloudAppConfigurationPOJO appConfig) {
		regCenter.persist(CloudAppConfigurationNode.getRootNodePath(appConfig.getAppName()), YamlEngine.marshal(appConfig));
	}

	/**
	 * Update cloud app configuration.
	 *
	 * @param appConfig
	 *            cloud app configuration
	 */
	public void update(final CloudAppConfigurationPOJO appConfig) {
		regCenter.update(CloudAppConfigurationNode.getRootNodePath(appConfig.getAppName()), YamlEngine.marshal(appConfig));
	}

	/**
	 * Load app configuration by app name.
	 *
	 * @param appName
	 *            application name
	 * @return cloud app configuration
	 */
	public Optional<CloudAppConfigurationPOJO> load(final String appName) {
		String configContent = regCenter.get(CloudAppConfigurationNode.getRootNodePath(appName));
		return Strings.isNullOrEmpty(configContent) ? Optional.empty()
				: Optional.of(YamlEngine.unmarshal(configContent, CloudAppConfigurationPOJO.class));
	}

	/**
	 * Load all registered cloud app configurations.
	 *
	 * @return collection of the registered cloud app configuration
	 */
	public Collection<CloudAppConfigurationPOJO> loadAll() {
		if (!regCenter.isExisted(CloudAppConfigurationNode.ROOT)) {
			return Collections.emptyList();
		}
		List<String> appNames = regCenter.getChildrenKeys(CloudAppConfigurationNode.ROOT);
		Collection<CloudAppConfigurationPOJO> result = new ArrayList<>(appNames.size());
		for (String each : appNames) {
			Optional<CloudAppConfigurationPOJO> config = load(each);
			config.ifPresent(result::add);
		}
		return result;
	}

	/**
	 * Remove cloud app configuration by app name.
	 *
	 * @param appName
	 *            to be removed application name
	 */

	public void remove(final String appName) {
		regCenter.remove(CloudAppConfigurationNode.getRootNodePath(appName));
	}

	/**
	 * Cloud app configuration node.
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	static final class CloudAppConfigurationNode {

		public static final String ROOT = "/config/app";

		private static final String APP_CONFIG = ROOT + "/%s";

		static String getRootNodePath(final String appName) {
			return String.format(APP_CONFIG, appName);
		}
	}

}
