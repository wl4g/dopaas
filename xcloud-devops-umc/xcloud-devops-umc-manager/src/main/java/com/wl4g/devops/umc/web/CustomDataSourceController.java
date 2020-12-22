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
package com.wl4g.devops.umc.web;

import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.umc.datasouces.MysqlDataSource;
import com.wl4g.component.core.bean.umc.model.DataSourceProvide;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.data.page.PageHolder;
import com.wl4g.devops.common.bean.umc.CustomDataSource;
import com.wl4g.devops.umc.service.CustomDataSourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.core.bean.umc.model.DataSourceProvide.MYSQL;
import static org.apache.shiro.authz.annotation.Logical.AND;

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
	@RequiresPermissions(value = { "umc:datasource" }, logical = AND)
	public RespBase<?> list(String name, PageHolder<CustomDataSource> pm) {
		RespBase<Object> resp = RespBase.create();
		return resp.withData(customDataSourceService.list(pm, name));
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "umc:datasource" }, logical = AND)
	public RespBase<?> save(String dataSource, String provider) {
		log.info("into CustomDatasourceController.save prarms::" + "customDataSource = {} ", dataSource);
		notNull(dataSource, "customDataSource is null");
		RespBase<Object> resp = RespBase.create();
		if (MYSQL.toString().equalsIgnoreCase(provider)) {
			MysqlDataSource mysqlDataSource = JacksonUtils.parseJSON(dataSource, MysqlDataSource.class);
			customDataSourceService.save(mysqlDataSource);
		}
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "umc:datasource" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(customDataSourceService.detal(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "umc:datasource" }, logical = AND)
	public RespBase<?> del(Long id) {
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

	@RequestMapping(value = "/dataSources")
	public RespBase<?> dataSources() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(customDataSourceService.dataSources());
		return resp;
	}

	@RequestMapping(value = "/testConnect")
	public RespBase<?> testConnect(String provider, String url, String username, String password, Long id) throws Exception {
		RespBase<Object> resp = RespBase.create();
		customDataSourceService.testConnect(DataSourceProvide.parse(provider), url, username, password, id);
		return resp;
	}

}