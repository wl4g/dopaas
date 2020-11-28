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
import com.wl4g.components.core.bean.model.PageModel;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.erm.service.AppClusterService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 应用组管理
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月2日
 * @since
 */
@RestController
@RequestMapping("/cluster")
public class AppClusterController extends BaseController {

	@Autowired
	private AppClusterService appClusterService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "erm:cluster" })
	public RespBase<?> list(PageModel<?> pm, String clusterName, Integer deployType) {
		RespBase<Object> resp = RespBase.create();
		Map<String, Object> result = appClusterService.list(pm, clusterName, deployType);
		resp.setData(result);
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "erm:cluster" })
	public RespBase<?> save(@RequestBody AppCluster appCluster) {
		RespBase<Object> resp = RespBase.create();
		appClusterService.save(appCluster);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "erm:cluster" })
	public RespBase<?> del(Long clusterId) {
		RespBase<Object> resp = RespBase.create();
		appClusterService.del(clusterId);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "erm:cluster" })
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		AppCluster detail = appClusterService.detail(id);
		resp.setData(detail);
		return resp;
	}

	@RequestMapping(value = "/clusters")
	public RespBase<?> clusters() {
		RespBase<Object> resp = RespBase.create();
		List<AppCluster> clusters = appClusterService.clusters();
		resp.forMap().put("clusters", clusters);
		return resp;
	}

	@RequestMapping(value = "/instances")
	public RespBase<?> instances(Long clusterId, String envType) {
		RespBase<Object> resp = RespBase.create();
		List<AppInstance> instances = appClusterService.getInstancesByClusterIdAndEnvType(clusterId, envType);
		resp.forMap().put("instances", instances);
		return resp;
	}

}