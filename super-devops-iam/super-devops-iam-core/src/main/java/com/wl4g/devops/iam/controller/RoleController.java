package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.iam.Role;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/list")
    public RespBase<?> list(CustomPage customPage, String name, String displayName) {
        RespBase<Object> resp = RespBase.create();
        Map<String,Object> result = roleService.list(customPage, name,displayName);
        resp.setData(result);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody Role role) {
        RespBase<Object> resp = RespBase.create();
        roleService.save(role);
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        roleService.del(id);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        Role role = roleService.detail(id);
        resp.getData().put("data",role);
        return resp;
    }

}
