package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.GroupUser;

public interface GroupUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupUser record);

    int insertSelective(GroupUser record);

    GroupUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupUser record);

    int updateByPrimaryKey(GroupUser record);
}