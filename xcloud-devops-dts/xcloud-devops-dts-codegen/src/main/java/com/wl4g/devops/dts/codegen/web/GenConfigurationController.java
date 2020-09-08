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
package com.wl4g.devops.dts.codegen.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.service.GenConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * {@link GenConfigurationController}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@Controller
@RequestMapping("/gen/configure")
public class GenConfigurationController {

    @Autowired
    private GenConfigurationService genConfigurationService;

    @RequestMapping("loadMetadata")
    public RespBase<?> loadMetadata(Integer databaseId,String tableName){
        RespBase<Object> resp = RespBase.create();
        GenTable genTable = genConfigurationService.loadMetadata(databaseId, tableName);
        resp.setData(genTable);
        return resp;
    }

    @RequestMapping("saveGenConfig")
    public RespBase<?> saveGenConfig(GenTable genTable){
        RespBase<Object> resp = RespBase.create();
        genConfigurationService.saveGenConfig(genTable);
        return resp;
    }

    @RequestMapping("genConfigDetail")
    public RespBase<?> genConfigDetail(Integer tableId){
        RespBase<Object> resp = RespBase.create();
        resp.setData(genConfigurationService.detail(tableId));
        return resp;
    }

    @RequestMapping("delGenConfig")
    public RespBase<?> delGenConfig(Integer tableId){
        RespBase<Object> resp = RespBase.create();
        genConfigurationService.delete(tableId);
        return resp;
    }


    @RequestMapping("generate")
    public RespBase<?> generate(String id){
        //TODO generate
        RespBase<Object> resp = RespBase.create();
        return resp;
    }







}
