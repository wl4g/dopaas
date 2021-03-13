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
package com.wl4g.devops.cmdb.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.cmdb.HostNetcard;
import com.wl4g.devops.cmdb.service.HostNetcardService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * 字典
 *
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/netcard")
public class HostNetcardController extends BaseController {

	@Autowired
	private HostNetcardService hostNetcardService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "cmdb:netcard" }, logical = AND)
	public RespBase<?> list(PageHolder<HostNetcard> pm, Long hostId, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(hostNetcardService.page(pm, hostId, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "cmdb:netcard" }, logical = AND)
	public RespBase<?> save(@RequestBody HostNetcard hostNetcard) {
		RespBase<Object> resp = RespBase.create();
		hostNetcardService.save(hostNetcard);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "cmdb:netcard" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(hostNetcardService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "cmdb:netcard" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		hostNetcardService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getHostTunnel")
	public RespBase<?> getHostTunnel() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(hostNetcardService.getHostTunnel());
		return resp;
	}

}