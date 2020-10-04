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
import com.wl4g.components.core.bean.erm.DockerRepository;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.devops.erm.service.DockerRepositoryService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * 字典
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/dockerRepository")
public class DockerRepositoryController extends BaseController {

	@Autowired
	private DockerRepositoryService dockerRepositoryService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerRepositoryService.page(pm, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody DockerRepository dockerRepository) {
		RespBase<Object> resp = RespBase.create();
		dockerRepositoryService.save(dockerRepository);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerRepositoryService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		dockerRepositoryService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getForSelect")
	public RespBase<?> getForSelect() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerRepositoryService.getForSelect());
		return resp;
	}

	@RequestMapping(value = "/getRepositoryProjects")
	public RespBase<?> getRepositoryProjects(Long id, String address, String name)
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		RespBase<Object> resp = RespBase.create();
		resp.setData(dockerRepositoryService.getRepositoryProjects(id, address, name));
		return resp;
	}

}