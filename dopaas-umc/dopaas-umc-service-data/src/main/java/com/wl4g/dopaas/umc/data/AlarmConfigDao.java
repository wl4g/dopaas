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
package com.wl4g.dopaas.umc.data;

import org.apache.ibatis.annotations.Param;

import com.wl4g.dopaas.common.bean.umc.AlarmConfig;

import java.util.List;

public interface AlarmConfigDao {
	int deleteByPrimaryKey(Long id);

	int insert(AlarmConfig record);

	int insertSelective(AlarmConfig record);

	AlarmConfig selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(AlarmConfig record);

	int updateByPrimaryKey(AlarmConfig record);

	List<AlarmConfig> selectAll();

	List<AlarmConfig> list(@Param("templateId") Long templateId, @Param("contactGroupId") Long contactGroupId);

	List<AlarmConfig> selectByTemplateId(Long templateId);

	List<AlarmConfig> getByClusterIdAndTemplateId(@Param("templateId") Long templateId, @Param("clusterId") Long clusterId);

	List<AlarmConfig> getAlarmConfigTpls(@Param("instanceIds") List<Long> instanceIds);
}