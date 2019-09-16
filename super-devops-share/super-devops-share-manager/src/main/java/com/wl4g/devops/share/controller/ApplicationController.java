package com.wl4g.devops.share.controller;

import com.wl4g.devops.common.bean.share.Application;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.ApplicationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-09-16 14:32:00
 */
@RestController
@RequestMapping("/application")
public class ApplicationController extends BaseController {

    @Autowired
    private ApplicationDao applicationDao;

    @RequestMapping(value = "/info")
    public RespBase<?> allType() {
        RespBase<Object> resp = RespBase.create();
        List<Application> list = applicationDao.getByAppNames(null);
        resp.getData().put("list", list);
        System.out.println(JacksonUtils.toJSONString(list));
        return resp;
    }

}
