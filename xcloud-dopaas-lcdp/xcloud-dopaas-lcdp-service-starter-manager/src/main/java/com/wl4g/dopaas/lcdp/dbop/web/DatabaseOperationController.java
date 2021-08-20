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
package com.wl4g.dopaas.lcdp.dbop.web;

import static org.apache.shiro.authz.annotation.Logical.AND;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.common.bean.lcdp.dbop.DatabaseOperation;
import com.wl4g.dopaas.lcdp.dds.service.DatabaseOperationService;

/**
 * {@link DatabaseOperationController}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
@RestController
@RequestMapping("/dds/operation")
public class DatabaseOperationController extends BaseController {

    private @Autowired DatabaseOperationService dbOperationService;

    @RequestMapping(value = "/list")
    @RequiresPermissions(value = { "lcdp:dds:dbop" }, logical = AND)
    public RespBase<PageHolder<DatabaseOperation>> list(PageHolder<DatabaseOperation> pm, String name) {
        RespBase<PageHolder<DatabaseOperation>> resp = RespBase.create();
        resp.setData(dbOperationService.page(pm, name));
        return resp;
    }

    @RequestMapping(value = "/save")
    @RequiresPermissions(value = { "lcdp:dds:dbop" }, logical = AND)
    public RespBase<?> save(@RequestBody DatabaseOperation gen) {
        RespBase<Object> resp = RespBase.create();
        dbOperationService.save(gen);
        return resp;
    }

    @RequestMapping(value = "/detail")
    @RequiresPermissions(value = { "lcdp:dds:dbop" }, logical = AND)
    public RespBase<?> detail(Long id) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(dbOperationService.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del")
    @RequiresPermissions(value = { "lcdp:dds:dbop" }, logical = AND)
    public RespBase<?> del(Long id) {
        RespBase<Object> resp = RespBase.create();
        dbOperationService.del(id);
        return resp;
    }

}
