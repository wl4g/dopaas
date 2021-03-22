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

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.dopaas.uds.service.elasticjobcloud.model.CloudAppConfigurationPOJO;

/**
 * Cloud app configuration service.
 */
public interface CloudAppConfigService {

	@Autowired

	/**
	 * Add cloud app configuration.
	 *
	 * @param appConfig
	 *            cloud app configuration
	 */
	void add(final CloudAppConfigurationPOJO appConfig);

	/**
	 * Update cloud app configuration.
	 *
	 * @param appConfig
	 *            cloud app configuration
	 */
	void update(final CloudAppConfigurationPOJO appConfig);

	/**
	 * Load app configuration by app name.
	 *
	 * @param appName
	 *            application name
	 * @return cloud app configuration
	 */
	Optional<CloudAppConfigurationPOJO> load(final String appName);

	/**
	 * Load all registered cloud app configurations.
	 *
	 * @return collection of the registered cloud app configuration
	 */
	Collection<CloudAppConfigurationPOJO> loadAll();

	/**
	 * Remove cloud app configuration by app name.
	 *
	 * @param appName
	 *            to be removed application name
	 */

	void remove(final String appName);

}
