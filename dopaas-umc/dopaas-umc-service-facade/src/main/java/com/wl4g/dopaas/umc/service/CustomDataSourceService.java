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
package com.wl4g.dopaas.umc.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.umc.CustomDataSource;
import com.wl4g.dopaas.common.bean.umc.datasource.BaseDataSource;
import com.wl4g.dopaas.common.bean.umc.model.DataSourceProvide;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
@FeignConsumer(name = "${provider.serviceId.umc-facade:umc-facade}")
@RequestMapping("/customDataSource-service")
public interface CustomDataSourceService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<CustomDataSource> list(@RequestBody PageHolder<CustomDataSource> pm, @RequestParam("name") String name);

	@RequestMapping(value = "/detail", method = GET)
	BaseDataSource detail(@RequestParam("id") Long id);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody BaseDataSource baseDataSource);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam("id") Long id);

	@RequestMapping(value = "/testConnect", method = POST)
	void testConnect(DataSourceProvide dataSourceProvide, @RequestParam("url") String url,
			@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("id") Long id)
			throws Exception;

	@RequestMapping(value = "/dataSources", method = GET)
	List<CustomDataSource> dataSources();

	@RequestMapping(value = "/properties2Model", method = POST)
	<T extends BaseDataSource> T properties2Model(@RequestBody CustomDataSource customDataSource);

}