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
package com.wl4g.devops.uci.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.uci.service.OrchestrationService;
import com.wl4g.devops.common.bean.uci.Orchestration;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * @author vjay
 * @date 2019-11-12 11:03:00
 */
@RestController
@RequestMapping("/orchestration")
public class OrchestrationController extends BaseController {

	@Autowired
	private OrchestrationService orchestrationService;

	@RequestMapping("/list")
	@RequiresPermissions(value = { "uci:orchestration" }, logical = AND)
	public RespBase<?> list(PageHolder<Orchestration> pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(orchestrationService.list(pm, name));
		return resp;
	}

	@RequestMapping("/save")
	@RequiresPermissions(value = { "uci:orchestration" }, logical = AND)
	public RespBase<?> save(@RequestBody Orchestration orchestration) {
		RespBase<Object> resp = RespBase.create();
		orchestrationService.save(orchestration);
		return resp;
	}

	@RequestMapping("/del")
	@RequiresPermissions(value = { "uci:orchestration" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		orchestrationService.del(id);
		return resp;
	}

	@RequestMapping("/detail")
	@RequiresPermissions(value = { "uci:orchestration" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		Orchestration orchestration = orchestrationService.detail(id);
		resp.setData(orchestration);
		return resp;
	}

	@RequestMapping("/run")
	@RequiresPermissions(value = { "uci:orchestration" }, logical = AND)
	public RespBase<?> run(Long id, String remark, @NotNull String trackId, @NotNull String trackType, String annex) {
		RespBase<Object> resp = RespBase.create();
		orchestrationService.run(id, remark, trackId, trackType, annex);
		return resp;
	}

}