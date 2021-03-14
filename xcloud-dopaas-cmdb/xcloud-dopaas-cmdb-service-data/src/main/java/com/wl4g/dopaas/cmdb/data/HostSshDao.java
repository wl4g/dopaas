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
package com.wl4g.dopaas.cmdb.data;

import org.apache.ibatis.annotations.Param;

import com.wl4g.dopaas.common.bean.cmdb.HostSsh;

import java.util.List;

public interface HostSshDao {
	int deleteByPrimaryKey(Long id);

	int deleteByHostId(Long hostId);

	int insert(HostSsh record);

	int insertSelective(HostSsh record);

	HostSsh selectByPrimaryKey(Long id);

	List<Long> selectByHostId(Long hostId);

	int updateByPrimaryKeySelective(HostSsh record);

	int updateByPrimaryKey(HostSsh record);

	int insertBatch(@Param("hostSshes") List<HostSsh> hostSshes);
}