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
package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenTableDao {

	int deleteByPrimaryKey(Long id);

	int insert(GenTable record);

	int insertSelective(GenTable record);

	GenTable selectByPrimaryKey(Long id);

	List<GenTable> selectByProjectId(Long projectId);

	Long countByProjectIdAndTableName(@Param("projectId") Long projectId, @Param("tableName") String tableName);

	int updateByPrimaryKeySelective(GenTable record);

	int updateByPrimaryKey(GenTable record);

	List<GenDataSource> list(@Param("tableName") String tableName, @Param("projectId") Long projectId);
}