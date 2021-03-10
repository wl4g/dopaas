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
package com.wl4g.devops.umc.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.devops.common.bean.umc.MetricTemplate;

import java.util.List;

public interface MetricTemplateDao {
	int deleteByPrimaryKey(Long id);

	int insert(MetricTemplate record);

	int insertSelective(MetricTemplate record);

	MetricTemplate selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(MetricTemplate record);

	int updateByPrimaryKey(MetricTemplate record);

	List<MetricTemplate> list(@Param("metric") String metric, @Param("classify") String classify);
}