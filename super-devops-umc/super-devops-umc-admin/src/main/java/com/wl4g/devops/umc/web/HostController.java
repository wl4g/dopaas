package com.wl4g.devops.umc.web;

import com.wl4g.devops.common.bean.app.AppHost;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.app.HostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/host")
public class HostController extends BaseController {

    @Autowired
    private HostDao hostDao;

    @RequestMapping(value = "/all")
    public RespBase<?> all() {
        RespBase<Object> resp = RespBase.create();
        List<AppHost> list = hostDao.list();
        resp.getData().put("list", list);
        return resp;
    }




}
