package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.iam.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vjay
 * @date 2019-10-30 15:44:00
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "/getMenuTree")
    public RespBase<?> getMenuTree() {
        RespBase<Object> resp = RespBase.create();

        return resp;
    }

}
