package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.GroupRole;

public interface GroupRoleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupRole record);

    int insertSelective(GroupRole record);

    GroupRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupRole record);

    int updateByPrimaryKey(GroupRole record);
}