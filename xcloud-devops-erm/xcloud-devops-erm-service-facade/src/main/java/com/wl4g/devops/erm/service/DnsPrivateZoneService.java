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
package com.wl4g.devops.erm.service;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import com.wl4g.devops.common.bean.erm.DnsPrivateZone;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 */
@SpringBootFeignClient(name = "${provider.serviceId.erm-facade:dnsPrivateZone-service}")
@RequestMapping("/dnsPrivateZone")
public interface DnsPrivateZoneService {

	@RequestMapping(value = "/page", method = POST)
	PageHolder<DnsPrivateZone> page(@RequestBody PageHolder<DnsPrivateZone> pm,
			@RequestParam(name = "zone", required = false) String zone);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody DnsPrivateZone dnsPrivateDomain);

	@RequestMapping(value = "/detail", method = POST)
	DnsPrivateZone detail(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/loadDnsAtStart", method = POST)
	void loadDnsAtStart();
}