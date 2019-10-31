package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.iam.Group;
import com.wl4g.devops.dao.iam.GroupDao;
import com.wl4g.devops.iam.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-29 16:19:00
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao groupDao;

    @Override
    public List<Group> getGroupsTree() {
        //TODO get groups tree by current logined user
        return getChildrens(1);
    }

    private List<Group> getChildrens(Integer parentId){
        List<Group> childrens = groupDao.selectByParentId(parentId);
        //groups.addAll(childrens);
        for(Group group : childrens){
            List<Group> childrens1 = getChildrens(group.getId());
            if(!CollectionUtils.isEmpty(childrens1)){
                group.setChildren(childrens1);
            }
        }
        return childrens;
    }





}
