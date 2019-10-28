package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Group;

public interface GroupDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);
}