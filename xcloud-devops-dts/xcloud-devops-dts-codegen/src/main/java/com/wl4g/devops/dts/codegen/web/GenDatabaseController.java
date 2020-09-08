package com.wl4g.devops.dts.codegen.web;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import com.wl4g.devops.dts.codegen.service.GenDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author vjay
 * @date 2020-09-08 14:22:00
 */
@Controller
@RequestMapping("/genDatabase")
public class GenDatabaseController extends BaseController {

    @Autowired
    private GenDatabaseService genDatabaseService;

    @RequestMapping(value = "/list")
    public RespBase<?> list(PageModel pm, String name) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(genDatabaseService.page(pm, name));
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody GenDatabase genDatabase) {
        RespBase<Object> resp = RespBase.create();
        genDatabaseService.save(genDatabase);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(genDatabaseService.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        genDatabaseService.del(id);
        return resp;
    }

    @RequestMapping(value = "/getForSelect")
    public RespBase<?> getForSelect() {
        RespBase<Object> resp = RespBase.create();
        resp.setData(genDatabaseService.getForSelect());
        return resp;
    }
}
