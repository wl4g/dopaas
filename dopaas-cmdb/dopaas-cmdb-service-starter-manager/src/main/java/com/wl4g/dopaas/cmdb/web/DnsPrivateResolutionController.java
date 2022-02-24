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
package com.wl4g.dopaas.cmdb.web;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.core.web.BaseController;
import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.cmdb.DnsPrivateResolution;
import com.wl4g.dopaas.cmdb.service.DnsPrivateResolutionService;

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
@RequestMapping("/dnsPrivateResolution")
public class DnsPrivateResolutionController extends BaseController {

	private @Autowired DnsPrivateResolutionService dnsPrivateResolutionService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageHolder<DnsPrivateResolution> pm, String host, Long domainId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dnsPrivateResolutionService.page(pm, host, domainId));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody DnsPrivateResolution dnsPrivateResolution) {
		RespBase<Object> resp = RespBase.create();
		dnsPrivateResolutionService.save(dnsPrivateResolution);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dnsPrivateResolutionService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		dnsPrivateResolutionService.del(id);
		return resp;
	}

}