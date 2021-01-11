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
package com.wl4g.devops.erm.service;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import com.wl4g.devops.common.bean.erm.SshBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 */
@SpringBootFeignClient("sshService")
@RequestMapping("/ssh")
public interface SshService {

	@RequestMapping(value = "/page", method = POST)
	PageHolder<SshBean> page(@RequestBody PageHolder<SshBean> pm,
							 String name);

	@RequestMapping(value = "/getForSelect", method = POST)
	List<SshBean> getForSelect();

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody SshBean ssh);

	@RequestMapping(value = "/detail", method = POST)
	SshBean detail(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/testSSHConnect", method = POST)
	void testSSHConnect(@RequestParam(name="hostId",required=false) Long hostId,
						@RequestParam(name="sshUser",required=false) String sshUser,
						@RequestParam(name="sshKey",required=false) String sshKey,
						@RequestParam(name="sshId",required=false) Long sshId) throws Exception, InterruptedException;
}