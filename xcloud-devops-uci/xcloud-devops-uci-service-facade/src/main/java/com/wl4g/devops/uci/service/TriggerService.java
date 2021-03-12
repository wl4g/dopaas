/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
 */
package com.wl4g.devops.uci.service;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.devops.common.bean.uci.Trigger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 * @date 2019-05-17 11:04:00
 */
@FeignConsumer(name = "${provider.serviceId.uci-facade:uci-facade}")
@RequestMapping("/trigger-service")
public interface TriggerService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Trigger> list(@RequestBody PageHolder<Trigger> pm,
							 @RequestParam(name="id",required=false) Long id,
							 @RequestParam(name="name",required=false) String name,
							 @RequestParam(name="taskId",required=false) Long taskId,
							 @RequestParam(name="enable",required=false) Integer enable,
							 @RequestParam(name="startDate",required=false) String startDate,
							 @RequestParam(name="endDate",required=false) String endDate);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Trigger trigger);

	@RequestMapping(value = "/delete", method = POST)
	int delete(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/enable", method = POST)
	void enable(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/disable", method = POST)
	void disable(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/updateSha", method = POST)
	void updateSha(@RequestParam(name="id",required=false) Long id,
				   @RequestParam(name="sha",required=false) String sha);

	@RequestMapping(value = "/getById", method = POST)
	Trigger getById(@RequestParam(name="id",required=false) Long id);

}