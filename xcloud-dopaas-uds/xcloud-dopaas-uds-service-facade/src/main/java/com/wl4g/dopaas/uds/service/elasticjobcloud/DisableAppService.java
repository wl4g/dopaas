/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uds.service.elasticjobcloud;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;

/**
 * {@link DisableAppService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-03-22
 * @sine v1.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.uds-facade:uds-facade}")
@RequestMapping("/disableApp-service")
public interface DisableAppService {

	/**
	 * Add application name to disable queue.
	 *
	 * @param appName
	 *            application name
	 */
	@RequestMapping(path = "add", method = POST)
	void add(@RequestParam("appName") String appName);

	/**
	 * Remove application name from disable queue.
	 *
	 * @param appName
	 *            application name
	 */
	@RequestMapping(path = "remove", method = POST)
	void remove(@RequestParam("appName") String appName);

	/**
	 * Check whether the application name is disabled or not.
	 *
	 * @param appName
	 *            application name
	 * @return true is in the disable queue, otherwise not
	 */
	@RequestMapping(path = "isDisabled", method = GET)
	boolean isDisabled(@RequestParam("appName") String appName);

}
