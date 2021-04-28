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

package com.wl4g.dopaas.uds.service.elasticjoblite;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wl4g.dopaas.uds.service.elasticjoblite.LiteRegistryCenterConfigService;
import com.wl4g.dopaas.uds.service.elasticjoblite.config.GlobalConfig;
import com.wl4g.dopaas.uds.service.elasticjoblite.domain.LiteRegistryCenterConfig;
import com.wl4g.dopaas.uds.service.elasticjoblite.domain.LiteRegistryCenterConfigs;
import com.wl4g.dopaas.uds.service.elasticjoblite.repository.ConfigurationsXmlRepository;
import com.wl4g.dopaas.uds.service.elasticjoblite.repository.impl.ConfigurationsXmlRepositoryImpl;

/**
 * Registry center configuration service implementation.
 */
@Service
public final class LiteRegistryCenterConfigServiceImpl implements LiteRegistryCenterConfigService {

	private ConfigurationsXmlRepository configurationsXmlRepository = new ConfigurationsXmlRepositoryImpl();

	@Override
	public LiteRegistryCenterConfigs loadAll() {
		return loadGlobal().getRegistryCenterConfigurations();
	}

	@Override
	public LiteRegistryCenterConfig load(final String name) {
		GlobalConfig configs = loadGlobal();
		LiteRegistryCenterConfig result = find(name, configs.getRegistryCenterConfigurations());
		setActivated(configs, result);
		return result;
	}

	@Override
	public LiteRegistryCenterConfig find(final String name, final LiteRegistryCenterConfigs configs) {
		for (LiteRegistryCenterConfig each : configs.getRegistryCenterConfiguration()) {
			if (name.equals(each.getName())) {
				return each;
			}
		}
		return null;
	}

	private void setActivated(final GlobalConfig configs, final LiteRegistryCenterConfig toBeConnectedConfig) {
		LiteRegistryCenterConfig activatedConfig = findActivatedRegistryCenterConfiguration(configs);
		if (!toBeConnectedConfig.equals(activatedConfig)) {
			if (null != activatedConfig) {
				activatedConfig.setActivated(false);
			}
			toBeConnectedConfig.setActivated(true);
			configurationsXmlRepository.save(configs);
		}
	}

	@Override
	public Optional<LiteRegistryCenterConfig> loadActivated() {
		return Optional.ofNullable(findActivatedRegistryCenterConfiguration(loadGlobal()));
	}

	private LiteRegistryCenterConfig findActivatedRegistryCenterConfiguration(final GlobalConfig configs) {
		for (LiteRegistryCenterConfig each : configs.getRegistryCenterConfigurations().getRegistryCenterConfiguration()) {
			if (each.isActivated()) {
				return each;
			}
		}
		return null;
	}

	@Override
	public boolean add(final LiteRegistryCenterConfig config) {
		GlobalConfig configs = loadGlobal();
		boolean result = configs.getRegistryCenterConfigurations().getRegistryCenterConfiguration().add(config);
		if (result) {
			configurationsXmlRepository.save(configs);
		}
		return result;
	}

	@Override
	public void delete(final String name) {
		GlobalConfig configs = loadGlobal();
		LiteRegistryCenterConfig toBeRemovedConfig = find(name, configs.getRegistryCenterConfigurations());
		if (null != toBeRemovedConfig) {
			configs.getRegistryCenterConfigurations().getRegistryCenterConfiguration().remove(toBeRemovedConfig);
			configurationsXmlRepository.save(configs);
		}
	}

	private GlobalConfig loadGlobal() {
		GlobalConfig result = configurationsXmlRepository.load();
		if (null == result.getRegistryCenterConfigurations()) {
			result.setRegistryCenterConfigurations(new LiteRegistryCenterConfigs());
		}
		return result;
	}
}
