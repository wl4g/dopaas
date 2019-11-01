package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.iam.Group;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.GroupService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-29 16:19:00
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @RequiresPermissions("iam:group:tree")
    @RequestMapping(value = "/getGroupsTree")
    public RespBase<?> getGroupsTree() {
        RespBase<Object> resp = RespBase.create();
        List<Group> groupsTree = groupService.getGroupsTree();
        resp.getData().put("data",groupsTree);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody Group group) {
        RespBase<Object> resp = RespBase.create();
        groupService.save(group);
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        groupService.del(id);
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        Group group = groupService.detail(id);
        resp.getData().put("data",group);
        return resp;
    }




}
