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
package com.wl4g.devops.cmdb.service;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.devops.common.bean.cmdb.DnsOperationLog;

/**
 * @author vjay
 */
@FeignConsumer(name = "${provider.serviceId.cmdb-facade:cmdb-facade}")
@RequestMapping("/dnsOperationLog-service")
public interface DnsOperationLogService {

	@RequestMapping(value = "/page", method = POST)
	PageHolder<DnsOperationLog> page(@RequestBody PageHolder<DnsOperationLog> pm,
			@RequestParam(name = "domain", required = false) String domain);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody DnsOperationLog dnsOperationLog);

	@RequestMapping(value = "/detail", method = POST)
	DnsOperationLog detail(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "id", required = false) Long id);
}