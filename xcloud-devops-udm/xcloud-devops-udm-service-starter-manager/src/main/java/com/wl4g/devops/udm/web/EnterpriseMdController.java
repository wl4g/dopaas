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
import com.wl4g.component.core.web.BaseController;
import com.wl4g.devops.common.bean.udm.EnterpriseDocument;
import com.wl4g.devops.udm.fsview.config.FsViewerProperties;
import com.wl4g.devops.udm.service.EnterpriseMdService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
* {@link EnterpriseDocument}
*
* @author root
* @version 0.0.1-SNAPSHOT
* @Date 
* @since v1.0
*/
@RestController
@RequestMapping("/md")
public class EnterpriseMdController extends BaseController {

    @Autowired
    private EnterpriseMdService enterpriseMdService;

    @Autowired
    private FsViewerProperties fsViewerProperties;

    @RequestMapping(value = "/mdToHtml", method = { POST, GET })
    public RespBase<?> mdToHtml(String md) throws IOException, TemplateException {
        RespBase<Object> resp = RespBase.create();
        resp.setData(enterpriseMdService.mdToHtml(md));
        return resp;
    }

    @RequestMapping(value = "/getTemplate", method = { POST, GET })
    public RespBase<?> getTemplate() {
        RespBase<Object> resp = RespBase.create();
        String templatePath = fsViewerProperties.getBasePath() + "/template";
        File templateDir = new File(templatePath);
        resp.setData(templateDir.list());
        return resp;
    }

    @RequestMapping(value = "/formatTemplate", method = { POST, GET })
    public void formatTemplate(HttpServletResponse response, String md, String template) throws Exception {
        //RespBase<Object> resp = RespBase.create();
        String genPath = enterpriseMdService.formatTemplate(md, template);
        //resp.setData();
        writeZip(response, genPath, "codegen-".concat(md).concat("-").concat(template));
        //return resp;
    }



}
