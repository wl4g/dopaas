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

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.erm.DockerCluster;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.devops.erm.service.DockerClusterService;
import com.wl4g.devops.page.PageModel;
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
@RequestMapping("/dockerCluster")
public class DockerClusterController extends BaseController {

	@Autowired
	private DockerClusterService dockerClusterService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerClusterService.page(pm, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody DockerCluster dockerCluster) {
		RespBase<Object> resp = RespBase.create();
		dockerClusterService.save(dockerCluster);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerClusterService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
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