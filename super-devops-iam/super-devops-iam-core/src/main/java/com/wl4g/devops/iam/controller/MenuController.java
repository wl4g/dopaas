package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-30 15:44:00
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;


    @RequestMapping(value = "/getMenuTree")
    public RespBase<?> getMenuTree() {
        RespBase<Object> resp = RespBase.create();
        List<Menu> menuTree = menuService.getMenuTree();
        resp.getData().put("data",menuTree);
        return resp;
    }

    @RequestMapping(value = "/getMenuList")
    public RespBase<?> getMenuList() {
        RespBase<Object> resp = RespBase.create();
        List<Menu> menus = menuService.getMenuList();
        resp.getData().put("data",menus);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody Menu menu) {
        RespBase<Object> resp = RespBase.create();
        menuService.save(menu);
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        menuService.del(id);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        Menu menu = menuService.detail(id);
        resp.getData().put("data",menu);
        return resp;
    }


}
