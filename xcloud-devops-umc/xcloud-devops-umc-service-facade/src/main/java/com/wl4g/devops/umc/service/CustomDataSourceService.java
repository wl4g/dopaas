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
package com.wl4g.devops.umc.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.devops.common.bean.umc.CustomDataSource;
import com.wl4g.devops.common.bean.umc.CustomDataSourceProperties;
import com.wl4g.devops.common.bean.umc.datasource.BaseDataSource;
import com.wl4g.devops.common.bean.umc.model.DataSourceProvide;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
@FeignConsumer(name = "${provider.serviceId.umc-facade:umc-facade}")
@RequestMapping("/customDataSource-service")
public interface CustomDataSourceService {

	PageHolder<CustomDataSource> list(PageHolder<CustomDataSource> pm, String name);

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