/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.erm.controller;

import com.wl4g.devops.common.bean.erm.Host;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.erm.service.HostService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/host")
public class AppHostController extends BaseController {

	@Autowired
	private HostService hostService;

	@RequestMapping(value = "/allHost")
	public RespBase<?> all() {
		RespBase<Object> resp = RespBase.create();
		List<Host> list = hostService.list(null, null, null);
		resp.setData(list);
		return resp;
	}

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "erm:host" })
	public RespBase<?> list(PageModel pm, String name, String hostname) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(hostService.page(pm, name, hostname, null));
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "erm:host" })
	public RespBase<?> save(Host host) {
		RespBase<Object> resp = RespBase.create();
		hostService.save(host);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "erm:host" })
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(hostService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "erm:host" })
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		hostService.del(id);
		return resp;
	}

}