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

import com.wl4g.components.common.io.ZipIOUtils;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static java.lang.Integer.valueOf;

/**
 * {@link GenerateController}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@RestController
@RequestMapping("/gen/configure")
public class GenerateController extends BaseController {

	@Autowired
	private GenerateService generateService;

	@RequestMapping("loadTables")
	public RespBase<?> loadTables(Integer databaseId) {
		RespBase<Object> resp = RespBase.create();
		List<String> strings = generateService.loadTables(databaseId);
		resp.setData(strings);
		return resp;
	}

	@RequestMapping("loadMetadata")
	public RespBase<?> loadMetadata(Integer databaseId, Integer projectId, String tableName) {
		RespBase<Object> resp = RespBase.create();
		GenTable genTable = generateService.loadMetadata(databaseId, projectId, tableName);
		resp.setData(genTable);
		return resp;
	}

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String tableName, Integer projectId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(generateService.page(pm, tableName, projectId));
		return resp;
	}

	@RequestMapping("save")
	public RespBase<?> save(@RequestBody GenTable genTable) {
		RespBase<Object> resp = RespBase.create();
		generateService.saveGenConfig(genTable);
		return resp;
	}

	@RequestMapping("detail")
	public RespBase<?> detail(Integer tableId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(generateService.detail(tableId));
		return resp;
	}

	@RequestMapping("del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		generateService.delete(id);
		return resp;
	}

	@RequestMapping("generate")
	public void generate(String id, HttpServletResponse response) {
		hasTextOf(id, "id");
		String jobPath = generateService.generate(valueOf(id));
		ZipIOUtils.zip(jobPath, response);
	}

}