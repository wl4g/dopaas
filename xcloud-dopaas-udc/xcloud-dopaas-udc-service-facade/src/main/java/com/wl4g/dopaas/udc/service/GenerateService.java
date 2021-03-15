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
package com.wl4g.dopaas.udc.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.udc.GenTable;
import com.wl4g.dopaas.common.bean.udc.GenTableColumn;
import com.wl4g.dopaas.common.bean.udc.model.GeneratedResult;
import com.wl4g.dopaas.common.bean.udc.model.TableMetadata;

/**
 * {@link GenerateService}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@FeignConsumer(name = "${provider.serviceId.udc-udc-facade}")
@RequestMapping("/generate-service")
public interface GenerateService {

	// ------------------------------------------
	// Generate tables/columns configuration.
	// ------------------------------------------

	/**
	 * Gets {@link GenTable}
	 * 
	 * @param tableId
	 * @return
	 */
	@RequestMapping(path = "findGenTableDetail", method = GET)
	RespBase<GenTable> findGenTableDetail(@RequestParam("tableId") Long tableId);

	/**
	 * Save {@link GenTable} and {@link GenTableColumn} configuration.
	 * 
	 * @param genTable
	 */
	@RequestMapping(path = "saveGenConfig", method = POST)
	void saveGenConfig(@RequestBody GenTable genTable);

	/**
	 * Deletion {@link GenTable}
	 * 
	 * @param tableId
	 */
	@RequestMapping(path = "deleteGenTable", method = POST)
	void deleteGenTable(@RequestParam("tableId") Long tableId);

	/**
	 * Sets {@link GenTable} status.
	 * 
	 * @param id
	 * @param status
	 */
	@RequestMapping(path = "setGenTableStatus", method = GET)
	void setGenTableStatus(@RequestParam("id") Long id, @RequestParam("status") String status);

	// ------------------------------------------
	// Generate configuration.
	// ------------------------------------------

	/**
	 * Query tables information.
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "loadTableMetadata", method = GET)
	List<TableMetadata> loadTableMetadata(@RequestParam("projectId") Long projectId) throws Exception;

	/**
	 * Load latest table metadata and columns information.
	 * 
	 * @param databaseId
	 * @param tableName
	 * @throws Exception
	 */
	@RequestMapping(path = "loadGenColumns", method = GET)
	RespBase<GenTable> loadGenColumns(@RequestParam("projectId") Long projectId, @RequestParam("tableName") String tableName)
			throws Exception;

	/**
	 * Gets {@link GenTableColumn} generated attribute types.
	 * 
	 * @param projectId
	 * @return
	 */
	@RequestMapping(path = "getAttrTypes", method = GET)
	Set<String> getAttrTypes(@RequestParam("projectId") Long projectId);

	/**
	 * Synchronizing {@link GenTable} columns.
	 * 
	 * @param id
	 * @param force
	 * @throws Exception
	 */
	@RequestMapping(path = "syncTableColumns", method = GET)
	void syncTableColumns(@RequestParam("projectId") Long id, @RequestParam("force") boolean force) throws Exception;

	// ------------------------------------------
	// Execution.
	// ------------------------------------------

	@RequestMapping(path = "generate", method = POST)
	GeneratedResult generate(@RequestParam("projectId") Long projectId);

}