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
package com.wl4g.devops.erm.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.common.bean.erm.DnsPrivateZone;
import com.wl4g.devops.erm.service.DnsPrivateZoneService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/dnsPrivateDomain")
public class DnsPrivateZoneController extends BaseController {

	@Autowired
	private DnsPrivateZoneService dnsPrivateDomainService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel<DnsPrivateZone> pm, String zone) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dnsPrivateDomainService.page(pm, zone));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody DnsPrivateZone dnsPrivateDomain) {
		RespBase<Object> resp = RespBase.create();
		dnsPrivateDomainService.save(dnsPrivateDomain);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dnsPrivateDomainService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		dnsPrivateDomainService.del(id);
		return resp;
	}

}