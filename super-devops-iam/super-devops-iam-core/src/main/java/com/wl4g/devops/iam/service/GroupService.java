package com.wl4g.devops.iam.service;

import com.wl4g.devops.common.bean.iam.Group;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-29 16:19:00
 */
public interface GroupService {

    List<Group> getGroupsTree();

    void save(Group group);

    void del(Integer id);

    Group detail(Integer id);

}
