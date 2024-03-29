// Generated by DoPaaS for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.dopaas.udm.data;

import com.wl4g.dopaas.common.bean.udm.EnterpriseApi;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * {@link EnterpriseApi}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date Dec 14, 2020
 * @since v1.0
 */
public interface EnterpriseApiDao {

	int insertSelective(EnterpriseApi enterpriseApi);

	int deleteByPrimaryKey(Long id);

	int deleteByModuleId(Long id);

	EnterpriseApi selectByPrimaryKey(Long id);

	EnterpriseApi selectByModuleIdAndUrl(@Param("moduleId") Long moduleId, @Param("url") String url);

	int updateByPrimaryKeySelective(EnterpriseApi enterpriseApi);

	int updateByPrimaryKey(EnterpriseApi enterpriseApi);

	List<EnterpriseApi> list(@Param("enterpriseApi") EnterpriseApi enterpriseApi);

	List<EnterpriseApi> getByModuleId(Long moduleId);

}