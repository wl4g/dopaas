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

import com.wl4g.devops.common.bean.iam.Contact;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactDao {
	int deleteByPrimaryKey(Integer id);

	int insert(Contact record);

	int insertSelective(Contact record);

	Contact selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Contact record);

	int updateByPrimaryKey(Contact record);

	List<Contact> list(@Param("name") String name);

	List<Contact> getContactByGroupIds(@Param("groupIds") List<Integer> groupIds);

}