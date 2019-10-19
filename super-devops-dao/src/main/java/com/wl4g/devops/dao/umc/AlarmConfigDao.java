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
package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlarmConfigDao {
	int deleteByPrimaryKey(Integer id);

	int insert(AlarmConfig record);

	int insertSelective(AlarmConfig record);

	AlarmConfig selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AlarmConfig record);

	int updateByPrimaryKey(AlarmConfig record);

	List<AlarmConfig> selectAll();

	List<AlarmConfig> list(@Param("templateId") Integer templateId, @Param("contactGroupId") Integer contactGroupId);

	List<AlarmConfig> selectByTemplateId(Integer templateId);

	List<AlarmConfig> getByCollectAddrAndTemplateId(@Param("templateId") Integer templateId,
			@Param("collectAddr") String collectAddr);

	List<AlarmConfig> getByClusterIdAndTemplateId(@Param("templateId") Integer templateId, @Param("clusterId") Integer clusterId);

	List<AlarmConfig> getAlarmConfigTpls(@Param("host") String host, @Param("endpoint") String endpoint);
}