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
package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.erm.ClusterConfig;
import com.wl4g.devops.dao.erm.ClusterConfigDao;
import com.wl4g.devops.iam.service.ClusterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-11-14 11:47:00
 */
@Service
public class ClusterConfigServiceImpl implements ClusterConfigService {

	@Value("${spring.profiles.active}")
	private String profile;

	@Autowired
	private ClusterConfigDao clusterConfigDao;

	@Override
	public Map<String, Object> info() {
		List<ClusterConfig> list = clusterConfigDao.getByAppNames(null, profile, null);
		Assert.notEmpty(list, "not found cluster config info , Please Check your db , table = 'erm_cluster_config'");
		Map<String, Object> map = new HashMap<>();
		for (ClusterConfig entryAddress : list) {
			map.put(entryAddress.getName(), entryAddress);
		}
		return map;
	}
}