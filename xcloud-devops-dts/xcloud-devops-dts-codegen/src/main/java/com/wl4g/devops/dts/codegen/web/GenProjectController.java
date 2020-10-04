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
package com.wl4g.devops.dts.codegen.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenProject.ExtraOptionDefinition;
import com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderSet;
import com.wl4g.devops.dts.codegen.service.GenProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
	public RespBase<?> list(PageModel pm, String projectName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genProjectService.page(pm, projectName));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody GenProject project) {
		RespBase<Object> resp = RespBase.create();
		genProjectService.save(project);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genProjectService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		genProjectService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getConfigOption")
	public RespBase<?> getConfigOption(String providerSet) {
		RespBase<Object> resp = RespBase.create();
		List<String> providers = GenProviderSet.getProviders(providerSet);
		resp.setData(ExtraOptionDefinition.getOptions(providers.toArray(new String[0])));
		return resp;
	}

}