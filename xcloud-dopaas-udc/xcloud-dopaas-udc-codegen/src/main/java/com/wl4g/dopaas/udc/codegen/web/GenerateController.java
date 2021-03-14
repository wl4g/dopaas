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
package com.wl4g.dopaas.udc.codegen.web;

import static com.wl4g.component.common.io.FileIOUtils.readFullyResourceString;
import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static org.apache.shiro.authz.annotation.Logical.AND;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.udc.codegen.bean.GenTable;
import com.wl4g.dopaas.udc.codegen.bean.GenTableColumn;
import com.wl4g.dopaas.udc.codegen.bean.extra.TableExtraOptionDefinition;
import com.wl4g.dopaas.udc.codegen.bean.extra.TableExtraOptionDefinition.GenTableExtraOption;
import com.wl4g.dopaas.udc.codegen.config.CodegenProperties;
import com.wl4g.dopaas.udc.codegen.engine.context.GeneratedResult;
import com.wl4g.dopaas.udc.codegen.engine.resolver.TableMetadata;
import com.wl4g.dopaas.udc.codegen.service.GenerateService;

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
	protected CodegenProperties config;

	@Autowired
	protected GenerateService genService;

	// --- GenTable/GenColumns configuration. ---

	/**
	 * Search page query of {@link GenTable}
	 * 
	 * @param pm
	 * @param tableName
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "udc:codegen" }, logical = AND)
	public RespBase<PageHolder<GenTable>> page(PageHolder<GenTable> pm, String tableName, Long projectId) {
		RespBase<PageHolder<GenTable>> resp = RespBase.create();
		resp.setData(genService.page(pm, tableName, projectId));
		return resp;
	}

	/**
	 * Save {@link GenTable} and {@link GenTableColumn} details.
	 * 
	 * @param table
	 * @return
	 */
	@RequestMapping("save")
	@RequiresPermissions(value = { "udc:codegen" }, logical = AND)
	public RespBase<?> save(@RequestBody GenTable table) {
		RespBase<Object> resp = RespBase.create();
		genService.saveGenConfig(table);
		return resp;
	}

	/**
	 * Query the {@link GenTable} and {@link GenTableColumn} details.
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping("detail")
	@RequiresPermissions(value = { "udc:codegen" }, logical = AND)
	public RespBase<?> detail(Long tableId) {
		return genService.detail(tableId);
	}

	/**
	 * Delete for {@link GenTable} and {@link GenTableColumn}.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("del")
	@RequiresPermissions(value = { "udc:codegen" }, logical = AND)
	public RespBase<?> delete(Long id) {
		RespBase<Object> resp = RespBase.create();
		genService.deleteGenTable(id);
		return resp;
	}

	/**
	 * Sets status of {@link GenTable}.
	 * 
	 * @param id
	 * @param status
	 * @return
	 */
	@RequestMapping("setGenTableStatus")
	public RespBase<?> setGenTableStatus(Long id, String status) {
		RespBase<Object> resp = RespBase.create();
		notNullOf(id, "id");
		genService.setGenTableStatus(id, status);
		return resp;
	}

	// --- Generate configuration. ---

	@RequestMapping("findTables")
	public RespBase<?> findTables(Long projectId) throws Exception {
		RespBase<List<TableMetadata>> resp = RespBase.create();
		resp.setData(genService.findTables(projectId));
		return resp;
	}

	/**
	 * Load latest table metadata and columns information.
	 * 
	 * @param projectId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("loadGenColumns")
	public RespBase<GenTable> loadGenColumns(Long projectId, String tableName) throws Exception {
		return genService.loadGenColumns(projectId, tableName);
	}

	@RequestMapping("getAttrTypes")
	public RespBase<?> getAttrTypes(Long projectId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genService.getAttrTypes(projectId));
		return resp;
	}

	/**
	 * Load table {@link GenTableExtraOption} of
	 * {@link TableExtraOptionDefinition}
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tableExtraOptions")
	public RespBase<?> tableExtraOptions() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(TableExtraOptionDefinition.getOptions());
		return resp;
	}

	/**
	 * Synchronize attributes of {@link GenTable} and {@link GenTableColumn}
	 * 
	 * @param id
	 * @param force
	 * @return
	 */
	@RequestMapping("syncGenTableConfig")
	public RespBase<?> syncGenTableConfig(Long id, boolean force) throws Exception {
		RespBase<Object> resp = RespBase.create();
		genService.syncTableColumns(id, force);
		return resp;
	}

	// --- Execution generates. ---

	@RequestMapping("generate")
	@RequiresPermissions(value = { "udc:codegen" }, logical = AND)
	public RespBase<?> generate(Long id, HttpServletResponse response) throws IOException {
		RespBase<Object> resp = RespBase.create();

		// Execution generate.
		GeneratedResult result = genService.generate(id);
		resp.setData(result.getJobId());
		return resp;
	}

	@RequestMapping("download")
	public void download(String jobId, HttpServletResponse response) throws IOException {
		hasTextOf(jobId, "jobId");

		// Make generated job dir.
		File jobDir = config.generateJobDir(jobId);

		// Add generated README
		FileIOUtils.writeFile(new File(jobDir, "GENERATED.md"), GENERATED_README, false);
		FileIOUtils.writeFile(new File(jobDir, "GENERATED_CN.md"), GENERATED_README_CN, false);

		// ZIP download
		writeZip(response, jobDir.getCanonicalPath(), "codegen-".concat(jobId));
	}

	/**
	 * Generated watermark readme.
	 */
	public static final String GENERATED_README = readFullyResourceString("project-templates/doc/GENERATED.md");
	public static final String GENERATED_README_CN = readFullyResourceString("project-templates/doc/GENERATED_CN.md");

}