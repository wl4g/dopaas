package com.wl4g.devops.dts.codegen.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.service.GenProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
* GenProjectServiceImpl
*
* @author heweijie
* @Date 2020-09-11
*/
@RestController
@RequestMapping("/project")
public class GenProjectController extends BaseController {

    @Autowired
    private GenProjectService genProjectService;

    @RequestMapping(value = "/list")
    public RespBase<?> list(PageModel pm, String projectName) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(genProjectService.page(pm, projectName));
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody GenProject genProject) {
        RespBase<Object> resp = RespBase.create();
        genProjectService.save(genProject);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(genProjectService.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        genProjectService.del(id);
        return resp;
    }

    @RequestMapping(value = "/getProviderConfig")
    public RespBase<?> getProviderConfig(String genProviderGroup) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(genProjectService.getProviderConfig(genProviderGroup));
        return resp;
    }

}
