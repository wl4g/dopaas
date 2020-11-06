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
package com.wl4g.devops.umc.service;

import com.wl4g.components.core.bean.umc.CustomDataSource;
import com.wl4g.components.core.bean.umc.CustomDataSourceProperties;
import com.wl4g.components.core.bean.umc.datasouces.BaseDataSource;
import com.wl4g.components.core.bean.umc.model.DataSourceProvide;
import com.wl4g.components.data.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface CustomDataSourceService {

	PageModel<CustomDataSource> list(PageModel<CustomDataSource> pm, String name);

	BaseDataSource detal(Long id);

	void save(BaseDataSource baseDataSource);

	void del(Long id);

	void testConnect(DataSourceProvide dataSourceProvide, String url, String username, String password, Long id) throws Exception;

	List<CustomDataSource> dataSources();

	CustomDataSource model2Properties(BaseDataSource baseDataSource);

	List<CustomDataSourceProperties> objectToCustomDataSourceProperties(Object obj, Long dataSourceId)
			throws IllegalAccessException;

	<T extends BaseDataSource> T properties2Model(CustomDataSource customDataSource);

}