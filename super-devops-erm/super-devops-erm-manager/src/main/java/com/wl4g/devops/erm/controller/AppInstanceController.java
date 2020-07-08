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

import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.components.tools.common.cli.ssh2.JschHolder;
import com.wl4g.devops.components.tools.common.cli.ssh2.SSH2Holders;
import com.wl4g.devops.erm.service.AppInstanceService;
import com.wl4g.devops.page.PageModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用组管理
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月2日
 * @since
 */
@RestController
@RequestMapping("/instance")
public class AppInstanceController extends BaseController {

	@Autowired
	private AppInstanceService appInstanceService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm,String name, Integer clusterId,String envType,Integer deployType) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(appInstanceService.list(pm, name ,clusterId,envType,deployType));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody AppInstance appInstance) {
		RespBase<Object> resp = RespBase.create();
		appInstanceService.save(appInstance);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		appInstanceService.del(id);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		AppInstance detail = appInstanceService.detail(id);
		resp.setData(detail);
		return resp;
	}

	@RequestMapping(value = "/connectTest")
	public RespBase<?> testSSHConnect(Integer hostId, String sshUser, String sshKey) throws Exception, InterruptedException {
		RespBase<Object> resp = RespBase.create();
		appInstanceService.testSSHConnect(hostId, sshUser, sshKey);
		return resp;
	}

	@RequestMapping(value = "/generateSshKeyPair")
	public RespBase<?> generateSshKeyPair() throws Exception {
		RespBase<Object> resp = RespBase.create();
		SSH2Holders.Ssh2KeyPair ssh2KeyPair = SSH2Holders.getInstance(JschHolder.class).generateKeypair(SSH2Holders.AlgorithmType.RSA, "generateBySystem");
		resp.setData(ssh2KeyPair);
		return resp;
	}

}