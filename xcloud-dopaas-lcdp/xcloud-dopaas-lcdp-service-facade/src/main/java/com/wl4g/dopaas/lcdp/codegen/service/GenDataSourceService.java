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
package com.wl4g.dopaas.lcdp.codegen.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.lcdp.GenDataSource;

/**
 * @author vjay
 */
@FeignConsumer(name = "${provider.serviceId.lcdp-facade:lcdp-facade}")
@RequestMapping("/genDataSource-service")
public interface GenDataSourceService {

	@RequestMapping(path = "page", method = POST)
	PageHolder<GenDataSource> page(@RequestBody PageHolder<GenDataSource> pm,
			@RequestParam("dataSourceName") String dataSourceName);

	@RequestMapping(path = "loadDatasources", method = GET)
	List<GenDataSource> loadDatasources();

	@RequestMapping(path = "save", method = POST)
	void save(@RequestBody GenDataSource genDatabase);

	@RequestMapping(path = "detail", method = GET)
	GenDataSource detail(@RequestParam("id") Long id);

	@RequestMapping(path = "del", method = POST)
	void del(@RequestParam("id") Long id);

	@RequestMapping(path = "testConnectDb", method = POST)
	void testConnectDb(@RequestBody GenDataSource datasource) throws Exception;

}