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
package com.wl4g.dopaas.cmdb.service;

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.cmdb.AppCluster;
import com.wl4g.dopaas.common.bean.cmdb.AppEnvironment;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Application cluster information service of {@link AppClusterService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-11-13
 * @sine v1.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.cmdb-facade:cmdb-facade}")
@RequestMapping("/appCluster-service")
public interface AppClusterService {

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody AppCluster appCluster);

	@RequestMapping(value = "/list", method = POST)
	Map<String, Object> list(@RequestBody PageHolder<?> pm,
			@RequestParam(name = "clusterName", required = false) String clusterName,
			@RequestParam(name = "deployType", required = false) Integer deployType);

	@RequestMapping(value = "/clusters", method = POST)
	List<AppCluster> clusters();

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "clusterId", required = false) Long clusterId);

	@RequestMapping(value = "/detail", method = POST)
	AppCluster detail(@RequestParam(name = "clusterId", required = false) Long clusterId);

	@RequestMapping(value = "/getInstancesByClusterIdAndEnvType", method = POST)
	List<AppInstance> getInstancesByClusterIdAndEnvType(@RequestParam(name = "clusterId", required = false) Long clusterId,
			@RequestParam(name = "envType", required = false) String envType);

	@RequestMapping(value = "/getAppClusterEnvironment", method = POST)
	AppEnvironment getAppClusterEnvironment(@RequestParam(name = "clusterId", required = false) Long clusterId,
			@RequestParam(name = "envType", required = false) String envType);


	@RequestMapping(value = "/getById", method = POST)
	AppCluster getById(@RequestParam(name = "clusterId", required = true) Long clusterId);

	@RequestMapping(value = "/getByName", method = POST)
	AppCluster getByName(@RequestParam(name = "name", required = true) String name);

	@RequestMapping(value = "/getIdsByLikeName", method = POST)
	List<Long> getIdsByLikeName(@RequestParam(name = "name", required = true) String name);

	@RequestMapping(value = "/getByLikeName", method = POST)
	List<AppCluster> getByLikeName(@RequestParam(name = "name", required = true) String name);

}