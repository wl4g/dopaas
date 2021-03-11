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
package com.wl4g.devops.doc.data;

import java.util.List;

import com.wl4g.devops.common.bean.doc.Share;

public interface ShareDao {
	int deleteByPrimaryKey(Long id);

	int insert(Share record);

	int insertSelective(Share record);

	Share selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Share record);

	int updateByPrimaryKey(Share record);

	List<Share> list();

	Share selectByShareCode(String shareCode);
}