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
package com.wl4g.dopaas.uds.data.elasticjoblite;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wl4g.dopaas.common.bean.uds.elasticjoblite.RegistryCenterConfig;

public interface RegCenterConfigDao {

	List<RegistryCenterConfig> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name,
			@Param("type") Integer type);

	int deleteByPrimaryKey(@Param("id") Long id);

	int insertSelective(RegistryCenterConfig config);

	int updateByPrimaryKeySelective(RegistryCenterConfig config);

}