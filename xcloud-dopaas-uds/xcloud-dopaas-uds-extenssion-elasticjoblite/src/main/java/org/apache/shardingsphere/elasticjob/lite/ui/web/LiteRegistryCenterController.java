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

package org.apache.shardingsphere.elasticjob.lite.ui.web;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.reg.RegistryCenterFactory;
import org.apache.shardingsphere.elasticjob.lite.ui.domain.LiteRegistryCenterConfig;
import org.apache.shardingsphere.elasticjob.lite.ui.service.LiteRegistryCenterConfigService;
import org.apache.shardingsphere.elasticjob.lite.ui.util.LiteSessionRegistryCenterFactory;
import org.apache.shardingsphere.elasticjob.reg.exception.RegException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;

/**
 * Registry center RESTful API.
 */
@RestController
@RequestMapping("/api/registry-center")
public final class LiteRegistryCenterController extends BaseController {

	public static final String REG_CENTER_CONFIG_KEY = "reg_center_config_key";

	private LiteRegistryCenterConfigService regCenterService;

	@Autowired
	public LiteRegistryCenterController(final LiteRegistryCenterConfigService regCenterService) {
		this.regCenterService = regCenterService;
	}

	/**
	 * Judge whether registry center is activated.
	 *
	 * @return registry center is activated or not
	 */
	@GetMapping("/activated")
	public RespBase<LiteRegistryCenterConfig> activated() {
		return RespBase.<LiteRegistryCenterConfig> create().withData(regCenterService.loadActivated().orElse(null));
	}

	/**
	 * Load configuration from registry center.
	 *
	 * @param request
	 *            HTTP request
	 * @return registry center configurations
	 */
	@GetMapping("/load")
	public RespBase<Collection<LiteRegistryCenterConfig>> load(final HttpServletRequest request) {
		regCenterService.loadActivated()
				.ifPresent(regCenterConfig -> setRegistryCenterNameToSession(regCenterConfig, request.getSession()));
		return RespBase.<Collection<LiteRegistryCenterConfig>> create()
				.withData(regCenterService.loadAll().getRegistryCenterConfiguration());
	}

	/**
	 * Add registry center.
	 *
	 * @param config
	 *            registry center configuration
	 * @return success to add or not
	 */
	@PostMapping("/add")
	public RespBase<Boolean> add(@RequestBody final LiteRegistryCenterConfig config) {
		return RespBase.<Boolean> create().withData(regCenterService.add(config));
	}

	/**
	 * Delete registry center.
	 *
	 * @param config
	 *            registry center configuration
	 */
	@DeleteMapping
	public RespBase<?> delete(@RequestBody final LiteRegistryCenterConfig config) {
		regCenterService.delete(config.getName());
		return RespBase.create();
	}

	/**
	 * Connect to registry center.
	 *
	 * @param config
	 *            config of registry center
	 * @param request
	 *            HTTP request
	 * @return connected or not
	 */
	@PostMapping(value = "/connect")
	public RespBase<Boolean> connect(@RequestBody final LiteRegistryCenterConfig config, final HttpServletRequest request) {
		boolean isConnected = setRegistryCenterNameToSession(regCenterService.find(config.getName(), regCenterService.loadAll()),
				request.getSession());
		if (isConnected) {
			regCenterService.load(config.getName());
		}
		return RespBase.<Boolean> create().withData(isConnected);
	}

	private boolean setRegistryCenterNameToSession(final LiteRegistryCenterConfig regCenterConfig, final HttpSession session) {
		session.setAttribute(REG_CENTER_CONFIG_KEY, regCenterConfig);
		try {
			RegistryCenterFactory.createCoordinatorRegistryCenter(regCenterConfig.getZkAddressList(),
					regCenterConfig.getNamespace(), regCenterConfig.getDigest());
			LiteSessionRegistryCenterFactory
					.setRegistryCenterConfiguration((LiteRegistryCenterConfig) session.getAttribute(REG_CENTER_CONFIG_KEY));
		} catch (final RegException ex) {
			return false;
		}
		return true;
	}

}
