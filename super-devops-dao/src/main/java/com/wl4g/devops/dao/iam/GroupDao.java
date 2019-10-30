package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Group;

import java.util.List;

public interface GroupDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);

    List<Group> selectByUserId(Integer userId);

    List<Group> selectByParentId(Integer parentId);


}