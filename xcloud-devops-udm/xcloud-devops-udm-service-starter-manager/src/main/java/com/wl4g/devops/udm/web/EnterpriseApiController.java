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

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.devops.common.bean.udm.EnterpriseApi;
import com.wl4g.devops.udm.fsview.config.FsViewerProperties;
import com.wl4g.devops.udm.service.EnterpriseApiService;
import com.wl4g.devops.udm.service.dto.EnterpriseApiPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
* {@link EnterpriseApi}
*
* @author root
* @version 0.0.1-SNAPSHOT
* @Date 
* @since v1.0
*/
@RestController
@RequestMapping("/enterpriseapi")
public class EnterpriseApiController extends BaseController {

    @Autowired
    private EnterpriseApiService enterpriseApiService;

    @Autowired
    private FsViewerProperties fsViewerProperties;

    @RequestMapping(value = "/list", method = { GET })
    public RespBase<PageHolder<EnterpriseApi>> list(EnterpriseApiPageRequest enterpriseApiPageRequest,PageHolder<EnterpriseApi> pm) {
        RespBase<PageHolder<EnterpriseApi>> resp = RespBase.create();
        enterpriseApiPageRequest.setPm(pm);
        resp.setData(enterpriseApiService.page(enterpriseApiPageRequest));
        return resp;
    }

    @RequestMapping(value = "/getByModuleId", method = { GET })
    public RespBase<List<EnterpriseApi>> getByModuleId(Long moduleId) {
        RespBase<List<EnterpriseApi>> resp = RespBase.create();
        resp.setData(enterpriseApiService.getByModuleId(moduleId));
        return resp;
    }

    @RequestMapping(value = "/save", method = { POST, PUT })
    public RespBase<?> save(@RequestBody EnterpriseApi enterpriseApi) {
        RespBase<Object> resp = RespBase.create();
        enterpriseApiService.save(enterpriseApi);
        return resp;
    }

    @RequestMapping(value = "/detail", method = { GET })
    public RespBase<EnterpriseApi> detail(@RequestParam(required = true) Long id) {
        RespBase<EnterpriseApi> resp = RespBase.create();
        resp.setData(enterpriseApiService.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del", method = { POST, DELETE })
    public RespBase<?> del(@RequestParam(required = true) Long id) {
        RespBase<Object> resp = RespBase.create();
        enterpriseApiService.del(id);
        return resp;
    }

    @RequestMapping(value = "/getConverterProviderKind", method = { POST,GET })
    public RespBase<?> getDataType() {
        RespBase<Object> resp = RespBase.create();
        List<String> dataTypes = enterpriseApiService.getConverterProviderKind();
        resp.setData(dataTypes);
        return resp;
    }

    @RequestMapping(value = "/importApi", method = { POST })
    public RespBase<?> importApi(String kind, String json, Long moduleId) {
        RespBase<Object> resp = RespBase.create();
        enterpriseApiService.importApi(kind,json,moduleId);
        return resp;
    }

    @RequestMapping(value = "/exportApi", method = { POST,GET })
    public void exportApi(HttpServletResponse response, String kind, Long moduleId) throws IOException {
        String s = enterpriseApiService.exportApi(kind, moduleId);
        File file = new File(fsViewerProperties.getBasePath()+ "/tmp/exportApi.json");
        FileIOUtils.writeFile(file, s, false);
        writeFile(response, file,"exportApi.json");
    }




}
