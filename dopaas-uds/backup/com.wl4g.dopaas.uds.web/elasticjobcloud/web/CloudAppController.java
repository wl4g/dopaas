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

package com.wl4g.dopaas.uds.elasticjobcloud.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.dopaas.uds.elasticjobcloud.web.model.CloudAppConfigModel;
import com.wl4g.dopaas.uds.service.elasticjobcloud.CloudAppConfigService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.CloudJobConfigService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.DisableAppService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.ProducerService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.exception.AppConfigurationException;
import com.wl4g.dopaas.uds.service.elasticjobcloud.model.CloudAppConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cloud app controller.
 */
@RestController
@RequestMapping("/api/app")
public class CloudAppController {

	private @Autowired ProducerService producerService;
	private @Autowired CloudAppConfigService cloudAppConfigService;
	private @Autowired CloudJobConfigService cloudJobConfigService;
	private @Autowired DisableAppService disableAppService;

	/**
	 * Register app config.
	 * 
	 * @param appConfig
	 *            cloud app config
	 */
	@PostMapping("/register")
	public RespBase<?> register(@RequestBody  CloudAppConfigurationPOJO appConfig) {
		Optional<CloudAppConfigurationPOJO> appConfigFromZk = cloudAppConfigService.load(appConfig.getAppName());
		if (appConfigFromZk.isPresent()) {
			throw new AppConfigurationException("app '%s' already existed.", appConfig.getAppName());
		}
		cloudAppConfigService.add(appConfig);
		return RespBase.create();
	}

	/**
	 * Update app config.
	 * 
	 * @param appConfig
	 *            cloud app config
	 */
	@PostMapping("/update")
	public RespBase<?> update(@RequestBody  CloudAppConfigurationPOJO appConfig) {
		cloudAppConfigService.update(appConfig);
		return RespBase.create();
	}

	/**
	 * Query app config.
	 * 
	 * @param appName
	 *            app name
	 * @return cloud app config
	 */
	@GetMapping("/detail")
	public RespBase<CloudAppConfigurationPOJO> detail( String appName) {
		Optional<CloudAppConfigurationPOJO> appConfig = cloudAppConfigService.load(appName);
		return RespBase.<CloudAppConfigurationPOJO> create().withData(appConfig.orElse(null));
	}

	/**
	 * Find all registered app configs.
	 * 
	 * @return collection of registered app configs
	 */
	@GetMapping("/list")
	public RespBase<Collection<CloudAppConfigModel>> findAllApps() {
		return RespBase.<Collection<CloudAppConfigModel>> create().withData(build(cloudAppConfigService.loadAll()));
	}

	/**
	 * Query the app is disabled or not.
	 * 
	 * @param appName
	 *            app name
	 * @return true is disabled, otherwise not
	 */
	@GetMapping("/disable")
	public boolean isDisabled( String appName) {
		return disableAppService.isDisabled(appName);
	}

	/**
	 * Disable app config.
	 * 
	 * @param appName
	 *            app name
	 */
	@PostMapping("/disable")
	public RespBase<?> disable( String appName) {
		if (cloudAppConfigService.load(appName).isPresent()) {
			disableAppService.add(appName);
		}
		return RespBase.create();
	}

	/**
	 * Enable app.
	 * 
	 * @param appName
	 *            app name
	 */
	@PostMapping("/enable")
	public RespBase<?> enable( String appName) {
		if (cloudAppConfigService.load(appName).isPresent()) {
			disableAppService.remove(appName);
		}
		return RespBase.create();
	}

	/**
	 * Deregister app.
	 * 
	 * @param appName
	 *            app name
	 */
	@DeleteMapping("/delete")
	public RespBase<?> deregister( String appName) {
		if (cloudAppConfigService.load(appName).isPresent()) {
			removeAppAndJobConfigurations(appName);
		}
		return RespBase.create();
	}

	private void removeAppAndJobConfigurations( String appName) {
		for (CloudJobConfigurationPOJO each : cloudJobConfigService.loadAll()) {
			if (appName.equals(each.getAppName())) {
				producerService.deregister(each.getJobName());
			}
		}
		disableAppService.remove(appName);
		cloudAppConfigService.remove(appName);
	}

	private Collection<CloudAppConfigModel> build( Collection<CloudAppConfigurationPOJO> cloudAppConfigurationPOJOS) {
		return cloudAppConfigurationPOJOS.stream().map(each -> convert(each)).collect(Collectors.toList());
	}

	private CloudAppConfigModel convert( CloudAppConfigurationPOJO cloudAppConfigurationPOJO) {
		CloudAppConfigModel cloudAppConfiguration = new CloudAppConfigModel();
		BeanUtils.copyProperties(cloudAppConfigurationPOJO, cloudAppConfiguration);
		cloudAppConfiguration.setDisabled(disableAppService.isDisabled(cloudAppConfigurationPOJO.getAppName()));
		return cloudAppConfiguration;
	}
}
