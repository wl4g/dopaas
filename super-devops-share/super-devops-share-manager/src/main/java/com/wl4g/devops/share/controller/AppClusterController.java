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
package com.wl4g.devops.share.controller;

import com.wl4g.devops.common.bean.PageModel;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.AppClusterDao;
import com.wl4g.devops.share.service.AppClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private AppClusterDao appClusterDao;

	@Autowired
	private AppClusterService appClusterService;

	@Value("${cipher-key}")
	protected String cipherKey;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel customPage, String clusterName) {
		RespBase<Object> resp = RespBase.create();
		Map<String, Object> result = appClusterService.list(customPage, clusterName);
		resp.setData(result);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody AppCluster appCluster) {
		RespBase<Object> resp = RespBase.create();
		appClusterService.save(appCluster, cipherKey);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer clusterId) {
		RespBase<Object> resp = RespBase.create();
		appClusterService.del(clusterId);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer clusterId) {
		RespBase<Object> resp = RespBase.create();
		AppCluster detail = appClusterService.detail(clusterId, cipherKey);
		resp.forMap().put("data", detail);
		return resp;
	}

	@RequestMapping(value = "/clusters")
	public RespBase<?> clusters() {
		RespBase<Object> resp = RespBase.create();
		List<AppCluster> clusters = appClusterDao.list(null);
		resp.forMap().put("clusters", clusters);
		return resp;
	}

	@RequestMapping(value = "/instances")
	public RespBase<?> instances(Integer clusterId, String envType) {
		RespBase<Object> resp = RespBase.create();
		List<AppInstance> instances = appClusterService.getInstancesByClusterIdAndEnvType(clusterId, envType);
		resp.forMap().put("instances", instances);
		return resp;
	}

}