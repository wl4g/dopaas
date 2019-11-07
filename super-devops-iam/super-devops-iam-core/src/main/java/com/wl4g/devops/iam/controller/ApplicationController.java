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
package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.share.ClusterConfig;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.ClusterConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-09-16 14:32:00
 */
@RestController
@RequestMapping("/application")
public class ApplicationController extends BaseController {

	@Autowired
	private ClusterConfigDao clusterConfigDao;

	@Value("${spring.profiles.active}")
	private String profile;

	@RequestMapping(value = "/info")
	public RespBase<?> allType() {
		RespBase<Object> resp = RespBase.create();
		List<ClusterConfig> list = clusterConfigDao.getByAppNames(null, profile, null);
		Map<String, Object> map = new HashMap<>();
		for (ClusterConfig entryAddress : list) {
			map.put(entryAddress.getName(), entryAddress);
		}
		resp.forMap().put("map", map);
		// System.out.println(JacksonUtils.toJSONString(list));
		return resp;
	}

}