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

package com.wl4g.dopaas.uds.extension.elasticjoblite.service;

import com.wl4g.dopaas.uds.extension.elasticjoblite.domain.LiteRegistryCenterConfig;
import com.wl4g.dopaas.uds.extension.elasticjoblite.domain.LiteRegistryCenterConfigs;

import java.util.Optional;

/**
 * Registry center configuration service.
 */
public interface LiteRegistryCenterConfigService {

	/**
	 * Load all registry center configurations.
	 *
	 * @return all registry center configurations
	 */
	LiteRegistryCenterConfigs loadAll();

	/**
	 * Load registry center configuration.
	 *
	 * @param name
	 *            name of registry center configuration
	 * @return registry center configuration
	 */
	LiteRegistryCenterConfig load(String name);

	/**
	 * Find registry center configuration.
	 * 
	 * @param name
	 *            name of registry center configuration
	 * @param configs
	 *            registry center configurations
	 * @return registry center configuration
	 */
	LiteRegistryCenterConfig find(String name, LiteRegistryCenterConfigs configs);

	/**
	 * Load activated registry center configuration.
	 *
	 * @return activated registry center configuration
	 */
	Optional<LiteRegistryCenterConfig> loadActivated();

	/**
	 * Add registry center configuration.
	 *
	 * @param config
	 *            registry center configuration
	 * @return success to add or not
	 */
	boolean add(LiteRegistryCenterConfig config);

	/**
	 * Delete registry center configuration.
	 *
	 * @param name
	 *            name of registry center configuration
	 */
	void delete(String name);
}
