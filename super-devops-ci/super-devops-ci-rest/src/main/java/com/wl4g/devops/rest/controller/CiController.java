/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.rest.controller;

import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CI/CD controller
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/ci")
public class CiController {

	@Autowired
	private CiService ciService;

	@RequestMapping(value = "/grouplist")
	public RespBase<?> grouplist() {
		RespBase<List<AppGroup>> resp = RespBase.create();
		resp.getData().put("appGroups", ciService.grouplist());
		return resp;
	}

	@RequestMapping(value = "/environmentlist")
	public RespBase<?> environmentlist(String groupId) {
		RespBase<List<Environment>> resp = RespBase.create();
		List<Environment> environments = ciService.environmentlist(groupId);
		resp.getData().put("environments", environments);
		return resp;
	}

	@RequestMapping(value = "/instancelist")
	public RespBase<?> instancelist(AppInstance appInstance) {
		RespBase<List<AppInstance>> resp = RespBase.create();
		List<AppInstance> appInstances = ciService.instancelist(appInstance);
		resp.getData().put("appInstances", appInstances);
		return resp;
	}

}