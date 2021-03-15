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

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.udc.GenTable;
import com.wl4g.dopaas.common.bean.udc.GenTableColumn;

/**
 * {@link GenTableService}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@FeignConsumer(name = "${provider.serviceId.udc-udc-facade}")
@RequestMapping("/genTable-service")
public interface GenTableService {

	PageHolder<GenTable> searchPage(@RequestBody PageHolder<GenTable> pm, @RequestParam("tableName") String tableName,
			@RequestParam("projectId") Long projectId);

	/**
	 * Gets {@link GenTable}
	 * 
	 * @param tableId
	 * @return
	 */
	GenTable getGenTable(@RequestParam("tableId") Long tableId);

	/**
	 * Query {@link GenTable} list.
	 * 
	 * @param projectId
	 * @return
	 */
	List<GenTable> findGenTables(@RequestParam("projectId") Long projectId);

	/**
	 * Gets {@link GenTable} count
	 * 
	 * @param projectId
	 * @param tableName
	 * @return
	 */
	Long getGenTableCount(@RequestParam("projectId") Long projectId, @RequestParam("tableName") String tableName);

	/**
	 * Query {@link GenTableColumn} list.
	 * 
	 * @param tableId
	 * @return
	 */
	List<GenTableColumn> findGenTableColumns(@RequestParam("tableId") Long tableId);

	/**
	 * Save {@link GenTable} and {@link GenTableColumn} configuration.
	 * 
	 * @param genTable
	 */
	void save(@RequestBody GenTable genTable);

	/**
	 * Deletion {@link GenTable}
	 * 
	 * @param tableId
	 */
	void deleteGenTable(@RequestParam("tableId") Long tableId);

}