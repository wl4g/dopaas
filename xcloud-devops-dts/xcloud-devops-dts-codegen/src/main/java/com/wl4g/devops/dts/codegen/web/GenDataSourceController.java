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
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.service.GenDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link GenDataSourceController}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @date 2020-09-08
 * @version v1.0 2020-09-10
 * @since
 */
@RestController
@RequestMapping("/gen/datasource")
public class GenDataSourceController extends BaseController {

	@Autowired
	private GenDataSourceService genDSService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String name) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genDSService.page(pm, name));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody GenDataSource gen) {
		RespBase<Object> resp = RespBase.create();
		genDSService.save(gen);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genDSService.detail(id));
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		genDSService.del(id);
		return resp;
	}

	@RequestMapping(value = "/loadDatasources")
	public RespBase<?> loadDatasources() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genDSService.loadDatasources());
		return resp;
	}

	@RequestMapping(value = "/testConnectDb")
	public RespBase<?> testConnectDb(@RequestBody GenDataSource dataSource) throws Exception {
		RespBase<Object> resp = RespBase.create();
		genDSService.testConnectDb(dataSource);
		return resp;
	}

}