package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.iam.Group;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/getGroupsTree")
    public RespBase<?> getGroupsTree() {
        RespBase<Object> resp = RespBase.create();
        List<Group> groupsTree = groupService.getGroupsTree();
        resp.getData().put("data",groupsTree);
        return resp;
    }




}
