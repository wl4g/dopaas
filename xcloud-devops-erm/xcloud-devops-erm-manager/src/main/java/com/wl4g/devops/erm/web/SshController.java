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

import com.wl4g.component.common.cli.ssh2.JschHolder;
import com.wl4g.component.common.cli.ssh2.SSH2Holders;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageModel;
import com.wl4g.devops.common.bean.erm.SshBean;
import com.wl4g.devops.erm.service.SshService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * 字典
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/ssh")
public class SshController extends BaseController {

	@Autowired
	private SshService sshService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "erm:ssh" }, logical = AND)
	public RespBase<?> list(PageModel<SshBean> pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(sshService.page(pm, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "erm:ssh" }, logical = AND)
	public RespBase<?> save(@RequestBody SshBean ssh) {
		RespBase<Object> resp = RespBase.create();
		sshService.save(ssh);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "erm:ssh" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(sshService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "erm:ssh" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		sshService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getForSelect")
	public RespBase<?> getForSelect() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(sshService.getForSelect());
		return resp;
	}

	@RequestMapping(value = "/connectTest")
	public RespBase<?> testSSHConnect(Long hostId, String sshUser, String sshKey, Long sshId)
			throws Exception, InterruptedException {
		RespBase<Object> resp = RespBase.create();
		sshService.testSSHConnect(hostId, sshUser, sshKey, sshId);
		return resp;
	}

	@RequestMapping(value = "/generateSshKeyPair")
	public RespBase<?> generateSshKeyPair() throws Exception {
		RespBase<Object> resp = RespBase.create();
		SSH2Holders.Ssh2KeyPair ssh2KeyPair = SSH2Holders.getInstance(JschHolder.class)
				.generateKeypair(SSH2Holders.AlgorithmType.RSA, "generateBySystem");
		resp.setData(ssh2KeyPair);
		return resp;
	}

}