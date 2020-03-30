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
package com.wl4g.devops.umc.web;

import com.wl4g.devops.common.bean.umc.CustomDataSource;
import com.wl4g.devops.common.bean.umc.model.DataSourceProvide;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.umc.service.CustomDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.devops.tool.common.lang.Assert2.hasText;
import static com.wl4g.devops.tool.common.lang.Assert2.notNull;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/datasource")
public class CustomDataSourceController extends BaseController {


	@Autowired
	private CustomDataSourceService customDataSourceService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String name, PageModel pm) {
		RespBase<Object> resp = RespBase.create();
		PageModel list = customDataSourceService.list(pm, name);
		resp.setData(list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody CustomDataSource customDataSource) {
		log.info("into CustomDatasourceController.save prarms::" + "customDataSource = {} ", customDataSource);
		notNull(customDataSource, "customDataSource is null");
		hasText(customDataSource.getName(), "name is null");
		hasText(customDataSource.getUrl(), "url is null");
		hasText(customDataSource.getProvider(), "provider is null");
		hasText(customDataSource.getUsername(), "username is null");
		RespBase<Object> resp = RespBase.create();
		customDataSourceService.save(customDataSource);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		CustomDataSource customDataSource = customDataSourceService.detal(id);
		resp.setData(customDataSource);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into CustomDatasourceController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		customDataSourceService.del(id);
		return resp;
	}

	@RequestMapping(value = "/dataSourceProvides")
	public RespBase<?> dataSourceProvides() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(DataSourceProvide.dataSourceProvides());
		return resp;
	}


	@RequestMapping(value = "/testConnect")
	public RespBase<?> testConnect(String provider, String url,String username,String password,Integer id) throws Exception {
		RespBase<Object> resp = RespBase.create();
		customDataSourceService.testConnect(DataSourceProvide.parse(provider),url,username,password,id);
		return resp;
	}



}