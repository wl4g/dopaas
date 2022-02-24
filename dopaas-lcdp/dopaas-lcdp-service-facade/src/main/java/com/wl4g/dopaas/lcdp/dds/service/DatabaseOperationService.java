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
package com.wl4g.dopaas.lcdp.dds.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.infra.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.lcdp.dbop.DatabaseOperation;

/**
 * {@link DatabaseOperationService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
@FeignConsumer(name = "${provider.serviceId.lcdp-facade:lcdp-facade}")
@RequestMapping("/dbop-service")
public interface DatabaseOperationService {

    @RequestMapping(path = "page", method = POST)
    PageHolder<DatabaseOperation> page(@RequestBody PageHolder<DatabaseOperation> pm,
            @RequestParam("dataSourceName") String dataSourceName);

    @RequestMapping(path = "save", method = POST)
    void save(@RequestBody DatabaseOperation operation);

    @RequestMapping(path = "detail", method = GET)
    DatabaseOperation detail(@RequestParam("id") Long id);

    @RequestMapping(path = "del", method = POST)
    void del(@RequestParam("id") Long id);

}