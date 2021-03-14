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
package com.wl4g.dopaas.uci.service;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.uci.Orchestration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@FeignConsumer(name = "${provider.serviceId.uci-facade:uci-facade}")
@RequestMapping("/orchestration-service")
public interface OrchestrationService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Orchestration> list(@RequestBody PageHolder<Orchestration> pm,@RequestParam(name="name",required=false) String name);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Orchestration orchestration);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name="id",required=false)Long id);

	@RequestMapping(value = "/detail", method = POST)
	Orchestration detail(@RequestParam(name="id",required=false)Long id);

	@RequestMapping(value = "/run", method = POST)
	void run(@RequestParam(name="id",required=false)Long id,
			 @RequestParam(name="remark",required=false)String remark,
			 @RequestParam(name="taskTraceId",required=false)String taskTraceId,
			 @RequestParam(name="taskTraceType",required=false)String taskTraceType,
			 @RequestParam(name="annex",required=false)String annex);

}