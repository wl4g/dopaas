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
package com.wl4g.devops.udc.codegen.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.udc.codegen.bean.GenProject;
import com.wl4g.devops.udc.codegen.bean.extra.ExtraOptionDefinition;
import com.wl4g.devops.udc.codegen.engine.GenProviderSetDefinition;
import com.wl4g.devops.udc.codegen.service.GenProjectService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * {@link GenProjectController}
 *
 * @author heweijie
 * @Date 2020-09-11
 */
@RestController
@RequestMapping("/gen/project")
public class GenProjectController extends BaseController {

	@Autowired
	private GenProjectService genProjectService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "dts:codegen:project" }, logical = AND)
	public RespBase<PageHolder<GenProject>> list(PageHolder<GenProject> pm, String projectName) {
		RespBase<PageHolder<GenProject>> resp = RespBase.create();
		resp.setData(genProjectService.page(pm, projectName));
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "dts:codegen:project" }, logical = AND)
	public RespBase<?> save(@RequestBody GenProject project) {
		RespBase<Object> resp = RespBase.create();
		genProjectService.save(project);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "dts:codegen:project" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genProjectService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "dts:codegen:project" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		genProjectService.del(id);
		return resp;
	}

	/**
	 * Load project {@link GenExtraOption} of {@link ExtraOptionDefinition}
	 * 
	 * @param providerSet
	 * @return
	 */
	@RequestMapping(value = "/extraOptions")
	public RespBase<?> extraOptions(String providerSet) {
		RespBase<Object> resp = RespBase.create();
		List<String> providers = GenProviderSetDefinition.getProviders(providerSet);
		resp.setData(ExtraOptionDefinition.getOptions(providers.toArray(new String[0])));
		return resp;
	}

	@RequestMapping(value = "/getGenProviderSet")
	public RespBase<?> getGenProviderSet(String providerSet) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(GenProviderSetDefinition.values());
		return resp;
	}

}