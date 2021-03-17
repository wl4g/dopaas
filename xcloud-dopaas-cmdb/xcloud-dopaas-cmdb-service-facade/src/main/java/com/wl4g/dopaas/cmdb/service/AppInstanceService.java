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

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;

/**
 * 应用组管理Service接口
 *
 * @author sut
 * @date 2018年9月20日
 */
@FeignConsumer(name = "${provider.serviceId.cmdb-facade:cmdb-facade}")
@RequestMapping("/appInstance-service")
public interface AppInstanceService {

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody AppInstance appInstance);

	@RequestMapping(value = "/list", method = POST)
	PageHolder<AppInstance> list(@RequestBody PageHolder<AppInstance> pm,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "clusterId", required = false) Long clusterId,
			@RequestParam(name = "envType", required = false) String envType,
			@RequestParam(name = "serverType", required = false) Integer serverType);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "clusterId", required = false) Long clusterId);

	@RequestMapping(value = "/detail", method = POST)
	AppInstance detail(@RequestParam(name = "instanceId", required = false) Long instanceId);

	@RequestMapping(value = "/getInstancesByClusterIdAndEnvType", method = POST)
	List<AppInstance> getInstancesByClusterIdAndEnvType(@RequestParam(name = "clusterId", required = false) Long clusterId,
			@RequestParam(name = "envType", required = false) String envType);

	@RequestMapping(value = "/testSSHConnect", method = POST)
	void testSSHConnect(@RequestParam(name = "hostId", required = false) Long hostId,
			@RequestParam(name = "sshUser", required = false) String sshUser,
			@RequestParam(name = "sshKey", required = false) String sshKey) throws Exception, InterruptedException;

}