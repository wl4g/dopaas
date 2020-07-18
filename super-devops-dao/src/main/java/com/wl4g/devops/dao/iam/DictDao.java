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
package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictDao {
	int deleteByPrimaryKey(String key);

	int insert(Dict record);

	int insertSelective(Dict record);

	Dict selectByPrimaryKey(String key);

	int updateByPrimaryKeySelective(Dict record);

	int updateByPrimaryKey(Dict record);

	List<Dict> selectByType(String type);

	List<String> allType();

	Dict getByKey(String key);

	List<Dict> list(@Param("key") String key, @Param("label") String label, @Param("type") String type,
			@Param("description") String description,@Param("orderBySort") String orderBySort);
}