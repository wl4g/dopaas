package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.ApplicationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<ApplicationInfo> list = applicationDao.getByAppNames(null);
        Map<String,Object> map = new HashMap<>();
        for(ApplicationInfo application : list){
            map.put(application.getAppName(),application);
        }
        resp.getData().put("map", map);
        //System.out.println(JacksonUtils.toJSONString(list));
        return resp;
    }

}
