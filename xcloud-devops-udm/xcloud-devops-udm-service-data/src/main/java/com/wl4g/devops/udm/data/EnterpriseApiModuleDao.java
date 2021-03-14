// Generated by XCloud PaaS for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.devops.udm.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wl4g.devops.common.bean.udm.EnterpriseApiModule;

/**
 * {@link EnterpriseApiModule}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date Dec 14, 2020
 * @since v1.0
 */
public interface EnterpriseApiModuleDao {

	int insertSelective(EnterpriseApiModule enterpriseApiModule);

	int deleteByPrimaryKey(Long id);

	EnterpriseApiModule selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(EnterpriseApiModule enterpriseApiModule);

	int updateByPrimaryKey(EnterpriseApiModule enterpriseApiModule);

	List<EnterpriseApiModule> list(@Param("enterpriseApiModule") EnterpriseApiModule enterpriseApiModule);

	List<EnterpriseApiModule> getByVersionIdAndParentId(@Param("versionId") Long versionId, @Param("parentId") Long parentId);

}