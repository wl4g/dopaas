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
import com.wl4g.dopaas.common.bean.cmdb.DockerCluster;
import com.wl4g.dopaas.cmdb.service.DockerClusterService;

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
@RequestMapping("/dockerCluster")
public class DockerClusterController extends BaseController {

	private @Autowired DockerClusterService dockerClusterService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "cmdb:dockercluster" }, logical = AND)
	public RespBase<?> list(PageHolder<DockerCluster> pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerClusterService.page(pm, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "cmdb:dockercluster" }, logical = AND)
	public RespBase<?> save(@RequestBody DockerCluster dockerCluster) {
		RespBase<Object> resp = RespBase.create();
		dockerClusterService.save(dockerCluster);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "cmdb:dockercluster" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerClusterService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "cmdb:dockercluster" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		dockerClusterService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getForSelect")
	public RespBase<?> getForSelect() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerClusterService.getForSelect());
		return resp;
	}
}