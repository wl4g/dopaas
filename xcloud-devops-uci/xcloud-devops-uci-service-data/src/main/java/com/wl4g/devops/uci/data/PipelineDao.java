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
package com.wl4g.devops.uci.data;

import com.wl4g.devops.common.bean.uci.Pipeline;
import com.wl4g.devops.common.bean.cmdb.AppInstance;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PipelineDao {
	int deleteByPrimaryKey(Long id);

	int insert(Pipeline record);

	int insertSelective(Pipeline record);

	Pipeline selectByPrimaryKey(Long id);

	List<Pipeline> selectByClusterId(Long clusterId);

	int updateByPrimaryKeySelective(Pipeline record);

	int updateByPrimaryKey(Pipeline record);

	List<Pipeline> list(@Param("organizationCodes") List<String> organizationCodes, @Param("id") Long id,
			@Param("pipeName") String pipeName, @Param("providerKind") String providerKind,
			@Param("environment") String environment, @Param("clusterName") String clusterName);

	List<AppInstance> selectInstancesByDeployId(@Param("deployId") Long deployId);
}