package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-29 16:07:00
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;


    @RequestMapping(value = "/getRoles")
    public RespBase<?> detail() {
        RespBase<Object> resp = RespBase.create();
        List roles = roleService.getRoles();
        resp.getData().put("data",roles);
        return resp;
    }

}
