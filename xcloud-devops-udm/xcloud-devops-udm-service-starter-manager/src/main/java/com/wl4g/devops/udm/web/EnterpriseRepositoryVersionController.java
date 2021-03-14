// Generated by XCloud PaaS for Codegen, refer: http://dts.devops.wl4g.com

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
package com.wl4g.devops.udm.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.devops.common.bean.udm.EnterpriseRepositoryVersion;
import com.wl4g.devops.udm.service.EnterpriseRepositoryVersionService;
import com.wl4g.devops.udm.service.dto.EnterpriseRepositoryVersionPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
* {@link EnterpriseRepositoryVersion}
*
* @author root
* @version 0.0.1-SNAPSHOT
* @Date 
* @since v1.0
*/
@RestController
@RequestMapping("/enterpriserepositoryversion")
public class EnterpriseRepositoryVersionController extends BaseController {

    @Autowired
    private EnterpriseRepositoryVersionService enterpriseRepositoryVersionService;

    @RequestMapping(value = "/list", method = { GET })
    public RespBase<PageHolder<EnterpriseRepositoryVersion>> list(EnterpriseRepositoryVersionPageRequest enterpriseRepositoryVersionPageRequest,PageHolder<EnterpriseRepositoryVersion> pm) {
        RespBase<PageHolder<EnterpriseRepositoryVersion>> resp = RespBase.create();
        enterpriseRepositoryVersionPageRequest.setPm(pm);
        resp.setData(enterpriseRepositoryVersionService.page(enterpriseRepositoryVersionPageRequest));
        return resp;
    }

    @RequestMapping(value = "/getVersionsByRepositoryId", method = { GET })
    public RespBase<List<EnterpriseRepositoryVersion>> getVersionsByRepositoryId(Long repositoryId) {
        RespBase<List<EnterpriseRepositoryVersion>> resp = RespBase.create();
        resp.setData(enterpriseRepositoryVersionService.getVersionsByRepositoryId(repositoryId));
        return resp;
    }

    @RequestMapping(value = "/save", method = { POST, PUT })
    public RespBase<?> save(@RequestBody EnterpriseRepositoryVersion enterpriseRepositoryVersion) {
        RespBase<Object> resp = RespBase.create();
        enterpriseRepositoryVersionService.save(enterpriseRepositoryVersion);
        return resp;
    }

    @RequestMapping(value = "/detail", method = { GET })
    public RespBase<EnterpriseRepositoryVersion> detail(@RequestParam(required = true) Long id) {
        RespBase<EnterpriseRepositoryVersion> resp = RespBase.create();
        resp.setData(enterpriseRepositoryVersionService.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del", method = { POST, DELETE })
    public RespBase<?> del(@RequestParam(required = true) Long id) {
        RespBase<Object> resp = RespBase.create();
        enterpriseRepositoryVersionService.del(id);
        return resp;
    }

}
