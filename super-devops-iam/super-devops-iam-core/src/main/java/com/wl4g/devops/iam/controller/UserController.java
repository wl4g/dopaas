package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-10-29 10:10:00
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequiresPermissions({"iam:user:list","iam:group:tree","iam:role:getRolesByUserGroups"})
    @RequestMapping(value = "/list")
    public RespBase<?> list(CustomPage customPage,String userName,String displayName) {
        RespBase<Object> resp = RespBase.create();
        Map<String,Object> result = userService.list(customPage, userName,displayName);
        resp.setData(result);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer userId) {
        Assert.notNull(userId,"userId is null");
        RespBase<Object> resp = RespBase.create();
        User detail = userService.detail(userId);
        resp.getData().put("data",detail);
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer userId) {
        Assert.notNull(userId,"userId is null");
        RespBase<Object> resp = RespBase.create();
        userService.del(userId);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody User user) {
        Assert.notNull(user,"user is null");
        RespBase<Object> resp = RespBase.create();
        userService.save(user);
        return resp;
    }


}
